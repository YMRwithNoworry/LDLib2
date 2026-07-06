package com.lowdragmc.lowdraglib2.client;

import com.lowdragmc.lowdraglib2.client.shader.LDLibShaders;
import com.lowdragmc.lowdraglib2.client.shader.management.ShaderManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote ClientCommands
 */
@OnlyIn(Dist.CLIENT)
public class ClientCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> createLiteral(String command) {
        return Commands.literal(command);
    }

    public static List<LiteralArgumentBuilder<CommandSourceStack>> createClientCommands() {
        var commands = new ArrayList<LiteralArgumentBuilder<CommandSourceStack>>();
        commands.add(createLDLib2ClientCommand("ldlib2_client"));
        commands.add(createUIEditorCommand("ldlib2_ui_editor"));
        return commands;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createLDLib2ClientCommand(String command) {
        return createLiteral(command)
                .executes(context -> ClientEditorCommands.openUIEditor())
                .then(createLiteral("ui_editor")
                        .executes(context -> ClientEditorCommands.openUIEditor()))
                .then(createLiteral("reload_shader")
                        .executes(context -> {
                            LDLibShaders.reload();
                            ShaderManager.getInstance().reload();
                            return 1;
                        }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createUIEditorCommand(String command) {
        return createLiteral(command)
                .executes(context -> ClientEditorCommands.openUIEditor());
    }
}
