package de.exceptionflug.protocolize.util;

import com.google.gson.*;
import de.exceptionflug.protocolize.api.util.ProtocolVersions;
import de.exceptionflug.protocolize.items.ItemIDMapping;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ItemTypeJsonProtoGenerator {

    public static void main(final String[] args) throws Exception {
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
        return cl.loadClass("org.bukkit.Material");
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
