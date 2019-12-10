package de.exceptionflug.protocolize.world.packet;

import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.world.Sound;
import de.exceptionflug.protocolize.world.SoundCategory;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class NamedSoundEffect extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = new HashMap<>();

    static {
        MAPPING.put(MINECRAFT_1_8, 0x29);
        MAPPING.put(MINECRAFT_1_9, 0x19);
        MAPPING.put(MINECRAFT_1_9_1, 0x19);
        MAPPING.put(MINECRAFT_1_9_2, 0x19);
        MAPPING.put(MINECRAFT_1_9_4, 0x19);
        MAPPING.put(MINECRAFT_1_10, 0x19);
        MAPPING.put(MINECRAFT_1_11, 0x19);
        MAPPING.put(MINECRAFT_1_11_2, 0x19);
        MAPPING.put(MINECRAFT_1_12, 0x19);
        MAPPING.put(MINECRAFT_1_12_1, 0x19);
        MAPPING.put(MINECRAFT_1_12_2, 0x19);
        MAPPING.put(MINECRAFT_1_13, 0x1A);
        MAPPING.put(MINECRAFT_1_13_1, 0x1A);
        MAPPING.put(MINECRAFT_1_13_2, 0x1A);
        MAPPING.put(MINECRAFT_1_14, 0x19);
        MAPPING.put(MINECRAFT_1_14_1, 0x19);
        MAPPING.put(MINECRAFT_1_14_2, 0x19);
        MAPPING.put(MINECRAFT_1_14_3, 0x19);
        MAPPING.put(MINECRAFT_1_14_4, 0x19);
        MAPPING.put(MINECRAFT_1_15, 0x1A);
    }

    private String sound;
    private Sound soundObject;
    private SoundCategory category;
    private double x, y, z;
    private float volume, pitch;

    public String getSound() {
        return sound;
    }

    public Sound getSoundObject() {
        return soundObject;
    }


    public void setSound(final String sound) {
        this.sound = sound;
    }

    public void setSound(final Sound soundObject) {
        this.soundObject = soundObject;
    }

    public SoundCategory getCategory() {
        return category;
    }

    public void setCategory(final SoundCategory category) {
        this.category = category;
    }

    public double getX() {
        return x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(final double z) {
        this.z = z;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(final float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }

    @Override
    public void read(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
        sound = readString(buf);
        soundObject = Sound.getSound(sound, protocolVersion);
        if(protocolVersion > MINECRAFT_1_8)
            category = SoundCategory.values()[readVarInt(buf)];
        x = buf.readInt() / 8D;
        y = buf.readInt() / 8D;
        z = buf.readInt() / 8D;
        volume = buf.readFloat();
        if(protocolVersion < MINECRAFT_1_10) {
            pitch = buf.readUnsignedByte() / 63F;
        } else {
            pitch = buf.readFloat();
        }
    }

    @Override
    public void write(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
        if(soundObject != null) {
            final String soundName = soundObject.getSoundName(protocolVersion);
            if(soundName == null) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "[Protocolize] Cannot play sound "+soundObject.name()+" on protocol "+protocolVersion);
                return;
            }
            writeString(soundName, buf);
        } else {
            writeString(sound, buf);
        }
        if(protocolVersion > MINECRAFT_1_8)
            writeVarInt(category.ordinal(), buf);
        buf.writeInt((int) (x * 8));
        buf.writeInt((int) (y * 8));
        buf.writeInt((int) (z * 8));
        buf.writeFloat(volume);
        if(protocolVersion < MINECRAFT_1_10) {
            buf.writeByte((byte) (pitch * 63) & 0xFF);
        } else {
            buf.writeFloat(pitch);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final NamedSoundEffect that = (NamedSoundEffect) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0 &&
                Float.compare(that.volume, volume) == 0 &&
                Float.compare(that.pitch, pitch) == 0 &&
                sound == that.sound &&
                category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sound, category, x, y, z, volume, pitch);
    }

    @Override
    public String toString() {
        return "NamedSoundEffect{" +
                "sound=" + sound +
                ", category=" + category +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", volume=" + volume +
                ", pitch=" + pitch +
                '}';
    }
}
