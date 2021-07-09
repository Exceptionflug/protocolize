package de.exceptionflug.protocolize.items.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.util.BufferUtil;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class WindowItems extends AbstractPacket {

  public static final HashMap<Integer, Integer> MAPPING = Maps.newHashMap();

  static {
    MAPPING.put(MINECRAFT_1_8, 0x30);
    MAPPING.put(MINECRAFT_1_9, 0x14);
    MAPPING.put(MINECRAFT_1_9_1, 0x14);
    MAPPING.put(MINECRAFT_1_9_2, 0x14);
    MAPPING.put(MINECRAFT_1_9_3, 0x14);
    MAPPING.put(MINECRAFT_1_10, 0x14);
    MAPPING.put(MINECRAFT_1_11, 0x14);
    MAPPING.put(MINECRAFT_1_11_1, 0x14);
    MAPPING.put(MINECRAFT_1_12, 0x14);
    MAPPING.put(MINECRAFT_1_12_1, 0x14);
    MAPPING.put(MINECRAFT_1_12_2, 0x14);
    MAPPING.put(MINECRAFT_1_13, 0x15);
    MAPPING.put(MINECRAFT_1_13_1, 0x15);
    MAPPING.put(MINECRAFT_1_13_2, 0x15);
    MAPPING.put(MINECRAFT_1_14, 0x14);
    MAPPING.put(MINECRAFT_1_14_1, 0x14);
    MAPPING.put(MINECRAFT_1_14_2, 0x14);
    MAPPING.put(MINECRAFT_1_14_3, 0x14);
    MAPPING.put(MINECRAFT_1_14_4, 0x14);
    MAPPING.put(MINECRAFT_1_15, 0x15);
    MAPPING.put(MINECRAFT_1_15_1, 0x15);
    MAPPING.put(MINECRAFT_1_15_2, 0x15);
    MAPPING.put(MINECRAFT_1_16, 0x14);
    MAPPING.put(MINECRAFT_1_16_1, 0x14);
    MAPPING.put(MINECRAFT_1_16_2, 0x13);
    MAPPING.put(MINECRAFT_1_16_3, 0x13);
    MAPPING.put(MINECRAFT_1_16_4, 0x13);
    MAPPING.put(MINECRAFT_1_17, 0x14);
    MAPPING.put(MINECRAFT_1_17_1, 0x14);
  }

  private short windowId;
  private List<ItemStack> items = new ArrayList<>();

  /**
   * @since 1.7.1-SNAPSHOT protocol 756
   */
  private int stateId;

  public WindowItems(final short windowId, final List<ItemStack> items) {
    this.windowId = windowId;
    this.items = items;
  }

  public WindowItems() {
  }

  @Override
  public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    buf.writeByte(windowId & 0xFF);
    if (protocolVersion >= MINECRAFT_1_17_1) {
      writeVarInt(stateId, buf);
      writeVarInt(items.size(), buf);
    } else {
      buf.writeShort(items.size());
    }
    for (ItemStack item : items) {
      if (item == null)
        item = ItemStack.NO_DATA;
      item.write(buf, protocolVersion);
    }
  }

  @Override
  public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    windowId = buf.readUnsignedByte();
    final int count;
    if (protocolVersion >= MINECRAFT_1_17_1) {
      stateId = readVarInt(buf);
      count = readVarInt(buf);
    } else {
      count = buf.readShort();
    }
    for (int i = 0; i < count; i++) {
      final ItemStack read = ItemStack.read(buf, protocolVersion);
      items.add(read);
    }
    BufferUtil.finishBuffer(this, buf, direction, protocolVersion);
  }

  public List<ItemStack> getItems() {
    return items;
  }

  public void setItems(final List<ItemStack> items) {
    this.items = items;
  }

  public ItemStack getItemStackAtSlot(final int slot) {
    return items.get(slot);
  }

  public boolean setItemStackAtSlot(final int slot, final ItemStack stack) {
    final ItemStack curr = items.get(slot);
    if ((curr == null || curr.getType() == ItemType.NO_DATA) && (stack == null || stack.getType() == ItemType.NO_DATA))
      return false;
    if (curr == null)
      return false;
    if (curr.equals(stack))
      return false;
    items.set(slot, stack);
    return true;
  }

  public short getWindowId() {
    return windowId;
  }

  public void setWindowId(final short windowId) {
    this.windowId = windowId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WindowItems that = (WindowItems) o;
    return windowId == that.windowId &&
            Objects.equals(items, that.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(windowId, items);
  }

  @Override
  public String toString() {
    return "WindowItems{" +
            "windowId=" + windowId +
            ", items=" + items +
            '}';
  }
}
