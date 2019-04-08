package de.exceptionflug.protocolize.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import de.exceptionflug.protocolize.inventory.adapter.*;
import de.exceptionflug.protocolize.inventory.event.InventoryCloseEvent;
import de.exceptionflug.protocolize.inventory.event.InventoryOpenEvent;
import de.exceptionflug.protocolize.inventory.packet.*;
import de.exceptionflug.protocolize.items.*;
import de.exceptionflug.protocolize.items.packet.WindowItems;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.MINECRAFT_1_8;

public final class InventoryModule {

    private static final Map<UUID, Map<Integer, Inventory>> WINDOW_MAP = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<Integer, Map<Integer, InventoryAction>>> ACTION_MAP = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> WINDOW_ID_COUNTER_MAP = new ConcurrentHashMap<>();

    private static boolean spigotInventoryTracking = false;

    private InventoryModule() {
    }

    public static void initModule() {
        // TO_CLIENT
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, OpenWindow.class, OpenWindow.MAPPING);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, CloseWindow.class, CloseWindow.MAPPING_CLIENTBOUND);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, ConfirmTransaction.class, ConfirmTransaction.MAPPING_CLIENTBOUND);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, WindowProperty.class, WindowProperty.MAPPING);

        // TO_SERVER
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, CloseWindow.class, CloseWindow.MAPPING_SERVERBOUND);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, ConfirmTransaction.class, ConfirmTransaction.MAPPING_SERVERBOUND);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, ClickWindow.class, ClickWindow.MAPPING);

        // Adapters
        ProtocolAPI.getEventManager().registerListener(new OpenWindowAdapter());
        ProtocolAPI.getEventManager().registerListener(new CloseWindowAdapter(Stream.UPSTREAM));
        ProtocolAPI.getEventManager().registerListener(new CloseWindowAdapter(Stream.DOWNSTREAM));
        ProtocolAPI.getEventManager().registerListener(new ClickWindowAdapter());
        ProtocolAPI.getEventManager().registerListener(new WindowItemsAdapter());
        ProtocolAPI.getEventManager().registerListener(new ConfirmTransactionAdapter());
    }

    public static void closeAllInventories(final ProxiedPlayer p) {
        Preconditions.checkNotNull(p, "The player cannot be null!");
        final Map<Integer, Inventory> toDel = Maps.newHashMap();
        getInventories(p.getUniqueId()).forEach((id, it) -> {
            ProxyServer.getInstance().getPluginManager().callEvent(new InventoryCloseEvent(p, it, null, id));
            toDel.put(id, it);
            p.unsafe().sendPacket(new CloseWindow(id));
        });
        toDel.forEach((id, it) -> registerInventory(p.getUniqueId(), id, null));
    }

    public static void registerInventory(final UUID playerId, final int windowId, final Inventory inventory) {
        Preconditions.checkNotNull(playerId, "The playerId cannot be null!");
        if (inventory == null) {
            WINDOW_MAP.computeIfAbsent(playerId, (id) -> Maps.newHashMap()).remove(windowId);
            ACTION_MAP.computeIfAbsent(playerId, (id) -> Maps.newHashMap()).remove(windowId);
            return;
        }
        WINDOW_MAP.computeIfAbsent(playerId, (id) -> Maps.newHashMap()).put(windowId, inventory);
    }

    public static Map<Integer, Inventory> getInventories(final UUID playerId) {
        return WINDOW_MAP.getOrDefault(playerId, Collections.emptyMap());
    }

    public static Inventory getInventory(final UUID playerId, final int windowId) {
        if (windowId == 0) {
            final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerId);
            if(player.getServer() == null || player.getServer().getInfo() == null)
                return null;
            final PlayerInventory inventory = InventoryManager.getCombinedSendInventory(playerId, player.getServer().getInfo().getName());
            final Inventory out = new Inventory(InventoryType.PLAYER, 46);
            out.setItems(inventory.getItemsIndexed());
            out.setHomebrew(false);
            return out;
        }
        return WINDOW_MAP.computeIfAbsent(playerId, (id) -> new ConcurrentHashMap<>()).get(windowId);
    }

    public static void uncache(final UUID playerId) {
        WINDOW_MAP.remove(playerId);
    }

    private static int generateWindowId(final UUID playerId) {
        int out = WINDOW_ID_COUNTER_MAP.getOrDefault(playerId, 101);
        if (out >= 200) {
            out = 101;
        }
        WINDOW_ID_COUNTER_MAP.put(playerId, out + 1);
        return out;
    }

    public static void registerInventoryAction(final UUID playerId, final int windowId, final int actionNumber, final InventoryAction action) {
        Preconditions.checkNotNull(action, "The action cannot be null!");
        Preconditions.checkNotNull(playerId, "The playerId cannot be null!");
        ACTION_MAP.computeIfAbsent(playerId, (id) -> Maps.newHashMap()).computeIfAbsent(windowId, wid -> Maps.newHashMap()).put(actionNumber, action);
    }

    public static InventoryAction getInventoryAction(final UUID playerId, final int windowId, final int actionNumber) {
        Preconditions.checkNotNull(playerId, "The playerId cannot be null!");
        return ACTION_MAP.computeIfAbsent(playerId, (id) -> Maps.newHashMap()).computeIfAbsent(windowId, wid -> Maps.newHashMap()).get(actionNumber);
    }

    public static void unregisterInventoryAction(final UUID playerId, final int windowId, final int actionNumber) {
        Preconditions.checkNotNull(playerId, "The playerId cannot be null!");
        ACTION_MAP.computeIfAbsent(playerId, (id) -> Maps.newHashMap()).computeIfAbsent(windowId, wid -> Maps.newHashMap()).remove(actionNumber);
    }

    public static void sendInventory(final ProxiedPlayer p, Inventory inventory) {
        Preconditions.checkNotNull(p, "The player cannot be null!");
        Preconditions.checkNotNull(inventory, "The inventory cannot be null!");
        if (inventory.getType() == InventoryType.PLAYER)
            throw new IllegalStateException("Inventory type PLAYER is only for internal usage");

        boolean alreadyOpen = false;
        int windowId = -1;
        final Map<Integer, Inventory> playerMap = WINDOW_MAP.computeIfAbsent(p.getUniqueId(), (id) -> new ConcurrentHashMap<>());
        for (final Integer id : playerMap.keySet()) {
            final Inventory val = playerMap.get(id);
            if (val == inventory) {
                alreadyOpen = true;
                break;
            }
        }

        // Close all inventories if not opened
        if (!alreadyOpen) {
            final Map<Integer, Inventory> toDel = Maps.newHashMap();
            final Inventory finalInventory = inventory;
            getInventories(p.getUniqueId()).forEach((id, it) -> {
                ProxyServer.getInstance().getPluginManager().callEvent(new InventoryCloseEvent(p, it, finalInventory, id));
                toDel.put(id, it);
            });
            toDel.forEach((id, it) -> registerInventory(p.getUniqueId(), id, null));
        }

        if (playerMap.containsValue(inventory)) {
            for (final Integer id : playerMap.keySet()) {
                final Inventory val = playerMap.get(id);
                if (val == inventory) {
                    windowId = id;
                    break;
                }
            }
            if (windowId == -1) {
                windowId = generateWindowId(p.getUniqueId());
                registerInventory(p.getUniqueId(), windowId, inventory);
            }
        } else {
            windowId = generateWindowId(p.getUniqueId());
            registerInventory(p.getUniqueId(), windowId, inventory);
        }

        final InventoryOpenEvent event = new InventoryOpenEvent(p, inventory, windowId);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        windowId = event.getWindowId();
        inventory = event.getInventory();

        if (inventory.getType() == InventoryType.CRAFTING_TABLE)
            inventory.setSize(0);

        if(!alreadyOpen)
            p.unsafe().sendPacket(new OpenWindow(windowId, inventory.getSize(), inventory.getType(), inventory.getTitle()));
        final List<ItemStack> items = Lists.newArrayList(inventory.getItemsIndexed());

        if (inventory.getType() == InventoryType.BREWING_STAND && ReflectionUtil.getProtocolVersion(p) == MINECRAFT_1_8)
            items.remove(4);

        if(ItemsModule.isSpigotInventoryTracking()) {
            final PlayerInventory playerInventory = InventoryManager.getInventory(p.getUniqueId());
            items.addAll(playerInventory.getItemsIndexedContainer());
        }
        p.unsafe().sendPacket(new WindowItems((short) windowId, items));
    }

    public static void setSpigotInventoryTracking(final boolean spigotInventoryTracking) {
        InventoryModule.spigotInventoryTracking = spigotInventoryTracking;
    }

    public static boolean isSpigotInventoryTracking() {
        return spigotInventoryTracking;
    }
}
