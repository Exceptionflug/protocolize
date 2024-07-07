package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.data.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {

    private AttributeType type;
    private UUID uuid;
    private String name;
    private double value;
    private Operation operation;
    private EquipmentSlot slot;

    /* These may change in the future */
    public enum Operation {
        ADD_VALUE,
        ADD_MULTIPLIED_BASE,
        ADD_MULTIPLIED_TOTAL
    }

    /* These may change in the future */
    public enum EquipmentSlot {
        ANY,
        MAIN_HAND,
        OFF_HAND,
        HAND,
        FEET,
        LEGS,
        CHEST,
        HEAD,
        ARMOR,
        BODY;
    }

}
