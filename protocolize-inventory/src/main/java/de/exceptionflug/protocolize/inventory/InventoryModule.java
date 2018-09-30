package de.exceptionflug.protocolize.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import de.exceptionflug.protocolize.inventory.adapter.ClickWindowAdapter;
import de.exceptionflug.protocolize.inventory.adapter.CloseWindowAdapter;
import de.exceptionflug.protocolize.inventory.adapter.OpenWindowAdapter;
import de.exceptionflug.protocolize.inventory.adapter.WindowItemsAdapter;
import de.exceptionflug.protocolize.inventory.event.InventoryCloseEvent;
import de.exceptionflug.protocolize.inventory.event.InventoryOpenEvent;
import de.exceptionflug.protocolize.inventory.packet.ClickWindow;
import de.exceptionflug.protocolize.inventory.packet.CloseWindow;
import de.exceptionflug.protocolize.inventory.packet.OpenWindow;
import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.PlayerInventory;
import de.exceptionflug.protocolize.items.packet.WindowItems;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.Protocol;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class InventoryModule {

    private static final Map<UUID, Map<Integer, Inventory>> WINDOW_MAP = Maps.newHashMap();
    private static final Map<UUID, Integer> WINDOW_ID_COUNTER_MAP = Maps.newHashMap();

    private InventoryModule() {}

    public static void initModule() {
        // TO_CLIENT
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME.TO_CLIENT, OpenWindow.class, OpenWindow.MAPPING);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME.TO_CLIENT, CloseWindow.class, CloseWindow.MAPPING_CLIENTBOUND);

        // TO_SERVER
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME.TO_SERVER, CloseWindow.class, CloseWindow.MAPPING_SERVERBOUND);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME.TO_SERVER, ClickWindow.class, ClickWindow.MAPPING);

        // Adapters
        ProtocolAPI.getEventManager().registerListener(new OpenWindowAdapter());
        ProtocolAPI.getEventManager().registerListener(new CloseWindowAdapter(Stream.UPSTREAM));
        ProtocolAPI.getEventManager().registerListener(new CloseWindowAdapter(Stream.DOWNSTREAM));
        ProtocolAPI.getEventManager().registerListener(new ClickWindowAdapter());
        ProtocolAPI.getEventManager().registerListener(new WindowItemsAdapter());
    }

    public static void closeAllInventories(final ProxiedPlayer p) {
        final Map<Integer, Inventory> toDel = Maps.newHashMap();
        getInventories(p.getUniqueId()).forEach((id, it) -> {
            ProxyServer.getInstance().getPluginManager().callEvent(new InventoryCloseEvent(p, it, null, id));
            toDel.put(id, it);
            p.unsafe().sendPacket(new CloseWindow(id));
        });
        toDel.forEach((id, it) -> registerInventory(p.getUniqueId(), id, null));
    }

    public static void registerInventory(final UUID playerId, final int windowId, final Inventory inventory) {
        if(inventory == null) {
            WINDOW_MAP.computeIfAbsent(playerId, (id) -> Maps.newHashMap()).remove(windowId);
            return;
        }
        WINDOW_MAP.computeIfAbsent(playerId, (id) -> Maps.newHashMap()).put(windowId, inventory);
    }

    public static Map<Integer, Inventory> getInventories(final UUID playerId) {
        return WINDOW_MAP.getOrDefault(playerId, Collections.emptyMap());
    }

    public static Inventory getInventory(final UUID playerId, final int windowId) {
        if(windowId == 0) {
            final PlayerInventory inventory = InventoryManager.getInventory(playerId);
            final Inventory out = new Inventory(InventoryType.PLAYER, 46);
            out.setItems(inventory.getItemsIndexed());
            return out;
        }
        return WINDOW_MAP.computeIfAbsent(playerId, (id) -> Maps.newHashMap()).get(windowId);
    }

    public static void uncache(final UUID playerId) {
        WINDOW_MAP.remove(playerId);
    }

    private static int generateWindowId(final UUID playerId) {
        int out = WINDOW_ID_COUNTER_MAP.getOrDefault(playerId, 101);
        if(out >= 200) {
            out = 101;
        }
        WINDOW_ID_COUNTER_MAP.put(playerId, out+1);
        return out;
    }

    public static void sendInventory(final ProxiedPlayer p, Inventory inventory) {
        if(inventory.getType() == InventoryType.PLAYER)
            throw new IllegalStateException("Inventory type PLAYER is only for internal usage");

        boolean alreadyOpen = false;
        int windowId = -1;
        final Map<Integer, Inventory> playerMap = WINDOW_MAP.computeIfAbsent(p.getUniqueId(), (id) -> Maps.newHashMap());
        for(final Integer id : playerMap.keySet()) {
            final Inventory val = playerMap.get(id);
            if(val == inventory) {
                alreadyOpen = true;
                break;
            }
        }

        // Close all inventories if not opened
        if(!alreadyOpen) {
            final Map<Integer, Inventory> toDel = Maps.newHashMap();
            final Inventory finalInventory = inventory;
            getInventories(p.getUniqueId()).forEach((id, it) -> {
                ProxyServer.getInstance().getPluginManager().callEvent(new InventoryCloseEvent(p, it, finalInventory, id));
                toDel.put(id, it);
            });
            toDel.forEach((id, it) -> registerInventory(p.getUniqueId(), id, null));
        }

        if(playerMap.containsValue(inventory)) {
            for(final Integer id : playerMap.keySet()) {
                final Inventory val = playerMap.get(id);
                if(val == inventory) {
                    windowId = id;
                    break;
                }
            }
            if(windowId == -1) {
                windowId = generateWindowId(p.getUniqueId());
                registerInventory(p.getUniqueId(), windowId, inventory);
            }
        } else {
            windowId = generateWindowId(p.getUniqueId());
            registerInventory(p.getUniqueId(), windowId, inventory);
        }

        final InventoryOpenEvent event = new InventoryOpenEvent(p, inventory, windowId);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        if(event.isCancelled())
            return;
        windowId = event.getWindowId();
        inventory = event.getInventory();

        if(inventory.getType() == InventoryType.CRAFTING_TABLE)
            inventory.setSize(0);

        p.unsafe().sendPacket(new OpenWindow(windowId, inventory.getSize(), inventory.getType(), inventory.getTitle()));
        final List<ItemStack> items = Lists.newArrayList(inventory.getItemsIndexed());

        if(inventory.getType() == InventoryType.BREWING_STAND && ReflectionUtil.getProtocolVersion(p) == 47)
            items.remove(4);

//        ProxyServer.getInstance().broadcast(items.toString());
        final PlayerInventory playerInventory = InventoryManager.getInventory(p.getUniqueId());
        items.addAll(playerInventory.getItemsIndexedContainer());
        p.unsafe().sendPacket(new WindowItems((short)windowId, items));
    }

}
