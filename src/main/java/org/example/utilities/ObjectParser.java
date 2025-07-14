package org.example.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class ObjectParser {

    public static String generate16BitUUID() {
        Random random = new Random();
        int value = random.nextInt(0x10000); // 0 to 0xFFFF
        return String.format("%04X", value); // e.g., "3A7F"
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

    static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static boolean isJsonList(String str) {
        if (str == null) return false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(str);
            return node.isArray();  // this is the correct way to check for JSON list
        } catch (Exception e) {
            return false;
        }
    }
}
