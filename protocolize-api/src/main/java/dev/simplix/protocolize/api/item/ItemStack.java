package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.ItemType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.*;
import lombok.experimental.Accessors;
import net.querz.nbt.tag.CompoundTag;

import java.util.*;

/**
 * This class represents a Minecraft item stack. <br>
 * <br>
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class ItemStack {

    public static final ItemStack NO_DATA = new ItemStack(null);

    private static final ComponentConverter CONVERTER = Protocolize.getService(ComponentConverterProvider.class)
        .platformConverter();

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private String displayNameJson;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private List<String> loreJson;

    private ItemType itemType;
    private CompoundTag nbtData = new CompoundTag();
    private byte amount;
    private short durability;
    private int hideFlags;

    public ItemStack(ItemType itemType) {
        this(itemType, 1);
    }

    public ItemStack(ItemType itemType, int amount) {
        this(itemType, amount, (short) -1);
    }

    public ItemStack(ItemType itemType, int amount, short durability) {
        this.itemType = itemType;
        this.amount = (byte) amount;
        this.durability = durability;
    }

    public boolean flagSet(ItemFlag flag) {
        return (hideFlags & (1 << flag.getBitIndex())) == 1;
    }

    public void itemFlag(ItemFlag flag, boolean active) {
        if (active)
            hideFlags |= (1 << flag.getBitIndex());
        else
            hideFlags &= ~(1 << flag.getBitIndex());
    }

    public Set<ItemFlag> itemFlags() {
        Set<ItemFlag> flags = new HashSet<>();
        for (ItemFlag flag : ItemFlag.values()) {
            if (flagSet(flag))
                flags.add(flag);
        }
        return Collections.unmodifiableSet(flags);
    }

    public boolean canBeStacked(ItemStack stack) {
        return stack.itemType() == itemType && stack.nbtData().equals(nbtData);
    }

    public <T> T displayName() {
        return displayName(false);
    }

    public <T> T displayName(boolean legacyString) {
        if (legacyString) {
            return (T) CONVERTER.toLegacyText(CONVERTER.fromJson(displayNameJson));
        } else {
            return (T) CONVERTER.fromJson(displayNameJson);
        }
    }

    public ItemStack displayName(String legacyName) {
        this.displayNameJson = CONVERTER.toJson(CONVERTER.fromLegacyText(legacyName));
        return this;
    }

    public ItemStack displayName(Object displayName) {
        this.displayNameJson = CONVERTER.toJson(displayName);
        return this;
    }

    public <T> List<T> lore() {
        return lore(false);
    }

    public <T> List<T> lore(boolean legacyString) {
        List<T> out = new ArrayList<>();
        for (String line : loreJson) {
            if (legacyString) {
                out.add((T) CONVERTER.toLegacyText(CONVERTER.fromJson(line)));
            } else {
                out.add((T) CONVERTER.fromJson(line));
            }
        }
        return out;
    }

    public void lore(List<Object> list, boolean legacyString) {
        List<String> out = new ArrayList<>();
        for (Object line : list) {
            if (legacyString) {
                out.add(CONVERTER.toJson(CONVERTER.fromLegacyText((String) line)));
            } else {
                out.add(CONVERTER.toJson(line));
            }
        }
        this.loreJson = out;
    }

    public void lore(int index, String legacyText) {
        loreJson.set(index, CONVERTER.toJson(CONVERTER.fromLegacyText(legacyText)));
    }

    public void lore(int index, Object component) {
        loreJson.set(index, CONVERTER.toJson(component));
    }

    public void addToLore(String legacyText) {
        loreJson.add(CONVERTER.toJson(CONVERTER.fromLegacyText(legacyText)));
    }

    public void addToLore(Object component) {
        loreJson.add(CONVERTER.toJson(component));
    }

    public ItemStack deepClone() {
        return deepClone(ProtocolVersions.MINECRAFT_LATEST);
    }

    public ItemStack deepClone(int protocolVersion) {
        ByteBuf buf = Unpooled.buffer();
        ItemStackSerializer.write(buf, this, protocolVersion);
        ItemStack itemStack = ItemStackSerializer.read(buf, protocolVersion);
        buf.release();
        return itemStack;
    }

}
