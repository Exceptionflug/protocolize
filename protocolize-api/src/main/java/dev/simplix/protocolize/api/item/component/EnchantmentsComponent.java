package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import java.util.Map;

public interface EnchantmentsComponent extends StructuredComponent {

    Map<Integer, Integer> getEnchantments();

    void setEnchantments(Map<Integer, Integer> enchantments);

    void removeEnchantment(int id);

    void addEnchantment(int id, int level);

    void setShowInTooltip(boolean show);

    void removeAllEnchantments();

    static EnchantmentsComponent create(Map<Integer, Integer> enchantments) {
        return Protocolize.getService(Factory.class).create(enchantments);
    }

    static EnchantmentsComponent create(Map<Integer, Integer> enchantments, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(enchantments, showInTooltip);
    }

    interface Factory {

        EnchantmentsComponent create(Map<Integer, Integer> enchantments);

        EnchantmentsComponent create(Map<Integer, Integer> enchantments, boolean showInTooltip);

    }

}
