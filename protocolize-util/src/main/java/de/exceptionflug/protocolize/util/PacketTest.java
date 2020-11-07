package de.exceptionflug.protocolize.util;

import com.google.common.io.ByteStreams;
import de.exceptionflug.protocolize.items.packet.BlockPlacement;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

public class PacketTest {

  public static void main(String[] args) throws IOException {
    ProxyServer.setInstance(new DummyProxyServer());
    ByteBuf byteBuf = Unpooled.wrappedBuffer(readBinary("/packet.bin"));

    BlockPlacement placement = new BlockPlacement();
    placement.read(byteBuf, Direction.TO_SERVER, 47);
    System.out.println(placement);
  }

  private static byte[] readBinary(String name) throws IOException {
    return ByteStreams.toByteArray(PacketTest.class.getResourceAsStream(name));
  }

}
