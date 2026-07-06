package com.lowdragmc.lowdraglib2.client;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.editor.ui.EditorWindow;
import com.lowdragmc.lowdraglib2.gui.editor.UIEditor;
import com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientEditorCommands {
    private ClientEditorCommands() {
    }

    public static int openUIEditor() {
        var minecraft = Minecraft.getInstance();
        var clientPlayer = minecraft.player;
        if (clientPlayer == null) {
            sendClientMessage("Failed to open LDLib2 UI editor: client player is not ready");
            LDLib2.LOGGER.warn("Failed to open LDLib2 UI editor from client command: client player is not ready");
            return 0;
        }

        var editorUI = new ModularUI(UI.of(EditorWindow.open(UIEditor.WINDOW_ID, UIEditor::new)))
                .shouldCloseOnEsc(false)
                .shouldCloseOnKeyInventory(false);
        var screen = new ModularUIScreen(editorUI, Component.translatable(UIEditor.WINDOW_ID.toLanguageKey()));
        LDLib2.LOGGER.info("Opening LDLib2 UI editor screen directly for {}", clientPlayer.getGameProfile().getName());
        minecraft.setScreen(screen);
        LDLib2.LOGGER.info("Opened LDLib2 UI editor screen directly for {}", clientPlayer.getGameProfile().getName());
        return 1;
    }

    private static void sendClientMessage(String message) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(Component.literal(message), false);
        }
    }
}
