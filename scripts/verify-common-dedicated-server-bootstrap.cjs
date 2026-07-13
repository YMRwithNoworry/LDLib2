const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");

function read(relativePath) {
  return fs.readFileSync(path.join(root, relativePath), "utf8");
}

function assert(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

function assertIncludes(file, source, expected) {
  assert(source.includes(expected), `${file} is missing: ${expected}`);
}

function assertExcludes(file, source, forbidden) {
  assert(!source.includes(forbidden), `${file} must not contain: ${forbidden}`);
}

const commonProxyPath = "common/src/main/java/com/lowdragmc/lowdraglib2/CommonProxy.java";
const commonProxy = read(commonProxyPath);
assertExcludes(commonProxyPath, commonProxy, "PropertyRegistry");
assertIncludes(commonProxyPath, commonProxy, "TypeHandles.init();");

const menuTypesPath = "common/src/main/java/com/lowdragmc/lowdraglib2/gui/factory/LDMenuTypes.java";
const menuTypes = read(menuTypesPath);
for (const clientEditorType of ["EditorWindow", "UIEditor"]) {
  assertExcludes(menuTypesPath, menuTypes, clientEditorType);
}

const clientProxyPath = "common/src/main/java/com/lowdragmc/lowdraglib2/client/ClientProxy.java";
const clientProxy = read(clientProxyPath);
assertIncludes(clientProxyPath, clientProxy, "PropertyRegistry.init();");
assertIncludes(clientProxyPath, clientProxy, "TypeHandleClientBootstrap.init();");
assertIncludes(clientProxyPath, clientProxy, "PlayerUIMenuType.register(UIEditor.WINDOW_ID");

const typeHandlesPath = "common/src/main/java/com/lowdragmc/lowdraglib2/nodegraphtookit/api/type/TypeHandles.java";
const typeHandles = read(typeHandlesPath);
for (const clientType of ["Icons", "ColorConfigurator", "IConfigurable"]) {
  assertExcludes(typeHandlesPath, typeHandles, clientType);
}

const compiledChecks = [
  [
    "common/build/classes/java/main/com/lowdragmc/lowdraglib2/CommonProxy.class",
    ["PropertyRegistry", "net/minecraft/client/", "com/mojang/blaze3d/"],
  ],
  [
    "common/build/classes/java/main/com/lowdragmc/lowdraglib2/gui/factory/LDMenuTypes.class",
    ["UIEditor", "EditorWindow", "net/minecraft/client/", "com/mojang/blaze3d/"],
  ],
  [
    "common/build/classes/java/main/com/lowdragmc/lowdraglib2/nodegraphtookit/api/type/TypeHandles.class",
    ["gui/texture/Icons", "ColorConfigurator", "net/minecraft/client/", "com/mojang/blaze3d/"],
  ],
];

for (const [relativePath, forbiddenReferences] of compiledChecks) {
  const compiledPath = path.join(root, relativePath);
  if (!fs.existsSync(compiledPath)) continue;
  const bytecode = fs.readFileSync(compiledPath).toString("latin1");
  for (const forbidden of forbiddenReferences) {
    assertExcludes(relativePath, bytecode, forbidden);
  }
}

console.log("Common bootstrap does not initialize client-only UI classes on dedicated servers.");
