package com.marruky;

import com.marruky.Http.HttpClient;
import com.marruky.Http.HttpParser;
import com.marruky.Http.HttpRequest;
import com.marruky.Http.HttpResponse;
import com.marruky.Json.JsonParser;
import com.marruky.Json.JsonSerializer;
import com.marruky.Routes.Handler;
import com.marruky.Routes.Router;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        try {
            HttpClient client = new HttpClient();
            String result = client.post("localhost", 8083, "/analyse", ">isolado_1\nATCGATCGATCG\n>isolado_2\nTTTTAAAACCCC");
            System.out.println(result);


            ServerSocket serverSocket = new ServerSocket(8082);
            System.out.println("Server listen on port 8082");
            ExecutorService executor = Executors.newFixedThreadPool(10);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "text/json");
            headers.put("Content-Length", "");
            headers.put("Connection", "");
            String body_route = "A resposta é esta e esta tudo certo";
            String body_error = "HE-HE";
            Router router = new Router();
            router.register("/users", "GET", request1 -> new HttpResponse(200, "OK", headers, body_route));
            router.register("/users", "POST", request1 -> new HttpResponse(201, "CREATED", headers, body_error));
            router.register("/products", "POST", request -> new HttpResponse(201, "CREATED", headers, body_route));

            JsonSerializer serializer = new JsonSerializer();
            JsonParser parser = new JsonParser();

            Map<String, Object> data = parser.parser("{\"nome\":\"elder\",\"idade\":25 ,\"altura\": 1.82}");
            System.out.println(data);
            System.out.println(serializer.serialize(data));
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client is connected");
                executor.submit(() -> {
                    try {
                        Thread.sleep(3000);
                        HttpRequest request = HttpParser.parser((socket.getInputStream()));
                        System.out.println("Method: " + request.getMethod());
                        System.out.println("Path: " + request.getPath());
                        System.out.println("Body: " + request.getBody());
                        HttpResponse response = router.dispatch(request);
                        byte[] responseBytes = response.toBytes();
                        OutputStream os = socket.getOutputStream();
                        os.write(responseBytes);
                        socket.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch (IOException e) {
            throw new RuntimeException("Server error: " + e);
        }
    }
}
