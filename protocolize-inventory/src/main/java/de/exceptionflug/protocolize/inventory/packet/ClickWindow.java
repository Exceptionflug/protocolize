package de.exceptionflug.protocolize.inventory.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.ClickType;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.items.ItemStack;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class ClickWindow extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = Maps.newHashMap();

    static {
        MAPPING.put(MINECRAFT_1_8, 0x0E);
        MAPPING.put(MINECRAFT_1_9, 0x07);
        MAPPING.put(MINECRAFT_1_9_1, 0x07);
        MAPPING.put(MINECRAFT_1_9_2, 0x07);
        MAPPING.put(MINECRAFT_1_9_3, 0x07);
        MAPPING.put(MINECRAFT_1_10, 0x07);
        MAPPING.put(MINECRAFT_1_11, 0x07);
        MAPPING.put(MINECRAFT_1_11_1, 0x07);
        MAPPING.put(MINECRAFT_1_12, 0x08);
        MAPPING.put(MINECRAFT_1_12_1, 0x07);
        MAPPING.put(MINECRAFT_1_12_2, 0x07);
        MAPPING.put(MINECRAFT_1_13, 0x08);
        MAPPING.put(MINECRAFT_1_13_1, 0x08);
        MAPPING.put(MINECRAFT_1_13_2, 0x08);
        MAPPING.put(MINECRAFT_1_14, 0x09);
        MAPPING.put(MINECRAFT_1_14_1, 0x09);
        MAPPING.put(MINECRAFT_1_14_2, 0x09);
        MAPPING.put(MINECRAFT_1_14_3, 0x09);
    }

    private int windowId, actionNumber;
    private short slot;
    private ClickType clickType;
    private ItemStack itemStack;

    public ClickWindow() {
    }

    public ClickWindow(final int windowId, final int actionNumber, final short slot, final ClickType clickType, final ItemStack itemStack) {
        this.windowId = windowId;
        this.actionNumber = actionNumber;
        this.slot = slot;
        this.clickType = clickType;
        this.itemStack = itemStack;
    }

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        windowId = buf.readUnsignedByte();
        slot = buf.readShort();
        final byte button = buf.readByte();
        actionNumber = buf.readShort();
        final int mode;
        if(protocolVersion == MINECRAFT_1_8)
            mode = buf.readByte();
        else
            mode = readVarInt(buf);
        clickType = ClickType.getType(mode, button);
        itemStack = ItemStack.read(buf, protocolVersion);
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        buf.writeByte(windowId & 0xFF);
        buf.writeShort(slot);
        buf.writeByte(clickType.getButton());
        buf.writeShort(actionNumber);
        if(protocolVersion == MINECRAFT_1_8)
            buf.writeByte(clickType.getMode());
        else
            writeVarInt(clickType.getMode(), buf);
        if(itemStack == null)
            ItemStack.NO_DATA.write(buf, protocolVersion);
        else
            itemStack.write(buf, protocolVersion);
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(final int windowId) {
        this.windowId = windowId;
    }

    public short getSlot() {
        return slot;
    }

    public void setSlot(final short slot) {
        this.slot = slot;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public void setClickType(final ClickType clickType) {
        this.clickType = clickType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getActionNumber() {
        return actionNumber;
    }

    public void setActionNumber(final int actionNumber) {
        this.actionNumber = actionNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClickWindow that = (ClickWindow) o;
        return windowId == that.windowId &&
                actionNumber == that.actionNumber &&
                slot == that.slot &&
                clickType == that.clickType &&
                Objects.equals(itemStack, that.itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowId, actionNumber, slot, clickType, itemStack);
    }

    @Override
    public String toString() {
        return "ClickWindow{" +
                "windowId=" + windowId +
                ", actionNumber=" + actionNumber +
                ", slot=" + slot +
                ", clickType=" + clickType +
                ", itemStack=" + itemStack +
                '}';
    }
}
