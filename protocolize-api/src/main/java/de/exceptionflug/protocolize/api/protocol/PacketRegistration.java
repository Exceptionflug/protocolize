package de.exceptionflug.protocolize.api.protocol;

import com.google.common.base.Preconditions;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * This class provides access to the access protected BungeeCord protocol implementation.
 */
public final class PacketRegistration {

  private Constructor protocolMappingConstructor;
  private Method getIdMethod;
  private Class<?> mappingClass, directionDataClass, protocolDataClass;
  private Field toServerField, toClientField, protocolsField, protocolDataPacketMapField, protocolDataConstructorsField;

  {
    try {
      mappingClass = Class.forName("net.md_5.bungee.protocol.Protocol$ProtocolMapping");
      directionDataClass = Class.forName("net.md_5.bungee.protocol.Protocol$DirectionData");
      protocolDataClass = Class.forName("net.md_5.bungee.protocol.Protocol$ProtocolData");
      protocolMappingConstructor = mappingClass.getConstructor(int.class, int.class);
      protocolMappingConstructor.setAccessible(true);
      protocolsField = directionDataClass.getDeclaredField("protocols");
      protocolsField.setAccessible(true);
      getIdMethod = directionDataClass.getDeclaredMethod("getId", Class.class, int.class);
      getIdMethod.setAccessible(true);
      protocolDataPacketMapField = protocolDataClass.getDeclaredField("packetMap");
      protocolDataPacketMapField.setAccessible(true);
      protocolDataConstructorsField = protocolDataClass.getDeclaredField("packetConstructors");
      protocolDataConstructorsField.setAccessible(true);

      toServerField = Protocol.class.getDeclaredField("TO_SERVER");
      toServerField.setAccessible(true);
      toClientField = Protocol.class.getDeclaredField("TO_CLIENT");
      toClientField.setAccessible(true);
    } catch (final Exception e) {
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while initializing PacketRegistration: ", e);
    }
  }

  public boolean isSupportedPlatform() {
    if (isWaterfall() || isBungeeCord() || isAegis()) {
      return true;
    }
    return false;
  }

  /**
   * This method registers a {@link AbstractPacket} for the PLAY / GAME protocol in direction to the client. This method is equal to
   * {@code registerPacket(Protocol.GAME.TO_CLIENT, clazz, protocolIdMapping);}
   *
   * @param clazz             the class of the packet
   * @param protocolIdMapping a map containing the protocol versions and their corresponding packet id.
   * @see PacketRegistration#registerPacket(Protocol, Direction, Class, Map)
   */
  public void registerPlayClientPacket(final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
    registerPacket(Protocol.GAME, Direction.TO_CLIENT, clazz, protocolIdMapping);
  }

  /**
   * This method registers a {@link AbstractPacket} for the PLAY / GAME protocol in direction to the client. This method is equal to
   * {@code registerPacket(Protocol.GAME.TO_SERVER, clazz, protocolIdMapping);}
   *
   * @param clazz             the class of the packet
   * @param protocolIdMapping a map containing the protocol versions and their corresponding packet id.
   * @see PacketRegistration#registerPacket(Protocol, Direction, Class, Map)
   */
  public void registerPlayServerPacket(final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
    registerPacket(Protocol.GAME, Direction.TO_SERVER, clazz, protocolIdMapping);
  }

  /**
   * This method registers a {@link AbstractPacket}.
   *
   * @param protocol          the protocol.
   * @param direction         the protocol direction
   * @param clazz             the class of the packet
   * @param protocolIdMapping a map containing the protocol versions and their corresponding packet id.
   */
  public void registerPacket(final Protocol protocol, final Direction direction, final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
    Preconditions.checkNotNull(clazz, "The clazz cannot be null!");
    Preconditions.checkNotNull(protocol, "The protocol cannot be null!");
    Preconditions.checkNotNull(direction, "The direction cannot be null!");
    Preconditions.checkNotNull(protocolIdMapping, "The protocolIdMapping cannot be null!");
    try {
      final TIntObjectMap<Object> protocols = (TIntObjectMap<Object>) protocolsField.get(getDirectionData(protocol, direction));
      for (final Integer protocolVersion : protocolIdMapping.keySet()) {
        if (isWaterfall()) {
          registerPacketWaterfall(protocols, protocolVersion, protocolIdMapping.get(protocolVersion), clazz);
        } else if (isAegis()) {
          registerPacketAegis(protocols, protocolVersion, protocolIdMapping.get(protocolVersion), clazz);
        } else {
          registerPacketBungeeCord(protocols, protocolVersion, protocolIdMapping.get(protocolVersion), clazz);
        }
      }
      ProxyServer.getInstance().getLogger().info("[Protocolize] Injected custom packet: " + clazz.getName());
    } catch (final Exception e) {
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while registering packet: " + clazz.getName(), e);
    }
  }

  /**
   * Returns a packet id for the given protocol and it's version.
   *
   * @param protocol        the protocol.
   * @param direction       the protocol direction
   * @param protocolVersion the protocol version
   * @param clazz           the class of the packet
   * @return the packet id or -1 if the packet is not registered in that specific direction
   * @see de.exceptionflug.protocolize.api.util.ProtocolVersions
   */
  public int getPacketID(final Protocol protocol, final Direction direction, final int protocolVersion, final Class<? extends DefinedPacket> clazz) {
    Preconditions.checkNotNull(clazz, "The clazz cannot be null!");
    Preconditions.checkNotNull(protocol, "The protocol cannot be null!");
    Preconditions.checkNotNull(direction, "The direction cannot be null!");
    final Object data = getDirectionData(protocol, direction);
    try {
      return (int) getIdMethod.invoke(data, clazz, protocolVersion);
    } catch (final IllegalAccessException | InvocationTargetException e) {
      if (e.getCause() != null && e.getCause() instanceof IllegalArgumentException) {
        try {
          return (int) getIdMethod.invoke(data, clazz, protocolVersion);
        } catch (final IllegalAccessException | InvocationTargetException e1) {
        }
      }
    }
    return -1;
  }

  private Object getDirectionData(final Protocol protocol, final Direction direction) {
    try {
      if (direction == Direction.TO_SERVER)
        return toServerField.get(protocol);
      else
        return toClientField.get(protocol);
    } catch (final IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean isWaterfall() {
    return protocolDataConstructorsField.getType().equals(Supplier[].class);
  }

  public boolean isBungeeCord() {
    return protocolDataConstructorsField.getType().equals(Constructor[].class);
  }

  public boolean isAegis() {
    return protocolDataConstructorsField.getType().equals(com.google.common.base.Supplier[].class);
  }

  private void registerPacketBungeeCord(final TIntObjectMap<Object> protocols, final int protocolVersion, final int packetId, final Class<?> clazz) throws IllegalAccessException, NoSuchMethodException {
    final Object protocolData = protocols.get(protocolVersion);
    if (protocolData == null) {
      ProxyServer.getInstance().getLogger().warning("[Protocolize] Protocol version " + protocolVersion + " is not supported on this bungeecord version. Skipping registration for that specific version.");
      return;
    }
    TObjectIntMap<Class<?>> map = ((TObjectIntMap<Class<?>>) protocolDataPacketMapField.get(protocolData));
    map.put(clazz, packetId);
    ((Constructor[]) protocolDataConstructorsField.get(protocolData))[packetId] = clazz.getDeclaredConstructor();
  }

  private void registerPacketWaterfall(final TIntObjectMap<Object> protocols, final int protocolVersion, final int packetId, final Class<?> clazz) throws IllegalAccessException, NoSuchMethodException {
    final Object protocolData = protocols.get(protocolVersion);
    if (protocolData == null) {
      ProxyServer.getInstance().getLogger().warning("[Protocolize] Protocol version " + protocolVersion + " is not supported on this waterfall version. Skipping registration for that specific version.");
      return;
    }
    ((TObjectIntMap<Class<?>>) protocolDataPacketMapField.get(protocolData)).put(clazz, packetId);
    ((Supplier[]) protocolDataConstructorsField.get(protocolData))[packetId] = () -> {
      try {
        return clazz.getDeclaredConstructor().newInstance();
      } catch (ReflectiveOperationException e) {
        e.printStackTrace();
      }
      return null;
    };
  }

  private void registerPacketAegis(final TIntObjectMap<Object> protocols, final int protocolVersion, final int packetId, final Class<?> clazz) throws IllegalAccessException, NoSuchMethodException {
    final Object protocolData = protocols.get(protocolVersion);
    if (protocolData == null) {
      ProxyServer.getInstance().getLogger().warning("[Protocolize] Protocol version " + protocolVersion + " is not supported on this aegis version. Skipping registration for that specific version.");
      return;
    }
    ((TObjectIntMap<Class<?>>) protocolDataPacketMapField.get(protocolData)).put(clazz, packetId);
    ((com.google.common.base.Supplier[]) protocolDataConstructorsField.get(protocolData))[packetId] = () -> {
      try {
        return clazz.getDeclaredConstructor().newInstance();
      } catch (ReflectiveOperationException e) {
        e.printStackTrace();
      }
      return null;
    };
  }

}
