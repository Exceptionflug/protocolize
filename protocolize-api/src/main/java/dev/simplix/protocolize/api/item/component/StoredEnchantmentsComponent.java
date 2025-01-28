package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.util.Either;
import dev.simplix.protocolize.data.Enchantment;

import java.util.Map;

public interface StoredEnchantmentsComponent extends StructuredComponent {

    Map<Either<Enchantment, Integer>, Integer> getEnchantments();

    void setEnchantments(Map<Either<Enchantment, Integer>, Integer> enchantments);

    void removeEnchantment(Enchantment enchantment);

    void removeEnchantment(int enchantmentId);

    void addEnchantment(Enchantment enchantment, int level);

    void addEnchantment(int enchantmentId, int level);

    void setShowInTooltip(boolean show);

    void removeAllEnchantments();

    static StoredEnchantmentsComponent create(Map<Either<Enchantment, Integer>, Integer> enchantments) {
        return Protocolize.getService(Factory.class).create(enchantments);
    }

    static StoredEnchantmentsComponent create(Map<Either<Enchantment, Integer>, Integer> enchantments, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(enchantments, showInTooltip);
    }

    interface Factory {

        StoredEnchantmentsComponent create(Map<Either<Enchantment, Integer>, Integer> enchantments);

        StoredEnchantmentsComponent create(Map<Either<Enchantment, Integer>, Integer> enchantments, boolean showInTooltip);

    }

}
