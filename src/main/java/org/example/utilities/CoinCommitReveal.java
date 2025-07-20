package org.example.utilities;

import lombok.experimental.UtilityClass;
import org.example.model.NodeDto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@UtilityClass
public class CoinCommitReveal {

    public static Map<String, byte[]> commitPhase(List<NodeDto> nodes) {
        Map<String, byte[]> commitments = new HashMap<>();
        for (NodeDto node : nodes) {
            if(Objects.nonNull(node.getCommitment())) {
                commitments.put(node.getId(), node.getCommitment());
            }
        }
        return commitments;
    }

    public static byte[] revealAndCombine(List<NodeDto> nodes, Map<String, byte[]> commitments) {
        byte[] combined = new byte[32]; // 256-bit for SHA-256

        for (NodeDto node : nodes) {
            byte[] expectedCommit = sha256(node.getRi());
            if (Arrays.equals(expectedCommit, commitments.get(node.getId()))) {
                for (int i = 0; i < combined.length; i++) {
                    combined[i] ^= node.getRi()[i];  // XOR
                }
            }
        }
        return combined;
    }

    public static int getCoinIndex(byte[] combinedHash, int range) {
        byte[] hash = sha256(combinedHash);
        int val = ((hash[0] & 0xFF) << 24) | ((hash[1] & 0xFF) << 16)
                | ((hash[2] & 0xFF) << 8) | (hash[3] & 0xFF);
        return Math.abs(val) % range;
    }

    // === Utility Methods ===
    public static byte[] generateRandomBytes() {
        byte[] random = new byte[32];
        new Random().nextBytes(random);
        return random;
    }

    public static byte[] sha256(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not found", e);
        }
    }




}
