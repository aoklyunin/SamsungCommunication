package com.alexey.samsung;

/**
 * Created by aokly on 25.09.2016.
 */

import com.alexey.samsung.controller.MainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;


public final class VkApi implements AutoCloseable {

    private static final String APP_ID = "5229876";
    public static final String ACCESS_TOKEN = "1f1e0beb1f47d99c4c2f2477f90f589de3cfd096aecb5e91741acf86aa452a0aa48636a3e2a252b1803be";

    private static final String API_VERSION = "5.53";

    private static final String AUTH_URL = "https://oauth.vk.com/authorize"
            + "?client_id={APP_ID}"
            + "&scope={PERMISSIONS}"
            + "&redirect_uri={REDIRECT_URI}"
            + "&display={DISPLAY}"
            + "&v={API_VERSION}"
            + "&response_type=token";

    private static final String API_REQUEST = "https://api.vk.com/method/{METHOD_NAME}"
            + "?{PARAMETERS}"
            + "&access_token={ACCESS_TOKEN}"
            + "&v=" + API_VERSION;

    static VkApi with(String appId, String accessToken) throws IOException {
        return new VkApi(appId, accessToken);
    }

    private static final String KET_AT = "vk_access_token";

    private String accessToken;

    private VkApi(String appId, String accessToken) throws IOException {
        this.accessToken = accessToken;
        if (accessToken == null || accessToken.isEmpty()) {
            auth(appId);
            throw new Error("Need access token");
        }
    }

    private boolean checkAccessToken() {
        try (DBHelper dbHelper = new DBHelper()) {
            dbHelper.connect();
            String s = dbHelper.getConfVal(KET_AT);
            if (s == null)
                return false;
            else {
                accessToken = s;
               //System.out.println(accessToken);
                return testVKconnection();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка доступа к БД из VKAPI:" + e);
            return false;
        } catch (Exception e) {
            System.out.println("Ошибка из VKAPI:" + e);
            return false;
        }
    }

    private void initAT() {
        Stage stage = new Stage();
        stage.setWidth(821);
        stage.setHeight(643);
        Scene scene = new Scene(new Group());

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(browser);

        webEngine.getLoadWorker().stateProperty()
                .addListener((ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        String url = webEngine.getLocation();
                        if (url.contains("access_token")) {
                            accessToken = url.substring(url.indexOf("access_token") + 12);
                            accessToken = accessToken.substring(1, accessToken.indexOf("&"));
                            if (accessToken.length() != 0) {
                                stage.close();
                                try (DBHelper dbHelper = new DBHelper()) {
                                    dbHelper.connect();
                                    dbHelper.setConfVal(KET_AT, accessToken);
                                    successVk();
                                } catch (SQLException e) {
                                    System.out.println("Ошибка доступа к БД");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
        String reqUrl = AUTH_URL
                .replace("{APP_ID}", APP_ID)
                .replace("{PERMISSIONS}", "photos,messages")
                .replace("{REDIRECT_URI}", "https://oauth.vk.com/blank.html")
                .replace("{DISPLAY}", "page")
                .replace("{API_VERSION}", API_VERSION);

        webEngine.load(reqUrl);

        scene.setRoot(scrollPane);

        stage.setScene(scene);
        stage.show();
    }

    public VkApi() {
        if (checkAccessToken()) {
            successVk();
        } else {
            initAT();
        }
    }

    public String getUsersFromGroup(String groupId) throws IOException {
        return invokeApi("groups.getMembers", Params.create()
                .add("group_id", groupId));
    }

    private void successVk() {
        System.out.println("Connection is success establish: ");
     }

    private void auth(String appId) throws IOException {
        String reqUrl = AUTH_URL
                .replace("{APP_ID}", appId)
                .replace("{PERMISSIONS}", "photos,messages")
                .replace("{REDIRECT_URI}", "https://oauth.vk.com/blank.html")
                .replace("{DISPLAY}", "page")
                .replace("{API_VERSION}", API_VERSION);
        try {
            Desktop.getDesktop().browse(new URL(reqUrl).toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    public String getDialogs() throws IOException {
        return invokeApi("messages.getDialogs", null);
    }

    String getHistory(String userId, int offset, int count, boolean rev) throws IOException {
        return invokeApi("messages.getHistory", Params.create()
                .add("user_id", userId)
                .add("offset", String.valueOf(offset))
                .add("count", String.valueOf(count))
                .add("rev", rev ? "1" : "0"));
    }

    String getUserInform(String userId) throws IOException {
        return invokeApi("users.get", Params.create()
                .add("user_ids", userId));
    }
    public String getAlbums(String userId) throws IOException {
        return invokeApi("photos.getAlbums", Params.create()
                .add("owner_id", userId)
                .add("photo_sizes", "1")
                .add("thumb_src", "1"));
    }

    private String invokeApi(String method, Params params) throws IOException {
        final String parameters = (params == null) ? "" : params.build();
        String reqUrl = API_REQUEST
                .replace("{METHOD_NAME}", method)
                .replace("{ACCESS_TOKEN}", accessToken)
                .replace("{PARAMETERS}&", parameters);
        return invokeApi(reqUrl);
    }

    private boolean testVKconnection() {
        try {
            String req = invokeApi("users.get", Params.create()
                    .add("user_ids", "1"));
            return !(req.contains("error"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String invokeApi(String requestUrl) throws IOException {
        final StringBuilder result = new StringBuilder();
        final URL url = new URL(requestUrl);
        try (InputStream is = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            reader.lines().forEach(result::append);
        }
        return result.toString();
    }

    @Override
    public void close() throws Exception {

    }

    private static class Params {

        static Params create() {
            return new Params();
        }

        private final HashMap<String, String> params;

        private Params() {
            params = new HashMap<>();
        }

        Params add(String key, String value) {
            params.put(key, value);
            return this;
        }

        String build() {
            if (params.isEmpty()) return "";
            final StringBuilder result = new StringBuilder();
            params.keySet().stream().forEach(key -> {
                result.append(key).append('=').append(params.get(key)).append('&');
            });
            return result.toString();
        }
    }
}