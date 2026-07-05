package com.lowdragmc.lowdraglib2.client;

import com.lowdragmc.lowdraglib2.gui.editor.UIEditor;
import com.lowdragmc.lowdraglib2.gui.factory.PlayerUIMenuType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientEditorCommands {
    private ClientEditorCommands() {
    }

    public static int openUIEditor() {
        var minecraft = Minecraft.getInstance();
        var server = minecraft.getSingleplayerServer();
        var clientPlayer = minecraft.player;
        if (server == null || clientPlayer == null || !server.isSingleplayer()) {
            sendClientMessage("This command can only be used in singleplayer");
            return 0;
        }

        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(clientPlayer.getUUID());
            if (serverPlayer == null) {
                sendClientMessage("Failed to open LDLib2 UI editor: server player is not ready");
                return;
            }
            if (!PlayerUIMenuType.openUI(serverPlayer, UIEditor.WINDOW_ID)) {
                sendClientMessage("Failed to open LDLib2 UI editor: player UI holder is not registered");
            }
        });
        return 1;
    }

    private static void sendClientMessage(String message) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(Component.literal(message), false);
        }
    }
}
