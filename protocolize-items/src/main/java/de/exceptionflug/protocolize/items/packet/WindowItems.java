package de.exceptionflug.protocolize.items.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.Protocol.DirectionData;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WindowItems extends AbstractPacket {

    public static final HashMap<Integer, Integer> MAPPING = Maps.newHashMap();

    private short windowId;
    private List<ItemStack> items = new ArrayList<>();

    static {
        MAPPING.put(47, 0x30);
        MAPPING.put(107, 0x14);
        MAPPING.put(108, 0x14);
        MAPPING.put(109, 0x14);
        MAPPING.put(110, 0x14);
        MAPPING.put(210, 0x14);
        MAPPING.put(315, 0x14);
        MAPPING.put(316, 0x14);
        MAPPING.put(335, 0x14);
        MAPPING.put(338, 0x14);
        MAPPING.put(340, 0x14);
        MAPPING.put(393, 0x15);
        MAPPING.put(401, 0x15);
    }

    public WindowItems(final short windowId, final List<ItemStack> items) {
        this.windowId = windowId;
        this.items = items;
    }

    public WindowItems() {
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        buf.writeByte(windowId & 0xFF);
        buf.writeShort(items.size());
        for(ItemStack item : items) {
            if(item == null)
                item = ItemStack.NO_DATA;
            item.write(buf, protocolVersion);
        }
    }

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        windowId = buf.readUnsignedByte();
        final short count = buf.readShort();
        for (int i = 0; i < count; i++) {
            final ItemStack read = ItemStack.read(buf, protocolVersion);
            items.add(read);
        }
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public ItemStack getItemStackAtSlot(final int slot) {
        return items.get(slot);
    }

    public boolean setItemStackAtSlot(final int slot, final ItemStack stack) {
        if(items.get(slot).equals(stack))
            return false;
        items.set(slot, stack);
        return true;
    }

    public short getWindowId() {
        return windowId;
    }

    public void setItems(final List<ItemStack> items) {
        this.items = items;
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
