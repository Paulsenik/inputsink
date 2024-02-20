package de.paulsenik.inputsink.action;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyAction extends Action {

  private boolean holdTriggerUntilExit = false;

  private final List<Integer> keyCodes;
  private final Robot keyPressAgent;

  public KeyAction(String displayName, List<Integer> keyCodes) throws AWTException {
    super(displayName);
    this.keyCodes = keyCodes;
    keyPressAgent = new Robot();
  }

  @Override
  public void onTriggerEnter(){
    for (int code : keyCodes) {
      keyPressAgent.keyPress(code);
    }
    if (!holdTriggerUntilExit) {
      for (int code : keyCodes) {
        keyPressAgent.keyRelease(code);
      }
    }
  }

  @Override
  public void onTriggerExit() {
    if (holdTriggerUntilExit) {
      for (int code : keyCodes) {
        keyPressAgent.keyRelease(code);
      }
    }
  }

  public static Map<String, Integer> getPossibleValues() {
    Map<String, Integer> s = new HashMap<>();

    for (KeyEvent e : KeyEvent.class.getEnumConstants()) {
      s.put(KeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode());
    }

    return s;
  }
}
