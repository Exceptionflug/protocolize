package dev.simplix.protocolize.api.item;

import java.util.List;
import java.util.Set;

public interface BaseItemStack {
  boolean flagSet(ItemFlag flag);

  void itemFlag(ItemFlag flag, boolean active);

  Set<ItemFlag> itemFlags();

  boolean canBeStacked(ItemStack stack);

  <T> T displayName();

  <T> T displayName(boolean legacyString);

  ItemStack displayName(String legacyName);

  ItemStack displayName(Object displayName);

  <T> List<T> lore();

  <T> List<T> lore(boolean legacyString);

  void lore(List<?> list, boolean legacyString);

  void lore(int index, String legacyText);

  void lore(int index, Object component);

  void addToLore(String legacyText);

  void addToLore(Object component);

  ItemStack deepClone();

  ItemStack deepClone(int protocolVersion);

  dev.simplix.protocolize.data.ItemType itemType();

  net.querz.nbt.tag.CompoundTag nbtData();

  byte amount();

  short durability();

  int hideFlags();

  ItemStack itemType(dev.simplix.protocolize.data.ItemType itemType);

  ItemStack nbtData(net.querz.nbt.tag.CompoundTag nbtData);

  ItemStack amount(byte amount);

  ItemStack durability(short durability);

  ItemStack hideFlags(int hideFlags);
}
