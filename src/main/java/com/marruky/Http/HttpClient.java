package com.marruky.Http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpClient {
    public String post(String host, int port, String path, String body) {
        try (Socket socket = new Socket(host, port)) {
            OutputStream os = socket.getOutputStream();
            String request = "POST " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "\r\n" +
                    body;
            byte[] bytes = request.getBytes(StandardCharsets.UTF_8);
            os.write(bytes);

            InputStream is = socket.getInputStream();
            Reader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();

            while(!line.isEmpty()) {
                line = bufferedReader.readLine();
            }
            StringBuilder responseBody = new StringBuilder();

            String bodyLine;
            while ((bodyLine = bufferedReader.readLine()) != null) {
                responseBody.append(bodyLine);
            }
            return responseBody.toString();



        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
