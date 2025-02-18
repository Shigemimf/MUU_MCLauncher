package main.java.com.example.muumclauncher;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Launcher {
    private final Authenticator authenticator = new Authenticator();
    private final VersionManager versionManager = new VersionManager();
    private final Downloader downloader = new Downloader();
    private final Settings settings = new Settings();

    public void start() {
        try {
            String username = settings.getSetting("username");
            String password = settings.getSetting("password");
            String version = settings.getSetting("version");

            if (authenticator.authenticate(username, password)) {
                versionManager.selectVersion(version);
                downloader.downloadFiles();
                launchGame(username, version);
            } else {
                JOptionPane.showMessageDialog(null, "Authentication failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void launchGame(String username, String version) {
        System.out.println("Launching game...");
        String javaPath = System.getProperty("java.home") + "/bin/java";
        String gameDir = System.getProperty("user.home") + "/AppData/Roaming/.minecraft"; // Windows
        String mainClass = "net.minecraft.client.main.Main";
        String authUUID = "uuid";
        String authAccessToken = "accessToken";
        String userType = "mojang";
        String assetsDir = gameDir + "/assets";

        ArrayList<String> command = new ArrayList<>();
        command.add(javaPath);
        command.add("-Djava.library.path=" + gameDir + "/versions/" + version + "/natives");
        command.add("-cp");

        StringBuilder classPath = new StringBuilder(gameDir + "/versions/" + version + "/" + version + ".jar");
        File librariesDir = new File(gameDir + "/libraries");
        if (librariesDir.exists() && librariesDir.isDirectory()) {
            addLibrariesToClasspath(librariesDir, classPath);
        } else {
            System.err.println("Library directory not found: " + librariesDir.getAbsolutePath());
            return;
        }
        command.add(classPath.toString());

        command.add(mainClass);
        command.add("--username");
        command.add(username);
        command.add("--version");
        command.add(version);
        command.add("--gameDir");
        command.add(gameDir);
        command.add("--assetsDir");
        command.add(assetsDir);
        command.add("--assetIndex");
        command.add(version);
        command.add("--uuid");
        command.add(authUUID);
        command.add("--accessToken");
        command.add(authAccessToken);
        command.add("--userType");
        command.add(userType);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO();

        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to launch game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLibrariesToClasspath(File dir, StringBuilder classPath) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addLibrariesToClasspath(file, classPath);
                } else if (file.isFile() && file.getName().endsWith(".jar")) {
                    classPath.append(File.pathSeparator).append(file.getAbsolutePath());
                }
            }
        } else {
            System.err.println("Failed to list files in directory: " + dir.getAbsolutePath());
        }
    }
}