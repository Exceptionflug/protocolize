package dev.simplix.protocolize.api.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockSet {
    private Integer type;
    private String identifier;
    private List<Integer> blockIds;
}
