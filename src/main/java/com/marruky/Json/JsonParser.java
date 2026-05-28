package com.marruky.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    public Map<String, Object> parser(String json) {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        // salta o '{'
        while (i < json.length() && json.charAt(i) != '{') i++;
        i++; // passa o '{'

        while (i < json.length() && json.charAt(i) != '}') {
            // salta espaços e vírgulas
            while (i < json.length() && (json.charAt(i) == ',' || json.charAt(i) == ' ' || json.charAt(i) == '\n')) i++;

            // extrai chave
            String key = extractValue(json, i);
            i += key.length(); // avança pelo tamanho da chave
            key = key.replace("\"", ""); // remove aspas

            // salta o ':'
            while (i < json.length() && json.charAt(i) != ':') i++;
            i++; // passa o ':'

            // salta espaços
            while (i < json.length() && json.charAt(i) == ' ') i++;

            // extrai valor
            String rawValue = extractValue(json, i);
            i += rawValue.length();

            // decide tipo e guarda
            map.put(key, parseValue(rawValue));
        }
        return map;
    }

    public List<Map<String, Object>> parserArray(String json) {
        List<Map<String,Object>> result = new ArrayList<>();
        int i = 0;
        while(i < json.length() && json.charAt(i)!= '[') i++;
        i++;

        while(i < json.length() && json.charAt(i) != ']') {

            while (i < json.length() && (json.charAt(i) == ',' || json.charAt(i) == ' ' || json.charAt(i) == '\n')) i++;

            if(json.charAt(i) == '{') {
                String object = extractValue(json, i);
                Map<String, Object> map = parser(object);
                result.add(map);
                i += object.length();
            }
        }
        return result;
    }

    public String extractValue(String json, int startIndex) {
        if (json.charAt(startIndex) == '"') {
            StringBuilder value = new StringBuilder("\"");
            int i = startIndex + 1;
            while (i < json.length()) {
                char c = json.charAt(i);
                value.append(c);
                if (c == '"') {
                    return value.toString();
                }
                i++;
            }
        } else if (json.charAt(startIndex) == '{') {
            int bracesCount = 1;
            StringBuilder value = new StringBuilder("{");
            int i = startIndex + 1;
            while (i < json.length()) {
                char c = json.charAt(i);
                value.append(c);
                if (c == '{') {
                    bracesCount++;
                }
                if (c == '}') {
                    bracesCount--;
                }

                if (bracesCount == 0) {
                    return value.toString();
                }
                i++;
            }
        } else if (json.charAt(startIndex) == '[') {
            int bracesCount = 1;
            StringBuilder value = new StringBuilder("[");
            int i = startIndex + 1;
            while (i < json.length()) {
                char c = json.charAt(i);
                value.append(c);

                if (c == '[') {
                    bracesCount++;
                }
                if (c == ']') {
                    bracesCount--;
                }
                if (bracesCount == 0) {
                    return value.toString();
                }
                i++;
            }
        } else {
            StringBuilder value = new StringBuilder();
            int i = startIndex;
            while (i < json.length()) {
                char c = json.charAt(i);
                if (c == ',' || c == '}') {
                    return value.toString();
                }
                value.append(c);
                i++;
            }
        }
        return "";
    }

    private Object parseValue(String raw) {
        if (raw.startsWith("\"")) return raw.replace("\"", "");
        if (raw.startsWith("{")) return parser(raw);
        if (raw.startsWith("[")) return parserArray(raw);
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
        }
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
        }
        return raw.trim();
    }

}

