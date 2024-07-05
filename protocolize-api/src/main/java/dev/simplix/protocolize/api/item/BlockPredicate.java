package dev.simplix.protocolize.api.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.querz.nbt.tag.CompoundTag;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockPredicate {

    BlockSet blockSet;
    List<Property> properties;
    CompoundTag nbtData;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Property {
        String name;
        String exactValue;
        String minValue;
        String maxValue;
    }

}
