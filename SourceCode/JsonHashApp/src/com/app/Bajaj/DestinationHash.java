package com.app.Bajaj;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONTokener;

public class DestinationHash {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar test.jar <PRN Number> <path to json file>");
            return;
        }

        String prn = args[0].toLowerCase().replaceAll("\\s", "");
        String filePath = args[1];

        try {
            String destinationValue = getDestinationValue(filePath);
            if (destinationValue != null) {
                String randomString = generateRandomString(8);
                String hashInput = prn + destinationValue + randomString;
                String hash = generateMD5Hash(hashInput);
                System.out.println(hash + ";" + randomString);
            } else {
                System.out.println("Key 'destination' not found in the JSON file.");
            }
        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    private static String getDestinationValue(String filePath) throws IOException {
        JSONObject jsonObject = new JSONObject(new JSONTokener(new FileReader(filePath)));
        return findDestination(jsonObject);
    }

    private static String findDestination(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestination((JSONObject) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}