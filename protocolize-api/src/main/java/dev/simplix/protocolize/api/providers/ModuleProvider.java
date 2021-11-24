package dev.simplix.protocolize.api.providers;

import dev.simplix.protocolize.api.module.ProtocolizeModule;

import java.util.Collection;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public interface ModuleProvider {

    void registerModule(ProtocolizeModule module);

    Collection<ProtocolizeModule> modules();
}
