package com.marruky.Http;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpParser {

    public static HttpRequest parser(InputStream socketRequest) {
        InputStreamReader requestReader = new InputStreamReader(socketRequest);
        BufferedReader requestBr = new BufferedReader(requestReader);
        try{

            String[] firstLine = requestBr.readLine().split(" ");
            String method = firstLine[0];
            String path = firstLine[1];
            Map<String,String> headers = new HashMap<>();
            String line = requestBr.readLine();
            while(line != null && !line.isEmpty()) {
                String[] lineSplit = line.split(":",2);
                headers.put(lineSplit[0], lineSplit[1]);
                line = requestBr.readLine();
            }
            String body = "";
            if (headers.containsKey("Content-Length")) {
                int bytes = Integer.parseInt(headers.get("Content-Length").trim());
                char[] buffer = new char[bytes];
                 if (bytes == requestBr.read(buffer, 0, bytes)) {
                     body = new String(buffer);}
            }
            return new HttpRequest(method, path, body, headers);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
