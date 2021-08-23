package dev.simplix.protocolize.api;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
final class SimpleMappingProvider implements MappingProvider {

    private final Multimap<Object, ProtocolMapping> mappingMultimap = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    @Override
    public void registerMapping(Object mappable, ProtocolMapping mapping) {
        mappingMultimap.put(mappable, mapping);
    }

    @Override
    public <T extends ProtocolMapping> T mapping(Object mappable, int protocolVersion) {
        for (ProtocolMapping mapping : mappingMultimap.get(mappable)) {
            if (mapping.inRange(protocolVersion)) {
                return (T) mapping;
            }
        }
        return null;
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

}
