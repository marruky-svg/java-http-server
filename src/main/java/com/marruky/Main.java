package com.marruky;

import auth.JwtService;
import com.marruky.Http.*;
import com.marruky.Json.JsonParser;
import com.marruky.Json.JsonSerializer;
import com.marruky.Routes.Router;
import com.marruky.db.DatabaseConnection;
import com.marruky.repository.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8082);
             ExecutorService executor = Executors.newFixedThreadPool(10);) {

            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Server listen on port 8082");
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "text/json");
            headers.put("Content-Length", "");
            headers.put("Connection", "");



            //============================
            //         ROUTER
            //============================

            Router router = new Router();
            router.register("/amr", "POST", request -> {
                if(!new JwtService().authenticate(request)) {
                    return new HttpResponse(401, "Unauthorized", headers, "");
                }

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
                if(!new JwtService().authenticate(request)) {
                    return new HttpResponse(401, "Unauthorized", headers, "");
                }

                FastaValidator validator = new FastaValidator();
                FastaValidator.ValidationResult validationResult = validator.validade(request.getBody(), 2);
                if (validationResult.isValid()) {
                    HttpClient client = new HttpClient();
                    String resultBody = client.post("localhost", 8083, "/compare", request.getBody());
                    JsonParser parser = new JsonParser();
                    Map<String, Object> sequences = parser.parser(resultBody);
                    AnalysisJobRepository analysisJobRepository = new AnalysisJobRepository();
                    CompareResultRepository repo = new CompareResultRepository();
                    int jobId = analysisJobRepository.save("COMPARE", "completed");
                    int compareId = repo.save(jobId, sequences.get("isoladoA").toString(), sequences.get("isoladoB").toString(),
                            Integer.parseInt(sequences.get("score").toString()), Double.parseDouble(sequences.get("similarity").toString()),
                            sequences.get("alignedA").toString(), sequences.get("alignedB").toString());
                    return new HttpResponse(200, "Compare saved with id: " + compareId, headers, resultBody);
                }
                return new HttpResponse(400, validationResult.getErrorMensage(), headers, "");
            });

            router.register("/analyse", "POST", request -> {
                if(!new JwtService().authenticate(request)) {
                    return new HttpResponse(401, "Unauthorized", headers, "");
                }

                FastaValidator validator = new FastaValidator();
                FastaValidator.ValidationResult validationResult = validator.validade(request.getBody());
                if (validationResult.isValid()) {
                    HttpClient client = new HttpClient();
                    String resultBody = client.post("localhost", 8083, "/analyse", request.getBody());
                    JsonParser parser = new JsonParser();
                    int analyseId = 0;
                    List<Map<String, Object>> sequences = parser.parserArray(resultBody);
                    AnalysisJobRepository repo = new AnalysisJobRepository();
                    AnalyseResultRepository analyseResultRepository = new AnalyseResultRepository();
                    int jobId = repo.save("ANALYSE", "completed");
                    for (Map<String, Object> sequence : sequences) {
                        Object isoladoId = sequence.get("id");
                        analyseId = analyseResultRepository.save(jobId, isoladoId.toString(), Integer.parseInt(sequence.get("length").toString()), Integer.parseInt(sequence.get("countA").toString()),
                                Integer.parseInt(sequence.get("countT").toString()), Integer.parseInt(sequence.get("countC").toString()), Integer.parseInt(sequence.get("countG").toString()),
                                Double.parseDouble(sequence.get("gcContent").toString()));
                    }
                    return new HttpResponse(200, "Analyse created with id: " + analyseId, headers, resultBody);
                }
                return new HttpResponse(400, validationResult.getErrorMensage(), headers, "");
            });

            router.register("/login", "POST", request -> {
                Map<String, Object> bodyParsed = new JsonParser().parser(request.getBody());
                String username = bodyParsed.get("username").toString();
                String password = bodyParsed.get("password").toString();
                JwtService jwt = new JwtService();
                String token = "";
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash1 = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                    String passwordHash1 = Base64.getEncoder().encodeToString(hash1);
                    UserRepository repo = new UserRepository();
                    if (repo.exists(username, passwordHash1)) {
                        token = jwt.generate(username, "researcher");
                        return new HttpResponse(200, "OK", headers, token);
                    }
                    return new HttpResponse(401, "Unauthorized", headers, "");

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            //============================
            //         /ROUTER
            //============================


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
