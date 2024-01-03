package dev.simplix.protocolize.velocity.providers;

import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.querz.nbt.tag.Tag;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
public final class VelocityComponentConverterProvider implements ComponentConverterProvider {

    @Override
    public ComponentConverter<?> platformConverter() {
        return AdventureComponentConverter.INSTANCE;
    }

    public static class AdventureComponentConverter implements ComponentConverter<Component> {

        static AdventureComponentConverter INSTANCE = new AdventureComponentConverter();

        private final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacySection();
        private final GsonComponentSerializer gsonComponentSerializer = System.getProperty(
            "protocolize.velocity.adventure.serializer.downsample", "true"
        ).equals("false") ? GsonComponentSerializer.colorDownsamplingGson() : GsonComponentSerializer.gson();

        private AdventureComponentConverter() {
        }

        @Override
        public String toLegacyText(Component component) {
            return legacyComponentSerializer.serialize(component);
        }

        @Override
        public String toJson(Component component) {
            return gsonComponentSerializer.serialize(component);
        }

        @Override
        public Tag<?> toNbt(Component component) {
            throw new UnsupportedOperationException("Adventure does not support nbt serialization yet");
        }

        @Override
        public Component fromLegacyText(String legacyText) {
            return legacyComponentSerializer.deserialize("§r" + legacyText);
        }

        @Override
        public Component fromJson(String json) {
            return gsonComponentSerializer.deserialize(json);
        }

        @Override
        public Component fromNbt(Tag<?> tag) {
            throw new UnsupportedOperationException("Adventure does not support nbt deserialization yet");
        }

        @Override
        public void disableItalic(Component component) {
            if (component.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
                component.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            }
        }

    }

}
