package main.java.com.example.muumclauncher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.Scanner;

public class AuthClient {

    private static final String CLIENT_ID = "YOUR_CLIENT_ID";
    private static final String REDIRECT_URI = "YOUR_REDIRECT_URI";
    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";

    private final OkHttpClient client = new OkHttpClient();

    public String getAuthorizationCode() {
        String url = "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize" +
                "?client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + REDIRECT_URI +
                "&response_mode=query" +
                "&scope=XboxLive.signin%20offline_access";

        System.out.println("Please open the following URL in your browser and authenticate:");
        System.out.println(url);

        System.out.println("Enter the code from the URL:");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public JsonObject getAccessToken(String authorizationCode) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("client_id", CLIENT_ID)
                .add("scope", "XboxLive.signin offline_access")
                .add("code", authorizationCode)
                .add("redirect_uri", REDIRECT_URI)
                .add("grant_type", "authorization_code")
                .add("client_secret", CLIENT_SECRET)
                .build();

        Request request = new Request.Builder()
                .url("https://login.microsoftonline.com/consumers/oauth2/v2.0/token")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return JsonParser.parseString(response.body().string()).getAsJsonObject();
        }
    }

    public JsonObject authenticateWithXboxLive(String accessToken) throws IOException {
        JsonObject json = new JsonObject();
        JsonObject properties = new JsonObject();
        properties.addProperty("AuthMethod", "RPS");
        properties.addProperty("SiteName", "user.auth.xboxlive.com");
        properties.addProperty("RpsTicket", "d=" + accessToken);
        json.add("Properties", properties);
        json.addProperty("RelyingParty", "http://auth.xboxlive.com");
        json.addProperty("TokenType", "JWT");

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://user.auth.xboxlive.com/user/authenticate")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return JsonParser.parseString(response.body().string()).getAsJsonObject();
        }
    }

    public JsonObject authorizeWithXsts(String xboxToken) throws IOException {
        JsonObject json = new JsonObject();
        JsonObject properties = new JsonObject();
        properties.addProperty("SandboxId", "RETAIL");
        JsonArray userTokens = new JsonArray();
        userTokens.add(xboxToken);
        properties.add("UserTokens", userTokens);
        json.add("Properties", properties);
        json.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        json.addProperty("TokenType", "JWT");

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://xsts.auth.xboxlive.com/xsts/authorize")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return JsonParser.parseString(response.body().string()).getAsJsonObject();
        }
    }

    public JsonObject loginToMinecraft(String xstsToken, String uhs) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("identityToken", "XBL3.0 x=" + uhs + ";" + xstsToken);

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://api.minecraftservices.com/authentication/login_with_xbox")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return JsonParser.parseString(response.body().string()).getAsJsonObject();
        }
    }

    public JsonObject getMinecraftProfile(String accessToken) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.minecraftservices.com/minecraft/profile")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return JsonParser.parseString(response.body().string()).getAsJsonObject();
        }
    }
}