package io.github.chaitanyabhardwaj.BlockchainSimulator.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class LOG {

    final private static Map<Long, String> globalTransmissionControlX = new HashMap<>();

    public static void init(long key) {
        if(globalTransmissionControlX.containsKey(key)) {
            globalTransmissionControlX.put(key, "Security Breached!");
            return;
        }
        globalTransmissionControlX.put(key, "");
    }

    public static void write(long key, String data) {
        data = Instant.now() + " $ " + data;
        if(globalTransmissionControlX.containsKey(key))
            globalTransmissionControlX.put(key, data);
        else {
            init(key);
            write(key, data);
        }
    }

    public static void append(long key, String data) {
        data = Instant.now()  + " $ " + data;
        if(globalTransmissionControlX.containsKey(key))
            globalTransmissionControlX.put(key, globalTransmissionControlX.get(key).concat(data));
        else {
            init(key);
            write(key, data);
        }
    }

    public static String compile(long key) {
        return globalTransmissionControlX.getOrDefault(key, "Block address violation!");
    }

}
