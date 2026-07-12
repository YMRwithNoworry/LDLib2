const crypto = require("crypto");
const fs = require("fs");
const path = require("path");
const zlib = require("zlib");

const root = path.resolve(__dirname, "..");
const releaseJar = process.argv[2] || process.env.LDLIB2_UPSTREAM_JAR;
const portJars = process.argv.slice(3);

if (!releaseJar) {
  throw new Error("Usage: node scripts/verify-upstream-2.2.28-compat.cjs <ldlib2-2.2.28.jar>");
}

if (!fs.existsSync(releaseJar)) {
  throw new Error(`Original LDLib2 jar does not exist: ${releaseJar}`);
}

function assert(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

function read(relativePath) {
  return fs.readFileSync(path.join(root, relativePath), "utf8");
}

function assertIncludes(relativePath, source, expected) {
  assert(source.includes(expected), `${relativePath} is missing: ${expected}`);
}

function assertOrderedIncludes(relativePath, source, expectedValues) {
  let offset = 0;
  for (const expected of expectedValues) {
    const nextOffset = source.indexOf(expected, offset);
    assert(nextOffset >= 0, `${relativePath} is missing: ${expected}`);
    offset = nextOffset + expected.length;
  }
}

function sha256(bytes) {
  return crypto.createHash("sha256").update(bytes).digest("hex");
}

class ZipArchive {
  constructor(file) {
    this.bytes = fs.readFileSync(file);
    this.entries = this.readEntries();
  }

  readEntries() {
    const minimumEocdOffset = Math.max(0, this.bytes.length - 0xffff - 22);
    let eocdOffset = -1;
    for (let offset = this.bytes.length - 22; offset >= minimumEocdOffset; offset -= 1) {
      if (this.bytes.readUInt32LE(offset) === 0x06054b50) {
        eocdOffset = offset;
        break;
      }
    }
    assert(eocdOffset >= 0, "The supplied file is not a ZIP/JAR archive.");

    const count = this.bytes.readUInt16LE(eocdOffset + 10);
    let offset = this.bytes.readUInt32LE(eocdOffset + 16);
    const entries = new Map();
    for (let index = 0; index < count; index += 1) {
      assert(this.bytes.readUInt32LE(offset) === 0x02014b50, "Invalid ZIP central-directory entry.");
      const compression = this.bytes.readUInt16LE(offset + 10);
      const compressedSize = this.bytes.readUInt32LE(offset + 20);
      const fileNameLength = this.bytes.readUInt16LE(offset + 28);
      const extraLength = this.bytes.readUInt16LE(offset + 30);
      const commentLength = this.bytes.readUInt16LE(offset + 32);
      const localOffset = this.bytes.readUInt32LE(offset + 42);
      const name = this.bytes.subarray(offset + 46, offset + 46 + fileNameLength).toString("utf8");
      entries.set(name, { compression, compressedSize, localOffset });
      offset += 46 + fileNameLength + extraLength + commentLength;
    }
    return entries;
  }

  readEntry(name) {
    const entry = this.entries.get(name);
    assert(entry, `Original jar is missing ${name}`);
    const local = entry.localOffset;
    assert(this.bytes.readUInt32LE(local) === 0x04034b50, `Invalid ZIP local entry for ${name}`);
    const fileNameLength = this.bytes.readUInt16LE(local + 26);
    const extraLength = this.bytes.readUInt16LE(local + 28);
    const dataStart = local + 30 + fileNameLength + extraLength;
    const compressed = this.bytes.subarray(dataStart, dataStart + entry.compressedSize);
    if (entry.compression === 0) {
      return Buffer.from(compressed);
    }
    if (entry.compression === 8) {
      return zlib.inflateRawSync(compressed);
    }
    throw new Error(`Unsupported ZIP compression ${entry.compression} for ${name}`);
  }
}

class NbtReader {
  constructor(bytes) {
    this.bytes = bytes;
    this.offset = 0;
  }

  readByte() {
    assert(this.offset < this.bytes.length, "Unexpected end of NBT input.");
    return this.bytes[this.offset++];
  }

  readInt() {
    this.require(4);
    const value = this.bytes.readInt32BE(this.offset);
    this.offset += 4;
    return value;
  }

  readString() {
    this.require(2);
    const length = this.bytes.readUInt16BE(this.offset);
    this.offset += 2;
    this.require(length);
    const value = this.bytes.subarray(this.offset, this.offset + length).toString("utf8");
    this.offset += length;
    return value;
  }

  require(length) {
    assert(length >= 0 && this.offset + length <= this.bytes.length, "Invalid NBT length.");
  }

  skip(length) {
    this.require(length);
    this.offset += length;
  }

  readPayload(type) {
    switch (type) {
      case 1: this.skip(1); return null;
      case 2: this.skip(2); return null;
      case 3: this.skip(4); return null;
      case 4: this.skip(8); return null;
      case 5: this.skip(4); return null;
      case 6: this.skip(8); return null;
      case 7: this.skip(this.readInt()); return null;
      case 8: return this.readString();
      case 9: {
        const elementType = this.readByte();
        const count = this.readInt();
        for (let index = 0; index < count; index += 1) this.readPayload(elementType);
        return null;
      }
      case 10: return this.readCompound();
      case 11: this.skip(this.readInt() * 4); return null;
      case 12: this.skip(this.readInt() * 8); return null;
      default: throw new Error(`Unsupported NBT tag type ${type}`);
    }
  }

  readCompound() {
    const values = new Map();
    while (true) {
      const type = this.readByte();
      if (type === 0) return values;
      values.set(this.readString(), this.readPayload(type));
    }
  }
}

function readRootCompound(bytes) {
  const reader = new NbtReader(bytes);
  assert(reader.readByte() === 10, "Expected a root NBT compound.");
  assert(reader.readString() === "", "Expected an unnamed root NBT compound.");
  const rootTag = reader.readCompound();
  assert(reader.offset === bytes.length, "Unexpected data after the root NBT compound.");
  return rootTag;
}

const archive = new ZipArchive(releaseJar);
const shippedEntries = [
  "assets/ldlib2/resources/examples/button.ui.nbt",
  "assets/ldlib2/resources/examples/example_layout.ui.nbt",
  "assets/ldlib2/lss/gdp.lss",
  "assets/ldlib2/lss/mc.lss",
  "assets/ldlib2/lss/modern.lss",
  "assets/ldlib2/lss/ore.lss",
];

for (const entry of shippedEntries) {
  const original = archive.readEntry(entry);
  const port = fs.readFileSync(path.join(root, "common", "src", "main", "resources", entry));
  assert(original.equals(port), `${entry} differs from the original 2.2.28 release (original=${sha256(original)}, port=${sha256(port)}).`);
}

for (const portJar of portJars) {
  assert(fs.existsSync(portJar), `Port artifact does not exist: ${portJar}`);
  const artifact = new ZipArchive(portJar);
  for (const entry of shippedEntries) {
    const original = archive.readEntry(entry);
    const port = artifact.readEntry(entry);
    assert(original.equals(port), `${path.basename(portJar)}:${entry} differs from the original 2.2.28 release (original=${sha256(original)}, port=${sha256(port)}).`);
  }
}

for (const entry of shippedEntries.filter((name) => name.endsWith(".ui.nbt"))) {
  const nbt = readRootCompound(archive.readEntry(entry));
  assert(nbt.get("type") === "ui", `${entry} must retain the upstream ui resource type.`);
  const data = nbt.get("data");
  assert(data instanceof Map && data.has("template"), `${entry} must retain the upstream UI template payload.`);
}

const fileProviderPath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/resource/FileResourceProvider.java";
const fileProvider = read(fileProviderPath);
assertIncludes(fileProviderPath, fileProvider, 'nbt.put("data", tag);');
assertIncludes(fileProviderPath, fileProvider, 'nbt.putString("type", resourceInstance.resource.getName());');
assertIncludes(fileProviderPath, fileProvider, "NbtIo.write(nbt, file);");
assertIncludes(fileProviderPath, fileProvider, "NbtIo.read(file);");
assertIncludes(fileProviderPath, fileProvider, 'data.putInt("_version", 1);');
assertIncludes(fileProviderPath, fileProvider, "data.putString(\"location\", realPath.replace('\\\\', '/'));" );

const packProviderPath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/resource/PackFileResourceProvider.java";
const packProvider = read(packProviderPath);
assertIncludes(packProviderPath, packProvider, 'if (nbt.getString("type").equals(resourceInstance.resource.getName()))');
assertIncludes(packProviderPath, packProvider, 'return resourceInstance.resource.deserializeResource(nbt.get("data"), provider);');

const projectPath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/project/IProject.java";
const project = read(projectPath);
assertIncludes(projectPath, project, 'data.put("meta", getMetadata());');
assertIncludes(projectPath, project, 'data.put("data", serializeProject(provider));');
assertIncludes(projectPath, project, 'deserializeProject(provider, nbt.getCompound("data"));');

const projectTypePath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/project/ProjectType.java";
const projectType = read(projectTypePath);
assertIncludes(projectTypePath, projectType, "NbtIo.write(fileData, file);");
assertIncludes(projectTypePath, projectType, "NbtIo.read(file);");

const resourceInstancePath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/resource/ResourceInstance.java";
const resourceInstance = read(resourceInstancePath);
assertIncludes(resourceInstancePath, resourceInstance, 'data.putString("displayMode", displayMode.name());');
assertIncludes(resourceInstancePath, resourceInstance, 'data.putInt("uiWidth", uiWidth);');
assertIncludes(resourceInstancePath, resourceInstance, 'data.put("customProviders", customProviders);');
assertIncludes(resourceInstancePath, resourceInstance, "NbtIo.write(data, metaFile);");

const layoutStorePath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/ui/EditorLayoutStore.java";
const layoutStore = read(layoutStorePath);
assertIncludes(layoutStorePath, layoutStore, "NbtIo.write(layout.serialize(), getFile(projectTypeName));");
assertIncludes(layoutStorePath, layoutStore, "var tag = NbtIo.read(file);");
assertIncludes(layoutStorePath, layoutStore, "EditorLayout.deserialize(tag)");

const xmlProjectPath = "common/src/main/java/com/lowdragmc/lowdraglib2/gui/editor/UIXmlProjectType.java";
const xmlProject = read(xmlProjectPath);
assertIncludes(xmlProjectPath, xmlProject, "Files.writeString(file.toPath(), xmlProject.getXml());");
assertIncludes(xmlProjectPath, xmlProject, "new UIXmlProject().setXml(rawText)");

const editorSettingsPath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/settings/EditorSettings.java";
const editorSettings = read(editorSettingsPath);
assertIncludes(editorSettingsPath, editorSettings, "var json = serializeSettings();");
assertIncludes(editorSettingsPath, editorSettings, "writer.write(json.toString());");
assertIncludes(editorSettingsPath, editorSettings, "JsonParser.parseReader(reader).getAsJsonObject()");

const fileUtilityPath = "common/src/main/java/com/lowdragmc/lowdraglib2/utils/FileUtility.java";
const fileUtility = read(fileUtilityPath);
assertIncludes(fileUtilityPath, fileUtility, "StandardCharsets.UTF_8");
assertIncludes(fileUtilityPath, fileUtility, "writer.write(GSON_PRETTY.toJson(element));");

const portModelPath = "common/src/main/java/com/lowdragmc/lowdraglib2/nodegraphtookit/model/node/PortModel.java";
const portModel = read(portModelPath);
assertOrderedIncludes(portModelPath, portModel, [
  "var newUid = computePortUid(nodeModel, direction, portId, portType, dataTypeHandle, parentPort);",
  "if (!newUid.equals(getUid())) {",
  "if (graphModel != null) graphModel.unregisterPort(this);",
  "setUid(newUid);",
  "if (graphModel != null) graphModel.registerPort(this);",
  "this.dataTypeHandle = dataTypeHandle;",
]);

console.log("LDLib2 2.2.28 release resources and persistence envelopes are compatible.");
