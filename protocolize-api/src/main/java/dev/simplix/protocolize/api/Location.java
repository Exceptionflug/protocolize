package dev.simplix.protocolize.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Date: 29.08.2021
 *
 * @author Exceptionflug
 */
@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class Location {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

}
