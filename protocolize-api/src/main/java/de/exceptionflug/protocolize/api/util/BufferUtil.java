package de.exceptionflug.protocolize.api.util;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;

public final class BufferUtil {

  private static PrintWriter printWriter;

  static {
    try {
      File file = new File("protocolize.log");
      if (!file.exists()) {
        file.createNewFile();
      }
      printWriter = new PrintWriter(new FileOutputStream("protocolize.log", true), true);
    } catch (IOException e) {
      ProxyServer.getInstance().getLogger().log(Level.WARNING, "[Protocolize] Cannot initialize logging", e);
    }
  }

  private BufferUtil() {
  }

  public static void finishBuffer(DefinedPacket packet, ByteBuf buffer, ProtocolConstants.Direction direction, int version) {
    try {
      int readableBytes = buffer.readableBytes();
      if (readableBytes > 0) {
        // Something is wrong
        ProxyServer.getInstance().getLogger().warning("[Protocolize] Packet " + packet.getClass().getName() +
                " was not read successfully. Please look into protocolize.log for further details.");
        printBufferHex(buffer, packet, direction, version, readableBytes);
      }
    } catch (Exception e) {
      ProxyServer.getInstance().getLogger().log(Level.WARNING, "[Protocolize] Unable to save debug information", e);
    }
  }

  private static void printBufferHex(ByteBuf buffer, DefinedPacket packet, ProtocolConstants.Direction direction,
                                     int version, int bytes) {
    buffer.readerIndex(0);
    int packetId = DefinedPacket.readVarInt(buffer);
    printWriter.println(DateFormat.getDateTimeInstance().format(new Date()) +
            " | Direction = " + direction.name() +
            " | Version = " + version +
            " | Packet = " + packet.getClass().getName() + " ("+packetId+")" +
            " | Packet has " + bytes + " trailing bytes left");
    while (buffer.isReadable()) {
      printWriter.print(String.format("%02x", buffer.readByte())+" ");
    }
    printWriter.println();
  }

}
