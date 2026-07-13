const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const forgeEntryPath = "forge/src/main/java/com/lowdragmc/lowdraglib2/forge/LDLib2Forge.java";
const forgeClientPath = "forge/src/main/java/com/lowdragmc/lowdraglib2/forge/client/ForgeClientBootstrap.java";
const propertyRegistryPath = "common/src/main/java/com/lowdragmc/lowdraglib2/gui/ui/style/PropertyRegistry.java";
const forgeEntry = fs.readFileSync(path.join(root, forgeEntryPath), "utf8");
const propertyRegistry = fs.readFileSync(path.join(root, propertyRegistryPath), "utf8");

function assert(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

for (const clientReference of [
  "com.lowdragmc.lowdraglib2.client.",
  "net.minecraft.client.",
  "net.minecraftforge.client.",
]) {
  assert(
    !forgeEntry.includes(clientReference),
    `${forgeEntryPath} must not reference client-only namespace ${clientReference}`,
  );
}

assert(
  forgeEntry.includes("ForgeClientBootstrap::init"),
  `${forgeEntryPath} must defer client setup to ForgeClientBootstrap`,
);
assert(
  fs.existsSync(path.join(root, forgeClientPath)),
  `${forgeClientPath} must contain the client-only Forge listeners`,
);
assert(
  !propertyRegistry.includes("Icons."),
  `${propertyRegistryPath} must not initialize client-only icon textures on a dedicated server`,
);

const compiledEntry = path.join(
  root,
  "forge/build/classes/java/main/com/lowdragmc/lowdraglib2/forge/LDLib2Forge.class",
);
if (fs.existsSync(compiledEntry)) {
  const bytecode = fs.readFileSync(compiledEntry).toString("latin1");
  for (const clientNamespace of ["net/minecraft/client/", "net/minecraftforge/client/"]) {
    assert(
      !bytecode.includes(clientNamespace),
      `Compiled LDLib2Forge.class must not reference ${clientNamespace}`,
    );
  }
}

const compiledPropertyRegistry = path.join(
  root,
  "common/build/classes/java/main/com/lowdragmc/lowdraglib2/gui/ui/style/PropertyRegistry.class",
);
if (fs.existsSync(compiledPropertyRegistry)) {
  const bytecode = fs.readFileSync(compiledPropertyRegistry).toString("latin1");
  assert(
    !bytecode.includes("com/lowdragmc/lowdraglib2/gui/texture/Icons"),
    "Compiled PropertyRegistry.class must not initialize client-only Icons",
  );
}

console.log("Forge common bootstrap is isolated from client-only classes.");
