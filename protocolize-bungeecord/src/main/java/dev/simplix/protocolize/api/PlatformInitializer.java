package dev.simplix.protocolize.api;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public final class PlatformInitializer {

    public static void initBungeeCord() {
        Protocolize.platform(Platform.BUNGEECORD);
    }

    public static void initVersion(String version) {
        Protocolize.version(version);
    }

}
