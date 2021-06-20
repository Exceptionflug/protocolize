package de.exceptionflug.protocolize.inventory.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.util.BufferUtil;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.inventory.LegacyWindow;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class OpenWindow extends AbstractPacket {

  public static final Map<Integer, Integer> MAPPING = Maps.newHashMap();

  static {
    MAPPING.put(MINECRAFT_1_7_2, 0x2D);
    MAPPING.put(MINECRAFT_1_7_6, 0x2D);
    MAPPING.put(MINECRAFT_1_8, 0x2D);
    MAPPING.put(MINECRAFT_1_9, 0x13);
    MAPPING.put(MINECRAFT_1_9_1, 0x13);
    MAPPING.put(MINECRAFT_1_9_2, 0x13);
    MAPPING.put(MINECRAFT_1_9_3, 0x13);
    MAPPING.put(MINECRAFT_1_10, 0x13);
    MAPPING.put(MINECRAFT_1_11, 0x13);
    MAPPING.put(MINECRAFT_1_11_1, 0x13);
    MAPPING.put(MINECRAFT_1_12, 0x13);
    MAPPING.put(MINECRAFT_1_12_1, 0x13);
    MAPPING.put(MINECRAFT_1_12_2, 0x13);
    MAPPING.put(MINECRAFT_1_13, 0x14);
    MAPPING.put(MINECRAFT_1_13_1, 0x14);
    MAPPING.put(MINECRAFT_1_13_2, 0x14);
    MAPPING.put(MINECRAFT_1_14, 0x2E);
    MAPPING.put(MINECRAFT_1_14_1, 0x2E);
    MAPPING.put(MINECRAFT_1_14_2, 0x2E);
    MAPPING.put(MINECRAFT_1_14_3, 0x2E);
    MAPPING.put(MINECRAFT_1_14_4, 0x2E);
    MAPPING.put(MINECRAFT_1_15, 0x2F);
    MAPPING.put(MINECRAFT_1_15_1, 0x2F);
    MAPPING.put(MINECRAFT_1_15_2, 0x2F);
    MAPPING.put(MINECRAFT_1_16, 0x2E);
    MAPPING.put(MINECRAFT_1_16_1, 0x2E);
    MAPPING.put(MINECRAFT_1_16_2, 0x2D);
    MAPPING.put(MINECRAFT_1_16_3, 0x2D);
    MAPPING.put(MINECRAFT_1_16_4, 0x2D);
    MAPPING.put(MINECRAFT_1_17, 0x2E);
  }

  private int windowId;
  private InventoryType inventoryType;
  private BaseComponent[] title;

  public OpenWindow(final int windowId, final InventoryType inventoryType, final BaseComponent... title) {
    this.windowId = windowId;
    this.inventoryType = inventoryType;
    this.title = title;
  }

  public OpenWindow() {
  }

  @Override
  public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    if (protocolVersion < MINECRAFT_1_8) {
      windowId = buf.readUnsignedByte();
      int invType = buf.readUnsignedByte();
      final String legacyId = LegacyWindow.getLegacyId(invType);
      title = new BaseComponent[] { new TextComponent(readString(buf)) };
      final int size = buf.readUnsignedByte();
      inventoryType = InventoryType.getType(legacyId, size, protocolVersion);
      buf.readBoolean(); // Skip use provided window title
      buf.readBytes(buf.readableBytes()); // Skip optional entity id
    } else if (protocolVersion < MINECRAFT_1_14) {
      windowId = buf.readUnsignedByte();
      final String legacyId = readString(buf);
      title = ComponentSerializer.parse(readString(buf));
      final int size = buf.readUnsignedByte();
      inventoryType = InventoryType.getType(legacyId, size, protocolVersion);
      buf.readBytes(buf.readableBytes()); // Skip optional entity id
    } else {
      windowId = readVarInt(buf);
      inventoryType = InventoryType.getType(readVarInt(buf), protocolVersion);
      try {
        title = ComponentSerializer.parse(readString(buf));
      } catch (Exception e) {
        ProxyServer.getInstance().getLogger()
                .warning("[Protocolize] Invalid message component in window title: windowId="
                        + windowId + " type=" + inventoryType.name() + " protocolVersion=" + protocolVersion);
      }
    }
    BufferUtil.finishBuffer(this, buf, direction, protocolVersion);
  }

  @Override
  public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    if (protocolVersion < MINECRAFT_1_8) {
      String legacyTitle = TextComponent.toLegacyText(title);

      if (legacyTitle.length() > 32) {
        // truncate titles more than 32 characters to prevent crashing 1.7 clients
        legacyTitle = legacyTitle.substring(0, 32);
      }

      buf.writeByte(windowId & 0xFF);
      buf.writeByte(inventoryType.getTypeId(protocolVersion) & 0xFF);
      writeString(legacyTitle, buf);
      buf.writeByte(inventoryType.getTypicalSize(protocolVersion) & 0xFF);
      buf.writeBoolean(true); // use provided window title
    } else if (protocolVersion < MINECRAFT_1_14) {
      buf.writeByte(windowId & 0xFF);
      writeString(Objects.requireNonNull(inventoryType.getLegacyTypeId(protocolVersion)), buf);
      writeString(ComponentSerializer.toString(title), buf);
      buf.writeByte(inventoryType.getTypicalSize(protocolVersion) & 0xFF);
    } else {
      writeVarInt(windowId, buf);
      writeVarInt(inventoryType.getTypeId(protocolVersion), buf);
      writeString(ComponentSerializer.toString(title), buf);
    }
  }

  public int getWindowId() {
    return windowId;
  }

  public void setWindowId(final int windowId) {
    this.windowId = windowId;
  }

  public InventoryType getInventoryType() {
    return inventoryType;
  }

  public void setInventoryType(final InventoryType inventoryType) {
    this.inventoryType = inventoryType;
  }

  public BaseComponent[] getTitle() {
    return title;
  }

  public void setTitle(final BaseComponent... title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpenWindow that = (OpenWindow) o;
    return windowId == that.windowId &&
            inventoryType == that.inventoryType &&
            Arrays.equals(title, that.title);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(windowId, inventoryType);
    result = 31 * result + Arrays.hashCode(title);
    return result;
  }

  @Override
  public String toString() {
    return "OpenWindow{" +
            "windowId=" + windowId +
            ", inventoryType=" + inventoryType +
            ", title=" + Arrays.toString(title) +
            '}';
  }
}
