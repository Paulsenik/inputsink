package de.paulsenik.inputsink.ui;

import de.paulsenik.inputsink.serivces.InputService;
import de.paulsenik.inputsink.serivces.UserPromptService;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.jpl.ui.PUIElement;
import de.paulsenik.jpl.ui.PUIList;
import de.paulsenik.jpl.ui.PUIText;
import de.paulsenik.jpl.ui.core.PUIFrame;
import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.FocusEvent.Cause;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class UI extends PUIFrame {

  private static String LOGO_PATH = "src/main/Inputsink-Colored.png";
  private static String ARDUINO_BUTTON_TEXT = "Arduino: ";
  private UserPromptService promptService;

  private PUIText arduinoButton;
  private PUIText addInputButton;
  private PUIList inputList;

  private int m = 10; // border-margin
  private int bSize = 50;

  public UI() {
    super("Inputsink", 1000, 800, false);
    promptService = new UserPromptService(this);

    initElements();
    for (PUIElement e : PUIElement.registeredElements) {
      e.doPaintOverOnPress(false);
      e.doPaintOverOnHover(false);
    }
    setUpdateElements(this::updateElements);

    try {
      initSystemTray();
    } catch (Exception e) {
      sendUserError("System tray error!");
      System.exit(0);
    }

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setVisible(true);
  }

  private void initElements() {
    arduinoButton = new PUIText(this, ARDUINO_BUTTON_TEXT + "---");
    arduinoButton.addActionListener((e) -> {
      if (InputService.instance.isSerialConnected()) {
        InputService.instance.disconnectSerial();
        arduinoButton.setText(ARDUINO_BUTTON_TEXT + "---");
        repaint();
      } else {
        String portname = UserPromptService.instance.getPortName();
        if (portname != null) {
          if (InputService.instance.connectSerial(portname)) {
            arduinoButton.setText(ARDUINO_BUTTON_TEXT + portname);
            repaint();
          } else {
            sendUserInfo("Connection to " + portname + " failed!");
          }
        }
      }
    });
    addInputButton = new PUIText(this, "+");
    addInputButton.addActionListener(puiElement -> {
      Trigger t = promptService.getTrigger();
      if (t != null) {
        InputService.instance.addTrigger(t);
        updateElements();
      }
    });
    inputList = new PUIList(this);
  }

  public void updateElements(int w, int h) {
    arduinoButton.setBounds(m, m, 300, bSize);
    inputList.setSliderWidth(bSize);
    int topBar = m * 2 + bSize;
    addInputButton.setBounds(w - m - bSize, m, bSize, bSize);
    inputList.setBounds(m, topBar, w - m * 2, h - topBar + m);
    updateInputList();
  }

  public void updateInputList() {
    inputList.clearElements();
    for (Trigger t : InputService.instance.trigger) {
      inputList.addElement(new InputMappingDisplay(this, t));
    }
  }

  private void initSystemTray() throws AWTException {
    Image icon = new ImageIcon(LOGO_PATH).getImage();
    setIconImage(icon);

    if (!SystemTray.isSupported()) {
      throw new AWTException("SystemTray is not supported");
    }

    final TrayIcon trayIcon = new TrayIcon(icon, "Inputsink");
    trayIcon.addActionListener(e -> toggleFocus());

    final PopupMenu popup = new PopupMenu();
    trayIcon.setPopupMenu(popup);

    // Create a pop-up menu components
    MenuItem exitItem = new MenuItem("Exit Inputsink");
    exitItem.setFont(new Font("Arial", Font.BOLD, 20));
    exitItem.addActionListener(e -> System.exit(0));
    popup.add(exitItem);

    trayIcon.setImageAutoSize(true);

    SystemTray.getSystemTray().add(trayIcon);
  }

  public void toggleFocus() {
    if (!isVisible()) {
      setVisible(true);
      requestFocus(Cause.ACTIVATION);
    } else {
      setVisible(false);
    }
  }
}
