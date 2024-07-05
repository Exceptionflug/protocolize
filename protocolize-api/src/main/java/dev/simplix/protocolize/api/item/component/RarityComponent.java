package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface RarityComponent extends StructuredComponent {

    Rarity getRarity();

    void setRarity(Rarity rarity);

    static RarityComponent create(Rarity rarity) {
        return Protocolize.getService(Factory.class).create(rarity);
    }

    enum Rarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC
    }

    interface Factory {

        RarityComponent create(Rarity rarity);

    }


}
