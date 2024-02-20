package de.paulsenik.inputsink.serivces;

import de.paulsenik.inputsink.trigger.MicroControllerTrigger;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.jpl.io.serial.PSerialConnection;
import de.paulsenik.jpl.io.serial.PSerialListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputService {

  public static InputService instance;

  public List<Trigger> trigger = new CopyOnWriteArrayList<>();

  PSerialConnection serialConnection;
  PSerialListener serialListener;

  public InputService() {
    serialListener = (s -> {
      for (Trigger t : trigger) {
        if (t instanceof MicroControllerTrigger) {
          ((MicroControllerTrigger) t).onInput(s);
        }
      }
    });
    instance = this;
  }

  public void addTrigger(Trigger t) {
    trigger.add(t);
  }

  public boolean connectSerial(String portName) {
    if (serialConnection != null) {
      serialConnection.disconnect();
    }

    serialConnection = new PSerialConnection(portName);
    serialConnection.addListener(serialListener);
    return serialConnection.connect();
  }

  public boolean isSerialConnected() {
    if (serialConnection == null) {
      return false;
    }
    return serialConnection.isConnected();
  }

  public boolean disconnectSerial() {
    return serialConnection.disconnect();
  }

}
