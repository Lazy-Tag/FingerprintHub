package com.example.uploadApp.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    private static final String HOST = "39.105.200.134";
    private static final int PORT = 5000;
    public void upload(String account, JSONObject data) {
        Thread thread = new Thread(() -> {
            try {
                Socket socket = new Socket(HOST, PORT);
                OutputStream outputStream = socket.getOutputStream();

                outputStream.write(("RequestHandler" + "\n" + account + "\n" + data.toString()).getBytes(StandardCharsets.UTF_8));
                socket.shutdownOutput();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public Boolean log(String account, String encryptedAccount) {
        final String[] result = {null};
        Thread thread = new Thread(() -> {
            try {
                Socket socket = new Socket(HOST, PORT);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(("LogHandler" + "\n" + account + "\n" + encryptedAccount).getBytes(StandardCharsets.UTF_8));
                socket.shutdownOutput();

                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                result[0] = reader.readLine();

                socket.shutdownInput();
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result[0].equals("success");
    }
}
