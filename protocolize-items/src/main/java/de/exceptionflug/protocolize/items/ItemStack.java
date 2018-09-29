package de.exceptionflug.protocolize.items;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public final class ItemStack {

    public final static ItemStack NO_DATA = new ItemStack(ItemType.NO_DATA);

    private String displayName;
    private byte amount;
    private short durability;
    private ItemType type;
    private boolean homebrew = true;
    private CompoundTag nbtdata = new CompoundTag("", new CompoundMap());

    public ItemStack(final ItemType type) {
        this(type, (byte) 1, (short) -1);
    }

    public ItemStack(final ItemType type, final int amount) {
        this(type, amount, (short) -1);
    }

    public ItemStack(final ItemType type, final int amount, final short durability) {
        this.type = type;
        this.amount = (byte) amount;
        this.durability = durability;
    }

    public Tag getNBTTag() {
        return nbtdata;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    private void setDisplayNameTag(final String name) {
        if(name == null)
            return;
        CompoundTag display = (CompoundTag) nbtdata.getValue().get("display");
        if (display == null) {
            display = new CompoundTag("display", new CompoundMap());
        }
        final StringTag tag = new StringTag("Name", name);
        display.getValue().put(tag);
        nbtdata.getValue().put(display);
    }

    public void setNBTTag(final CompoundTag nbtdata) {
        this.nbtdata = nbtdata;
    }

    public void write(final ByteBuf buf, final int protocolVersion) {
        Preconditions.checkNotNull(buf, "The buf cannot be null!");
        try {
            final int protocolID;
            if(type == null)
                protocolID = -1;
            else
                protocolID = type.getProtocolID(protocolVersion);
            if(protocolID == -2) {
                buf.writeShort(-1);
                ProxyServer.getInstance().getLogger().warning("[Protocolize] "+type.name()+" cannot be used on protocol version "+protocolVersion);
                return;
            }
            buf.writeShort(protocolID);
            if (protocolID == -1)
                return;
            if(durability == -1)
                durability = (short) type.getProtocolData(protocolVersion);
            buf.writeByte(amount);
            if (protocolVersion < 393)
                buf.writeShort(durability);
            if (nbtdata == null) {
                nbtdata = new CompoundTag("", new CompoundMap());
            }
            if (protocolVersion >= 393) {
                nbtdata.getValue().put(new IntTag("Damage", durability));
                setDisplayNameTag(ComponentSerializer.toString(new TextComponent(displayName)));
            } else {
                setDisplayNameTag(displayName);
            }
            buf.markWriterIndex();
            try {
                writeNBTTag(nbtdata, buf);
            } catch (final Exception e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "[Protocolize] Error when writing NBT data to ItemStack: "+nbtdata, e);
                buf.resetWriterIndex();
                writeNBTTag(new CompoundTag("", new CompoundMap()), buf);
            }
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Exception occurred when writing ItemStack to buffer. Protocol version = " + protocolVersion, e);
        }
    }

    private void writeNBTTag(final Tag nbtdata, final ByteBuf buf) throws IOException {
        Preconditions.checkNotNull(nbtdata, "The nbtdata cannot be null!");
        Preconditions.checkNotNull(buf, "The buf cannot be null!");
        try (final NBTOutputStream outputStream = new NBTOutputStream(new ByteBufOutputStream(buf), false)) {
            outputStream.writeTag(nbtdata);
        }
    }

    public static ItemStack read(final ByteBuf buf, final int protocolVersion) {
        Preconditions.checkNotNull(buf, "The buf cannot be null!");
        try {
            final int id = buf.readShort();
            if(id == -1)
                return ItemStack.NO_DATA;
            if (id >= 0) {
                final byte amount = buf.readByte();
                short durability = 0;
                if (protocolVersion < 393) {
                    durability = buf.readShort();
                }
                if (id == 0) {
                    final ItemStack out = new ItemStack(ItemType.getType(id, protocolVersion), amount, durability);
                    out.homebrew = false;
                    return out;
                } else {
                    final CompoundTag tag = (CompoundTag) readNBTTag(buf);
                    String displayName = null;
                    if (protocolVersion >= 393 && tag != null) {
                        final CompoundMap value = tag.getValue();
                        final IntTag damage = (IntTag) value.get("Damage");
                        if (damage != null)
                            durability = damage.getValue().shortValue();
                        final String json = getDisplayNameTag(tag);
                        if(json != null) {
                            final BaseComponent[] displayNameComponents = ComponentSerializer.parse(json);
                            displayName = BaseComponent.toLegacyText(displayNameComponents);
                        }
                    } else {
                        displayName = getDisplayNameTag(tag);
                    }
                    final ItemStack out = new ItemStack(ItemType.getType(id, durability, protocolVersion), amount, durability);
                    out.homebrew = false;
                    out.displayName = displayName;
                    out.setNBTTag(tag);
                    return out;
                }
            }
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Exception occurred when reading ItemStack from buffer. Protocol version = " + protocolVersion, e);
        }
        return null;
    }

    private static Tag readNBTTag(final ByteBuf buf) throws IOException {
        final int i = buf.readerIndex();
        final short b0 = buf.readUnsignedByte();
        if (b0 == 0) {
            return null;
        } else {
            buf.readerIndex(i);
            try (final NBTInputStream inputStream = new NBTInputStream(new ByteBufInputStream(buf), false)) {
                return inputStream.readTag();
            }
        }
    }

    public ItemType getType() {
        return type;
    }

    public byte getAmount() {
        return amount;
    }

    public short getDurability() {
        return durability;
    }

    public boolean isHomebrew() {
        return homebrew;
    }

    public void setAmount(final byte amount) {
        this.amount = amount;
    }

    public void setDurability(final short durability) {
        this.durability = durability;
    }

    public void setType(final ItemType type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    private static String getDisplayNameTag(final CompoundTag nbtdata) {
        if(nbtdata == null)
            return null;
        final CompoundTag display = (CompoundTag) nbtdata.getValue().get("display");
        if (display == null) {
            return null;
        }
        final StringTag name = (StringTag) display.getValue().get("Name");
        if (name == null) {
            return null;
        }
        return name.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStack stack = (ItemStack) o;
        return amount == stack.amount &&
                durability == stack.durability &&
                homebrew == stack.homebrew &&
                Objects.equals(displayName, stack.displayName) &&
                type == stack.type &&
                Objects.equals(nbtdata, stack.nbtdata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, amount, durability, type, homebrew, nbtdata);
    }

    @Override
    public String toString() {
        return "ItemStack{" +
                "displayName='" + displayName + '\'' +
                ", amount=" + amount +
                ", durability=" + durability +
                ", type=" + type +
                ", homebrew=" + homebrew +
                ", nbtdata=" + nbtdata +
                '}';
    }
}
