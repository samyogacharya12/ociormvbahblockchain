package org.example.zmq;

import com.backblaze.erasure.ReedSolomon;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ShareUtils {

    public  static byte[][] encodeMessageWithReedSolomon(byte[] data, int N, int T) {
        int shardSize = (data.length + T - 1) / T;
        int parityShards = N - T;
        byte[][] shards = new byte[N][shardSize];

        for (int i = 0; i < T; i++) {
            int copyLen = Math.min(shardSize, data.length - i * shardSize);
            if (copyLen > 0) {
                System.arraycopy(data, i * shardSize, shards[i], 0, copyLen);
            }
        }
        ReedSolomon reedSolomon = ReedSolomon.create(T, parityShards);
        reedSolomon.encodeParity(shards, 0, shardSize);
        return shards;
    }


    byte[] decodeMessageWithReedSolomon(List<byte[]> subset, int N, int T) throws Exception {
        int shardSize = subset.get(0).length;
        byte[][] shards = new byte[N][shardSize];
        boolean[] shardPresent = new boolean[N];

        for (int i = 0; i < subset.size(); i++) {
            shards[i] = subset.get(i);
            shardPresent[i] = true;
        }

        ReedSolomon reedSolomon = ReedSolomon.create(T, N - T);
        reedSolomon.decodeMissing(shards, shardPresent, 0, shardSize);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < T; i++) out.write(shards[i]);
        return out.toByteArray();
    }
}
