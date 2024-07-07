package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.data.Enchantment;

import java.util.Map;

public interface StoredEnchantmentsComponent extends StructuredComponent {

    Map<Enchantment, Integer> getEnchantments();

    void setEnchantments(Map<Enchantment, Integer> enchantments);

    void removeEnchantment(Enchantment enchantment);

    void addEnchantment(Enchantment enchantment, int level);

    void setShowInTooltip(boolean show);

    void removeAllEnchantments();

    static StoredEnchantmentsComponent create(Map<Enchantment, Integer> enchantments) {
        return Protocolize.getService(Factory.class).create(enchantments);
    }

    static StoredEnchantmentsComponent create(Map<Enchantment, Integer> enchantments, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(enchantments, showInTooltip);
    }

    interface Factory {

        StoredEnchantmentsComponent create(Map<Enchantment, Integer> enchantments);

        StoredEnchantmentsComponent create(Map<Enchantment, Integer> enchantments, boolean showInTooltip);

    }

}
