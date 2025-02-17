package main.java.com.example.muumclauncher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Launcher {
    private final Authenticator authenticator = new Authenticator();
    private final VersionManager versionManager = new VersionManager();
    private final Downloader downloader = new Downloader();

    public void start() {
        try {
            // Implement launcher logic here
            System.out.println("Starting launcher...");
            if (authenticator.authenticate("username", "password")) {
                versionManager.selectVersion("1.16.5");
                downloader.downloadFiles();
                launchGame();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchGame() {
        System.out.println("Launching game...");
        String javaPath = System.getProperty("java.home") + "/bin/java";
        String gameDir = System.getProperty("user.home") + "/AppData/Roaming/.minecraft"; // Windows
        String version = "1.16.5";
        String mainClass = "net.minecraft.client.main.Main";
        String authUsername = "username";
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
        command.add(authUsername);
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