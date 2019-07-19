package de.exceptionflug.protocolize.util;

import com.google.gson.*;
import de.exceptionflug.protocolize.api.util.ProtocolVersions;
import de.exceptionflug.protocolize.items.ItemIDMapping;
import de.exceptionflug.protocolize.items.SpawnEggItemIDMapping;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.MINECRAFT_1_14;
import static de.exceptionflug.protocolize.api.util.ProtocolVersions.MINECRAFT_1_9;

public class ItemTypeJsonProtoGenerator {

    public static void main(final String[] args) throws Exception {
        if (args[0].equalsIgnoreCase("combine")) {
            final File file = new File("registries.json");
            final File target = new File("ItemType.json");
            final Map<String, JsonObject> targetObjects = new ConcurrentHashMap<>();
            if (target.exists()) {
                final JsonArray root = (JsonArray) new JsonParser().parse(new FileReader(target));
                root.forEach(it -> {
                    final JsonObject obj = it.getAsJsonObject();
                    targetObjects.put(obj.get("name").getAsString(), obj);
                });
            }
            target.createNewFile();
            final List<String> toDelLel = new ArrayList<>();
            for (final String type : targetObjects.keySet()) {
                final JsonObject obj = targetObjects.get(type);
                JsonArray mappings = obj.get("mappings").getAsJsonArray();
                final List<ItemIDMapping> mappingList = new ArrayList<>();
                for (final JsonElement e : mappings) {
                    final JsonObject mapping = e.getAsJsonObject();
                    final int rangeStart = mapping.get("protocolRangeStart").getAsInt();
                    final JsonPrimitive protocolRangeEnd = mapping.get("protocolRangeEnd").getAsJsonPrimitive();
                    final int rangeEnd;
                    if(protocolRangeEnd.isString()) {
                        rangeEnd = ProtocolVersions.class.getField(protocolRangeEnd.getAsString()).getInt(null);
                    } else {
                        rangeEnd = protocolRangeEnd.getAsInt();
                    }
                    if (mapping.get("type").getAsString().equals(ItemIDMapping.class.getName())) {
                        if (mapping.has("durability")) {
                            mappingList.add(new ItemIDMapping(rangeStart, rangeEnd, mapping.get("id").getAsInt(), mapping.get("durability").getAsInt()));
                        } else {
                            mappingList.add(new ItemIDMapping(rangeStart, rangeEnd, mapping.get("id").getAsInt()));
                        }
                    }
                }
                boolean found = false;
                do {
                    found = false;
                    final List<ItemIDMapping> toDel = new ArrayList<>();
                    final List<ItemIDMapping> toAdd = new ArrayList<>();
                    out:
                    for (final ItemIDMapping mapping : mappingList) {
                        for (final ItemIDMapping mapping2 : mappingList) {
                            if (mapping.getProtocolVersionRangeStart() == mapping2.getProtocolVersionRangeStart() && mapping.getProtocolVersionRangeEnd() == mapping2.getProtocolVersionRangeEnd())
                                continue;
                            if (mapping.getId() == mapping2.getId() && mapping.getData() == mapping2.getData()) {
                                System.out.println("Optimizing " + type + "...");
                                toDel.add(mapping);
                                toDel.add(mapping2);
                                found = true;
                                toAdd.add(new ItemIDMapping(Math.min(mapping.getProtocolVersionRangeStart(), mapping2.getProtocolVersionRangeStart()), Math.max(mapping.getProtocolVersionRangeEnd(), mapping2.getProtocolVersionRangeEnd()), mapping.getId(), mapping.getData()));
                                break out;
                            }
                        }
                    }
                    for (final ItemIDMapping del : toDel) {
                        mappingList.remove(del);
                    }
                    mappingList.addAll(toAdd);
                } while (found);

                if (mappingList.size() == 1) {
                    if (mappingList.get(0).getProtocolVersionRangeEnd() != MINECRAFT_1_14) {
                        toDelLel.add(type);
                        continue;
                    }
                }

                mappings = new JsonArray();
                final JsonArray finalMappings = mappings;
                mappingList.forEach(it -> {
                    final JsonObject mapping = new JsonObject();
                    mapping.addProperty("type", ItemIDMapping.class.getName());
                    mapping.addProperty("protocolRangeStart", it.getProtocolVersionRangeStart());
                    mapping.addProperty("protocolRangeEnd", it.getProtocolVersionRangeEnd());
                    mapping.addProperty("id", it.getId());
                    if (it.getData() != 0) {
                        mapping.addProperty("durability", it.getData());
                    }
                    finalMappings.add(mapping);
                });
                obj.add("mappings", mappings);
                targetObjects.put(type, obj);
            }

            toDelLel.forEach(it -> {
                System.out.println("Remove legacy type " + it);
                targetObjects.remove(it);
            });

            final JsonArray array = new JsonArray();
            for (final JsonObject object : targetObjects.values()) {
                array.add(object);
            }

            try (final FileWriter writer = new FileWriter(target)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(array, writer);
            }
            return;
        } else if (args[0].equalsIgnoreCase("remove")) {
            final int tstart = Integer.parseInt(args[1]);
            final int tend = Integer.parseInt(args[2]);

            final File file = new File("registries.json");
            final File target = new File("ItemType.json");
            final Map<String, JsonObject> targetObjects = new ConcurrentHashMap<>();
            if (target.exists()) {
                final JsonArray root = (JsonArray) new JsonParser().parse(new FileReader(target));
                root.forEach(it -> {
                    final JsonObject obj = it.getAsJsonObject();
                    targetObjects.put(obj.get("name").getAsString(), obj);
                });
            }
            target.createNewFile();
            final List<String> toDelLel = new ArrayList<>();
            for (final String type : targetObjects.keySet()) {
                final JsonObject obj = targetObjects.get(type);
                JsonArray mappings = obj.get("mappings").getAsJsonArray();
                final List<ItemIDMapping> mappingList = new ArrayList<>();
                for (final JsonElement e : mappings) {
                    final JsonObject mapping = e.getAsJsonObject();
                    final int rangeStart = mapping.get("protocolRangeStart").getAsInt();
                    final JsonPrimitive protocolRangeEnd = mapping.get("protocolRangeEnd").getAsJsonPrimitive();
                    final int rangeEnd;
                    if(protocolRangeEnd.isString()) {
                        rangeEnd = ProtocolVersions.class.getField(protocolRangeEnd.getAsString()).getInt(null);
                    } else {
                        rangeEnd = protocolRangeEnd.getAsInt();
                    }
                    if (mapping.get("type").getAsString().equals(ItemIDMapping.class.getName())) {
                        if (mapping.has("durability")) {
                            mappingList.add(new ItemIDMapping(rangeStart, rangeEnd, mapping.get("id").getAsInt(), mapping.get("durability").getAsInt()));
                        } else {
                            mappingList.add(new ItemIDMapping(rangeStart, rangeEnd, mapping.get("id").getAsInt()));
                        }
                    }
                }
                boolean found = false;
                do {
                    found = false;
                    final List<ItemIDMapping> toDel = new ArrayList<>();
                    for (final ItemIDMapping mapping : mappingList) {
                        if (mapping.getProtocolVersionRangeStart() == tstart && mapping.getProtocolVersionRangeEnd() == tend) {
                            System.out.println("Deleting " + type + "...");
                            toDel.add(mapping);
                            found = true;
                        }
                    }
                    for (final ItemIDMapping del : toDel) {
                        mappingList.remove(del);
                    }
                } while (found);

                if (mappingList.size() == 1) {
                    if (mappingList.get(0).getProtocolVersionRangeEnd() != MINECRAFT_1_14) {
                        toDelLel.add(type);
                        continue;
                    }
                }

                mappings = new JsonArray();
                final JsonArray finalMappings = mappings;
                mappingList.forEach(it -> {
                    final JsonObject mapping = new JsonObject();
                    mapping.addProperty("type", ItemIDMapping.class.getName());
                    mapping.addProperty("protocolRangeStart", it.getProtocolVersionRangeStart());
                    mapping.addProperty("protocolRangeEnd", it.getProtocolVersionRangeEnd());
                    mapping.addProperty("id", it.getId());
                    if (it.getData() != 0) {
                        mapping.addProperty("durability", it.getData());
                    }
                    finalMappings.add(mapping);
                });
                obj.add("mappings", mappings);
                targetObjects.put(type, obj);
            }
            toDelLel.forEach(it -> {
                System.out.println("Remove type " + it);
                targetObjects.remove(it);
            });

            final JsonArray array = new JsonArray();
            for (final JsonObject object : targetObjects.values()) {
                array.add(object);
            }

            try (final FileWriter writer = new FileWriter(target)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(array, writer);
            }
            return;
        }
        final int rangeStart = Integer.parseInt(args[0]);
        final int rangeEnd = Integer.parseInt(args[1]);

        final File file = new File("registries.json");
        final File target = new File("ItemType.json");
        final Map<String, JsonObject> targetObjects = new LinkedHashMap<>();
        if (target.exists()) {
            final JsonArray root = (JsonArray) new JsonParser().parse(new FileReader(target));
            root.forEach(it -> {
                final JsonObject obj = it.getAsJsonObject();
                targetObjects.put(obj.get("name").getAsString(), obj);
            });
        }
        target.createNewFile();

        final List<String> covered = new ArrayList<>();
        if (file.exists()) {
            final JsonObject root = (JsonObject) new JsonParser().parse(new FileReader(file));
            final JsonObject itemEntries = root.getAsJsonObject("minecraft:item").getAsJsonObject("entries");
            for (final Map.Entry<String, JsonElement> entry : itemEntries.entrySet()) {
                addItem(rangeStart, rangeEnd, targetObjects, entry, covered);
            }
        } else {
            final File items = new File("items.json");
            if (items.exists()) {
                final JsonObject root = (JsonObject) new JsonParser().parse(new FileReader(items));
                final Set<Map.Entry<String, JsonElement>> entries = root.entrySet();
                for (final Map.Entry<String, JsonElement> entry : entries) {
                    addItem(rangeStart, rangeEnd, targetObjects, entry, covered);
                }
            } else {
                final Class<?> materialClass = loadMaterialEnum();
                if (!materialClass.isEnum()) {
                    System.out.println("ERROR: Material.class is not enum!!");
                    return;
                }
                for (final Enum e : (Enum[]) materialClass.getMethod("values").invoke(null)) {
                    final int id = (int) materialClass.getMethod("getId").invoke(e);
                    System.out.println(e.name());
                    final JsonObject mapping = new JsonObject();
                    mapping.addProperty("type", ItemIDMapping.class.getName());
                    mapping.addProperty("protocolRangeStart", rangeStart);
                    mapping.addProperty("protocolRangeEnd", rangeEnd);
                    mapping.addProperty("id", id);

                    final JsonObject itemObj;
                    if (targetObjects.containsKey(e.name())) {
                        itemObj = targetObjects.get(e.name());
                    } else {
                        itemObj = new JsonObject();
                        itemObj.addProperty("name", e.name());
                        itemObj.addProperty("maxStackSize", 64);
                    }
                    final JsonArray array;
                    if (itemObj.has("mappings")) {
                        array = itemObj.get("mappings").getAsJsonArray();
                    } else {
                        array = new JsonArray();
                    }
                    array.add(mapping);
                    itemObj.add("mappings", array);
                    targetObjects.put(e.name(), itemObj);
                }
            }
        }
        covered.removeAll(targetObjects.keySet());
        covered.forEach(it -> System.out.println("NOT COVERED: " + it));

        final JsonArray array = new JsonArray();
        for (final JsonObject object : targetObjects.values()) {
            array.add(object);
        }

        try (final FileWriter writer = new FileWriter(target)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(array, writer);
        }
    }

    private static Class<?> loadMaterialEnum() throws ClassNotFoundException, MalformedURLException {
        URL url = new File("").getAbsoluteFile().toURI().toURL();
        URLClassLoader cl = new URLClassLoader(new URL[]{url});
        try {
            final Method findClass = ClassLoader.class.getDeclaredMethod("findClass", String.class);
            findClass.setAccessible(true);
            return (Class<?>) findClass.invoke(cl, "org.bukkit.Material");
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addItem(int rangeStart, int rangeEnd, Map<String, JsonObject> targetObjects, Map.Entry<String, JsonElement> entry, List<String> covered) {
        final String constantName = entry.getKey().substring(10).toUpperCase();
        covered.add(constantName);
        final int newProtocolId = ((JsonObject) entry.getValue()).get("protocol_id").getAsInt();

        final JsonObject mapping = new JsonObject();
        mapping.addProperty("type", ItemIDMapping.class.getName());
        mapping.addProperty("protocolRangeStart", rangeStart);
        mapping.addProperty("protocolRangeEnd", rangeEnd);
        mapping.addProperty("id", newProtocolId);

        final JsonObject itemObj;
        if (targetObjects.containsKey(constantName)) {
            itemObj = targetObjects.get(constantName);
        } else {
            itemObj = new JsonObject();
            itemObj.addProperty("name", constantName);
            itemObj.addProperty("maxStackSize", 64);
        }
        final JsonArray array;
        if (itemObj.has("mappings")) {
            array = itemObj.get("mappings").getAsJsonArray();
        } else {
            array = new JsonArray();
        }
        array.add(mapping);
        itemObj.add("mappings", array);
        targetObjects.put(constantName, itemObj);
    }

}
