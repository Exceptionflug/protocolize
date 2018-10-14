package de.exceptionflug.protocolize.world.packet;

import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.world.Difficulty;
import de.exceptionflug.protocolize.world.Dimension;
import de.exceptionflug.protocolize.world.Gamemode;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class JoinGame extends AbstractPacket {

    public static Map<Integer, Integer> MAPPING = new HashMap<>();

    static {
        MAPPING.put(MINECRAFT_1_8, 0x01);
        MAPPING.put(MINECRAFT_1_9, 0x23);
        MAPPING.put(MINECRAFT_1_9_1, 0x23);
        MAPPING.put(MINECRAFT_1_9_2, 0x23);
        MAPPING.put(MINECRAFT_1_9_3, 0x23);
        MAPPING.put(MINECRAFT_1_10, 0x23);
        MAPPING.put(MINECRAFT_1_11, 0x23);
        MAPPING.put(MINECRAFT_1_11_1, 0x23);
        MAPPING.put(MINECRAFT_1_12, 0x23);
        MAPPING.put(MINECRAFT_1_12_1, 0x23);
        MAPPING.put(MINECRAFT_1_12_2, 0x23);
        MAPPING.put(MINECRAFT_1_13, 0x25);
        MAPPING.put(MINECRAFT_1_13_1, 0x25);
    }

    private int entityId;
    private Gamemode gamemode;
    private boolean hardcore;
    private Difficulty difficulty;
    private Dimension dimension;
    private int maxPlayers;
    private String levelType;
    private boolean reducedDebugInfo;

    public JoinGame(final int entityId, final Gamemode gamemode, final boolean hardcore, final Difficulty difficulty, final Dimension dimension, final int maxPlayers, final String levelType, final boolean reducedDebugInfo) {
        this.entityId = entityId;
        this.gamemode = gamemode;
        this.hardcore = hardcore;
        this.difficulty = difficulty;
        this.dimension = dimension;
        this.maxPlayers = maxPlayers;
        this.levelType = levelType;
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public JoinGame() {

    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(final int entityId) {
        this.entityId = entityId;
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public void setGamemode(final Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public void setHardcore(final boolean hardcore) {
        this.hardcore = hardcore;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(final Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(final Dimension dimension) {
        this.dimension = dimension;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(final String levelType) {
        this.levelType = levelType;
    }

    public boolean isReducedDebugInfo() {
        return reducedDebugInfo;
    }

    public void setReducedDebugInfo(final boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        entityId = buf.readInt();
        int modeAndHardcore = buf.readUnsignedByte();
        hardcore = (modeAndHardcore & 8) == 8;
        modeAndHardcore = modeAndHardcore & -9;
        gamemode = Gamemode.values()[modeAndHardcore];
        if(protocolVersion < MINECRAFT_1_9_1)
            dimension = Dimension.getByID(buf.readByte());
        else
            dimension = Dimension.getByID(buf.readInt());
        difficulty = Difficulty.values()[buf.readUnsignedByte()];
        maxPlayers = buf.readUnsignedByte();
        levelType = readString(buf);
        reducedDebugInfo = buf.readBoolean();
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        buf.writeInt(entityId);
        int modeAndHardcore = gamemode.ordinal();
        if(hardcore) {
            modeAndHardcore |= 8;
        }
        buf.writeByte(modeAndHardcore);
        if(protocolVersion < MINECRAFT_1_9_1)
            buf.writeByte(dimension.getId());
        else
            buf.writeInt(dimension.getId());
        buf.writeByte(difficulty.ordinal());
        buf.writeByte(maxPlayers);
        writeString(levelType, buf);
        buf.writeBoolean(reducedDebugInfo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinGame joinGame = (JoinGame) o;
        return entityId == joinGame.entityId &&
                hardcore == joinGame.hardcore &&
                maxPlayers == joinGame.maxPlayers &&
                reducedDebugInfo == joinGame.reducedDebugInfo &&
                gamemode == joinGame.gamemode &&
                difficulty == joinGame.difficulty &&
                dimension == joinGame.dimension &&
                Objects.equals(levelType, joinGame.levelType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, gamemode, hardcore, difficulty, dimension, maxPlayers, levelType, reducedDebugInfo);
    }

    @Override
    public String toString() {
        return "JoinGame{" +
                "entityId=" + entityId +
                ", gamemode=" + gamemode +
                ", hardcore=" + hardcore +
                ", difficulty=" + difficulty +
                ", dimension=" + dimension +
                ", maxPlayers=" + maxPlayers +
                ", levelType='" + levelType + '\'' +
                ", reducedDebugInfo=" + reducedDebugInfo +
                '}';
    }
}
