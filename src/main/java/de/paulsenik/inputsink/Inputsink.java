package de.paulsenik.inputsink;

import de.paulsenik.inputsink.action.KeyAction;
import de.paulsenik.inputsink.serivces.InputService;
import de.paulsenik.inputsink.trigger.MicroControllerTrigger;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.inputsink.ui.UI;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.util.List;

public class Inputsink {

  public static void main(String[] args) throws RuntimeException, AWTException {

    InputService inputService = new InputService();

    Trigger t = new MicroControllerTrigger("test123", "am");

    t.bind(new KeyAction("testasdf",
        List.of(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_P)));

    inputService.addTrigger(t);

    new UI();
  }
}
