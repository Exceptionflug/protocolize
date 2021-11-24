package dev.simplix.protocolize.bungee.commands;

import dev.simplix.protocolize.api.util.DebugUtil;
import dev.simplix.protocolize.bungee.ProtocolizePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ProtocolizeCommand extends Command {

    private final ProtocolizePlugin plugin;

    public ProtocolizeCommand(ProtocolizePlugin plugin) {
        super("protocolize");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(new ComponentBuilder("Protocolize version " + plugin.getDescription().getVersion()
                + " by " + plugin.getDescription().getAuthor()).color(ChatColor.AQUA).create());
            ;
        } else if (args[0].equalsIgnoreCase("debug")) {
            if (commandSender instanceof ProxiedPlayer) {
                commandSender.sendMessage("§cThis command can only be executed by the console.");
                return;
            }
            String s = DebugUtil.createDebugPaste();
            commandSender.sendMessage("Debug file saved to ./protocolize-debug.txt");
            try (FileOutputStream fileOutputStream = new FileOutputStream("protocolize-debug.txt")) {
                fileOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
