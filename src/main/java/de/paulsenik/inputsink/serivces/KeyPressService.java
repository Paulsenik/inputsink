package de.paulsenik.inputsink.serivces;

import java.awt.AWTException;
import java.awt.Robot;

public class KeyPressService {

  public static KeyPressService instance;
  private final Robot keyPressAgent = new Robot();

  public KeyPressService() throws AWTException {
    instance = this;
  }

  public synchronized void keyPress(int code) {
    keyPressAgent.keyPress(code);
  }

  public synchronized void keyRelease(int code) {
    keyPressAgent.keyRelease(code);
  }

}
