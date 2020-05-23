package de.exceptionflug.protocolize.example;

import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import de.exceptionflug.protocolize.items.PlayerInventory;
import de.exceptionflug.protocolize.items.event.PlayerInteractEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class PlayerInventoryExample implements Listener {

    public PlayerInventoryExample(final Plugin plugin) {
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    public void setItems(final ProxiedPlayer player) {
        // Gets the player inventory
        final PlayerInventory inventory = InventoryManager.getInventory(player.getUniqueId());

        // A simple item
        final ItemStack item = new ItemStack(ItemType.OAK_SIGN);
        item.setDisplayName("§6Click me");
        inventory.setItem(44, item); // NOTICE: You have to use the minecraft slot indexes! See: https://wiki.vg/Inventory#Windows

        // Another simple item
        final ItemStack item2 = new ItemStack(ItemType.BLUE_WOOL);
        item2.setDisplayName("§aThis is a test");
        inventory.setItem(36, item2);

        inventory.update(); // Don't forget to update the inventory when you are done with changing it
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {

        /*
        NOTICE when using PlayerInteractEvent.
        The event will be fired several times:

        When interacting while looking at a block with a item in a hand:
          - Call 1: BlockPlacement packet
          - Call 2: UseItem packet

        When interacting with NO item in a hand (1.9+):
          - Call 1: UseItem packet with hand MAIN_HAND
          - Call 2: UseItem packet with hand OFF_HAND
          - Call 3: BlockPlacement packet

        You can check this by checking if(event.getClickedBlockPosition() == null). If this returns true,
        the event was called by the UseItem packet. You can ensure that because UseItem contains no information
        about the block you clicked.

         */

        final ItemStack itemStack = event.getCurrentItem();
        final ProxiedPlayer player = event.getPlayer();
        if(itemStack != null) {
            if(itemStack.getType() == ItemType.OAK_SIGN) {
                player.sendMessage("§6You interacted with a sign!");
            }
        }
    }

}
