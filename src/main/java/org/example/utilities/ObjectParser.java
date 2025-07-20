package org.example.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.example.model.NodeDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class ObjectParser {

    public static String generate16BitUUID() {
        Random random = new Random();
        int value = random.nextInt(0x10000); // 0 to 0xFFFF
        return String.format("%04X", value); // e.g., "3A7F"
    }

    public static boolean parseValidHexMap(String input) {

        // Accept only pairs like A01:313233 (key:hex)
        Pattern pattern = Pattern.compile("([a-zA-Z0-9]+):([0-9a-fA-F]+)");

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return true;
        }
        return false;
    }


    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str); // or Double.parseDouble(str)
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

  public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len/2];
        for (int i=0; i<len; i+=2) data[i/2]=(byte)((Character.digit(hex.charAt(i),16)<<4)
                +Character.digit(hex.charAt(i+1),16));
        return data;
    }

    public static String mapToHexString(Map<String, byte[]> map) {
        return map.entrySet().stream()
                .map(e -> e.getKey() + ":" + bytesToHex(e.getValue()))
                .collect(Collectors.joining(","));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static boolean isListOfNodeDto(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Try to parse as List<NodeDto>
            List<NodeDto> nodeList = mapper.readValue(json, new TypeReference<List<NodeDto>>() {});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isJsonList(String str) {
        if (str == null) return false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(str);
            if (!node.isArray()) return false;
            for (JsonNode item : node) {
                if (containsMap(item)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean containsMap(JsonNode node) {
        if (node.isObject()) {
            return true;
        }

        if (node.isArray()) {
            for (JsonNode child : node) {
                if (containsMap(child)) return true;
            }
        }

        return false;
    }

    public static Map<String, byte[]> parseStringToByteMap(String input) {
        Map<String, byte[]> map = new HashMap<>();

        String[] pairs = input.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length == 2) {
                map.put(kv[0], hexToBytes(kv[1]));
            }
        }
        return map;
    }
}
