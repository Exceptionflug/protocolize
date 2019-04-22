package de.exceptionflug.protocolize.example;

import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.inventory.event.InventoryClickEvent;
import de.exceptionflug.protocolize.inventory.packet.CloseWindow;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class InventoryExample implements Listener {

    public InventoryExample(final Plugin plugin) {
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    public void openInventory(final ProxiedPlayer player) {
        // Creates a new chest inventory with 4 rows
        final Inventory inventory = new Inventory(InventoryType.GENERIC_9X4, new TextComponent("§9Inventory title"));

        // A simple item
        final ItemStack item = new ItemStack(ItemType.SIGN);
        item.setDisplayName("§6Click me");
        inventory.setItem(0, item);

        // Another simple item
        final ItemStack item2 = new ItemStack(ItemType.BLUE_WOOL);
        item2.setDisplayName("§aThis is a test");
        inventory.setItem(35, item2);

        InventoryModule.sendInventory(player, inventory);
    }

    private void openBrewingStandInventory(final ProxiedPlayer player) {
        // Creates a new brewing stand gui
        final Inventory brewingStand = new Inventory(InventoryType.BREWING_STAND, new TextComponent("§5A brewing stand!"));

        // A simple item
        final ItemStack item = new ItemStack(ItemType.SIGN);
        item.setDisplayName("§6Click me");
        brewingStand.setItem(0, item);

        InventoryModule.sendInventory(player, brewingStand);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final ItemStack clicked = event.getClickedItem();
        final BaseComponent[] baseComponents = event.getInventory().getTitle();
        final ProxiedPlayer player = event.getPlayer();
        if(clicked == null)
            return;
        if(baseComponents.length == 0)
            return;
        if(baseComponents[0].toString().equals(new TextComponent("§9Inventory title").toString())) {
            if(clicked.getType() == ItemType.SIGN) {
                player.sendMessage("§6Hihi, you clicked me!");
            } else if(clicked.getType() == ItemType.BLUE_WOOL) {
                openBrewingStandInventory(player);
            }
        } else if(baseComponents[0].toString().equals(new TextComponent("§5A brewing stand!").toString())) {
            if(clicked.getType() == ItemType.SIGN) {
                InventoryModule.closeAllInventories(player); // Closes the current gui
                player.sendMessage("§6You clicked me again :)");
            }
        }
    }

}
