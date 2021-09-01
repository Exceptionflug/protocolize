package dev.simplix.protocolize.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import dev.simplix.protocolize.velocity.ProtocolizePlugin;
import dev.simplix.protocolize.velocity.util.MessageUtil;

public final class ProtocolizeCommand implements SimpleCommand {

    private final ProtocolizePlugin plugin;

    public ProtocolizeCommand(ProtocolizePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        MessageUtil.sendLegacyMessage(invocation.source(), "§bProtocolize version " + plugin.version()
            + " by " + plugin.description().getAuthors().toString().replace("[", "").replace("]", ""));
    }

}
