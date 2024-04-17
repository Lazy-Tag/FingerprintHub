package org.example.handler;

import java.io.BufferedReader;
import java.net.Socket;

public abstract class Handler {
    protected Socket socket;
    protected BufferedReader reader;
    static final String path = "/root/data/";

    public Handler(Socket socket, BufferedReader reader) {
        this.socket = socket;
        this.reader = reader;
    }

    public abstract void exec();
}
