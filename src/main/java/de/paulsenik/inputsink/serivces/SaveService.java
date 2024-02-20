package de.paulsenik.inputsink.serivces;

import de.paulsenik.inputsink.action.Action;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.jpl.io.PDataStorage;
import de.paulsenik.jpl.io.PFolder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class SaveService {

  public static final String SETTINGS_PATH = "conf/inputsink.conf";
  public static final String TRIGGER_FOLDER = "conf/trigger/";

  public static SaveService instance;
  PDataStorage settings = new PDataStorage();

  public SaveService() {
    instance = this;
    readSaveFile();
  }

  public boolean readSaveFile() {
    settings.read(SETTINGS_PATH);
    InputService.instance.connectSerial(settings.getString("port"));

    for (String s : PFolder.getFiles(TRIGGER_FOLDER, ".ser")) {
      Object obj = deSerialize(s);
      if (obj != null) {
        Trigger trigger = (Trigger) obj;
        System.out.println("deserialized: "
            + trigger.displayName + " " + trigger.getEnterDisplayName() + " "
            + trigger.getExitDisplayName());
        InputService.instance.addTrigger(trigger);
        for (Action a : trigger.getActions()) {
          System.out.println("deserialized: " + a.getDisplayValue());
        }
      }
    }
    return false;
  }

  public void save() {
    PFolder.createFolder(TRIGGER_FOLDER);

    List<Trigger> trigger = InputService.instance.trigger;
    for (int i = 0; i < trigger.size(); i++) {
      serializeObj(trigger.get(i), TRIGGER_FOLDER + i + "trigger.ser");
    }

    settings.clear();
    settings.add("port", InputService.instance.getSerialPort());
    settings.save(SETTINGS_PATH);

    System.out.println("saved");
  }

  private void serializeObj(Object obj, String path) {
    try {
      FileOutputStream fos = new FileOutputStream(path);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(obj);
      oos.flush();
      oos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Object deSerialize(String path) {
    try {
      FileInputStream fis = new FileInputStream(path);
      ObjectInputStream ois = new ObjectInputStream(fis);
      Object obj = (Trigger) ois.readObject();
      ois.close();
      return obj;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }
}
