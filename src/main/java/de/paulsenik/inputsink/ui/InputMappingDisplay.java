package de.paulsenik.inputsink.ui;

import de.paulsenik.inputsink.action.Action;
import de.paulsenik.inputsink.serivces.InputService;
import de.paulsenik.inputsink.serivces.SaveService;
import de.paulsenik.inputsink.serivces.UserPromptService;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.jpl.ui.PUIElement;
import de.paulsenik.jpl.ui.PUIList;
import de.paulsenik.jpl.ui.PUIText;
import de.paulsenik.jpl.ui.core.PUIFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class InputMappingDisplay extends PUIElement {

  private PUIList actionList;
  private PUIText triggerName;
  private PUIText addActionButton;
  private PUIText deleteInputMapping;
  private Trigger trigger;

  private int p = 20; // padding
  private int b = 50; // button-size

  public InputMappingDisplay(UI f, Trigger t) {
    super(f, 10);
    trigger = t;
    setDraw(this::draw);

    initElements(f, t);
    updateActions();
  }

  private void initElements(UI f, Trigger t) {
    triggerName = new PUIText(f, t.displayName, 1);
    triggerName.doPaintOverOnHover(false);
    triggerName.doPaintOverOnPress(false);
    triggerName.addActionListener(puiElement -> {
      Trigger newTrigger = UserPromptService.instance.getTrigger(f);
      if (newTrigger != null) {
        for (Action a : trigger.getActions()) {
          newTrigger.bind(a);
        }
        InputService.instance.removeTrigger(trigger);
        InputService.instance.addTrigger(newTrigger);
        System.out.println("updated trigger");
        f.updateElements();
      }
    });

    deleteInputMapping = new PUIText(f, "-", 1);
    deleteInputMapping.doPaintOverOnHover(false);
    deleteInputMapping.doPaintOverOnPress(false);
    deleteInputMapping.addActionListener(puiElement -> {
      if (f.getUserConfirm("Delete " + t.displayName + " ?", "Trigger")) {
        InputService.instance.removeTrigger(trigger);
        f.updateElements();
      }
    });

    actionList = new PUIList(f, 1);
    actionList.setShowedElements(4);
    actionList.doPaintOverOnHover(false);
    actionList.doPaintOverOnPress(false);
    actionList.showSlider(false);

    addActionButton = new PUIText(f, "+", 1);
    addActionButton.doPaintOverOnHover(false);
    addActionButton.doPaintOverOnPress(false);
    addActionButton.addActionListener(puiElement -> {
      Action a = UserPromptService.instance.getAction(f);

      if (a != null) {
        trigger.bind(a);
        updateActions();
        frame.updateElements();
      }
    });
  }

  private void updateActions() {
    actionList.clearElements();
    for (Action a : trigger.getActions()) {
      PUIText e = new PUIText(frame, a.getDisplayValue());
      e.doPaintOverOnHover(false);
      e.doPaintOverOnPress(false);
      e.addActionListener(puiElement -> {
        if (frame.getUserConfirm("Delete " + e.getText() + "?", "Delete Binding")) {
          trigger.unbind(a);
        }
        frame.updateElements();
      });
      actionList.addElement(e);
    }
  }

  @Override
  public synchronized void setBounds(int x, int y, int w, int h) {
    super.setBounds(x, y, w, h);

    b = h/5;
    p = b/2;

    triggerName.setBounds(x + p, y + p, w / 2 - p * 2, b);
    deleteInputMapping.setBounds(x + p, y + h - p - b, b, b);
    addActionButton.setBounds(x + w / 2 - p - b, y + h - p - b, b, b);
    actionList.setBounds(x + w / 2 + p, y + p, w / 2 - p * 2, h - p * 2);
  }

  public void draw(Graphics2D g, int x, int y, int w, int h) {
    g.setColor(Color.white);
    g.drawRoundRect(x + 5, y + 5, w - 10, h - 10, 10, 10);

    g.setFont(new Font("Arial", Font.PLAIN, b / 2));

    g.drawString("On Enter: " + trigger.getEnterDisplayName(), x + p, (int) (y + p * 2 + b * 1.5));
    g.drawString("On Exit: " + trigger.getExitDisplayName(), x + p, y + p * 2 + b * 2);
  }

  @Override
  public void release() {
    super.release();

    frame.remove(triggerName);
    triggerName.release();

    frame.remove(actionList);
    actionList.release();

    frame.remove(addActionButton);
    addActionButton.release();

    frame.remove(deleteInputMapping);
    deleteInputMapping.release();
  }
}
