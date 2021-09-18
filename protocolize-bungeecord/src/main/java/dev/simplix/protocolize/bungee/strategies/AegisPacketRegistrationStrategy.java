package dev.simplix.protocolize.bungee.strategies;

import com.google.common.base.Supplier;
import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.bungee.strategy.PacketRegistrationStrategy;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import net.md_5.bungee.api.ProxyServer;

import java.lang.reflect.Field;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
@Deprecated
public class AegisPacketRegistrationStrategy implements PacketRegistrationStrategy {

    private final Class<?> protocolDataClass = ReflectionUtil.getClassOrNull("net.md_5.bungee.protocol.Protocol$ProtocolData");
    private final Field protocolDataConstructorsField = ReflectionUtil.fieldOrNull(protocolDataClass, "packetConstructors", true);
    private final Field protocolDataPacketMapField = ReflectionUtil.fieldOrNull(protocolDataClass, "packetMap", true);

    @Override
    public void registerPacket(TIntObjectMap<Object> protocols, int protocolVersion, int packetId, Class<?> clazz) throws IllegalAccessException {
        final Object protocolData = protocols.get(protocolVersion);
        if (protocolData == null) {
            ProxyServer.getInstance().getLogger().finest("[Protocolize | DEBUG] Protocol version " + protocolVersion + " is not supported on this aegis version. Skipping registration for that specific version.");
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
        boolean equals = protocolDataConstructorsField.getType().equals(Supplier[].class);
        if (equals) {
            ProxyServer.getInstance().getLogger().warning("[Protocolize] You are running Aegis. Please note that the Aegis support is no longer updated and may break in the future.");
        }
        return equals;
    }

}
