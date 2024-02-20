package de.paulsenik.inputsink;

import de.paulsenik.inputsink.action.KeyAction;
import de.paulsenik.inputsink.serivces.InputService;
import de.paulsenik.inputsink.trigger.MicroControllerTrigger;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.inputsink.ui.UI;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Hello world!
 */
public class App {

  public static void main(String[] args) throws RuntimeException, AWTException {

    InputService inputService = new InputService();

    System.out.println(inputService.getSerialSelection());
    System.out.println(inputService.connectSerial(inputService.getSerialSelection().get(0)));

    Trigger t = new MicroControllerTrigger("test123", "am");

    t.bind(new KeyAction("testasdf",
        List.of(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_P)));

    inputService.addTrigger(t);

    new UI();
  }
}
