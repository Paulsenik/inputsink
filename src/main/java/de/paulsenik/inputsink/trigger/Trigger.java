package de.paulsenik.inputsink.trigger;

import de.paulsenik.inputsink.action.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Trigger {

  public List<Action> actions = new CopyOnWriteArrayList<>();

  public String displayName;

  public Trigger(String displayName){
    this.displayName = displayName;
  }

  public void bind(Action a){
    actions.add(a);
  }

  public void unbind(Action a){
    actions.remove(a);
  }

  public void triggerEnter(){
    for(Action a : actions){
      a.onTriggerEnter();
    }
  }

  public void triggerExit(){
    for(Action a : actions){
      a.onTriggerExit();
    }
  }

  public abstract String getEnterDisplayName();
  public abstract String getExitDisplayName();

}
