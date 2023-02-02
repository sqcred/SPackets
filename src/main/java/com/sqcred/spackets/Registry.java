package com.sqcred.spackets;

import java.util.HashMap;
import java.util.Map;

public class Registry {

    private static final Map<Byte, Class<? extends Packet>> registry = new HashMap<>();

    public static void registerPacket(Class<? extends Packet> packet, byte id){
        registry.put(id, packet);
    }

    public static Class<? extends Packet> getPacketById(byte id){
        return registry.get(id);
    }

    public static byte getIdByPacket(Class<? extends Packet> packet){
        for (Map.Entry<Byte, Class<? extends Packet>> entry : registry.entrySet()) {
            if(entry.getValue().equals(packet)){
                return entry.getKey();
            }
        }
        return -1;
    }

}
