package com.sqcred.packetsystem;

import com.sqcred.packetsystem.sockets.ClientSocket;

import lombok.Getter;

import java.net.URI;
import java.net.URISyntaxException;

public class PacketSender {

    private ClientSocket client;

    @Getter
    private final URI uri;
    @Getter
    private final boolean debug;
    @Getter
    private String token = "undefined";

    public PacketSender(String hostname, int port, boolean debug, String token){
        this.debug = debug;
        if(token != null){
            this.token = token;
        }
        try {
            this.uri = new URI("ws://" + hostname + ":" + port);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> this.client = new ClientSocket(this), "SimplePacketSystem - Client").start();
    }

    public PacketSender(String hostname, int port, boolean debug){
        this(hostname, port, debug, null);
    }

    public PacketSender(String hostname, int port){
        this(hostname, port, false, null);
    }

    public PacketSender(String hostname, int port, String token){
        this(hostname, port, false, token);
    }

    public void sendPacket(Packet packet){
        this.client.sendPacket(packet);
    }

}
