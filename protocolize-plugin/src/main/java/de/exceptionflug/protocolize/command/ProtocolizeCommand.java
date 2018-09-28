package de.exceptionflug.protocolize.command;

import de.exceptionflug.protocolize.ProtocolizePlugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ProtocolizeCommand extends Command {

    private final ProtocolizePlugin plugin;

    public ProtocolizeCommand(final ProtocolizePlugin plugin) {
        super("protocolize");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args) {
        commandSender.sendMessage("§aProtocolize version "+plugin.getDescription().getVersion()+" by "+plugin.getDescription().getAuthor());
    }

}
