package com.marruky;

import com.marruky.Http.*;
import com.marruky.Json.JsonParser;
import com.marruky.Json.JsonSerializer;
import com.marruky.Routes.Handler;
import com.marruky.Routes.Router;
import com.marruky.db.DatabaseConnection;
import com.marruky.repository.AmrResultRepository;
import com.marruky.repository.AnalysisJobRepository;

import javax.management.ObjectName;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        try {

            Connection conn = DatabaseConnection.getConnection();
            AnalysisJobRepository jobRepository = new AnalysisJobRepository();
            int id = jobRepository.save("analyse", "completed");
            System.out.println("Job saved with id: " + id);

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
            router.register("/users", "GET", request -> new HttpResponse(200, "OK", headers, body_route));
            router.register("/users", "POST", request -> new HttpResponse(201, "CREATED", headers, body_error));


            router.register("/amr", "POST", request -> {
                FastaValidator validator = new FastaValidator();
                FastaValidator.ValidationResult validationResult = validator.validade(request.getBody());
                if (validationResult.isValid()) {
                    HttpClient client = new HttpClient();
                    String resultBody = client.post("localhost", 8083, "/amr", request.getBody());
                    JsonParser parser = new JsonParser();
                    List<Map<String, Object>> sequences = parser.parserArray(resultBody);
                    AnalysisJobRepository analysisJobRepository = new AnalysisJobRepository();
                    AmrResultRepository repo = new AmrResultRepository();
                    int amrId = 0;
                    int jobId = analysisJobRepository.save("AMR", "completed");
                    for (Map<String, Object> sequence : sequences) {
                        Object isoladoId = sequence.get("id");
                        List<Map<String, Object>> genes = (List<Map<String, Object>>) sequence.get("genes");
                        for (Map<String, Object> gene : genes) {
                          amrId = repo.save(jobId, isoladoId.toString(), gene.get("gene").toString(), gene.get("antibioticClass").toString(),
                                    Double.parseDouble(gene.get("similarity").toString()), Integer.parseInt(gene.get("score").toString()));
                        }
                    }
                    return new HttpResponse(200, "Amr saved with id: " + amrId, headers, resultBody);
                }
                return new HttpResponse(400, validationResult.getErrorMensage(), headers, "");
            });
            router.register("/compare", "POST", request -> {
                FastaValidator validator = new FastaValidator();
                FastaValidator.ValidationResult validationResult = validator.validade(request.getBody(), 2);
                if (validationResult.isValid()) {
                    HttpClient client = new HttpClient();
                    String resultBody = client.post("localhost", 8083, "/compare", request.getBody());
                    return new HttpResponse(200, "OK", headers, resultBody);
                }
                return new HttpResponse(400, validationResult.getErrorMensage(), headers, "");
            });

            router.register("/analyse", "POST", request -> {
                FastaValidator validator = new FastaValidator();
                FastaValidator.ValidationResult validationResult = validator.validade(request.getBody());
                if (validationResult.isValid()) {
                    HttpClient client = new HttpClient();
                    String resultBody = client.post("localhost", 8083, "/analyse", request.getBody());
                    return new HttpResponse(200, "OK", headers, resultBody);
                }
                return new HttpResponse(400, validationResult.getErrorMensage(), headers, "");
            });


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

        } catch (Exception e) {
            throw new RuntimeException("Server error: " + e);
        }
    }
}
