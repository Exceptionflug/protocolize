package dev.simplix.protocolize.velocity.providers;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import dev.simplix.protocolize.velocity.util.AdventureNbtToQuerzNbtMapper;
import net.kyori.adventure.nbt.BinaryTag;
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
            BinaryTag adventureNbtTag = new ComponentHolder(ProtocolVersion.MINECRAFT_1_20_3, component).getBinaryTag();
            return AdventureNbtToQuerzNbtMapper.adventureToQuerz(adventureNbtTag);
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
            BinaryTag adventureNbtTag = AdventureNbtToQuerzNbtMapper.querzToAdventure(tag);
            return new ComponentHolder(ProtocolVersion.MINECRAFT_1_20_3, adventureNbtTag).getComponent();
        }

        @Override
        public Component disableItalic(Component component) {
            if (component.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
                return component.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            }
            return component;
        }

    }

}
