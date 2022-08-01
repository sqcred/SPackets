package com.sqcred.spackets;

import java.util.HashMap;
import java.util.Map;

public class Packets {

    private static final Map<Byte, Class<? extends Packet>> packets = new HashMap<>();

    public static void registerPacket(Class<? extends Packet> packet, byte id){
        packets.putIfAbsent(id, packet);
    }

    public static Class<? extends Packet> findPacketById(byte id){
        return packets.get(id);
    }

}
