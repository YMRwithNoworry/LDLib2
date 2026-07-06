const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");

function read(relativePath) {
  return fs.readFileSync(path.join(root, relativePath), "utf8");
}

function assertIncludes(file, source, expected) {
  if (!source.includes(expected)) {
    throw new Error(`${file} is missing: ${expected}`);
  }
}

const forgeEntryPath = "forge/src/main/java/com/lowdragmc/lowdraglib2/forge/LDLib2Forge.java";
const forgeEntry = read(forgeEntryPath);

assertIncludes(forgeEntryPath, forgeEntry, "event.enqueueWork(ClientProxy::registerCommonClientHooks);");
assertIncludes(forgeEntryPath, forgeEntry, "MinecraftForge.EVENT_BUS.addListener(LDLib2Forge::registerClientCommands);");
if (forgeEntry.includes("MenuScreens.register(") || forgeEntry.includes("registerMenuScreens()")) {
  throw new Error(`${forgeEntryPath} must not directly register menu screens; ClientProxy.registerCommonClientHooks owns that path.`);
}

const clientCommandsPath = "common/src/main/java/com/lowdragmc/lowdraglib2/client/ClientCommands.java";
const clientCommands = read(clientCommandsPath);
assertIncludes(clientCommandsPath, clientCommands, 'createLiteral("ldlib2_client")');
assertIncludes(clientCommandsPath, clientCommands, 'createLiteral("ui_editor")');
assertIncludes(clientCommandsPath, clientCommands, 'createLiteral("ldlib2_ui_editor")');
assertIncludes(clientCommandsPath, clientCommands, "ClientEditorCommands.openUIEditor()");

const clientEditorPath = "common/src/main/java/com/lowdragmc/lowdraglib2/client/ClientEditorCommands.java";
const clientEditor = read(clientEditorPath);
assertIncludes(clientEditorPath, clientEditor, "Minecraft.getInstance()");
assertIncludes(clientEditorPath, clientEditor, "getSingleplayerServer()");
assertIncludes(clientEditorPath, clientEditor, "PlayerUIMenuType.openUI(serverPlayer, UIEditor.WINDOW_ID)");

const menuTypesPath = "common/src/main/java/com/lowdragmc/lowdraglib2/gui/factory/LDMenuTypes.java";
const menuTypes = read(menuTypesPath);
assertIncludes(menuTypesPath, menuTypes, "PlayerUIMenuType.register(UIEditor.WINDOW_ID");
assertIncludes(menuTypesPath, menuTypes, "EditorWindow.open(UIEditor.WINDOW_ID, UIEditor::new)");

const screenPath = "common/src/main/java/com/lowdragmc/lowdraglib2/gui/holder/ModularUIContainerScreen.java";
const screen = read(screenPath);
assertIncludes(screenPath, screen, "Initializing LDLib2 modular UI screen");
assertIncludes(screenPath, screen, "modularUI.setScreenAndInit(this)");

console.log("Forge editor opening chain is wired.");
