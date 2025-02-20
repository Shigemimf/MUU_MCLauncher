package main.java.com.example.muumclauncher;

import com.google.gson.JsonObject;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        AuthClient authClient = new AuthClient();
        try {
            String authorizationCode = authClient.getAuthorizationCode();
            JsonObject tokenResponse = authClient.getAccessToken(authorizationCode);
            String accessToken = tokenResponse.get("access_token").getAsString();

            JsonObject xboxResponse = authClient.authenticateWithXboxLive(accessToken);
            String xboxToken = xboxResponse.get("Token").getAsString();
            String uhs = xboxResponse.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();

            JsonObject xstsResponse = authClient.authorizeWithXsts(xboxToken);
            String xstsToken = xstsResponse.get("Token").getAsString();

            JsonObject minecraftLoginResponse = authClient.loginToMinecraft(xstsToken, uhs);
            String minecraftAccessToken = minecraftLoginResponse.get("access_token").getAsString();

            JsonObject minecraftProfile = authClient.getMinecraftProfile(minecraftAccessToken);
            System.out.println("Minecraft Profile: " + minecraftProfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}