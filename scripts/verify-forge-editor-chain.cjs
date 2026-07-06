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

assertIncludes(forgeEntryPath, forgeEntry, "import net.minecraft.client.gui.screens.MenuScreens;");
assertIncludes(forgeEntryPath, forgeEntry, "registerMenuScreens();");
assertIncludes(forgeEntryPath, forgeEntry, "private static void registerMenuScreens()");
assertIncludes(forgeEntryPath, forgeEntry, "MenuScreens.register(LDMenuTypes.PLAYER_UI.get(), ModularUIContainerScreen::new);");
assertIncludes(forgeEntryPath, forgeEntry, "MenuScreens.register(LDMenuTypes.HELD_ITEM_UI.get(), ModularUIContainerScreen::new);");
assertIncludes(forgeEntryPath, forgeEntry, "MenuScreens.register(LDMenuTypes.BLOCK_UI.get(), ModularUIContainerScreen::new);");
assertIncludes(forgeEntryPath, forgeEntry, "MinecraftForge.EVENT_BUS.addListener(LDLib2Forge::registerClientCommands);");

const clientCommandsPath = "common/src/main/java/com/lowdragmc/lowdraglib2/client/ClientCommands.java";
const clientCommands = read(clientCommandsPath);
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
