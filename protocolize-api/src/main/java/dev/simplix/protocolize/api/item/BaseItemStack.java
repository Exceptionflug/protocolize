package dev.simplix.protocolize.api.item;

import java.util.List;
import java.util.Set;

public interface BaseItemStack {
  boolean flagSet(ItemFlag flag);

  void itemFlag(ItemFlag flag, boolean active);

  Set<ItemFlag> itemFlags();

  List<String> loreJson();

  String displayNameJson();

  boolean canBeStacked(BaseItemStack stack);

  <T> T displayName();

  <T> T displayName(boolean legacyString);

  BaseItemStack displayName(String legacyName);

  BaseItemStack displayName(Object displayName);

  <T> List<T> lore();

  <T> List<T> lore(boolean legacyString);

  void lore(List<?> list, boolean legacyString);

  void lore(int index, String legacyText);

  void lore(int index, Object component);

  void addToLore(String legacyText);

  void addToLore(Object component);

  BaseItemStack deepClone();

  BaseItemStack deepClone(int protocolVersion);

  dev.simplix.protocolize.data.ItemType itemType();

  net.querz.nbt.tag.CompoundTag nbtData();

  byte amount();

  short durability();

  int hideFlags();

  BaseItemStack itemType(dev.simplix.protocolize.data.ItemType itemType);

  BaseItemStack nbtData(net.querz.nbt.tag.CompoundTag nbtData);

  BaseItemStack amount(byte amount);

  BaseItemStack durability(short durability);

  BaseItemStack hideFlags(int hideFlags);
}
