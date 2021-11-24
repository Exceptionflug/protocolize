package dev.simplix.protocolize.api;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public final class PlatformInitializer {

    public static void initVelocity(String version) {
        Protocolize.platform(Platform.VELOCITY);
        Protocolize.version(version);
    }

}
