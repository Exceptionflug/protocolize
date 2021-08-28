package dev.simplix.protocolize.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.kyori.adventure.text.Component;

/**
 * Date: 27.08.2021
 *
 * @author Exceptionflug
 */
public class TestCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        Inventory inventory = new Inventory(InventoryType.GENERIC_9X1);
        inventory.title("§cFirst ever Inventory on Velocity");
        inventory.item(3, new ItemStack(ItemType.NETHER_STAR).displayName("§cTest"));
        inventory.item(4, new ItemStack(ItemType.TNT).displayName("§cTest"));
        inventory.item(5, new ItemStack(ItemType.NETHER_STAR).displayName("§cTest"));
        inventory.onClose(inventoryClose -> {
            Player handle = inventoryClose.player().handle();
            handle.sendMessage(Component.text("Du hast das Inventar geschlossen. Wie kannst du nur!?"));
        });
        inventory.onClick(inventoryClick -> {
            inventoryClick.cancelled(true);
        });

        Player source = (Player) invocation.source();
        Protocolize.playerProvider().player(source.getUniqueId()).openInventory(inventory);
    }

}
