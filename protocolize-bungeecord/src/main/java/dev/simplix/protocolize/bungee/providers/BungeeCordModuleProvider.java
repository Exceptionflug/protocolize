package dev.simplix.protocolize.bungee.providers;

import dev.simplix.protocolize.api.Platform;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.module.ProtocolizeModule;
import dev.simplix.protocolize.api.providers.ModuleProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j(topic = "Protocolize")
public final class BungeeCordModuleProvider implements ModuleProvider {

    private final List<ProtocolizeModule> modules = new ArrayList<>();
    private boolean enabled;

    @Override
    public void registerModule(ProtocolizeModule module) {
        if (!supportedPlatform(module.supportedPlatforms())) {
            log.warn("Won't register module " + module.getClass().getName() + ": Only supports " + Arrays.toString(module.supportedPlatforms()));
            return;
        }
        modules.add(module);
        if (enabled) {
            enableModule(module);
        }
    }

    @Override
    public boolean moduleInstalled(String name) {
        return modules.stream().anyMatch(protocolizeModule -> protocolizeModule.getClass().getSimpleName().equals(name));
    }

    @Override
    public ProtocolizeModule module(String name) {
        return modules.stream().filter(protocolizeModule -> protocolizeModule.getClass().getSimpleName().equals(name))
            .findAny().orElse(null);
    }

    @Override
    public Collection<ProtocolizeModule> modules() {
        return modules;
    }

    private boolean supportedPlatform(Platform[] supportedPlatforms) {
        for (Platform platform : supportedPlatforms) {
            if (Protocolize.platform() == platform) {
                return true;
            }
        }
        return false;
    }

    public void enableAll() {
        if (enabled) {
            return;
        }
        enabled = true;
        for (ProtocolizeModule module : modules) {
            enableModule(module);
        }
    }

    private void enableModule(ProtocolizeModule module) {
        module.registerMappings(Protocolize.mappingProvider());
        module.registerPackets(Protocolize.protocolRegistration());
        log.info("Enabled module " + module.getClass().getName());
    }

}
