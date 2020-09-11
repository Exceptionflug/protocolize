package de.exceptionflug.protocolize.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.exceptionflug.protocolize.api.util.ProtocolVersions;
import de.exceptionflug.protocolize.items.ItemIDMapping;
import de.exceptionflug.protocolize.items.ItemType;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;

public class ItemEnumGenerator {

  public static void main(final String[] args) throws Exception {
    final Map<ItemType, String> enumConstantString = new HashMap<>();
    final List<String> lines = Files.readAllLines(new File("protocolize-items/src/main/java/de/exceptionflug/protocolize/items/ItemType.java").toPath(), StandardCharsets.UTF_8);
    final List<String> newLines = new ArrayList<>();
    for (final String line : lines) {
      for (final ItemType type : ItemType.values()) {
        if (line.trim().startsWith(type.name())) {
          enumConstantString.put(type, line);
        }
      }
    }

    final File file = new File("registries.json");
    final JsonObject root = (JsonObject) new JsonParser().parse(new FileReader(file));
    final File out = new File("Item.java");
    out.createNewFile();
    final JsonObject itemEntries = root.getAsJsonObject("minecraft:item").getAsJsonObject("entries");
    final Set<Entry<String, JsonElement>> entries = itemEntries.entrySet();
    for (final Entry<String, JsonElement> element : entries) {
      final String constantName = element.getKey().substring(10).toUpperCase();
      final int newProtocolId = ((JsonObject) element.getValue()).get("protocol_id").getAsInt();
      try {
        final ItemType type = ItemType.valueOf(constantName);
        final ItemIDMapping mapping = type.getApplicableMapping(ProtocolVersions.MINECRAFT_1_13_2);
        if (mapping.getId() != newProtocolId) {
          // Add new mapping
          String line = enumConstantString.get(type);
          line = line.substring(0, line.length() - 2);
          line = line.concat(", new ItemIDMapping(MINECRAFT_1_14, MINECRAFT_1_14, ".concat(Integer.toString(newProtocolId)).concat(")),"));
          enumConstantString.put(type, line);
          System.out.println("Added new mapping for MINECRAFT_1_14 for " + type.name() + " with protocolId " + newProtocolId);
        } else {
          // Update protocolVersionRangeEnd of existing mapping
          final String line = enumConstantString.get(type).replace("new ItemIDMapping(MINECRAFT_1_13, MINECRAFT_1_13_2,", "new ItemIDMapping(MINECRAFT_1_13, MINECRAFT_1_14,");
          enumConstantString.put(type, line);
          System.out.println("Extended MINECRAFT_1_13 mapping range to MINECRAFT_1_14 for " + type.name());
        }
      } catch (final IllegalArgumentException e) {
        // Unknown, insert new constant
        final StringBuilder sb = new StringBuilder();
        sb.append(constantName);
        sb.append("(");
        sb.append("new ItemIDMapping(MINECRAFT_1_14, MINECRAFT_1_14, ");
        sb.append(newProtocolId);
        sb.append(")");
        sb.append("),");
        newLines.add(sb.toString());
        System.out.println("Created new enum constant " + constantName + " with protocolId " + newProtocolId);
      }
    }
    try (final PrintWriter writer = new PrintWriter(new FileWriter(out))) {
      for (final ItemType type : enumConstantString.keySet()) {
        final String entry = enumConstantString.get(type);
        writer.println(entry);
      }
      newLines.forEach(writer::println);
      writer.flush();
    }
  }

}
