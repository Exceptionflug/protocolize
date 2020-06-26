package de.exceptionflug.protocolize.items;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.DefinedPacket;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public final class ItemStack implements Cloneable {

    public final static ItemStack NO_DATA = new ItemStack(ItemType.NO_DATA);

    private BaseComponent[] displayName;
    private List<BaseComponent[]> lore;
    private byte amount;
    private short durability;
    private ItemType type;
    private boolean homebrew = true;
    private CompoundTag nbtdata = new CompoundTag("", new CompoundMap());
    private int hideFlags;

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
        this.displayName = TextComponent.fromLegacyText(displayName);
    }

    public void setDisplayName(BaseComponent... displayName) {
        this.displayName = displayName;
    }

    private void setDisplayNameTag(final String name) {
        if (name == null)
            return;
        CompoundTag display = (CompoundTag) nbtdata.getValue().get("display");
        if (display == null) {
            display = new CompoundTag("display", new CompoundMap());
        }
        final StringTag tag = new StringTag("Name", name);
        display.getValue().put(tag);
        nbtdata.getValue().put(display);
    }

    private void setLoreTag(final List<BaseComponent[]> lore, final int protocolVersion) {
        if (lore == null)
            return;
        CompoundTag display = (CompoundTag) nbtdata.getValue().get("display");
        if (display == null) {
            display = new CompoundTag("display", new CompoundMap());
        }
        if(protocolVersion < MINECRAFT_1_14) {
            final ListTag<StringTag> tag = new ListTag<>("Lore", StringTag.class, lore.stream().map(i -> new StringTag(String.valueOf(ThreadLocalRandom.current().nextLong()), TextComponent.toLegacyText(i))).collect(Collectors.toList()));
            display.getValue().put(tag);
            nbtdata.getValue().put(display);
        } else {
            final ListTag<StringTag> tag = new ListTag<>("Lore", StringTag.class, lore.stream().map(components -> {
                for(BaseComponent component : components) {
                    if(!component.isItalic()) {
                        component.setItalic(false);
                    }
                }
                return new StringTag(String.valueOf(ThreadLocalRandom.current().nextLong()), ComponentSerializer.toString(components));
            }).collect(Collectors.toList()));
            display.getValue().put(tag);
            nbtdata.getValue().put(display);
        }
    }

    public void setNBTTag(final CompoundTag nbtdata) {
        this.nbtdata = nbtdata;
    }

    public void write(final ByteBuf buf, final int protocolVersion) {
        Preconditions.checkNotNull(buf, "The buf cannot be null!");
        try {
            final int protocolID;
            final ItemIDMapping applicableMapping;
            if (type == null) {
                protocolID = -1;
                applicableMapping = null;
            } else {
                applicableMapping = type.getApplicableMapping(protocolVersion);
                if(applicableMapping == null) {
                    protocolID = -2;
                } else {
                    protocolID = Objects.requireNonNull(applicableMapping).getId();
                }
            }
            if (protocolID == -2) {
                buf.writeShort(-1);
                ProxyServer.getInstance().getLogger().warning("[Protocolize] " + type.name() + " cannot be used on protocol version " + protocolVersion);
                return;
            }
            if(protocolVersion < MINECRAFT_1_13_2) {
                buf.writeShort(protocolID);
                if (protocolID == -1)
                    return;
            } else {
                if(protocolID == -1) {
                    buf.writeBoolean(false);
                    return;
                } else {
                    buf.writeBoolean(true);
                    DefinedPacket.writeVarInt(protocolID, buf);
                }
            }
            if (durability == -1)
                durability = (short) Objects.requireNonNull(applicableMapping).getData();
            buf.writeByte(amount);
            if (protocolVersion < MINECRAFT_1_13)
                buf.writeShort(durability);
            if (nbtdata == null) {
                nbtdata = new CompoundTag("", new CompoundMap());
            }
            if (protocolVersion >= MINECRAFT_1_13) {
                nbtdata.getValue().put(new IntTag("Damage", durability));
                setDisplayNameTag(ComponentSerializer.toString(displayName));
            } else {
                setDisplayNameTag(TextComponent.toLegacyText(displayName));
            }
            setLoreTag(lore, protocolVersion);
            setHideFlags(hideFlags);
            if (applicableMapping instanceof AbstractCustomItemIDMapping) {
                ((AbstractCustomItemIDMapping) applicableMapping).apply(this, protocolVersion);
            }
            buf.markWriterIndex();
            try {
                writeNBTTag(nbtdata, buf);
            } catch (final Exception e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "[Protocolize] Error when writing NBT data to ItemStack:", e);
                buf.resetWriterIndex();
                writeNBTTag(new CompoundTag("", new CompoundMap()), buf);
            }
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Exception occurred when writing ItemStack to buffer. Protocol version = " + protocolVersion, e);
        }
    }

    private void setHideFlags(final int hideFlags) {
        nbtdata.getValue().put(new IntTag("HideFlags", hideFlags));
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
            final int id;
            if(protocolVersion < MINECRAFT_1_13_2) {
                id = buf.readShort();
            } else {
                final boolean present = buf.readBoolean();
                if(present)
                    id = DefinedPacket.readVarInt(buf);
                else
                    id = -1;
            }
            if (id == -1)
                return ItemStack.NO_DATA;
            if (id >= 0) {
                final byte amount = buf.readByte();
                short durability = 0;
                if (protocolVersion < MINECRAFT_1_13) {
                    durability = buf.readShort();
                }
                if (id == 0) {
                    final ItemStack out = new ItemStack(ItemType.AIR, amount, durability);
                    out.homebrew = false;
                    return out;
                } else {
                    final CompoundTag tag = (CompoundTag) readNBTTag(buf);
                    BaseComponent[] displayName = null;
                    final List<BaseComponent[]> loreOut;
                    if (protocolVersion >= MINECRAFT_1_13 && tag != null) {
                        final CompoundMap value = tag.getValue();
                        final IntTag damage = (IntTag) value.get("Damage");
                        if (damage != null)
                            durability = damage.getValue().shortValue();
                        final String json = getDisplayNameTag(tag);
                        if (json != null) {
                            displayName = ComponentSerializer.parse(json);
                        }
                    } else {
                        String displayNameTag = getDisplayNameTag(tag);
                        if(displayNameTag != null) {
                            displayName = TextComponent.fromLegacyText(displayNameTag);
                        }
                    }
                    loreOut = getLoreTag(tag, protocolVersion);
                    final ItemStack out = new ItemStack(null, amount, durability);
                    out.homebrew = false;
                    out.displayName = displayName;
                    out.lore = loreOut;
                    out.setNBTTag(tag);
                    out.hideFlags = getHideFlags(tag);
                    out.setType(ItemType.getType(id, durability, protocolVersion, out));
                    return out;
                }
            }
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Exception occurred when reading ItemStack from buffer. Protocol version = " + protocolVersion, e);
        }
        return null;
    }

    private static int getHideFlags(final CompoundTag tag) {
        if(tag == null)
            return 0;
        if(tag.getValue().containsKey("HideFlags")) {
            return ((IntTag)tag.getValue().get("HideFlags")).getValue();
        }
        return 0;
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
        return BaseComponent.toLegacyText(displayName);
    }

    public BaseComponent[] getDisplayNameComponents() {
        return displayName;
    }

    public boolean isFlagSet(final ItemFlag flag) {
        return (hideFlags & (1 << flag.getBitIndex())) == 1;
    }

    public void setFlag(final ItemFlag flag, final boolean active) {
        if(active)
            hideFlags |= (1 << flag.getBitIndex());
        else
            hideFlags &= ~(1 << flag.getBitIndex());
    }

    public Set<ItemFlag> getItemFlags() {
        final Set<ItemFlag> flags = new HashSet<>();
        for(final ItemFlag flag : ItemFlag.values()) {
            if(isFlagSet(flag))
                flags.add(flag);
        }
        return Collections.unmodifiableSet(flags);
    }

    private static String getDisplayNameTag(final CompoundTag nbtdata) {
        if (nbtdata == null)
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

    private static List<BaseComponent[]> getLoreTag(final CompoundTag nbtdata, final int protocolVersion) {
        if (nbtdata == null)
            return null;
        final CompoundTag display = (CompoundTag) nbtdata.getValue().get("display");
        if (display == null) {
            return null;
        }
        final ListTag<StringTag> lore = (ListTag<StringTag>) display.getValue().get("Lore");
        if (lore == null) {
            return null;
        }
        if(protocolVersion < MINECRAFT_1_14) {
            return lore.getValue().stream().map(stringTag -> TextComponent.fromLegacyText(stringTag.getValue())).collect(Collectors.toList());
        } else {
            return lore.getValue().stream().map(it -> ComponentSerializer.parse(it.getValue())).collect(Collectors.toList());
        }
    }

    public boolean isPlayerSkull() {
        return type == ItemType.PLAYER_HEAD;
    }

    public void setSkullTexture(final String textureHash) {
        Preconditions.checkNotNull(textureHash, "The textureHash cannot be null!");
        Preconditions.checkArgument(!textureHash.isEmpty(), "The textureHash cannot be empty!");
        Preconditions.checkState(type == ItemType.PLAYER_HEAD, "The item type must be PLAYER_HEAD");
        final CompoundTag skullOwner = (CompoundTag) ((CompoundTag)getNBTTag()).getValue().getOrDefault("SkullOwner", new CompoundTag("SkullOwner", new CompoundMap()));
        skullOwner.getValue().put(new StringTag("Name", textureHash));
        final CompoundTag properties = (CompoundTag) skullOwner.getValue().getOrDefault("Properties", new CompoundTag("Properties", new CompoundMap()));
        final CompoundTag texture = new CompoundTag("value", new CompoundMap());
        texture.getValue().put(new StringTag("Value", textureHash));
        final ListTag<CompoundTag> textures = new ListTag<>("textures", CompoundTag.class, Lists.newArrayList(texture));
        properties.getValue().put(textures);
        skullOwner.getValue().put(properties);
        ((CompoundTag)getNBTTag()).getValue().put(skullOwner);
    }

    public void setSkullOwner(final String skullOwner) {
        Preconditions.checkState(type == ItemType.PLAYER_HEAD, "The item type must be PLAYER_HEAD");
        ((CompoundTag)getNBTTag()).getValue().put(new StringTag("SkullOwner", skullOwner));
    }

    public String getSkullOwner() {
        if(((CompoundTag)getNBTTag()).getValue().containsKey("SkullOwner")) {
            final Tag t = ((CompoundTag)getNBTTag()).getValue().get("SkullOwner");
            if(t instanceof StringTag) {
                return ((StringTag) t).getValue();
            } else if(t instanceof CompoundTag) {
                final CompoundTag skullOwner = (CompoundTag) t;
                final Tag t2 = skullOwner.getValue().get("Name");
                if(t2 instanceof StringTag) {
                    return ((StringTag) t2).getValue();
                }
            }
        }
        return null;
    }

    public List<String> getLore() {
        return lore.stream().map(TextComponent::toLegacyText).collect(Collectors.toList());
    }

    public List<BaseComponent[]> getLoreComponents() {
        return lore;
    }

    public void setLore(final List<String> lore) {
        this.lore = lore.stream().map(TextComponent::fromLegacyText).collect(Collectors.toList());
    }

    public void setLoreComponents(List<BaseComponent[]> lore) {
        this.lore = lore;
    }

    public ItemStack deepClone() {
        final ByteBuf buf = Unpooled.buffer();
        write(buf, MINECRAFT_1_13_2);
        final ItemStack itemStack = read(buf, MINECRAFT_1_13_2);
        itemStack.homebrew = homebrew;
        buf.release();
        return itemStack;
    }

    public boolean canBeStacked(final ItemStack stack) {
        return stack.getType() == type && stack.getNBTTag().equals(nbtdata);
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
                Objects.equals(lore, stack.lore) &&
                type == stack.type &&
                Objects.equals(nbtdata, stack.nbtdata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, lore, amount, durability, type, homebrew, nbtdata);
    }

    @Override
    public String toString() {
        return "ItemStack{" +
                "displayName='" + displayName + '\'' +
                ", lore=" + lore +
                ", amount=" + amount +
                ", durability=" + durability +
                ", type=" + type +
                ", homebrew=" + homebrew +
                '}';
    }
}
