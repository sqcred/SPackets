package com.sqcred.packetsystem;


import com.sqcred.packetsystem.sockets.ServerSocket;

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
    private final boolean debug;

    @Getter
    private String token = "undefined";

    public PacketReceiver(String hostname, int port, boolean debug, String token, IReceiver iReceiver){
        this.iReceiver = iReceiver;
        this.hostname = hostname;
        this.port = port;
        this.debug = debug;
        if(token != null){
            this.token = token;
        }
        new Thread(() -> new ServerSocket(this), "SimplePacketSystem - Server").start();
    }

    public PacketReceiver(String hostname, int port, IReceiver iReceiver){
        this(hostname, port, false, null, iReceiver);
    }

    public PacketReceiver(String hostname, int port, String token, IReceiver iReceiver){
        this(hostname, port, false, token, iReceiver);
    }

    public void handlePacket(Packet packet){
        // Call method on Listener
        try {
            //Method method = iReceiver.getClass().getMethod("onPacket", handshake.getClass());
            //method.invoke(iReceiver, handshake);

            for(Method method : iReceiver.getClass().getMethods()){
                for(Class<?> type : method.getParameterTypes()){
                    if(type == packet.getClass() && method.getParameterCount() == 1){
                        method.invoke(iReceiver, packet);
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println("[SimplePacketSystem] Unknown error accrued: " + e.getMessage());
        }
    }

}
