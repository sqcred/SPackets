/*
 * Copyright (c) 2022 sqcred
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.sqcred.spackets;

import com.sqcred.spackets.sockets.ClientSocket;

import lombok.Getter;

import java.net.URI;
import java.net.URISyntaxException;

public class PacketSender {

    private ClientSocket client;

    @Getter
    private final URI uri;
    @Getter
    private boolean debug;
    @Getter
    private String token = "undefined";

    public PacketSender(String hostname, int port, String token){
        if(token != null){
            this.token = token;
        }
        try {
            this.uri = new URI("ws://" + hostname + ":" + port);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> this.client = new ClientSocket(this), "SPackets - Client").start();
    }

    public PacketSender(String hostname, int port){
        this(hostname, port, null);
    }

    public void sendPacket(Packet packet){
        this.client.sendPacket(packet);
    }

    public void enableDebug(){
        this.debug = true;
    }

}
