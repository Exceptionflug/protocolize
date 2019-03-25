package de.exceptionflug.protocolize.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class SoundEnumGenerator {

    public static void main(final String[] args) throws IOException {
        final File out = new File("Sound.java");
        out.createNewFile();
        try(final PrintWriter printWriter = new PrintWriter(out)) {
            printWriter.println("public enum Sound {");
            printWriter.println();
            printWriter.println();
            for(final Sound sound : Sound.values()) {
                boolean found = false;
                for(final SoundHelper soundHelper : SoundHelper.values()) {
                    if(sound.name().equals(soundHelper.name())) {
                        printWriter.println("\t"+sound.name()+"(new SoundMapping(MINECRAFT_1_8, MINECRAFT_1_8, \""+soundHelper.getOldSound()+"\"), new SoundMapping(MINECRAFT_1_9, MINECRAFT_1_13_2, \""+soundHelper.getNewSound()+"\")),");
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    printWriter.println("\t"+sound.name()+"(new SoundMapping(MINECRAFT_1_9, MINECRAFT_1_13_2, \""+sound.name().toLowerCase().replace("_", ".")+"\")),");
                }
            }
            printWriter.println();
            printWriter.println();
            printWriter.println("\tprivate final SoundMapping[] mappings;");
            printWriter.println();
            printWriter.println("\tSound(final SoundMapping... mappings) {");
            printWriter.println("\t\tthis.mappings = mappings;");
            printWriter.println("\t}");
            printWriter.println();
            printWriter.println("\tpublic String getSoundName(final int protocolVersion) {");
            printWriter.println("\t\tfor(final SoundMapping mapping : mappings) {");
            printWriter.println("\t\t\tif(mapping.getProtocolVersionRangeStart() <= protocolVersion && mapping.getProtocolVersionRangeEnd() >= protocolVersion) {");
            printWriter.println("\t\t\t\treturn mapping.getSoundName();");
            printWriter.println("\t\t\t}");
            printWriter.println("\t\t}");
            printWriter.println("\t\treturn null;");
            printWriter.println("\t}");
            printWriter.println("}");
            printWriter.flush();
        }
    }

}
