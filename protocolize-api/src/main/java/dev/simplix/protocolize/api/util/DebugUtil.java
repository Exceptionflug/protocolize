package dev.simplix.protocolize.api.util;

import dev.simplix.protocolize.api.Protocolize;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Date: 03.10.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public final class DebugUtil {

    private static final File FOLDER = new File("protocolize-logs");

    public static final boolean enabled = System.getProperties().contains("dev.simplix.protocolize.debug");

    public static void writeDump(ByteBuf buf, Throwable throwable) {
        File file = new File(FOLDER, new Date().toString().replace(" ", "-") + ".zip");
        file.getParentFile().mkdirs();
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            ZipEntry throwableEntry = new ZipEntry("throwable.txt");
            outputStream.putNextEntry(throwableEntry);
            try (PrintWriter printWriter = new PrintWriter(outputStream)) {
                throwable.printStackTrace(printWriter);
            }
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

}
