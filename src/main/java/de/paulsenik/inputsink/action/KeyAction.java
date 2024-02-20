package de.paulsenik.inputsink.action;

import de.paulsenik.inputsink.serivces.KeyPressService;
import de.paulsenik.inputsink.serivces.SaveService;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyAction extends Action implements Serializable {

  private boolean holdTriggerUntilExit = true;
  private final List<Integer> keyCodes;

  public KeyAction(String displayName, List<Integer> keyCodes) throws AWTException {
    super(displayName);
    this.keyCodes = keyCodes;
  }

  @Override
  public void onTriggerEnter() {
    for (int code : keyCodes) {
      KeyPressService.instance.keyPress(code);
    }
    if (!holdTriggerUntilExit) {
      for (int code : keyCodes) {
        KeyPressService.instance.keyRelease(code);
      }
    }
  }

  @Override
  public void onTriggerExit() {
    if (holdTriggerUntilExit) {
      for (int code : keyCodes) {
        KeyPressService.instance.keyRelease(code);
      }
    }
  }

  @Override
  public String getDisplayValue() {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < keyCodes.size(); i++) {
      Integer key = keyCodes.get(i);
      b.append(KeyEvent.getKeyText(key));
      if (i != keyCodes.size() - 1) {
        b.append(" + ");
      }
    }
    return b.toString();
  }

  public static Map<String, Integer> getPossibleValues() {
    Map<String, Integer> s = new HashMap<>();

    // Iterate over all the public static fields of the KeyEvent class
    for (java.lang.reflect.Field field : KeyEvent.class.getFields()) {
      if (field.getType().equals(int.class)) {
        try {
          int keyCode = (int) field.get(null);
          s.put(KeyEvent.getKeyText(keyCode), keyCode);
        } catch (IllegalAccessException e) {
          // Handle the exception, or log it
          e.printStackTrace();
        }
      }
    }

    return s;
  }
}
