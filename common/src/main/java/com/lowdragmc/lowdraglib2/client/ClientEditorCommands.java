package com.lowdragmc.lowdraglib2.client;

import com.lowdragmc.lowdraglib2.LDLib2;
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
            LDLib2.LOGGER.warn("Failed to open LDLib2 UI editor from client command: no singleplayer server or client player");
            return 0;
        }

        LDLib2.LOGGER.info("Requesting LDLib2 UI editor open from client command for {}", clientPlayer.getGameProfile().getName());
        server.execute(() -> {
            ServerPlayer serverPlayer = server.getPlayerList().getPlayer(clientPlayer.getUUID());
            if (serverPlayer == null) {
                sendClientMessage("Failed to open LDLib2 UI editor: server player is not ready");
                LDLib2.LOGGER.warn("Failed to open LDLib2 UI editor from client command: server player is not ready");
                return;
            }
            if (!PlayerUIMenuType.openUI(serverPlayer, UIEditor.WINDOW_ID)) {
                sendClientMessage("Failed to open LDLib2 UI editor: player UI holder is not registered");
                LDLib2.LOGGER.warn("Failed to open LDLib2 UI editor from client command for {}: player UI holder is not registered",
                        serverPlayer.getGameProfile().getName());
            } else {
                LDLib2.LOGGER.info("Opened LDLib2 UI editor for {} from client command", serverPlayer.getGameProfile().getName());
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
