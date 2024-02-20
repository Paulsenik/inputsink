package de.paulsenik.inputsink.trigger;

public class MicroControllerTrigger extends Trigger {

  String enterTriggerValue;
  String exitTriggerValue;

  public MicroControllerTrigger(String displayName, String triggerValue) {
    this(displayName, triggerValue, triggerValue);
  }

  public MicroControllerTrigger(String displayName, String enterTriggerValue,
      String exitTriggerValue) {
    super(displayName);
    this.enterTriggerValue = enterTriggerValue;
    this.exitTriggerValue = exitTriggerValue;
  }

  public void onInput(String input) {
    if (input.contains(enterTriggerValue)) {
      triggerEnter();
    } else if (input.contains(exitTriggerValue)) {
      triggerExit();
    }
  }

  @Override
  public String getEnterDisplayName() {
    return enterTriggerValue;
  }

  @Override
  public String getExitDisplayName() {
    return exitTriggerValue;
  }

}
