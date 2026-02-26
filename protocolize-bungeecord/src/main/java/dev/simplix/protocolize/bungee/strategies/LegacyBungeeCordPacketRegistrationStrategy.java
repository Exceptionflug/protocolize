package dev.simplix.protocolize.bungee.strategies;

import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.bungee.strategy.PacketRegistrationStrategy;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
public final class LegacyBungeeCordPacketRegistrationStrategy implements PacketRegistrationStrategy {

    private final Class<?> protocolDataClass = ReflectionUtil.getClassOrNull("net.md_5.bungee.protocol.Protocol$ProtocolData");
    private final Field protocolDataConstructorsField = ReflectionUtil.fieldOrNull(protocolDataClass, "packetConstructors", true);
    private final Field protocolDataPacketMapField = ReflectionUtil.fieldOrNull(protocolDataClass, "packetMap", true);

    @SneakyThrows
    @Override
    public void registerPacket(Int2ObjectMap<Object> protocols, int protocolVersion, int packetId, Class<?> clazz) throws IllegalAccessException {
        final Object protocolData = protocols.get(protocolVersion);
        if (protocolData == null) {
            ProxyServer.getInstance().getLogger().finest("[Protocolize | DEBUG] Protocol version " + protocolVersion + " is not supported on this version. Skipping registration for that specific version.");
            return;
        }
        Object2IntMap<Class<?>> map = ((Object2IntMap<Class<?>>) protocolDataPacketMapField.get(protocolData));
        map.put(clazz, packetId);
        ((Constructor[]) protocolDataConstructorsField.get(protocolData))[packetId] = clazz.getDeclaredConstructor();
    }

    @Override
    public boolean compatible() {
        if (protocolDataClass == null || protocolDataConstructorsField == null || protocolDataPacketMapField == null) {
            return false;
        }
        return protocolDataConstructorsField.getType().equals(Constructor[].class);
    }

}
