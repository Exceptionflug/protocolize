package de.exceptionflug.protocolize.items.packet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.items.ItemStack;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class SetSlot extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = Maps.newHashMap();

    static {
        MAPPING.put(MINECRAFT_1_8, 0x2F);
        MAPPING.put(MINECRAFT_1_9, 0x16);
        MAPPING.put(MINECRAFT_1_9_1, 0x16);
        MAPPING.put(MINECRAFT_1_9_2, 0x16);
        MAPPING.put(MINECRAFT_1_9_3, 0x16);
        MAPPING.put(MINECRAFT_1_10, 0x16);
        MAPPING.put(MINECRAFT_1_11, 0x16);
        MAPPING.put(MINECRAFT_1_11_1, 0x16);
        MAPPING.put(MINECRAFT_1_12, 0x16);
        MAPPING.put(MINECRAFT_1_12_1, 0x16);
        MAPPING.put(MINECRAFT_1_12_2, 0x16);
        MAPPING.put(MINECRAFT_1_13, 0x17);
        MAPPING.put(MINECRAFT_1_13_1, 0x17);
        MAPPING.put(MINECRAFT_1_13_2, 0x17);
        MAPPING.put(MINECRAFT_1_14, 0x16);
        MAPPING.put(MINECRAFT_1_14_1, 0x16);
        MAPPING.put(MINECRAFT_1_14_2, 0x16);
    }

    private byte windowId;
    private short slot;
    private ItemStack stack;

    public SetSlot(final byte windowId, final short slot, final ItemStack stack) {
        Preconditions.checkNotNull(stack, "The stack cannot be null!");
        this.slot = slot;
        this.stack = stack;
        this.windowId = windowId;
    }

    public SetSlot() {
    }

    public SetSlot(final short slot, final ItemStack stack) {
        Preconditions.checkNotNull(stack, "The stack cannot be null!");
        this.slot = slot;
        this.stack = stack;
    }

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        Preconditions.checkNotNull(buf, "The buf cannot be null!");
        windowId = buf.readByte();
        slot = buf.readShort();
        stack = ItemStack.read(buf, protocolVersion);
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        Preconditions.checkNotNull(buf, "The buf cannot be null!");
        buf.writeByte(windowId);
        buf.writeShort(slot);
        if(stack == null)
            buf.writeShort(-1);
        else
            stack.write(buf, protocolVersion);
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public byte getWindowId() {
        return windowId;
    }

    public short getSlot() {
        return slot;
    }

    public void setSlot(final short slot) {
        this.slot = slot;
    }

    public void setItemStack(final ItemStack stack) {
        this.stack = stack;
    }

    public void setWindowId(final byte windowId) {
        this.windowId = windowId;
    }

    @Override
    public String toString() {
        return "SetSlot{" +
                "windowId=" + windowId +
                ", slot=" + slot +
                ", stack=" + stack +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SetSlot setSlot = (SetSlot) o;
        return windowId == setSlot.windowId &&
                slot == setSlot.slot &&
                Objects.equals(stack, setSlot.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowId, slot, stack);
    }
}
