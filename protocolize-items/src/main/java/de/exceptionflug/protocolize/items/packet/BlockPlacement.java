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

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class BlockPlacement extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = Maps.newHashMap();

    static {
        MAPPING.put(MINECRAFT_1_8, 0x08);
        MAPPING.put(MINECRAFT_1_9, 0x1C);
        MAPPING.put(MINECRAFT_1_9_1, 0x1C);
        MAPPING.put(MINECRAFT_1_9_2, 0x1C);
        MAPPING.put(MINECRAFT_1_9_3, 0x1C);
        MAPPING.put(MINECRAFT_1_10, 0x1C);
        MAPPING.put(MINECRAFT_1_11, 0x1C);
        MAPPING.put(MINECRAFT_1_12, 0x1F);
        MAPPING.put(MINECRAFT_1_12_2, 0x1F);
        MAPPING.put(MINECRAFT_1_13, 0x29);
        MAPPING.put(MINECRAFT_1_13_1, 0x29);
        MAPPING.put(MINECRAFT_1_13_2, 0x29);
        MAPPING.put(MINECRAFT_1_14, 0x2C);
    }

    private BlockPosition position;
    private BlockFace face;
    private Hand hand;
    private float hitVecX, hitVecY, hitVecZ;
    private boolean insideBlock;

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
        if(protocolVersion < MINECRAFT_1_14) {
            position = BlockPosition.read(buf, protocolVersion);
            if(protocolVersion > MINECRAFT_1_8) {
                face = BlockFace.getBlockFace(readVarInt(buf));
                hand = Hand.getHandByID(readVarInt(buf));
                if(protocolVersion < MINECRAFT_1_11) {
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
        } else {
            hand = Hand.getHandByID(readVarInt(buf));
            position = BlockPosition.read(buf, protocolVersion);
            face = BlockFace.getBlockFace(readVarInt(buf));
            hitVecX = buf.readFloat();
            hitVecY = buf.readFloat();
            hitVecZ = buf.readFloat();
            insideBlock = buf.readBoolean();
        }
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        Preconditions.checkNotNull(position, "The position cannot be null!");
        Preconditions.checkNotNull(face, "The face cannot be null!");
        Preconditions.checkNotNull(hand, "The hand cannot be null!");
        if(protocolVersion < MINECRAFT_1_14) {
            position.write(buf, protocolVersion);
            if(protocolVersion > MINECRAFT_1_8) {
                writeVarInt(face.getProtocolId(), buf);
                writeVarInt(hand.getProtocolId(), buf);
                if(protocolVersion < MINECRAFT_1_11) {
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
        } else {
            writeVarInt(hand.getProtocolId(), buf);
            position.write(buf, protocolVersion);
            writeVarInt(face.getProtocolId(), buf);
            buf.writeFloat(hitVecX);
            buf.writeFloat(hitVecY);
            buf.writeFloat(hitVecZ);
            buf.writeBoolean(insideBlock);
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

    public boolean isInsideBlock() {
        return insideBlock;
    }

    public void setInsideBlock(final boolean insideBlock) {
        this.insideBlock = insideBlock;
    }

    @Deprecated
    public ItemStack getItemStack() {
        return stack;
    }

    @Deprecated
    public void setItemStack(final ItemStack stack) {
        this.stack = stack;
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
