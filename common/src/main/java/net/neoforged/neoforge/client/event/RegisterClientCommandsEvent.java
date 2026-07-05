package net.neoforged.neoforge.client.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class RegisterClientCommandsEvent {
    private final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }
}
