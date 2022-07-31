package com.sqcred.packetsystem;

public abstract class Packet {

    public abstract byte id();
    public abstract byte[] encode();
    public abstract void decode(byte[] bytes);

}
