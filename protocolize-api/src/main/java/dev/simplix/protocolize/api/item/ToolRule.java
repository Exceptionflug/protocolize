package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.api.util.Either;
import dev.simplix.protocolize.data.Block;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolRule {
    Either<String, List<Block>> blockSet;
    Float speed;
    Boolean correctToolForDrops;
}
