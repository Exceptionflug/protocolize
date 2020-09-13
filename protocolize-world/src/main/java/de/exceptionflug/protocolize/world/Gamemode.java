package de.exceptionflug.protocolize.world;

public enum Gamemode {

  SURVIVAL(0), CREATIVE(1), ADVENTURE(2), SPECTATOR(3);

  private final int id;

  Gamemode(final int id) {
    this.id = id;
  }

  public static Gamemode getByID(final int id) {
    for (final Gamemode gamemode : values()) {
      if (gamemode.id == id)
        return gamemode;
    }
    return null;
  }

  public int getId() {
    return id;
  }

}
