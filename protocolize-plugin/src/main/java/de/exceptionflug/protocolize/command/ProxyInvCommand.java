package de.exceptionflug.protocolize.command;

import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.PlayerInventory;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ProxyInvCommand extends Command {

  public ProxyInvCommand() {
    super("proxyinv", "protocolize.command.proxyinv");
  }

  @Override
  public void execute(final CommandSender commandSender, final String[] strings) {
    if (commandSender instanceof ProxiedPlayer) {
      final ProxiedPlayer p = (ProxiedPlayer) commandSender;
      final PlayerInventory inventory = InventoryManager.getCombinedSendInventory(p.getUniqueId(), p.getServer().getInfo().getName());
      inventory.update();
      p.sendMessage("§aInventory sent.");
    }
  }

}
