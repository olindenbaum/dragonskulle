package org.dragonskulle.game.lobby;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Log
public class TestGetRequest {
    private static final String API_URL = "https://dragonskulle.vercel.app/api/hosts";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0";

    public static void main(String[] args) {
        Test get = new Test();
        get.start();
    }

    private static void onConnectedHosts(String message, boolean success) {
        System.out.println(message);
    }

    private static class Test extends Thread {
        @Override
        public void run() {
            URL mUrl = null;
            try {
                mUrl = new URL(API_URL);
            } catch (MalformedURLException ignored) {
                return;
            }
            String mMethod = "GET";
            try {
                HttpURLConnection con = (HttpURLConnection) mUrl.openConnection();
                con.setRequestMethod(mMethod);
                con.setConnectTimeout(9000);
                con.setReadTimeout(9000);
                con.setRequestProperty("User-Agent", USER_AGENT);

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                org.dragonskulle.game.lobby.TestGetRequest.onConnectedHosts(builder.toString(), con.getResponseCode() == HttpURLConnection.HTTP_OK);
            } catch (IOException e) {
                log.warning(String.format("%s request to %s failed", mMethod, mUrl));
                log.severe(e.getMessage());
            }
        }
    }

}

