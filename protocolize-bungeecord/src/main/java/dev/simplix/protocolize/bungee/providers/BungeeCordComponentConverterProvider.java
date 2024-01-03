package dev.simplix.protocolize.bungee.providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import dev.simplix.protocolize.bungee.util.JoNbtToQuerzNbtMapper;
import dev.simplix.protocolize.bungee.util.QuerzNbtToJoNbtMapper;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.TagUtil;
import net.querz.nbt.tag.Tag;
import se.llbit.nbt.SpecificTag;

import java.lang.reflect.Field;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
public final class BungeeCordComponentConverterProvider implements ComponentConverterProvider {

    @Override
    public ComponentConverter<?> platformConverter() {
        return BungeeCordComponentConverter.INSTANCE;
    }

    public static class BungeeCordComponentConverter implements ComponentConverter<BaseComponent[]> {

        private static Field italicField;

        static {
            try {
                italicField = BaseComponent.class.getDeclaredField("italic");
                italicField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

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
        public void disableItalic(BaseComponent[] component) {
            for (BaseComponent c : component) {
                if (italicField.get(c) == null) {
                    c.setItalic(false);
                }
            }
        }

    }

}
