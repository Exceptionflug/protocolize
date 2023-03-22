package dev.simplix.protocolize.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.util.DebugUtil;
import dev.simplix.protocolize.velocity.ProtocolizePlugin;
import dev.simplix.protocolize.velocity.util.MessageUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ProtocolizeCommand implements SimpleCommand {

    private final ProtocolizePlugin plugin;

    public ProtocolizeCommand(ProtocolizePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.arguments().length == 0) {
            MessageUtil.sendLegacyMessage(invocation.source(), "§bProtocolize version " + plugin.version()
                + " by " + plugin.description().getAuthors().toString().replace("[", "").replace("]", ""));
        } else if (invocation.arguments()[0].equalsIgnoreCase("debug")) {
            if (invocation.source() instanceof Player) {
                MessageUtil.sendLegacyMessage(invocation.source(), "§cThis command can only be executed by the console.");
                return;
            }
            String s = DebugUtil.createDebugPaste();
            MessageUtil.sendLegacyMessage(invocation.source(), "Debug file saved to ./protocolize-debug.txt");
            try (FileOutputStream fileOutputStream = new FileOutputStream("protocolize-debug.txt")) {
                fileOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
