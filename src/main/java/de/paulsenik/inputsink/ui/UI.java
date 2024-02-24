package de.paulsenik.inputsink.ui;

import de.paulsenik.inputsink.Inputsink;
import de.paulsenik.inputsink.serivces.InputService;
import de.paulsenik.inputsink.serivces.SaveService;
import de.paulsenik.inputsink.serivces.UserPromptService;
import de.paulsenik.inputsink.trigger.Trigger;
import de.paulsenik.jpl.ui.PUIElement;
import de.paulsenik.jpl.ui.PUIList;
import de.paulsenik.jpl.ui.PUIText;
import de.paulsenik.jpl.ui.core.PUICanvas;
import de.paulsenik.jpl.ui.core.PUIFrame;
import java.awt.AWTException;
import java.awt.Color;
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

  public static UI instance;

  private static String LOGO_PATH = "Inputsink-Colored.png";
  private static String ARDUINO_BUTTON_TEXT = "Arduino: ";
  private UserPromptService promptService;

  private PUIText arduinoButton;
  private PUIText addInputButton;
  private PUIList inputList;

  private int m = 20; // border-margin
  private int bSize = 90;
  int topBar = 110;

  public UI() {
    super("Inputsink", 1000, 800, false);
    instance = this;
    promptService = UserPromptService.instance;

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
    setVisible(SaveService.instance.visibility);
  }

  private void initElements() {
    String port = InputService.instance.getSerialPort();

    new PUICanvas(this, (g, x, y, w, h) -> {
      g.setColor(Color.white);
      g.setFont(new Font("Consolas", Font.BOLD, 10));
      g.drawString(Inputsink.VERSION, x, h);

      g.drawString("In> " + InputService.instance.getLastInput(), x + 50, h);
    });

    arduinoButton = new PUIText(this,
        ARDUINO_BUTTON_TEXT + ((port == null || !InputService.instance.isSerialConnected()) ? "---"
            : port));
    arduinoButton.addActionListener((e) -> {
      if (InputService.instance.isSerialConnected()) {
        InputService.instance.disconnectSerial();
        arduinoButton.setText(ARDUINO_BUTTON_TEXT + "---");
        repaint();
      } else {
        String portname = UserPromptService.instance.getPortName(this);
        if (portname != null) {
          if (InputService.instance.connectSerial(portname, true)) {
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
      Trigger t = promptService.getTrigger(this);
      if (t != null) {
        InputService.instance.addTrigger(t);
        updateElements();
      }
    });
    inputList = new PUIList(this);
  }

  public void updateElements(int w, int h) {
    bSize = Math.min(h / 14, w / 10);
    topBar = m * 2 + bSize;

    if (h > w && w != 0) {
      inputList.setShowedElements(h / w * 4);
    } else {
      inputList.setShowedElements(3);
    }

    arduinoButton.setBounds(m, m, w - bSize - m * 3, bSize);
    inputList.setSliderWidth(bSize);
    addInputButton.setBounds(w - m - bSize, m, bSize, bSize);
    inputList.setBounds(m, topBar, w - m * 2, h - topBar - m * 4);
    updateInputList();
  }

  public void updateInputList() {
    inputList.clearElements();
    for (Trigger t : InputService.instance.getTriggerList()) {
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

  public void updatePortDisplay(String port) {
    arduinoButton.setText(ARDUINO_BUTTON_TEXT + (port == null ? " --- " : port));
    repaint();
  }
}
