package de.paulsenik.inputsink.trigger;

import de.paulsenik.inputsink.action.Action;
import java.util.ArrayList;
import java.util.List;

public abstract class Trigger {

  private List<Action> actions = new ArrayList<>();

  String displayName;

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

}
