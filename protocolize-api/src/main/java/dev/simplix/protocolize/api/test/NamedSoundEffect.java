package dev.simplix.protocolize.api.test;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolUtil.*;
import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@ToString
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public class NamedSoundEffect extends AbstractPacket {

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x29),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_12_2, 0x19),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x1A),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_14_4, 0x19),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x1A),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x19),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_1_16_4, 0x18),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_LATEST, 0x19)
    );

    private String sound;
    private int category;
    private double x, y, z;
    private float volume, pitch;

    @Override
    public void read(ByteBuf buf, PacketDirection direction, int protocolVersion) {
        sound = readString(buf);
        if (protocolVersion > MINECRAFT_1_8)
            category = readVarInt(buf);
        x = buf.readInt() / 8D;
        y = buf.readInt() / 8D;
        z = buf.readInt() / 8D;
        volume = buf.readFloat();
        if (protocolVersion < MINECRAFT_1_10) {
            pitch = buf.readUnsignedByte() / 63F;
        } else {
            pitch = buf.readFloat();
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection direction, int protocolVersion) {
        writeString(buf, sound);
        if (protocolVersion > MINECRAFT_1_8)
            writeVarInt(buf, category);
        buf.writeInt((int) (x * 8));
        buf.writeInt((int) (y * 8));
        buf.writeInt((int) (z * 8));
        buf.writeFloat(volume);
        if (protocolVersion < MINECRAFT_1_10) {
            buf.writeByte((byte) (pitch * 63) & 0xFF);
        } else {
            buf.writeFloat(pitch);
        }
    }

}
