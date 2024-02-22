package de.paulsenik.inputsink.serivces;

import de.paulsenik.inputsink.trigger.MicroControllerTrigger;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.inputsink.ui.UI;
import de.paulsenik.jpl.io.serial.PSerialConnection;
import de.paulsenik.jpl.io.serial.PSerialListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputService {

  public static InputService instance;

  private List<Trigger> trigger = new CopyOnWriteArrayList<>();
  private PSerialConnection serialConnection;
  private PSerialListener serialListener;
  private Runnable disconnectHook;
  private String lastInput = "";

  public InputService() {
    instance = this;
    serialListener = (s -> {
      if (!lastInput.equals(s)) {
        lastInput = s;
        UI.instance.repaint();
      }
      for (Trigger t : trigger) {
        if (t instanceof MicroControllerTrigger) {
          ((MicroControllerTrigger) t).onInput(s);
        }
      }
    });
  }

  public void setSerialDisconnectHook(Runnable r) {
    disconnectHook = r;
  }

  public void addTrigger(Trigger t, boolean save) {
    trigger.add(t);
    if (save) {
      SaveService.instance.save();
    }
  }

  public void addTrigger(Trigger t) {
    addTrigger(t, true);
  }

  public void removeTrigger(Trigger t) {
    trigger.remove(t);
    SaveService.instance.save();
  }

  public List<Trigger> getTriggerList() {
    return trigger;
  }

  public boolean connectSerial(String portName, boolean save) {
    if (serialConnection != null) {
      serialConnection.disconnect();
    }
    if (save) {
      SaveService.instance.save();
    }

    serialConnection = new PSerialConnection(portName);
    serialConnection.setDisconnectEvent(disconnectHook);
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

  public String getSerialPort() {
    return serialConnection == null ? null : serialConnection.getPortName();
  }

  public String getLastInput() {
    return lastInput;
  }

}
