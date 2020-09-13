package de.exceptionflug.protocolize.world;

public enum SoundCategory {

  MASTER("master"),
  MUSIC("music"),
  RECORDS("record"),
  WEATHER("weather"),
  BLOCKS("block"),
  HOSTILE("hostile"),
  NEUTRAL("neutral"),
  PLAYERS("player"),
  AMBIENT("ambient"),
  VOICE("voice");

  private final String categoryName;

  SoundCategory(final String categoryName) {
    this.categoryName = categoryName;
  }

  public static SoundCategory getCategory(final String readString) {
    for (final SoundCategory cat : values()) {
      if (cat.categoryName.equals(readString))
        return cat;
    }
    return null;
  }

  public String getCategoryName() {
    return categoryName;
  }
}
