package de.exceptionflug.protocolize.items.packet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.BlockFace;
import de.exceptionflug.protocolize.api.BlockPosition;
import de.exceptionflug.protocolize.api.Hand;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

public class BlockPlacement extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = Maps.newHashMap();

    static {
        MAPPING.put(47, 0x08);
        MAPPING.put(107, 0x1C);
        MAPPING.put(108, 0x1C);
        MAPPING.put(109, 0x1C);
        MAPPING.put(110, 0x1C);
        MAPPING.put(210, 0x1C);
        MAPPING.put(315, 0x1C);
        MAPPING.put(335, 0x1F);
        MAPPING.put(340, 0x1F);
        MAPPING.put(393, 0x29);
        MAPPING.put(401, 0x29);
    }

    private BlockPosition position;
    private BlockFace face;
    private Hand hand;
    private float hitVecX, hitVecY, hitVecZ;

    @Deprecated
    private ItemStack stack = ItemStack.NO_DATA;

    public BlockPlacement(final BlockPosition position, final BlockFace face, final Hand hand, final float hitVecX, final float hitVecY, final float hitVecZ) {
        this.position = position;
        this.face = face;
        this.hand = hand;
        this.hitVecX = hitVecX;
        this.hitVecY = hitVecY;
        this.hitVecZ = hitVecZ;
    }

    public BlockPlacement() {}

    public BlockPosition getPosition() {
        return position;
    }

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        position = BlockPosition.read(buf);
        if(protocolVersion > 47) {
            face = BlockFace.getBlockFace(readVarInt(buf));
            hand = Hand.getHandByID(readVarInt(buf));
            if(protocolVersion < 300) {
                hitVecX = buf.readByte() / 15F;
                hitVecY = buf.readByte() / 15F;
                hitVecZ = buf.readByte() / 15F;
            } else {
                hitVecX = buf.readFloat();
                hitVecY = buf.readFloat();
                hitVecZ = buf.readFloat();
            }
        } else {
            face = BlockFace.getBlockFace(buf.readByte());
            hand = Hand.MAIN_HAND;
            stack = ItemStack.read(buf, protocolVersion);
            hitVecX = buf.readByte() / 15F;
            hitVecY = buf.readByte() / 15F;
            hitVecZ = buf.readByte() / 15F;
        }
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        Preconditions.checkNotNull(position, "The position cannot be null!");
        Preconditions.checkNotNull(face, "The face cannot be null!");
        Preconditions.checkNotNull(hand, "The hand cannot be null!");
        position.write(buf);
        if(protocolVersion > 47) {
            writeVarInt(face.getProtocolId(), buf);
            writeVarInt(hand.getProtocolId(), buf);
            if(protocolVersion < 300) {
                buf.writeByte((int) (hitVecX * 15));
                buf.writeByte((int) (hitVecY * 15));
                buf.writeByte((int) (hitVecZ * 15));
            } else {
                buf.writeFloat(hitVecX);
                buf.writeFloat(hitVecY);
                buf.writeFloat(hitVecZ);
            }
        } else {
            buf.writeByte(face.getProtocolId());
            stack.write(buf, protocolVersion);
            buf.writeByte((int) (hitVecX * 15));
            buf.writeByte((int) (hitVecY * 15));
            buf.writeByte((int) (hitVecZ * 15));
        }
    }

    public void setPosition(final BlockPosition position) {
        this.position = position;
    }

    public BlockFace getFace() {
        return face;
    }

    public void setFace(final BlockFace face) {
        this.face = face;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(final Hand hand) {
        this.hand = hand;
    }

    public float getHitVecX() {
        return hitVecX;
    }

    public void setHitVecX(final float hitVecX) {
        this.hitVecX = hitVecX;
    }

    public float getHitVecY() {
        return hitVecY;
    }

    public void setHitVecY(final float hitVecY) {
        this.hitVecY = hitVecY;
    }

    public float getHitVecZ() {
        return hitVecZ;
    }

    public void setHitVecZ(final float hitVecZ) {
        this.hitVecZ = hitVecZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPlacement that = (BlockPlacement) o;
        return Float.compare(that.hitVecX, hitVecX) == 0 &&
                Float.compare(that.hitVecY, hitVecY) == 0 &&
                Float.compare(that.hitVecZ, hitVecZ) == 0 &&
                Objects.equals(position, that.position) &&
                face == that.face &&
                hand == that.hand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, face, hand, hitVecX, hitVecY, hitVecZ);
    }

    @Override
    public String toString() {
        return "BlockPlacement{" +
                "position=" + position +
                ", face=" + face +
                ", hand=" + hand +
                ", hitVecX=" + hitVecX +
                ", hitVecY=" + hitVecY +
                ", hitVecZ=" + hitVecZ +
                '}';
    }
}
