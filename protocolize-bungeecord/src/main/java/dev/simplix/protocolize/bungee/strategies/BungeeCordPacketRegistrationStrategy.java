package dev.simplix.protocolize.bungee.strategies;

import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.bungee.strategy.PacketRegistrationStrategy;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;

import java.lang.reflect.Field;
import java.util.function.Supplier;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public final class BungeeCordPacketRegistrationStrategy implements PacketRegistrationStrategy {

    private final Class<?> protocolDataClass = ReflectionUtil.getClassOrNull("net.md_5.bungee.protocol.Protocol$ProtocolData");
    private final Field protocolDataConstructorsField = ReflectionUtil.fieldOrNull(protocolDataClass, "packetConstructors", true);
    private final Field protocolDataPacketMapField = ReflectionUtil.fieldOrNull(protocolDataClass, "packetMap", true);

    @Override
    public void registerPacket(TIntObjectMap<Object> protocols, int protocolVersion, int packetId, Class<?> clazz) throws IllegalAccessException {
        final Object protocolData = protocols.get(protocolVersion);
        if (protocolData == null) {
            log.debug("[Protocolize | DEBUG] Protocol version " + protocolVersion + " is not supported on this version. Skipping registration for that specific version.");
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

    @Override
    public boolean compatible() {
        if (protocolDataClass == null || protocolDataConstructorsField == null || protocolDataPacketMapField == null) {
            return false;
        }
        return protocolDataConstructorsField.getType().equals(Supplier[].class);
    }

}
