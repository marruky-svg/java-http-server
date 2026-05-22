package com.marruky.Json;

import java.util.HashMap;
import java.util.Map;

public class JsonParser {

    public Map<String, Object> parser(String json) {
        Map<String, Object> data = new HashMap<>();
        String jsonNoBracesNoQuotes = json.replace("{", "");
        jsonNoBracesNoQuotes = jsonNoBracesNoQuotes.replace("}", "");
        jsonNoBracesNoQuotes = jsonNoBracesNoQuotes.replace("\"", "");

        String[] jsonSemiParsed = jsonNoBracesNoQuotes.split(",");
        for (String s : jsonSemiParsed) {
            String[] jsonParsed = s.split(":", 2);
            try {
                int value = Integer.parseInt(jsonParsed[1].trim());
                data.put(jsonParsed[0], value);
            } catch (NumberFormatException e) {
                try {
                    double valueDouble = Double.parseDouble(jsonParsed[1].trim());
                    data.put(jsonParsed[0], valueDouble);
                } catch (NumberFormatException error) {
                    data.put(jsonParsed[0], jsonParsed[1]);
                }
            }
        }
        return data;
    }
}

