/*
 * Copyright (c) 2022 sqcred
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.sqcred.spackets.sockets;

import com.sqcred.spackets.Packet;
import com.sqcred.spackets.PacketSender;

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
            System.out.println("[SPackets][Client] Connected to server.");
        }
    }

    @Override
    public void onMessage(String s) {
        // ignore
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(sender.isDebug()){
            System.out.println("[SPackets][Client] Connection closed for reason: " + reason);
        }
    }

    @Override
    public void onError(Exception e) {
        if(sender.isDebug()){
            System.out.println("[SPackets][Client] An unknown error accrued: " + e.getMessage());
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
                System.out.println("[SPackets][Client] Something went wrong encoding packet.");
            }
        }
    }

}
