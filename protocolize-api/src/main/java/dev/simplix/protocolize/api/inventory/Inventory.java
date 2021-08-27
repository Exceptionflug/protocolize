package dev.simplix.protocolize.api.inventory;

import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import dev.simplix.protocolize.data.inventory.InventoryType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date: 27.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@EqualsAndHashCode
@Accessors(fluent = true)
public class Inventory {

    private static final ComponentConverter CONVERTER = Protocolize.getService(ComponentConverterProvider.class)
            .platformConverter();

    private final Map<Integer, ItemStack> items = new ConcurrentHashMap<>();
    private InventoryType type;
    private String titleJson;

    public void items(List<ItemStack> itemsIndexed) {
        int slot = 0;
        for (ItemStack stack : itemsIndexed) {
            if (stack != null && stack.itemType() != null) {
                items.put(slot, stack);
            }
            slot++;
        }
    }

    public List<ItemStack> itemsIndexed(int protocolVersion) {
        ItemStack[] outArray = new ItemStack[type.getTypicalSize(protocolVersion)];
        for (Integer id : items.keySet()) {
            outArray[id] = items.get(id);
        }
        return Arrays.asList(outArray);
    }

    public boolean removeItem(int slot) {
        return items.remove(slot) != null;
    }

    public ItemStack item(int slot) {
        return items.get(slot);
    }

    public ItemStack item(int slot, ItemStack stack) {
        return items.put(slot, stack);
    }

    public <T> T title() {
        return title(false);
    }

    public <T> T title(boolean legacyString) {
        if (legacyString) {
            return (T) CONVERTER.toLegacyText(CONVERTER.fromJson(titleJson));
        } else {
            return (T) CONVERTER.fromJson(titleJson);
        }
    }

    public void title(String legacyName) {
        this.titleJson = CONVERTER.toJson(CONVERTER.fromLegacyText(legacyName));
    }

    public void title(Object titleComponent) {
        this.titleJson = CONVERTER.toJson(titleComponent);
    }


}
