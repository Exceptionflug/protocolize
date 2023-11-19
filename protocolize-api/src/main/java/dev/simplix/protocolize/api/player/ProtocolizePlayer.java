package dev.simplix.protocolize.api.player;

import com.google.common.collect.Lists;
import dev.simplix.protocolize.api.*;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.Sound;
import dev.simplix.protocolize.data.packets.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
public interface ProtocolizePlayer {

    UUID uniqueId();

    PlayerInventory proxyInventory();

    default void sendPacket(Object packet) {
        sendPacket(packet, Protocol.PLAY);
    }

    void sendPacket(Object packet, Protocol protocol);

    default void sendPacketToServer(Object packet) {
        sendPacketToServer(packet, Protocol.PLAY);
    }

    void sendPacketToServer(Object packet, Protocol protocol);

    Map<Integer, Inventory> registeredInventories();

    int generateWindowId();

    int protocolVersion();

    Protocol decodeProtocol();

    Protocol encodeProtocol();

    <T> T handle();

    Location location();

    void onInteract(Consumer<PlayerInteract> interactConsumer);

    void handleInteract(PlayerInteract interact);

    default void playSound(Sound sound, SoundCategory category, float volume, float pitch) {
        playSound(location(), sound, category, volume, pitch);
    }

    default void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
        if (protocolVersion() >= ProtocolVersions.MINECRAFT_1_19_3) {
            sendPacket(new SoundEffect(sound, category, location.x(), location.y(), location.z(), volume, pitch));
        } else {
            sendPacket(new NamedSoundEffect(sound, category, location.x(), location.y(), location.z(), volume, pitch));
        }
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

        if (!alreadyOpen) {
            sendPacket(new OpenWindow(windowId, inventory.type(), inventory.titleJson()));
        }
        int protocolVersion;
        try {
            protocolVersion = protocolVersion();
        } catch (Throwable t) {
            protocolVersion = 47;
        }
        List<BaseItemStack> items = new ArrayList<>(Lists
            .newArrayList(inventory.itemsIndexed(protocolVersion)));
        sendPacket(new WindowItems((short) windowId, items, 0));
    }

}
