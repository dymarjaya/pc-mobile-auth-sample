package com.payconfirm.demo.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
public class Utils {

    private static SecureRandom random = new SecureRandom();

    public static String generateRandomString(int length, String character) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(character.length());
            char rndChar = character.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }

    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for(int j = 0; j < bytes.length; ++j) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 15];
        }

        return new String(hexChars);
    }

    public static byte[] randomBytes(int length) {
        byte[] b = new byte[length];
        new Random().nextBytes(b);
        return b;
    }

    public static void appendStrToFile(String fileName, String log)
    {
        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String date = "\n" + format.format(new Date()) + "  LOG --- EVENTS POST URL";

            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(fileName, true));
            out.write(date);
            out.write("\n");
            out.write(log);
            out.write("\n");
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occured" + e);
        }
    }

}
