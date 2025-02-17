package main.java.com.example.muumclauncher;

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
    }
}
