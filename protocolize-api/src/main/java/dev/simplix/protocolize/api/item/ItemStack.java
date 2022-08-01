package dev.simplix.protocolize.api.item;

import com.google.common.base.Preconditions;
import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.ItemType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.*;
import lombok.*;
import lombok.experimental.Accessors;
import net.querz.nbt.tag.CompoundTag;

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
public class ItemStack implements BaseItemStack {

  public static final ItemStack NO_DATA = new ItemStack(null);

  protected static final ComponentConverter CONVERTER = Protocolize.getService(ComponentConverterProvider.class)
          .platformConverter();

  @Getter(AccessLevel.PACKAGE)
  @Setter(AccessLevel.PACKAGE)
  private String displayNameJson;

  @Getter(AccessLevel.PACKAGE)
  @Setter(AccessLevel.PACKAGE)
  private List<String> loreJson = new ArrayList<>();

  protected ItemType itemType;
  protected CompoundTag nbtData = new CompoundTag();
  protected byte amount;
  protected short durability;
  protected int hideFlags;

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

  @Override
  public boolean flagSet(ItemFlag flag) {
    return (this.hideFlags & (1 << flag.getBitIndex()))==1;
  }

  @Override
  public void itemFlag(ItemFlag flag, boolean active) {
    if (active) {
      this.hideFlags |= (1 << flag.getBitIndex());
    } else {
      this.hideFlags &= ~(1 << flag.getBitIndex());
    }
  }

  @Override
  public Set<ItemFlag> itemFlags() {
    Set<ItemFlag> flags = new HashSet<>();
    for (ItemFlag flag : ItemFlag.values()) {
      if (flagSet(flag)) {
        flags.add(flag);
      }
    }
    return Collections.unmodifiableSet(flags);
  }

  @Override
  public boolean canBeStacked(ItemStack stack) {
    return stack.itemType()==this.itemType && stack.nbtData().equals(this.nbtData);
  }

  @Override
  public <T> T displayName() {
    return displayName(false);
  }

  @Override
  public <T> T displayName(boolean legacyString) {
    if (this.displayNameJson==null) {
      return null;
    }
    if (legacyString) {
      return (T) CONVERTER.toLegacyText(CONVERTER.fromJson(this.displayNameJson));
    } else {
      return (T) CONVERTER.fromJson(this.displayNameJson);
    }
  }

  @Override
  public ItemStack displayName(String legacyName) {
    this.displayNameJson = CONVERTER.toJson(CONVERTER.fromLegacyText(legacyName));
    return this;
  }

  @Override
  public ItemStack displayName(Object displayName) {
    this.displayNameJson = CONVERTER.toJson(displayName);
    return this;
  }

  @Override
  public <T> List<T> lore() {
    return lore(false);
  }

  @Override
  public <T> List<T> lore(boolean legacyString) {
    List<T> out = new ArrayList<>();
    for (String line : this.loreJson) {
      if (legacyString) {
        out.add((T) CONVERTER.toLegacyText(CONVERTER.fromJson(line)));
      } else {
        out.add((T) CONVERTER.fromJson(line));
      }
    }
    return out;
  }

  @Override
  public void lore(List<?> list, boolean legacyString) {
    Preconditions.checkNotNull(list, "The lore list cannot be null.");
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

  @Override
  public void lore(int index, String legacyText) {
    this.loreJson.set(index, CONVERTER.toJson(CONVERTER.fromLegacyText(legacyText)));
  }

  @Override
  public void lore(int index, Object component) {
    this.loreJson.set(index, CONVERTER.toJson(component));
  }

  @Override
  public void addToLore(String legacyText) {
    this.loreJson.add(CONVERTER.toJson(CONVERTER.fromLegacyText(legacyText)));
  }

  @Override
  public void addToLore(Object component) {
    this.loreJson.add(CONVERTER.toJson(component));
  }

  @Override
  public ItemStack deepClone() {
    return deepClone(ProtocolVersions.MINECRAFT_LATEST);
  }

  @Override
  public ItemStack deepClone(int protocolVersion) {
    ByteBuf buf = Unpooled.buffer();
    ItemStackSerializer.write(buf, this, protocolVersion);
    ItemStack itemStack = ItemStackSerializer.read(buf, protocolVersion);
    buf.release();
    return itemStack;
  }

}
