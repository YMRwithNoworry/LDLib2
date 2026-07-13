const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const forgeEntryPath = "forge/src/main/java/com/lowdragmc/lowdraglib2/forge/LDLib2Forge.java";
const forgeClientPath = "forge/src/main/java/com/lowdragmc/lowdraglib2/forge/client/ForgeClientBootstrap.java";
const forgeEntry = fs.readFileSync(path.join(root, forgeEntryPath), "utf8");

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

console.log("Forge mod entrypoint is isolated from client-only classes.");
