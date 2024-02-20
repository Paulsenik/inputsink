package de.paulsenik.inputsink;

import de.paulsenik.inputsink.serivces.InputService;
import de.paulsenik.inputsink.serivces.KeyPressService;
import de.paulsenik.inputsink.serivces.SaveService;
import de.paulsenik.inputsink.serivces.UserPromptService;
import de.paulsenik.inputsink.ui.UI;
import java.awt.AWTException;

public class Inputsink {

  public static void main(String[] args) throws AWTException {
    new InputService();
    new KeyPressService();
    
    new SaveService();

    new UserPromptService();
    new UI();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> SaveService.instance.save()));
  }
}
