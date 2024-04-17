package org.example;

import org.example.handler.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {

        int port = 5000;
        final String Package = "org.example.handler.";
        ServerSocket server = new ServerSocket(port);
        while(true) { // 循环接受客户端请求
            Socket socket = server.accept();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String category = reader.readLine();

                Class<?> shapeClass = Class.forName(Package + category);
                Constructor<?> constructor = shapeClass.getDeclaredConstructor(Socket.class, BufferedReader.class);
                Handler handler = (Handler) constructor.newInstance(socket, reader);
                handler.exec();
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            socket.close();
        }
    }
}