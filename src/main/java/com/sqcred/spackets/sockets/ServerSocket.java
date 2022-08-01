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
import com.sqcred.spackets.PacketReceiver;
import com.sqcred.spackets.Packets;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class ServerSocket extends WebSocketServer {

    private final PacketReceiver receiver;

    public ServerSocket(PacketReceiver receiver){
        // Init Websocket Server
        super(new InetSocketAddress(receiver.getHostname(), receiver.getPort()));
        this.receiver = receiver;
        this.run();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        // Client connected
        if(receiver.isDebug()){
            System.out.println("[SPackets][Server] New client connected. (" + webSocket.getRemoteSocketAddress() + ")");
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        // Client disconnected
        if(receiver.isDebug()){
            System.out.println("[SPackets][Server] Disconnected client " + webSocket.getRemoteSocketAddress() + " for reason: " + reason);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        // ignore
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        // handle incoming packet

        byte[] packetArray = message.array();

        byte id = packetArray[0];
        byte[] data = new byte[packetArray.length - 1];

        for(int i = 1; i < packetArray.length; i++){
            data[i - 1] = packetArray[i];
        }

        Class<? extends Packet> packetType = Packets.findPacketById(id);

        if(packetType == null){
            // Unknown packet
            System.out.println("[SPackets][Server] Received unknown packet with id " + id + ", ignoring...");
            return;
        }

        try {
            Packet packet = packetType.newInstance();
            packet.decode(data);

            receiver.handlePacket(packet);
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println("[SPackets][Server] Packet " + packetType.getName() + " does not have an empty constructor, please create one, ignoring...");
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        if(receiver.isDebug()){
            System.out.println("[SPackets][Server] Unknown error accrued: " + e.getMessage());
        }
    }

    @Override
    public void onStart() {
        if(receiver.isDebug()){
            System.out.println("[SPackets][Server] Server started.");
        }
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
        if(!request.getFieldValue("TOKEN").equals(receiver.getToken())){
            throw new InvalidDataException(CloseFrame.REFUSE, "Wrong token.");
        }
        return builder;
    }

}
