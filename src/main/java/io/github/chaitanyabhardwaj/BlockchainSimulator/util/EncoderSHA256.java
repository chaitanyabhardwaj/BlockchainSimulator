package io.github.chaitanyabhardwaj.BlockchainSimulator.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class EncoderSHA256 implements Encoder{

    @Override
    public String encode(String originalString) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] byteHash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(byteHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // Convert each byte to its hexadecimal representation
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                // Pad single digit hexadecimal numbers with a leading zero
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
