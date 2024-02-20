package de.paulsenik.inputsink.serivces;

import de.paulsenik.inputsink.trigger.MicroControllerTrigger;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.jpl.io.serial.PSerialConnection;
import de.paulsenik.jpl.io.serial.PSerialListener;
import java.util.ArrayList;
import java.util.List;

public class InputService {

  public List<Trigger> trigger = new ArrayList<>();

  PSerialConnection serialConnection;
  PSerialListener serialListener;

  public InputService(){
    serialListener = (s -> {
      for(Trigger t : trigger){
        if(t instanceof MicroControllerTrigger){
          ((MicroControllerTrigger)t).onInput(s);
        }
      }
    });
  }

  public void addTrigger(Trigger t){
    trigger.add(t);
  }

  public boolean connectSerial(String portName){
    serialConnection = new PSerialConnection(portName);
    serialConnection.addListener(serialListener);
    return serialConnection.connect();
  }

  public List<String> getSerialSelection(){
    return List.of(PSerialConnection.getSerialPorts());
  }

}
