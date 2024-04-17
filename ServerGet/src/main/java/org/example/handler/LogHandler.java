package org.example.handler;
import java.io.*;
import java.net.Socket;

public class LogHandler extends Handler{
    private String account;
    private String securityKey;

    private static final String success = "success", failed = "failed";
    public LogHandler(Socket socket, BufferedReader reader) {
        super(socket, reader);
        try {
            account = reader.readLine();
            securityKey = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void exec() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            String message = "";
            String fileName = "securityKey";
            String parentPath = path + account + "/";
            String filePath = parentPath + fileName + ".txt";
            File parentDir = new File(parentPath);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            File file = new File(filePath);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String key = reader.readLine();
                if (key.equals(securityKey)) {
                    message = success;
                } else {
                    message = failed;
                }
            } else {
                FileWriter writer = new FileWriter(file);
                writer.write(securityKey);
                writer.close();
                message = success;
            }
            outputStream.write(message.getBytes());
            outputStream.flush();
            socket.shutdownOutput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
