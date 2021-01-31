package de.exceptionflug.protocolize.items;

import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.items.adapter.*;
import de.exceptionflug.protocolize.items.packet.*;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ItemsModule {

  private static boolean spigotInventoryTracking = false;
  private static boolean disableWarnOnUnknownItemMapping = false;

  private ItemsModule() {
  }

  public static void initModule() {
    // TO_CLIENT
    ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, SetSlot.class, SetSlot.MAPPING);
    ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, WindowItems.class, WindowItems.MAPPING);
    ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, HeldItemChange.class, HeldItemChange.MAPPING_CLIENTBOUND);

    // TO_SERVER
    ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, UseItem.class, UseItem.MAPPING);
    ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, BlockPlacement.class, BlockPlacement.MAPPING);
    ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, HeldItemChange.class, HeldItemChange.MAPPING_SERVERBOUND);

    // ADAPTERS
    ProtocolAPI.getEventManager().registerListener(new WindowItemsAdapter());
    ProtocolAPI.getEventManager().registerListener(new SetSlotItemsAdapter());
    ProtocolAPI.getEventManager().registerListener(new UseItemAdapter());
    ProtocolAPI.getEventManager().registerListener(new BlockPlacementAdapter());
    ProtocolAPI.getEventManager().registerListener(new HeldItemChangeAdapter(Stream.UPSTREAM));
    ProtocolAPI.getEventManager().registerListener(new HeldItemChangeAdapter(Stream.DOWNSTREAM));
  }

  public static boolean isSpigotInventoryTracking() {
    return spigotInventoryTracking;
  }

  public static boolean isDisableWarnOnUnknownItemMapping() {
    return disableWarnOnUnknownItemMapping;
  }

  public static void setDisableWarnOnUnknownItemMapping(boolean disableWarnOnUnknownItemMapping) {
    ItemsModule.disableWarnOnUnknownItemMapping = disableWarnOnUnknownItemMapping;
  }

  public static void setSpigotInventoryTracking(final boolean spigotInventoryTracking) {
    ItemsModule.spigotInventoryTracking = spigotInventoryTracking;
  }

}
