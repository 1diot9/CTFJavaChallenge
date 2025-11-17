package org.h2.tools;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Locale;
import org.h2.engine.Constants;
import org.h2.jdbc.JdbcConnection;
import org.h2.util.Utils;
import org.springframework.web.servlet.tags.BindTag;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/GUIConsole.class */
public class GUIConsole extends Console implements ActionListener, MouseListener, WindowListener {
    private long lastOpenNs;
    private boolean trayIconUsed;
    private Font font;
    private Frame statusFrame;
    private TextField urlText;
    private Button startBrowser;
    private Frame createFrame;
    private TextField pathField;
    private TextField userField;
    private TextField passwordField;
    private TextField passwordConfirmationField;
    private Button createButton;
    private TextArea errorArea;
    private Object tray;
    private Object trayIcon;

    @Override // org.h2.tools.Console
    void show() {
        if (!GraphicsEnvironment.isHeadless()) {
            loadFont();
            try {
                if (!createTrayIcon()) {
                    showStatusWindow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Image loadImage(String str) {
        try {
            byte[] resource = Utils.getResource(str);
            if (resource == null) {
                return null;
            }
            return Toolkit.getDefaultToolkit().createImage(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // org.h2.tools.Console, org.h2.server.ShutdownHandler
    public void shutdown() {
        super.shutdown();
        if (this.statusFrame != null) {
            this.statusFrame.dispose();
            this.statusFrame = null;
        }
        if (this.trayIconUsed) {
            try {
                Utils.callMethod(this.tray, "remove", this.trayIcon);
            } catch (Exception e) {
            } finally {
                this.trayIcon = null;
                this.tray = null;
                this.trayIconUsed = false;
            }
            System.gc();
            if (System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("mac")) {
                for (Thread thread : Thread.getAllStackTraces().keySet()) {
                    if (thread.getName().startsWith("AWT-")) {
                        thread.interrupt();
                    }
                }
            }
            Thread.currentThread().interrupt();
        }
    }

    private void loadFont() {
        if (this.isWindows) {
            this.font = new Font("Dialog", 0, 11);
        } else {
            this.font = new Font("Dialog", 0, 12);
        }
    }

    private boolean createTrayIcon() {
        String str;
        try {
            if (!((Boolean) Utils.callStaticMethod("java.awt.SystemTray.isSupported", new Object[0])).booleanValue()) {
                return false;
            }
            PopupMenu popupMenu = new PopupMenu();
            MenuItem menuItem = new MenuItem("H2 Console");
            menuItem.setActionCommand("console");
            menuItem.addActionListener(this);
            menuItem.setFont(this.font);
            popupMenu.add(menuItem);
            MenuItem menuItem2 = new MenuItem("Create a new database...");
            menuItem2.setActionCommand("showCreate");
            menuItem2.addActionListener(this);
            menuItem2.setFont(this.font);
            popupMenu.add(menuItem2);
            MenuItem menuItem3 = new MenuItem("Status");
            menuItem3.setActionCommand(BindTag.STATUS_VARIABLE_NAME);
            menuItem3.addActionListener(this);
            menuItem3.setFont(this.font);
            popupMenu.add(menuItem3);
            MenuItem menuItem4 = new MenuItem("Exit");
            menuItem4.setFont(this.font);
            menuItem4.setActionCommand("exit");
            menuItem4.addActionListener(this);
            popupMenu.add(menuItem4);
            this.tray = Utils.callStaticMethod("java.awt.SystemTray.getSystemTray", new Object[0]);
            Dimension dimension = (Dimension) Utils.callMethod(this.tray, "getTrayIconSize", new Object[0]);
            if (dimension.width >= 24 && dimension.height >= 24) {
                str = "/org/h2/res/h2-24.png";
            } else if (dimension.width >= 22 && dimension.height >= 22) {
                str = "/org/h2/res/h2-64-t.png";
            } else {
                str = "/org/h2/res/h2.png";
            }
            this.trayIcon = Utils.newInstance("java.awt.TrayIcon", loadImage(str), "H2 Database Engine", popupMenu);
            Utils.callMethod(this.trayIcon, "addMouseListener", this);
            Utils.callMethod(this.tray, BeanUtil.PREFIX_ADDER, this.trayIcon);
            this.trayIconUsed = true;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void showStatusWindow() {
        if (this.statusFrame != null) {
            return;
        }
        this.statusFrame = new Frame("H2 Console");
        this.statusFrame.addWindowListener(this);
        Image loadImage = loadImage("/org/h2/res/h2.png");
        if (loadImage != null) {
            this.statusFrame.setIconImage(loadImage);
        }
        this.statusFrame.setResizable(false);
        this.statusFrame.setBackground(SystemColor.control);
        GridBagLayout gridBagLayout = new GridBagLayout();
        this.statusFrame.setLayout(gridBagLayout);
        Panel panel = new Panel(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints.gridy = 0;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridwidth = 2;
        gridBagConstraints2.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.anchor = 13;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.weightx = 1.0d;
        gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints3.gridx = 1;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 0;
        Label label = new Label("H2 Console URL:", 0);
        label.setFont(this.font);
        panel.add(label, gridBagConstraints4);
        this.urlText = new TextField();
        this.urlText.setEditable(false);
        this.urlText.setFont(this.font);
        this.urlText.setText(this.web.getURL());
        if (this.isWindows) {
            this.urlText.setFocusable(false);
        }
        panel.add(this.urlText, gridBagConstraints3);
        this.startBrowser = new Button("Start Browser");
        this.startBrowser.setFocusable(false);
        this.startBrowser.setActionCommand("console");
        this.startBrowser.addActionListener(this);
        this.startBrowser.setFont(this.font);
        panel.add(this.startBrowser, gridBagConstraints2);
        this.statusFrame.add(panel, gridBagConstraints);
        this.statusFrame.setSize(300, 120);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.statusFrame.setLocation((screenSize.width - 300) / 2, (screenSize.height - 120) / 2);
        try {
            this.statusFrame.setVisible(true);
        } catch (Throwable th) {
        }
        try {
            this.statusFrame.setAlwaysOnTop(true);
            this.statusFrame.setAlwaysOnTop(false);
        } catch (Throwable th2) {
        }
    }

    private void startBrowser() {
        if (this.web != null) {
            String url = this.web.getURL();
            if (this.urlText != null) {
                this.urlText.setText(url);
            }
            long currentNanoTime = Utils.currentNanoTime();
            if (this.lastOpenNs == 0 || currentNanoTime - this.lastOpenNs > 100000000) {
                this.lastOpenNs = currentNanoTime;
                openBrowser(url);
            }
        }
    }

    private void showCreateDatabase() {
        if (this.createFrame != null) {
            return;
        }
        this.createFrame = new Frame("H2 Console");
        this.createFrame.addWindowListener(this);
        Image loadImage = loadImage("/org/h2/res/h2.png");
        if (loadImage != null) {
            this.createFrame.setIconImage(loadImage);
        }
        this.createFrame.setResizable(false);
        this.createFrame.setBackground(SystemColor.control);
        GridBagLayout gridBagLayout = new GridBagLayout();
        this.createFrame.setLayout(gridBagLayout);
        Panel panel = new Panel(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        Label label = new Label("Database path:", 0);
        label.setFont(this.font);
        panel.add(label, gridBagConstraints);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.insets = new Insets(10, 5, 0, 0);
        gridBagConstraints2.gridx = 1;
        this.pathField = new TextField();
        this.pathField.setFont(this.font);
        this.pathField.setText("./test");
        panel.add(this.pathField, gridBagConstraints2);
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 1;
        Label label2 = new Label("Username:", 0);
        label2.setFont(this.font);
        panel.add(label2, gridBagConstraints3);
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = 2;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 1.0d;
        gridBagConstraints4.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints4.gridx = 1;
        this.userField = new TextField();
        this.userField.setFont(this.font);
        this.userField.setText("sa");
        panel.add(this.userField, gridBagConstraints4);
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.fill = 2;
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.gridy = 2;
        Label label3 = new Label("Password:", 0);
        label3.setFont(this.font);
        panel.add(label3, gridBagConstraints5);
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.fill = 2;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.weightx = 1.0d;
        gridBagConstraints6.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints6.gridx = 1;
        this.passwordField = new TextField();
        this.passwordField.setFont(this.font);
        this.passwordField.setEchoChar('*');
        panel.add(this.passwordField, gridBagConstraints6);
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.fill = 2;
        gridBagConstraints7.gridx = 0;
        gridBagConstraints7.gridy = 3;
        Label label4 = new Label("Password confirmation:", 0);
        label4.setFont(this.font);
        panel.add(label4, gridBagConstraints7);
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.fill = 2;
        gridBagConstraints8.gridy = 3;
        gridBagConstraints8.weightx = 1.0d;
        gridBagConstraints8.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints8.gridx = 1;
        this.passwordConfirmationField = new TextField();
        this.passwordConfirmationField.setFont(this.font);
        this.passwordConfirmationField.setEchoChar('*');
        panel.add(this.passwordConfirmationField, gridBagConstraints8);
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.gridx = 0;
        gridBagConstraints9.gridwidth = 2;
        gridBagConstraints9.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints9.gridy = 4;
        gridBagConstraints9.anchor = 13;
        this.createButton = new Button("Create");
        this.createButton.setFocusable(false);
        this.createButton.setActionCommand("create");
        this.createButton.addActionListener(this);
        this.createButton.setFont(this.font);
        panel.add(this.createButton, gridBagConstraints9);
        GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        gridBagConstraints10.fill = 2;
        gridBagConstraints10.gridy = 5;
        gridBagConstraints10.weightx = 1.0d;
        gridBagConstraints10.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints10.gridx = 0;
        gridBagConstraints10.gridwidth = 2;
        this.errorArea = new TextArea();
        this.errorArea.setFont(this.font);
        this.errorArea.setEditable(false);
        panel.add(this.errorArea, gridBagConstraints10);
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.weightx = 1.0d;
        gridBagConstraints11.weighty = 1.0d;
        gridBagConstraints11.fill = 1;
        gridBagConstraints11.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints11.gridy = 0;
        this.createFrame.add(panel, gridBagConstraints11);
        this.createFrame.setSize(400, 400);
        this.createFrame.pack();
        this.createFrame.setLocationRelativeTo((Component) null);
        try {
            this.createFrame.setVisible(true);
        } catch (Throwable th) {
        }
        try {
            this.createFrame.setAlwaysOnTop(true);
            this.createFrame.setAlwaysOnTop(false);
        } catch (Throwable th2) {
        }
    }

    private void createDatabase() {
        if (this.web == null || this.createFrame == null) {
            return;
        }
        String text = this.pathField.getText();
        String text2 = this.userField.getText();
        String text3 = this.passwordField.getText();
        if (!text3.equals(this.passwordConfirmationField.getText())) {
            this.errorArea.setForeground(Color.RED);
            this.errorArea.setText("Passwords don't match");
            return;
        }
        if (text3.isEmpty()) {
            this.errorArea.setForeground(Color.RED);
            this.errorArea.setText("Specify a password");
            return;
        }
        String str = Constants.START_URL + text;
        try {
            new JdbcConnection(str, null, text2, text3, false).close();
            this.errorArea.setForeground(new Color(0, 153, 0));
            this.errorArea.setText("Database was created successfully.\n\nJDBC URL for H2 Console:\n" + str);
        } catch (Exception e) {
            this.errorArea.setForeground(Color.RED);
            this.errorArea.setText(e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        if ("exit".equals(actionCommand)) {
            shutdown();
            return;
        }
        if ("console".equals(actionCommand)) {
            startBrowser();
            return;
        }
        if ("showCreate".equals(actionCommand)) {
            showCreateDatabase();
            return;
        }
        if (BindTag.STATUS_VARIABLE_NAME.equals(actionCommand)) {
            showStatusWindow();
        } else if (this.startBrowser == actionEvent.getSource()) {
            startBrowser();
        } else if (this.createButton == actionEvent.getSource()) {
            createDatabase();
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 1) {
            startBrowser();
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void windowClosing(WindowEvent windowEvent) {
        if (this.trayIconUsed) {
            Frame window = windowEvent.getWindow();
            if (window == this.statusFrame) {
                this.statusFrame.dispose();
                this.statusFrame = null;
                return;
            } else {
                if (window == this.createFrame) {
                    this.createFrame.dispose();
                    this.createFrame = null;
                    return;
                }
                return;
            }
        }
        shutdown();
    }

    public void windowActivated(WindowEvent windowEvent) {
    }

    public void windowClosed(WindowEvent windowEvent) {
    }

    public void windowDeactivated(WindowEvent windowEvent) {
    }

    public void windowDeiconified(WindowEvent windowEvent) {
    }

    public void windowIconified(WindowEvent windowEvent) {
    }

    public void windowOpened(WindowEvent windowEvent) {
    }
}
