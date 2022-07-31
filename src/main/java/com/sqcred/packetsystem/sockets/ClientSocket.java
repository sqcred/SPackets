package com.sqcred.packetsystem.sockets;

import com.sqcred.packetsystem.Packet;
import com.sqcred.packetsystem.PacketSender;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ClientSocket extends WebSocketClient {

    private final PacketSender sender;

    public ClientSocket(PacketSender packetSender) {
        super(packetSender.getUri());
        this.sender = packetSender;
        this.addHeader("TOKEN", sender.getToken());
        connect();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        if(sender.isDebug()){
            System.out.println("[SimplePacketSystem][Client] Connected to server.");
        }
    }

    @Override
    public void onMessage(String s) {
        // ignore
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(sender.isDebug()){
            System.out.println("[SimplePacketSystem][Client] Connection closed for reason: " + reason);
        }
    }

    @Override
    public void onError(Exception e) {
        if(sender.isDebug()){
            System.out.println("[SimplePacketSystem][Client] An unknown error accrued: " + e.getMessage());
        }
    }

    public void sendPacket(Packet packet){
        try {
            byte[] bytes = packet.encode();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(packet.id());
            outputStream.write(bytes);

            this.send(outputStream.toByteArray());
        } catch (IOException e) {
            if(sender.isDebug()){
                System.out.println("[SimplePacketSystem][Client] Something went wrong encoding packet.");
            }
        }
    }

}
