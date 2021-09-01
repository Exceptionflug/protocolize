package dev.simplix.protocolize.velocity.util;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
public final class MessageUtil {

    private static final LegacyComponentSerializer COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().build();

    private MessageUtil() {
    }

    public static void sendLegacyMessage(CommandSource commandSource, String message) {
        commandSource.sendMessage(COMPONENT_SERIALIZER.deserialize(message));
    }

}
