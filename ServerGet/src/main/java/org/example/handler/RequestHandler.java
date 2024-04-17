package org.example.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class RequestHandler extends Handler {
    private final String account;
    static final int MaxFile = 100;

    public RequestHandler(Socket socket, BufferedReader reader) throws IOException {
        super(socket, reader);
        account = reader.readLine();
    }

    public void exec() {
        try {
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(data.toString());
            String randomFileName = RandomStringUtils.randomAlphanumeric(10);
            String filePath = path + account + "/" + randomFileName + ".json";
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            int length = Objects.requireNonNull(parentDir.listFiles()).length;
            if (length <= MaxFile) {
                objectMapper.writeValue(file, jsonNode);
            }
            System.out.println(file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
