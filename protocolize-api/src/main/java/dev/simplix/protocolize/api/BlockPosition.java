package dev.simplix.protocolize.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Date: 25.08.2021
 *
 * @author Exceptionflug
 */
@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class BlockPosition {

    private int x;
    private int y;
    private int z;

}
