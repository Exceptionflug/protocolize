package dev.simplix.protocolize.api.inventory;

import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import dev.simplix.protocolize.data.inventory.InventoryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

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

    private final Map<Integer, BaseItemStack> items = new ConcurrentHashMap<>();
    private final List<Consumer<InventoryClick>> clickConsumers = new ArrayList<>();
    private final List<Consumer<InventoryClose>> closeConsumers = new ArrayList<>();
    private InventoryType type;
    private String titleJson;

    public Inventory(InventoryType type) {
        this.type = type;
    }

    public void items(List<BaseItemStack> itemsIndexed) {
        int slot = 0;
        for (BaseItemStack stack : itemsIndexed) {
            if (stack != null && stack.itemType() != null) {
                items.put(slot, stack);
            }
            slot++;
        }
    }

    public List<BaseItemStack> itemsIndexed(int protocolVersion) {
        BaseItemStack[] outArray = new ItemStack[type.getTypicalSize(protocolVersion)];
        for (Integer id : items.keySet()) {
            outArray[id] = items.get(id);
        }
        return Arrays.asList(outArray);
    }

    public boolean removeItem(int slot) {
        return items.remove(slot) != null;
    }

    public BaseItemStack item(int slot) {
        return items.get(slot);
    }

    public Inventory item(int slot, ItemStack stack) {
        items.put(slot, stack);
        return this;
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

    public Inventory title(String legacyName) {
        this.titleJson = CONVERTER.toJson(CONVERTER.fromLegacyText(legacyName));
        return this;
    }

    public Inventory title(Object titleComponent) {
        this.titleJson = CONVERTER.toJson(titleComponent);
        return this;
    }

    public Inventory onClick(Consumer<InventoryClick> consumer) {
        clickConsumers.add(consumer);
        return this;
    }

    public Inventory onClose(Consumer<InventoryClose> consumer) {
        closeConsumers.add(consumer);
        return this;
    }


}
