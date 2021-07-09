package de.exceptionflug.protocolize.util;

import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.util.ProtocolVersions;
import de.exceptionflug.protocolize.items.packet.WindowItems;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Date: 09.07.2021
 *
 * @author Exceptionflug
 */
public class PacketDebugger {

    public static void main(String[] args) throws IOException {
        ProxyServer.setInstance(new DummyProxyServer());
        byte[] bytes = readLogFile(2);
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);

        WindowItems windowItems = new WindowItems();
        windowItems.read(buf, ProtocolConstants.Direction.TO_CLIENT, ProtocolVersions.MINECRAFT_1_17_1);
        System.out.println(windowItems);
    }

    private static byte[] readLogFile(int entry) throws IOException {
        int i = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("./protocolize.log"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("|")) {
                    if (i == entry) {
                        System.out.println(line);
                    }
                } else {
                    // No separator found. Must be data line.
                    String[] split = line.split(" ");
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    for (String byteString : split) {
                        if (byteString.isEmpty()) {
                            continue;
                        }
                        byte data = (byte) (Integer.parseInt(byteString, 16) & 0xFF);
                        byteArrayOutputStream.write(data);
                    }
                    if (i == entry) {
                        return byteArrayOutputStream.toByteArray();
                    }
                    i ++;
                }
            }
        }
        return null;
    }

}
