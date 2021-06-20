package de.exceptionflug.protocolize.items;

import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.api.protocol.Stream;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ItemsModule {

  private static boolean spigotInventoryTracking = false;
  private static boolean disableWarnOnUnknownItemMapping = false;

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
