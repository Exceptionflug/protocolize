package dev.simplix.protocolize.api.player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import dev.simplix.protocolize.api.Location;
import dev.simplix.protocolize.api.PlayerInteract;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.Sound;
import dev.simplix.protocolize.data.packets.CloseWindow;
import dev.simplix.protocolize.data.packets.NamedSoundEffect;
import dev.simplix.protocolize.data.packets.OpenWindow;
import dev.simplix.protocolize.data.packets.WindowItems;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
public interface ProtocolizePlayer {

    UUID uniqueId();

    PlayerInventory proxyInventory();

    void sendPacket(Object packet);

    void sendPacketToServer(Object packet);

    Map<Integer, Inventory> registeredInventories();

    int generateWindowId();

    int protocolVersion();

    <T> T handle();

    Location location();

    void onInteract(Consumer<PlayerInteract> interactConsumer);

    void handleInteract(PlayerInteract interact);

    default void playSound(Sound sound, SoundCategory category, float volume, float pitch) {
        playSound(location(), sound, category, volume, pitch);
    }

    default void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
        sendPacket(new NamedSoundEffect(sound, category, location.x(), location.y(), location.z(), volume, pitch));
    }

    default void registerInventory(int windowId, Inventory inventory) {
        if (inventory == null) {
            registeredInventories().remove(windowId);
        } else {
            registeredInventories().put(windowId, inventory);
        }
    }

    default void closeInventory() {
        registeredInventories().forEach((id, it) -> {
            sendPacket(new CloseWindow(id));
        });
        registeredInventories().clear();
    }

    default void openInventory(Inventory inventory) {
        boolean alreadyOpen = false;
        int windowId = -1;
        for (Integer id : registeredInventories().keySet()) {
            Inventory val = registeredInventories().get(id);
            if (val == inventory) {
                alreadyOpen = true;
                break;
            }
        }

        // Close all inventories if not opened
        if (!alreadyOpen) {
            closeInventory();
        }

        if (registeredInventories().containsValue(inventory)) {
            for (Integer id : registeredInventories().keySet()) {
                Inventory val = registeredInventories().get(id);
                if (val == inventory) {
                    windowId = id;
                    break;
                }
            }
            if (windowId == -1) {
                windowId = generateWindowId();
                registerInventory(windowId, inventory);
            }
        } else {
            windowId = generateWindowId();
            registerInventory(windowId, inventory);
        }

        if (!alreadyOpen)
            sendPacket(new OpenWindow(windowId, inventory.type(), inventory.titleJson()));
        int protocolVersion;
        try {
            protocolVersion = protocolVersion();
        } catch (Throwable t) {
            protocolVersion = 47;
        }
        List<ItemStack> items = Lists.newArrayList(inventory.itemsIndexed(protocolVersion));
        sendPacket(new WindowItems((short) windowId, items, 0));
    }

}
