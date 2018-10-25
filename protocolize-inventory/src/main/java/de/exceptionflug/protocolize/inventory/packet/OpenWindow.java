package de.exceptionflug.protocolize.inventory.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.inventory.InventoryType;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class OpenWindow extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = Maps.newHashMap();

    static {
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
    }

    private int windowId, size, entityId;
    private InventoryType inventoryType;
    private BaseComponent[] title;

    public OpenWindow(final int windowId, final int size, final InventoryType inventoryType, final BaseComponent... title) {
        this.windowId = windowId;
        this.size = size;
        this.inventoryType = inventoryType;
        this.title = title;
    }

    public OpenWindow() {}

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        windowId = buf.readUnsignedByte();
        inventoryType = InventoryType.getInventoryType(readString(buf));
        title = ComponentSerializer.parse(readString(buf));
        size = buf.readUnsignedByte();
        if(inventoryType == InventoryType.HORSE)
            entityId = buf.readInt();
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        buf.writeByte(windowId & 0xFF);
        writeString(inventoryType.getProtocolId(), buf);
        writeString(ComponentSerializer.toString(title), buf);
        buf.writeByte(size & 0xFF);
        if(inventoryType == InventoryType.HORSE)
            buf.writeInt(entityId);
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(final int windowId) {
        this.windowId = windowId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(final int entityId) {
        this.entityId = entityId;
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
                size == that.size &&
                entityId == that.entityId &&
                inventoryType == that.inventoryType &&
                Arrays.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(windowId, size, entityId, inventoryType);
        result = 31 * result + Arrays.hashCode(title);
        return result;
    }

    @Override
    public String toString() {
        return "OpenWindow{" +
                "windowId=" + windowId +
                ", size=" + size +
                ", entityId=" + entityId +
                ", inventoryType=" + inventoryType +
                ", title=" + Arrays.toString(title) +
                '}';
    }
}
