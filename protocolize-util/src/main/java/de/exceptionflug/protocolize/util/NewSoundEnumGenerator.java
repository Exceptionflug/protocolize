package de.exceptionflug.protocolize.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.exceptionflug.protocolize.world.Sound;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map.Entry;
import java.util.Set;

public class NewSoundEnumGenerator {

  public static void main(final String[] args) throws Exception {
    final File file = new File("registries.json");
    final JsonObject root = (JsonObject) new JsonParser().parse(new FileReader(file));
    final File out = new File("Sound.java");
    out.createNewFile();
    final StringBuilder sb = new StringBuilder("public enum Sound {");
    sb.append("\n\n");
    int i = 0;
    final JsonObject soundEntries = root.getAsJsonObject("minecraft:sound_event").getAsJsonObject("entries");
    final Set<Entry<String, JsonElement>> entries = soundEntries.entrySet();
    for (final Entry<String, JsonElement> element : entries) {
      final String enumConstant = element.getKey().substring(10).toUpperCase().replace(".", "_");
      try {
        Sound.valueOf(enumConstant);
        continue;
      } catch (final IllegalArgumentException e) {
      }
      sb.append("\t");
      sb.append(enumConstant);
      sb.append("(");
      sb.append("new SoundIDMapping(MINECRAFT_1_17, MINECRAFT_LATEST, \"");
      sb.append(element.getKey().substring(10));
      sb.append("\")");
      sb.append(")");
      if (i != entries.size() - 1)
        sb.append(",");
      else
        sb.append(";");
      sb.append("\n");
      i++;
    }
    sb.append("\n");
    sb.append("\t");
    sb.append("private final IDMapping[] mappings;\n");
    sb.append("\n\n\t");
    sb.append("Item(final IDMapping... mappings) {\n");
    sb.append("\t\tthis.mappings = mappings;\n");
    sb.append("\t}");
    sb.append("\n\n");
    sb.append("\tpublic int getProtocolID(final int protocolVersion) {\n");
    sb.append("\t\tfor(final IDMapping mapping : mappings) {\n");
    sb.append("\t\t\tif(mapping.getProtocolVersionRangeStart() <= protocolVersion && mapping.getProtocolVersionRangeEnd() >= protocolVersion)\n");
    sb.append("\t\t\t\treturn mapping.getId();\n\t\t}\n");
    sb.append("\t\treturn 0;\n\t}");
    sb.append("\n");
    sb.append("\tpublic int getProtocolData(final int protocolVersion) {\n");
    sb.append("\t\tfor(final IDMapping mapping : mappings) {\n");
    sb.append("\t\t\tif(mapping.getProtocolVersionRangeStart() <= protocolVersion && mapping.getProtocolVersionRangeEnd() >= protocolVersion)\n");
    sb.append("\t\t\t\treturn mapping.getData();\n\t\t}\n");
    sb.append("\t\treturn 0;\n");
    sb.append("\t}");
    sb.append("\n}");
    try (final FileWriter writer = new FileWriter(out)) {
      writer.write(sb.toString());
      writer.flush();
    }
  }

}
