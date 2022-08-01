package com.sqcred.spackets;

public abstract class Packet {

    public abstract byte id();
    public abstract byte[] encode();
    public abstract void decode(byte[] bytes);

}