package de.exceptionflug.protocolize.inventory;

public enum InventoryType {

    CONTAINER("minecraft:container", -1),
    CHEST("minecraft:chest", -1),
    CRAFTING_TABLE("minecraft:crafting_table", 10),
    FURNACE("minecraft:furnace", 3),
    DISPENSER("minecraft:dispenser", 9),
    ENCHANTMENT_TABLE("minecraft:enchanting_table", 2),
    BREWING_STAND("minecraft:brewing_stand", 5),
    VILLAGER("minecraft:villager", 3),
    BEACON("minecraft:beacon", 1),
    ANVIL("minecraft:anvil", 3),
    HOPPER("minecraft:hopper", 5),
    DROPPER("minecraft:dropper", 9),
    SHULKER_BOX("minecraft:shulker_box", 27),
    HORSE("EntityHorse", -1),

    PLAYER("Player", 45);

    private final String protocolId;
    private final int typicalSize;

    InventoryType(final String protocolId, final int typicalSize) {
        this.protocolId = protocolId;
        this.typicalSize = typicalSize;
    }

    public String getProtocolId() {
        return protocolId;
    }

    public static InventoryType getInventoryType(final String id) {
        for(final InventoryType type : values()) {
            if(type.protocolId.equals(id))
                return type;
        }
        return null;
    }

    public int getTypicalSize() {
        return typicalSize;
    }
}
