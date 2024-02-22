package de.paulsenik.inputsink.serivces;

import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.inputsink.ui.UI;
import de.paulsenik.jpl.io.PDataStorage;
import de.paulsenik.jpl.io.PFolder;
import de.paulsenik.jpl.utils.PSystem;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class SaveService {

  public static final String SETTINGS_PATH =
      PSystem.getWorkingDirectory() + PSystem.getFileSeparator() + "conf"
          + PSystem.getFileSeparator() + "inputsink.conf";
  public static final String TRIGGER_FOLDER =
      PSystem.getWorkingDirectory() + PSystem.getFileSeparator() + "conf"
          + PSystem.getFileSeparator() + "trigger" + PSystem.getFileSeparator();

  public static SaveService instance;
  PDataStorage settings = new PDataStorage();

  public boolean visibility = true;

  public SaveService() {
    instance = this;
    readSaveFile();
    System.out.println("[SaveService] :: PWD=" + PSystem.getWorkingDirectory());
  }

  public boolean readSaveFile() {
    settings.read(SETTINGS_PATH);
    int triggerAmount = Integer.MAX_VALUE;
    try {
      String port = settings.getString("port");
      visibility = settings.getBoolean("vis");
      triggerAmount = settings.getInteger("trigger");
      if (port != null && !port.isBlank()) {
        InputService.instance.connectSerial(port, false);
      }
    } catch (IllegalArgumentException ignored) {
    }

    String[] files = PFolder.getFiles(TRIGGER_FOLDER, ".ser");
    if (files != null) {
      for (int i = 0; i < Math.min(files.length, triggerAmount); i++) {
        Object obj = deSerialize(files[i]);
        if (obj != null) {
          Trigger trigger = (Trigger) obj;
          InputService.instance.addTrigger(trigger, false);
        }
      }
    }
    return false;
  }

  public void save() {
    PFolder.createFolder(TRIGGER_FOLDER);

    List<Trigger> trigger = InputService.instance.getTriggerList();
    for (int i = 0; i < trigger.size(); i++) {
      if (!serializeObj(trigger.get(i), TRIGGER_FOLDER + i + "trigger.ser")) {
        System.out.println(
            "[SaveService] :: failed Serialization of " + trigger.get(i).displayName);
      }
    }

    settings.clear();
    settings.add("port", InputService.instance.getSerialPort());
    settings.add("trigger", InputService.instance.getTriggerList().size());
    if (UI.instance != null) {
      settings.add("vis", UI.instance.isVisible());
    }
    settings.save(SETTINGS_PATH);

    System.out.println("[SaveService] :: saved");
  }

  private boolean serializeObj(Object obj, String path) {
    try {
      FileOutputStream fos = new FileOutputStream(path);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(obj);
      oos.flush();
      oos.close();
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private Object deSerialize(String path) {
    try {
      FileInputStream fis = new FileInputStream(path);
      ObjectInputStream ois = new ObjectInputStream(fis);
      Object obj = (Trigger) ois.readObject();
      ois.close();
      return obj;
    } catch (Exception e) {
      return null;
    }
  }
}
