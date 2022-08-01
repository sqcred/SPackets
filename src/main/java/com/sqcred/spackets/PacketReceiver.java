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


import com.sqcred.spackets.sockets.ServerSocket;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketReceiver {

    private final IReceiver iReceiver;
    @Getter
    private final String hostname;
    @Getter
    private final int port;

    @Getter
    private boolean debug;

    @Getter
    private String token = "undefined";

    public PacketReceiver(String hostname, int port, IReceiver iReceiver, String token){
        this.iReceiver = iReceiver;
        this.hostname = hostname;
        this.port = port;
        if(token != null){
            this.token = token;
        }
        new Thread(() -> new ServerSocket(this), "SPackets - Server").start();
    }

    public PacketReceiver(String hostname, int port, IReceiver iReceiver){
        this(hostname, port, iReceiver, null);
    }

    public void handlePacket(Packet packet){
        // Call method on Listener
        try {

            for(Method method : iReceiver.getClass().getMethods()){
                for(Class<?> type : method.getParameterTypes()){
                    if(type == packet.getClass() && method.getParameterCount() == 1){
                        method.invoke(iReceiver, packet);
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            if(debug){
                System.out.println("[SPackets] Unknown error accrued: " + e.getMessage());
            }
        }
    }

    public void enableDebug(){
        this.debug = true;
    }

}
