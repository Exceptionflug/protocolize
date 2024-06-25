package dev.simplix.protocolize.api;

import com.google.common.collect.*;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.packet.RegisteredPacket;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.Sound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
final class SimpleMappingProvider implements MappingProvider {

    private final Multimap<Object, ProtocolMapping> mappingMultimap = ArrayListMultimap.create();

    @Override
    public synchronized void registerMapping(Object mappable, ProtocolMapping mapping) {
        mappingMultimap.put(mappable, mapping);
    }

    @Override
    public <T extends ProtocolMapping> T mapping(Object mappable, int protocolVersion) {
        return (T) mappingMultimap.get(mappable).parallelStream()
            .filter(protocolMapping -> protocolMapping.inRange(protocolVersion))
            .findFirst().orElse(null);
    }

    @Override
    public <T extends ProtocolMapping> List<T> mappings(Object mappable) {
        List<T> out = new ArrayList<>();
        for (ProtocolMapping mapping : mappingMultimap.get(mappable)) {
            out.add((T) mapping);
        }
        return out;
    }

    @Override
    public <O, T extends ProtocolMapping> Multimap<O, T> mappings(Class<O> type, int protocolVersion) {
        Multimap<O, T> out = ArrayListMultimap.create();
        for (Object obj : mappingMultimap.keySet()) {
            if (type.isAssignableFrom(obj.getClass())) {
                ProtocolMapping mapping = mapping(obj, protocolVersion);
                if (mapping != null) {
                    out.put((O) obj, (T) mapping);
                }
            }
        }
        return out;
    }

    @Override
    public String debugInformation() {
        StringBuilder builder = new StringBuilder("Generated export of " + getClass().getName() + ":\n\n");
        List<Map.Entry<Object, Collection<ProtocolMapping>>> sorted = new ArrayList<>(mappingMultimap.asMap().entrySet());
        sorted.sort((e1, e2) -> {
            Object o1 = e1.getKey();
            Object o2 = e2.getKey();
            if (o1 instanceof RegisteredPacket && o2 instanceof RegisteredPacket) {
                return ((RegisteredPacket) o1).direction().compareTo(((RegisteredPacket) o2).direction());
            }
            if (o1 instanceof ItemType && o2 instanceof ItemType) {
                return ((ItemType) o1).name().compareTo(((ItemType) o2).name());
            }
            if (o1 instanceof Sound && o2 instanceof Sound) {
                return ((Sound) o1).name().compareTo(((Sound) o2).name());
            }
            if (o1 instanceof StructuredComponentType<?> && o2 instanceof StructuredComponentType<?>) {
                return ((StructuredComponentType<?>) o1).getName().compareTo(((StructuredComponentType<?>) o2).getName());
            }
            return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
        });

        for (Map.Entry<Object, Collection<ProtocolMapping>> e : sorted) {
            Object o = e.getKey();
            StringBuilder b = new StringBuilder("<" + o.getClass().getSimpleName() + "> ");
            b.append(o).append(" [");
            for (ProtocolMapping mapping : e.getValue()) {
                b.append(mapping).append(", ");
            }
            builder.append(b.substring(0, b.length() - 2)).append("]").append("\n");
        }

        return builder.toString();
    }

}
