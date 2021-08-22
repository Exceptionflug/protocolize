package dev.simplix.protocolize.velocity.util;

import dev.simplix.protocolize.api.packet.AbstractPacket;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
public final class ProtocolizeNamingPolicy implements NamingPolicy {

    /**
     * This allows to test collisions of {@code key.hashCode()}.
     */
    private final static boolean STRESS_HASH_CODE = Boolean.getBoolean("net.sf.cglib.test.stressHashCodes");

    private final Class<? extends AbstractPacket> protocolizePacket;

    public ProtocolizeNamingPolicy(Class<? extends AbstractPacket> protocolizePacket) {
        this.protocolizePacket = protocolizePacket;
    }

    public String getClassName(String prefix, String source, Object key, Predicate names) {
        if (prefix == null) {
            prefix = "net.sf.cglib.empty.Object";
        } else if (prefix.startsWith("java")) {
            prefix = "$" + prefix;
        }
        String base =
                prefix + "$$" +
                        protocolizePacket.getSimpleName() + "$$" +
                        Integer.toHexString(STRESS_HASH_CODE ? 0 : key.hashCode());
        String attempt = base;
        int index = 2;
        while (names.evaluate(attempt))
            attempt = base + "_" + index++;
        return attempt;
    }

}
