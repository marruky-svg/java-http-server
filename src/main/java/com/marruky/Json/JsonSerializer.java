package com.marruky.Json;

import java.util.HashMap;
import java.util.Map;

public class JsonSerializer {

    public String serialize(Map<String, Object> data) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getKey()).append("\"").append(": ").append("\"").append(entry.getValue()).append("\"").append(",");
                continue;
            }
            json.append("\"").append(entry.getKey()).append("\"").append(": ").append(entry.getValue()).append(",");
        }
        json.deleteCharAt((json.length() - 1));
        json.append("}");
        return json.toString();
    }
}


