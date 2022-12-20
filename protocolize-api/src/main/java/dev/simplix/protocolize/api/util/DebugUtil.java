package dev.simplix.protocolize.api.util;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.module.ProtocolizeModule;
import dev.simplix.protocolize.api.providers.ModuleProvider;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Date: 03.10.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public final class DebugUtil {

    private static final Hastebin HASTEBIN = new Hastebin();
    private static final File FOLDER = new File("protocolize-logs");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
    private static final DateFormat DATE_FORMAT_FILE = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");

    public static final boolean enabled = System.getProperties().contains("dev.simplix.protocolize.debug");

    public static void writeDump(ByteBuf buf, Throwable throwable) {
        File file = new File(FOLDER, DATE_FORMAT_FILE.format(new Date()) + ".zip");
        file.getParentFile().mkdirs();
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            ZipEntry throwableEntry = new ZipEntry("throwable.txt");
            outputStream.putNextEntry(throwableEntry);
            PrintWriter printWriter = new PrintWriter(outputStream);
            throwable.printStackTrace(printWriter);
            outputStream.closeEntry();

            ZipEntry data = new ZipEntry("data.bin");
            outputStream.putNextEntry(data);
            buf.resetReaderIndex();
            byte[] allData = new byte[buf.readableBytes()];
            buf.readBytes(allData);
            outputStream.write(allData);
            outputStream.closeEntry();

            outputStream.flush();
        } catch (IOException e) {
            log.error("Unable to write dump file " + file.getName(), e);
        }
    }

    public static String createDebugPaste() {
        StringBuilder builder = new StringBuilder("==== Protocolize debug information ====\n");
        builder.append("Generated = ").append(DATE_FORMAT.format(new Date())).append("\n");
        builder.append("Version = ").append(Protocolize.version()).append("\n");
        builder.append("Platform = ").append(Protocolize.platform().name()).append("\n");
        builder.append("Modules:\n");
        for (ProtocolizeModule module : Protocolize.getService(ModuleProvider.class).modules()) {
            builder.append(" - ").append(module.getClass().getName()).append(" ")
                .append(Arrays.toString(module.supportedPlatforms())).append("\n");
        }
        builder.append("Mappings:\n");
        builder.append(Protocolize.mappingProvider().debugInformation()).append("\n\n");
        builder.append("Listeners: \n");
        builder.append(Protocolize.listenerProvider().debugInformation()).append("\n\n");
        builder.append("Packets: \n");
        builder.append(Protocolize.protocolRegistration().debugInformation()).append("\n\n");
        return builder.toString();
    }
}
