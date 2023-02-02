package com.sqcred.spackets.packets;

import com.sqcred.spackets.Packet;

public class PacketKeepalive extends Packet {

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    @Override
    public void decode(byte[] bytes) {

    }

}
