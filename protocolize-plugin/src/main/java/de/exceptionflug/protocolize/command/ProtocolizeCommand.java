package de.exceptionflug.protocolize.command;

import de.exceptionflug.protocolize.ProtocolizePlugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ProtocolizeCommand extends Command {

    private final ProtocolizePlugin plugin;

    public ProtocolizeCommand(final ProtocolizePlugin plugin) {
        super("protocolize", "protocolize.command.protocolize");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args) {
        if(!commandSender.hasPermission(this.getPermission())){
            commandSender.sendMessage(new TextComponent("§cYou don't have permission to use this command"));
            return;
        }
        if(args.length == 0) {
            commandSender.sendMessage("§aProtocolize version "+plugin.getDescription().getVersion()+" by "+plugin.getDescription().getAuthor());
            if(commandSender.hasPermission("protocolize.toggle")) {
                commandSender.sendMessage("Protocolize is currently " + (plugin.isEnabled() ? "§aenabled" : "§cdisabled"));
            }
        } else {
            if(args[0].equalsIgnoreCase("toggle") && commandSender.hasPermission("protocolize.toggle")) {
                plugin.setEnabled(!plugin.isEnabled());
                commandSender.sendMessage("Protocolize is now " + (plugin.isEnabled() ? "§aenabled" : "§cdisabled"));
            }
        }
    }

}
