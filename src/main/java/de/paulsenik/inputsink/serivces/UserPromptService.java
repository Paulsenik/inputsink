package de.paulsenik.inputsink.serivces;

import de.paulsenik.inputsink.action.Action;
import de.paulsenik.inputsink.action.KeyAction;
import de.paulsenik.inputsink.trigger.MicroControllerTrigger;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.jpl.io.serial.PSerialConnection;
import de.paulsenik.jpl.ui.core.PUIFrame;
import java.awt.AWTException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserPromptService {

  public static UserPromptService instance;


  public UserPromptService() {
    instance = this;
  }

  public String getPortName(PUIFrame frame) {

    ArrayList<String> ports = new ArrayList<>();
    ports.add("");
    for (String s : PSerialConnection.getSerialPorts()) {
      ports.add(s);
    }

    int portIndex = frame.getUserSelection("Select Controller-Port", ports);

    if (portIndex != 0) {
      return ports.get(portIndex);
    }
    return null;
  }

  public Action getAction(PUIFrame frame) {
    List<Integer> keys = new ArrayList<>();
    Map<String, Integer> selection = KeyAction.getPossibleValues();
    selection.put("", Integer.MAX_VALUE); // escape

    List<String> sorted = new ArrayList<>(selection.keySet());
    Collections.sort(sorted);

    ArrayList<String> selectionWithEscape = new ArrayList<>();
    selectionWithEscape.add("");
    selectionWithEscape.addAll(sorted);

    boolean again = true;
    do {
      int sel = frame.getUserSelection("Select a Key", selectionWithEscape);
      if (sel == 0) {
        again = false;
      } else {
        keys.add(selection.get(selectionWithEscape.get(sel)));
      }
    } while (again);

    if (keys.isEmpty()) {
      return null;
    }

    try {
      return new KeyAction("KeyAction", keys);
    } catch (AWTException e) {
      frame.sendUserWarning("An Error occured while creating a Key-Action!");
      e.printStackTrace();
      return null;
    }
  }

  public Trigger getTrigger(PUIFrame frame) {
    String name = frame.getUserInput("Trigger-Name", "trigger");
    if (isInvalid(name)) {
      return null;
    }
    String enterTriggerValue = frame.getUserInput("Trigger Enter:", "enter-value");
    if (isInvalid(enterTriggerValue)) {
      return null;
    }
    String exitTriggerValue = frame.getUserInput("Trigger Exit:", enterTriggerValue);
    if (isInvalid(exitTriggerValue)) {
      enterTriggerValue = exitTriggerValue;
    }

    boolean confirm = frame.getUserConfirm(
        "Name: " + name + "\n- Enter: " + enterTriggerValue + "\n- Exit: " + exitTriggerValue,
        "Confirm new Trigger:");

    if (confirm) {

      return new MicroControllerTrigger(name, enterTriggerValue, exitTriggerValue);
    }
    return null;
  }

  private boolean isInvalid(String input) {
    return input == null || input.isBlank() || input.isEmpty();
  }

}
