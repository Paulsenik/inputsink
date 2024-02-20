package de.paulsenik.inputsink.action;

import java.io.Serializable;

/**
 * A list of Actions is linked to an Input/Trigger
 */
public abstract class Action implements Serializable {

  boolean triggerOnEnter = true;
  boolean triggerOnExit;

  protected String displayName;

  public Action(String displayName) {
    this.displayName = displayName;
  }

  public abstract void onTriggerEnter();

  public abstract void onTriggerExit();

  public abstract String getDisplayValue();

}
