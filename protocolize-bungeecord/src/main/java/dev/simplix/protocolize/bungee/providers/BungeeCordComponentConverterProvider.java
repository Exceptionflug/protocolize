package dev.simplix.protocolize.bungee.providers;

import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import dev.simplix.protocolize.bungee.util.JoNbtToQuerzNbtMapper;
import dev.simplix.protocolize.bungee.util.QuerzNbtToJoNbtMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.TagUtil;
import net.querz.nbt.tag.Tag;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public final class BungeeCordComponentConverterProvider implements ComponentConverterProvider {

    @Override
    public ComponentConverter<?> platformConverter() {
        return BungeeCordComponentConverter.INSTANCE;
    }

    public static class BungeeCordComponentConverter implements ComponentConverter<BaseComponent[]> {

        static BungeeCordComponentConverter INSTANCE = new BungeeCordComponentConverter();

        private BungeeCordComponentConverter() {
        }

        @Override
        public String toLegacyText(BaseComponent[] components) {
            return new TextComponent(components).toLegacyText();
        }

        @Override
        public String toJson(BaseComponent[] components) {
            return ComponentSerializer.toString(components);
        }

        @Override
        public Tag<?> toNbt(BaseComponent[] component) {
            BaseComponent baseComponent = component.length == 1 ? component[0] : new TextComponent(component);
            return JoNbtToQuerzNbtMapper.convertSpecificTag(TagUtil.fromJson(ComponentSerializer.toJson(baseComponent)));
        }

        @Override
        public BaseComponent[] fromLegacyText(String legacyText) {
            return TextComponent.fromLegacyText("§r" + legacyText);
        }

        @Override
        public BaseComponent[] fromJson(String json) {
            return ComponentSerializer.parse(json);
        }

        @Override
        public BaseComponent[] fromNbt(Tag<?> tag) {
            return new BaseComponent[]{ComponentSerializer.deserialize(TagUtil.toJson(QuerzNbtToJoNbtMapper.convertSpecificTag(tag)))};
        }

        @SneakyThrows
        @Override
        public BaseComponent[] disableItalic(BaseComponent[] component) {
            for (BaseComponent c : component) {
                if (c.isItalicRaw() == null) {
                    c.setItalic(false);
                }
            }
            return component;
        }

    }

}
