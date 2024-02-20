package de.paulsenik.inputsink.ui;

import de.paulsenik.inputsink.serivces.InputService;
import de.paulsenik.jpl.ui.PUIElement;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class UI extends PUIFrame {

  private static String LOGO_PATH = "src/main/Inputsink-Colored.png";
  private InputService inputService;

  private PUIText arduinoButton;

  private int mainMargin = 10;

  public UI(InputService inputService) {
    super("Inputsink", 1000, 800, false);
    this.inputService = inputService;

    initElements();

    try {
      initSystemTray();
    } catch (Exception e) {
      sendUserError("System tray error!");
      System.exit(0);
    }

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setVisible(true);
  }

  private void initElements(){
    arduinoButton = new PUIText(this,"Arduino: ---");
    arduinoButton.setBounds(mainMargin, mainMargin, 300,60);
  }

  private void initSystemTray()
      throws AWTException {
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
