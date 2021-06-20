package de.exceptionflug.protocolize.world.packet;

import de.exceptionflug.protocolize.api.BlockPosition;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;
import static java.nio.charset.StandardCharsets.*;

public class SignUpdate extends AbstractPacket {

  public static final Map<Integer, Integer> MAPPING_SERVERBOUND = new HashMap<>();
  public static final Map<Integer, Integer> MAPPING_CLIENTBOUND = new HashMap<>();

  static {
    MAPPING_SERVERBOUND.put(MINECRAFT_1_7_2, 0x12);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_7_6, 0x12);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_8, 0x12);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9, 0x19);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9_1, 0x19);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9_2, 0x19);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9_4, 0x19);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_10, 0x19);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_11, 0x19);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_11_2, 0x19);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_12, 0x1C);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_12_1, 0x1C);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_12_2, 0x1C);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_13, 0x26);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_13_1, 0x26);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_13_2, 0x26);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14, 0x29);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_1, 0x29);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_2, 0x29);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_3, 0x29);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_4, 0x29);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_15, 0x29);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_15_1, 0x29);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_15_2, 0x29);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16, 0x2A);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_1, 0x2A);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_2, 0x2B);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_3, 0x2B);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_4, 0x2B);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_17, 0x2B);

    MAPPING_CLIENTBOUND.put(MINECRAFT_1_7_2, 0x33);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_7_6, 0x33);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_8, 0x33);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9, 0x46);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_1, 0x46);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_2, 0x46);
  }



  private BlockPosition position;
  private String[] lines;

  @Override
  public void read(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
    if (direction == ProtocolConstants.Direction.TO_SERVER) {
      position = BlockPosition.read(buf, protocolVersion);
      if (protocolVersion > MINECRAFT_1_8) {
        lines = new String[4];
        lines[0] = readString(buf);
        lines[1] = readString(buf);
        lines[2] = readString(buf);
        lines[3] = readString(buf);
      } else {
        readLineStrings(buf, protocolVersion < MINECRAFT_1_8);
      }
    } else {
      position = BlockPosition.read(buf, protocolVersion);
      readLineStrings(buf, protocolVersion < MINECRAFT_1_8);
    }
    BufferUtil.finishBuffer(this, buf, direction, protocolVersion);
  }

  private void readLineStrings(ByteBuf buf, boolean legacy) {
    lines = new String[4];
    String line1 = readString(buf);
    if (line1.isEmpty() || line1.equals("null")) {
      lines[0] = "";
    } else {
      lines[0] = legacy ? line1 : ComponentSerializer.parse(line1)[0].toLegacyText();
    }
    String line2 = readString(buf);
    if (line2.isEmpty() || line2.equals("null")) {
      lines[1] = "";
    } else {
      lines[1] = legacy ? line2 : ComponentSerializer.parse(line2)[0].toLegacyText();
    }
    String line3 = readString(buf);
    if (line3.isEmpty() || line3.equals("null")) {
      lines[2] = "";
    } else {
      lines[2] = legacy ? line3 : ComponentSerializer.parse(line3)[0].toLegacyText();
    }
    String line4 = readString(buf);
    if (line4.isEmpty() || line4.equals("null")) {
      lines[3] = "";
    } else {
      lines[3] = legacy ? line4 : ComponentSerializer.parse(line4)[0].toLegacyText();
    }
  }

  @Override
  public void write(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
    if (direction == ProtocolConstants.Direction.TO_SERVER) {
      position.write(buf, protocolVersion);
      if (protocolVersion > MINECRAFT_1_8) {
        writeString(lines[0], buf);
        writeString(lines[1], buf);
        writeString(lines[2], buf);
        writeString(lines[3], buf);
      } else {
        writeString(ComponentSerializer.toString(new TextComponent(lines[0])), buf);
        writeString(ComponentSerializer.toString(new TextComponent(lines[1])), buf);
        writeString(ComponentSerializer.toString(new TextComponent(lines[2])), buf);
        writeString(ComponentSerializer.toString(new TextComponent(lines[3])), buf);
      }
    } else {
      writeString(ComponentSerializer.toString(new TextComponent(lines[0])), buf);
      writeString(ComponentSerializer.toString(new TextComponent(lines[1])), buf);
      writeString(ComponentSerializer.toString(new TextComponent(lines[2])), buf);
      writeString(ComponentSerializer.toString(new TextComponent(lines[3])), buf);
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final SignUpdate that = (SignUpdate) o;
    return Objects.equals(position, that.position) &&
            Arrays.equals(lines, that.lines);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(position);
    result = 31 * result + Arrays.hashCode(lines);
    return result;
  }

  @Override
  public String toString() {
    return "SignUpdate{" +
            "position=" + position +
            ", lines=" + Arrays.toString(lines) +
            '}';
  }

  /**
   * Gives the lines on the sign created sign.
   * @return An array of Strings which contain the lines
   */
  public String[] getLines(){
    return lines;
  }

  /**
   * Allows to write to the sign packet.
   * @param l The full array, with the new strings.
   */
  public void writeLines(String[] l)throws IllegalArgumentException{
    lines = l;
    if(l.length==4){
      for(int i = 0; i <4; i++){
        if(l[i].getBytes(UTF_8).length <= 384 ){
          this.lines[i] = l[i];
        }
        else{
          throw new IllegalArgumentException("Line " + i+ "is too long");
        }
      }
    }
  }

  /**
   * Allows to write to a certain line on the sign
   * @param line The content to write
   * @param where On which line this should be. Counting starts at zero
   * @throws IllegalArgumentException If the line or the integer is not valid
   */
  public void writeLines(String line, int where) throws IllegalArgumentException{
    if(where> 3) throw new IllegalArgumentException("A sign has 4 lines, cant therefore write to line" + where);
    else if(line.getBytes(UTF_8).length > 384) throw new IllegalArgumentException("The string is to long");
    else{
      lines[where]=line;
    }
  }


  /**
   * Gives the Blockposition of the altered sign. (Note that changing the position does not seem to make any sense.
   * Therefore there is no setter for BlockPosition
   *
   * @return The BlockPosition of the sign
   */
  public BlockPosition getPosition() {
    return position;
  }
}
