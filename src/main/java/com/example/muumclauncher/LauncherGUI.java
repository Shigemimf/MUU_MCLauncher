package main.java.com.example.muumclauncher;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LauncherGUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> versionComboBox;
    private JButton launchButton;
    private Settings settings;

    public LauncherGUI() {
        settings = new Settings();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("MUU_MCLauncher");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 50, 100, 30);
        frame.add(usernameLabel);

        usernameField = new JTextField(settings.getSetting("username"));
        usernameField.setBounds(150, 50, 200, 30);
        frame.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 100, 30);
        frame.add(passwordLabel);

        passwordField = new JPasswordField(settings.getSetting("password"));
        passwordField.setBounds(150, 100, 200, 30);
        frame.add(passwordField);

        JLabel versionLabel = new JLabel("Version:");
        versionLabel.setBounds(50, 150, 100, 30);
        frame.add(versionLabel);

        versionComboBox = new JComboBox<>(new String[]{"1.16.5", "1.17.1", "1.18"});
        versionComboBox.setSelectedItem(settings.getSetting("version"));
        versionComboBox.setBounds(150, 150, 200, 30);
        frame.add(versionComboBox);

        launchButton = new JButton("Launch");
        launchButton.setBounds(150, 200, 100, 30);
        frame.add(launchButton);

        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.setSetting("username", usernameField.getText());
                settings.setSetting("password", new String(passwordField.getPassword()));
                settings.setSetting("version", (String) versionComboBox.getSelectedItem());
                settings.saveSettings();

                Launcher launcher = new Launcher();
                launcher.start();
            }
        });

        frame.setVisible(true);
    }
}