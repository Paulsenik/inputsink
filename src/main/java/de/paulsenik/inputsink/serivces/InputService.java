package de.paulsenik.inputsink.serivces;

import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import de.paulsenik.inputsink.trigger.MicroControllerTrigger;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.inputsink.ui.UI;
import de.paulsenik.jpl.io.serial.PSerialConnection;
import de.paulsenik.jpl.io.serial.PSerialListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputService {

  public static InputService instance;
  public static final int MAX_RECONNECT_TRIES = 5;
  public static final int RECONNECT_DELAY = 1000;

  private List<Trigger> trigger = new CopyOnWriteArrayList<>();
  private PSerialConnection serialConnection;
  private PSerialListener serialListener;
  private String lastInput = "";
  private int reconnectCount = 0;
  private String lastPort = "";

  public InputService() {
    instance = this;
    serialListener = (s -> {
      if (!lastInput.equals(s)) {
        lastInput = s;
        if (UI.instance != null) {
          UI.instance.repaint();
        }
      }
      for (Trigger t : trigger) {
        if (t instanceof MicroControllerTrigger) {
          ((MicroControllerTrigger) t).onInput(s);
        }
      }
    });
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

    lastPort = portName;
    try {
      serialConnection = new PSerialConnection(portName);
      serialConnection.setDisconnectEvent(() -> {
        System.out.println("[InputService] :: Unexpected disconnect on " + portName + "!");
        disconnected();
        UI.instance.updatePortDisplay(null);
      });
      serialConnection.addListener(serialListener);

      if (serialConnection.connect()) {
        System.out.println(
            "[InputService] :: Device connected to " + serialConnection.getPortName());
        return true;
      }
      return false;

    } catch (SerialPortInvalidPortException e) {
      return false;
    }
  }

  public boolean isSerialConnected() {
    if (serialConnection == null) {
      return false;
    }
    return serialConnection.isConnected();
  }

  public boolean disconnectSerial() {
    System.out.println("[InputService] :: Disconnected " + serialConnection.getPortName() + "!");
    return serialConnection.disconnect();
  }

  public String getSerialPort() {
    return serialConnection == null ? null : serialConnection.getPortName();
  }

  public String getLastInput() {
    return lastInput;
  }

  /**
   * Event-Function is called when
   */
  private void disconnected() {
    Thread t = new Thread(() -> {
      try {
        for (reconnectCount = 0; !isSerialConnected() && reconnectCount < MAX_RECONNECT_TRIES;
            reconnectCount++) {
          System.out.println(
              "[InputService] :: reconnecting " + reconnectCount + "/" + MAX_RECONNECT_TRIES);

          connectSerial(lastPort, true);

          Thread.sleep(RECONNECT_DELAY);
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      if (!isSerialConnected()) {
        System.out.println("[InputService] :: reconnecting failed");
        UI.instance.updatePortDisplay(null);
      } else {
        System.out.println("[InputService] :: reconnected successfully");
        UI.instance.updatePortDisplay(lastPort);
      }
    });
    t.start();
  }

}
