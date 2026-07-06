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

function walk(dir, results = []) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const fullPath = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      walk(fullPath, results);
    } else {
      results.push(fullPath);
    }
  }
  return results;
}

const forgeEntryPath = "forge/src/main/java/com/lowdragmc/lowdraglib2/forge/LDLib2Forge.java";
const forgeEntry = read(forgeEntryPath);

assertIncludes(forgeEntryPath, forgeEntry, "ClientProxy.registerCommonClientHooks();");
assertIncludes(forgeEntryPath, forgeEntry, "registerMenuScreens();");
assertIncludes(forgeEntryPath, forgeEntry, "MinecraftForge.EVENT_BUS.addListener(LDLib2Forge::registerClientCommands);");
assertIncludes(forgeEntryPath, forgeEntry, "eventBus.addListener(LDLib2Forge::registerShaders);");
assertIncludes(forgeEntryPath, forgeEntry, "eventBus.addListener(LDLib2Forge::registerTooltipComponents);");
assertIncludes(forgeEntryPath, forgeEntry, "eventBus.addListener(LDLib2Forge::registerClientReloadListeners);");
assertIncludes(forgeEntryPath, forgeEntry, "eventBus.addListener(LDLib2Forge::registerAdditionalModels);");
assertIncludes(forgeEntryPath, forgeEntry, "Registering LDLib2 Forge menu screens");
assertIncludes(forgeEntryPath, forgeEntry, "Registering LDLib2 Forge shaders");
assertIncludes(forgeEntryPath, forgeEntry, "LDLibShaders.registerShaders(event.getResourceProvider(), event::registerShader)");
assertIncludes(forgeEntryPath, forgeEntry, "MenuScreens.register(LDMenuTypes.PLAYER_UI.get(), ModularUIContainerScreen::new)");
assertIncludes(forgeEntryPath, forgeEntry, "MenuScreens.register(LDMenuTypes.HELD_ITEM_UI.get(), ModularUIContainerScreen::new)");
assertIncludes(forgeEntryPath, forgeEntry, "MenuScreens.register(LDMenuTypes.BLOCK_UI.get(), ModularUIContainerScreen::new)");

const clientProxyPath = "common/src/main/java/com/lowdragmc/lowdraglib2/client/ClientProxy.java";
const clientProxy = read(clientProxyPath);
assertIncludes(clientProxyPath, clientProxy, "if (!Platform.isForge())");
assertIncludes(clientProxyPath, clientProxy, "MenuRegistry.registerScreenFactory(LDMenuTypes.PLAYER_UI.get(), ModularUIContainerScreen::new)");

const clientCommandsPath = "common/src/main/java/com/lowdragmc/lowdraglib2/client/ClientCommands.java";
const clientCommands = read(clientCommandsPath);
assertIncludes(clientCommandsPath, clientCommands, 'createLDLib2ClientCommand("ldlib2_client")');
assertIncludes(clientCommandsPath, clientCommands, 'createLiteral("ui_editor")');
assertIncludes(clientCommandsPath, clientCommands, 'createUIEditorCommand("ldlib2_ui_editor")');
assertIncludes(clientCommandsPath, clientCommands, "ClientEditorCommands.openUIEditor()");

const clientEditorPath = "common/src/main/java/com/lowdragmc/lowdraglib2/client/ClientEditorCommands.java";
const clientEditor = read(clientEditorPath);
assertIncludes(clientEditorPath, clientEditor, "Minecraft.getInstance()");
assertIncludes(clientEditorPath, clientEditor, "EditorWindow.open(UIEditor.WINDOW_ID, UIEditor::new)");
assertIncludes(clientEditorPath, clientEditor, "new ModularUIScreen(editorUI");
assertIncludes(clientEditorPath, clientEditor, "minecraft.setScreen(screen)");
assertIncludes(clientEditorPath, clientEditor, "Opening LDLib2 UI editor screen directly");

const menuTypesPath = "common/src/main/java/com/lowdragmc/lowdraglib2/gui/factory/LDMenuTypes.java";
const menuTypes = read(menuTypesPath);
assertIncludes(menuTypesPath, menuTypes, "PlayerUIMenuType.register(UIEditor.WINDOW_ID");
assertIncludes(menuTypesPath, menuTypes, "EditorWindow.open(UIEditor.WINDOW_ID, UIEditor::new)");

const screenPath = "common/src/main/java/com/lowdragmc/lowdraglib2/gui/holder/ModularUIContainerScreen.java";
const screen = read(screenPath);
assertIncludes(screenPath, screen, "Initializing LDLib2 modular UI screen");
assertIncludes(screenPath, screen, "modularUI.setScreenAndInit(this)");

const directScreenPath = "common/src/main/java/com/lowdragmc/lowdraglib2/gui/holder/ModularUIScreen.java";
const directScreen = read(directScreenPath);
assertIncludes(directScreenPath, directScreen, "Initializing LDLib2 modular UI direct screen");
assertIncludes(directScreenPath, directScreen, "modularUI.setScreenAndInit(this)");

const renderTypesPath = "common/src/main/java/com/lowdragmc/lowdraglib2/client/shader/LDLibRenderTypes.java";
const renderTypes = read(renderTypesPath);
assertIncludes(renderTypesPath, renderTypes, "GameRenderer.getPositionTexColorShader()");
assertIncludes(renderTypesPath, renderTypes, "LDLibShaders.getGuiTexture() == null");

const spriteTexturePath = "common/src/main/java/com/lowdragmc/lowdraglib2/gui/texture/SpriteTexture.java";
const spriteTexture = read(spriteTexturePath);
assertIncludes(spriteTexturePath, spriteTexture, "LDLibShaders.getSpriteBlitShader()");
assertIncludes(spriteTexturePath, spriteTexture, "if (shader == null)");
assertIncludes(spriteTexturePath, spriteTexture, "drawWrappedQuads(buffer, matrix");

const commonsFunctionReferences = walk(path.join(root, "common", "src", "main"))
  .filter((file) => /\.(java|kt)$/.test(file))
  .filter((file) => fs.readFileSync(file, "utf8").includes("org.apache.commons.lang3.function"));
if (commonsFunctionReferences.length > 0) {
  throw new Error(`Forge 1.20.1 runtime lacks commons-lang3 function helpers: ${commonsFunctionReferences.join(", ")}`);
}

const reflectionPath = "common/src/main/java/com/lowdragmc/lowdraglib2/utils/ReflectionUtils.java";
const reflection = read(reflectionPath);
assertIncludes(reflectionPath, reflection, "Path.of(resource.toURI()).toString()");
assertIncludes(reflectionPath, reflection, "URI.create(\"file:\" + stripJarIndex(jarPath))");
assertIncludes(reflectionPath, reflection, "encodedFragment");
if (reflection.includes("URLDecoder.decode")) {
  throw new Error(`${reflectionPath} must not use URLDecoder.decode for jar paths; it turns + into spaces.`);
}

const packMetaPath = "common/src/main/resources/pack.mcmeta";
const packMeta = JSON.parse(read(packMetaPath));
if (packMeta.pack?.pack_format !== 15) {
  throw new Error(`${packMetaPath} must declare pack_format 15 for Minecraft 1.20.1.`);
}

console.log("Forge editor opening chain is wired.");
