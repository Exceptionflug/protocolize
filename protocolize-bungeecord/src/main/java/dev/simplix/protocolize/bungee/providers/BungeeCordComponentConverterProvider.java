package dev.simplix.protocolize.bungee.providers;

import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

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

        static BungeeCordComponentConverter INSTANCE = new BungeeCordComponentConverter();

        private BungeeCordComponentConverter() {}

        @Override
        public String toLegacyText(BaseComponent[] components) {
            return new TextComponent(components).toLegacyText();
        }

        @Override
        public String toJson(BaseComponent[] components) {
            return ComponentSerializer.toString(components);
        }

        @Override
        public BaseComponent[] fromLegacyText(String legacyText) {
            return TextComponent.fromLegacyText("§r" + legacyText);
        }

        @Override
        public BaseComponent[] fromJson(String json) {
            return ComponentSerializer.parse(json);
        }

    }

}
