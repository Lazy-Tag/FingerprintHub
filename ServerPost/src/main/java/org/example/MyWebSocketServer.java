package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.*;
import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;

public class MyWebSocketServer extends WebSocketServer {

    private static final String success = "success", failed = "failed";

    private final String path = "/root/data/";

    public MyWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection opened");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);
        String[] messages = message.split(" ");
        if (messages[0].equals("upload")) {
            post(conn, message);
        } else {
            String account = messages[0];
            String key = messages[1];
            File keyFile = new File(path + account + "/" + "securityKey.txt");
            String response;
            if (!keyFile.exists()) {
                response = failed;
            } else {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(keyFile));
                    String truth = reader.readLine();
                    if (key.equals(truth)) {
                        response = success;
                    } else {
                        response = failed;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            conn.send(response);
        }
    }

    private void post(WebSocket conn, String message) {
        String[] messages = message.split(" ");
        File directory = new File(path + messages[1]);
        File[] files = directory.listFiles();
        if (files == null) {
            String response = "Account is not exist!\n";
            conn.send(response);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonNode = objectMapper.getNodeFactory().objectNode();
            try {
                int cnt = 0;
                for (File file : files) {
                    if (file.getName().equals("securityKey.txt")) {
                        continue;
                    }
                    String fileInfo = readFile(file);
                    JsonNode fileJson = objectMapper.readTree(fileInfo);
                    jsonNode.put(Integer.toString(cnt), fileJson.toString());
                    cnt ++ ;
                }
                String response = jsonNode.toString();
                conn.send(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String readFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "false";
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started successfully");
    }
}
