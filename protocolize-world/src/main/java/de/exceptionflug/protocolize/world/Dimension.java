package de.exceptionflug.protocolize.world;

@Deprecated
public enum Dimension {

  NETHER(-1),
  OVERWORLD(0),
  END(1);

  private final int id;

  Dimension(int id) {
    this.id = id;
  }

  public static Dimension getByID(final int id) {
    for (final Dimension dimension : values()) {
      if (dimension.getId() == id)
        return dimension;
    }
    return null;
  }

  public int getId() {
    return id;
  }

}
