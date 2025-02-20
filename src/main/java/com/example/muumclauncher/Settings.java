package main.java.com.example.muumclauncher;

import java.io.*;
import java.util.Properties;

public class Settings {
    private static final String SETTINGS_FILE = System.getProperty("user.home") + "/.muumclauncher/settings.properties";
    private Properties properties;

    public Settings() {
        properties = new Properties();
        loadSettings();
    }

    public void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try (InputStream input = new FileInputStream(file)) {
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Settings file not found. Creating a new one.");
            saveSettings();
        }
    }

    public void saveSettings() {
        File file = new File(SETTINGS_FILE);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (OutputStream output = new FileOutputStream(file)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSetting(String key) {
        return properties.getProperty(key);
    }

    public void setSetting(String key, String value) {
        properties.setProperty(key, value);
    }
}