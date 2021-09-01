package dev.simplix.protocolize.api.module;

import dev.simplix.protocolize.api.Platform;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;

/**
 * Modules are providing additional features like custom implemented packets or mappings.
 * <br><br>
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public interface ProtocolizeModule {

    void registerMappings(MappingProvider mappingProvider);

    void registerPackets(ProtocolRegistrationProvider registrationProvider);

    default Platform[] supportedPlatforms() {
        return Platform.values();
    }

}
