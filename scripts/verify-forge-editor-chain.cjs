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
const forgeClientPath = "forge/src/main/java/com/lowdragmc/lowdraglib2/forge/client/ForgeClientBootstrap.java";
const forgeClient = read(forgeClientPath);

assertIncludes(forgeEntryPath, forgeEntry, "ForgeClientBootstrap::init");
assertIncludes(forgeClientPath, forgeClient, "ClientProxy.registerCommonClientHooks();");
assertIncludes(forgeClientPath, forgeClient, "registerMenuScreens();");
assertIncludes(forgeClientPath, forgeClient, "MinecraftForge.EVENT_BUS.addListener(ForgeClientBootstrap::registerClientCommands);");
assertIncludes(forgeClientPath, forgeClient, "eventBus.addListener(ForgeClientBootstrap::registerShaders);");
assertIncludes(forgeClientPath, forgeClient, "eventBus.addListener(ForgeClientBootstrap::registerTooltipComponents);");
assertIncludes(forgeClientPath, forgeClient, "eventBus.addListener(ForgeClientBootstrap::registerClientReloadListeners);");
assertIncludes(forgeClientPath, forgeClient, "eventBus.addListener(ForgeClientBootstrap::registerAdditionalModels);");
assertIncludes(forgeClientPath, forgeClient, "Registering LDLib2 Forge menu screens");
assertIncludes(forgeClientPath, forgeClient, "Registering LDLib2 Forge shaders");
assertIncludes(forgeClientPath, forgeClient, "LDLibShaders.registerShaders(event.getResourceProvider(), event::registerShader)");
assertIncludes(forgeClientPath, forgeClient, "MenuScreens.register(LDMenuTypes.PLAYER_UI.get(), ModularUIContainerScreen::new)");
assertIncludes(forgeClientPath, forgeClient, "MenuScreens.register(LDMenuTypes.HELD_ITEM_UI.get(), ModularUIContainerScreen::new)");
assertIncludes(forgeClientPath, forgeClient, "MenuScreens.register(LDMenuTypes.BLOCK_UI.get(), ModularUIContainerScreen::new)");

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

const texturesResourcePath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/resource/TexturesResource.java";
const texturesResource = read(texturesResourcePath);
assertIncludes(texturesResourcePath, texturesResource, "Sprites.init(resourceInstance);");
assertIncludes(texturesResourcePath, texturesResource, "MCSprites.init(resourceInstance);");
assertIncludes(texturesResourcePath, texturesResource, "OreSprites.init(resourceInstance);");

const modLoaderShimPath = "common/src/main/java/net/neoforged/fml/ModLoader.java";
const modLoaderShim = read(modLoaderShimPath);
assertIncludes(modLoaderShimPath, modLoaderShim, "return event.isCanceled();");

const resourceInstancePath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/resource/ResourceInstance.java";
const resourceInstance = read(resourceInstancePath);
assertIncludes(resourceInstancePath, resourceInstance, "packFileProvider.checkAndUpdateResourceProvider()");
assertIncludes(resourceInstancePath, resourceInstance, "addBuiltinProvider(packFileProvider);");

const packFileResourceProviderPath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/resource/PackFileResourceProvider.java";
const packFileResourceProvider = read(packFileResourceProviderPath);
assertIncludes(packFileResourceProviderPath, packFileResourceProvider, "extends ResourceProvider<T>");
assertIncludes(packFileResourceProviderPath, packFileResourceProvider, 'private static final String RESOURCE_ROOT = "resources";');
assertIncludes(packFileResourceProviderPath, packFileResourceProvider, "ResourceHelper.getResourceManager().listResources(RESOURCE_ROOT");
assertIncludes(packFileResourceProviderPath, packFileResourceProvider, "new FilePath(location)");
assertIncludes(packFileResourceProviderPath, packFileResourceProvider, "return \"mod_resources\";");

const packResourceManagerPath = "common/src/main/java/com/lowdragmc/lowdraglib2/editor/resource/PackResourceManager.java";
const packResourceManager = read(packResourceManagerPath);
assertIncludes(packResourceManagerPath, packResourceManager, "provider.clearCachedResources();");

const uiExamples = walk(path.join(root, "common", "src", "main", "resources", "assets", "ldlib2", "resources"))
  .filter((file) => file.replaceAll("\\", "/").endsWith(".ui.nbt"));
if (uiExamples.length === 0) {
  throw new Error("LDLib2 built-in UI resources must be packaged under assets/ldlib2/resources.");
}

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
