package de.exceptionflug.protocolize.api;

public enum ClickType {

  LEFT_CLICK(0, 0),
  RIGHT_CLICK(0, 1),
  SHIFT_LEFT_CLICK(1, 0),
  SHIFT_RIGHT_CLICK(1, 1),
  NUMBER_BUTTON_1(2, 0),
  NUMBER_BUTTON_2(2, 1),
  NUMBER_BUTTON_3(2, 2),
  NUMBER_BUTTON_4(2, 3),
  NUMBER_BUTTON_5(2, 4),
  NUMBER_BUTTON_6(2, 5),
  NUMBER_BUTTON_7(2, 6),
  NUMBER_BUTTON_8(2, 7),
  NUMBER_BUTTON_9(2, 8),
  CREATIVE_MIDDLE_CLICK(3, 2),
  DROP(4, 0),
  DROP_ALL(4, 1),
  LEFT_CLICK_OUTSIDE_INVENTORY_HOLDING_NOTHING(4, 0),
  RIGHT_CLICK_OUTSIDE_INVENTORY_HOLDING_NOTHING(4, 1),
  DRAG_START_LEFT(5, 0),
  DRAG_START_RIGHT(5, 4),
  DRAG_START_CREATIVE_MIDDLE(5, 8),
  DRAG_ADD_SLOT_LEFT(5, 1),
  DRAG_ADD_SLOT_RIGHT(5, 5),
  DRAG_ADD_SLOT_CREATIVE_MIDDLE(5, 9),
  DRAG_STOP_LEFT(5, 2),
  DRAG_STOP_RIGHT(5, 6),
  DRAG_STOP_CREATIVE_MIDDLE(6, 10),
  DOUBLE_CLICK(6, 0);

  private final int mode, button;

  ClickType(final int mode, final int button) {
    this.mode = mode;
    this.button = button;
  }

  public static ClickType getType(final int mode, final int button) {
    for (final ClickType clickType : values()) {
      if (clickType.getButton() == button && clickType.getMode() == mode)
        return clickType;
    }
    return null;
  }

  public int getButton() {
    return button;
  }

  public int getMode() {
    return mode;
  }

}
