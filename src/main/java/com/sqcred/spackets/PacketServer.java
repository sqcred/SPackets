package com.sqcred.spackets;

import com.sqcred.spackets.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class PacketServer {

    private final IReceiver iReceiver;
    private final int port;

    private boolean running = false;

    private ServerSocket socket;

    private final List<PacketClient> clients = new ArrayList<>();

    private final List<Packet> packetQueue = new ArrayList<>();

    public PacketServer(int port, IReceiver iReceiver) {
        this.port = port;
        this.iReceiver = iReceiver;
        if(iReceiver == null){
            Logger.error("IReceiver cannot be null!", Logger.Side.SERVER);
            System.exit(0);
        }
    }

    public void start() {

        try {

            socket = new ServerSocket(port);

            // handling incoming connections
            new Thread(() -> {
                try {
                    while (running){
                        Logger.debug("Waiting for clients to connect...", Logger.Side.SERVER);
                        PacketClient client = new PacketClient(socket.accept(), iReceiver);
                        clients.add(client);
                        client.start();
                        Logger.debug("New client connected!", Logger.Side.SERVER);
                    }
                } catch (Exception e){
                    Logger.error("Error | " + e.getMessage(), Logger.Side.SERVER);
                    e.printStackTrace();
                }
            }).start();

            // broadcasting packets
            new Thread(() -> {
                try {
                    if(!packetQueue.isEmpty()){
                        Packet packet = packetQueue.get(0);
                        packetQueue.remove(0);

                        clients.forEach(client -> {
                            client.sendPacket(packet);
                        });
                    }
                } catch (Exception e){
                    Logger.error("Error broadcasting packets | " + e.getMessage(), Logger.Side.SERVER);
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e){
            Logger.error("Error while starting up server | " + e.getMessage(), Logger.Side.SERVER);
            e.printStackTrace();
        }

    }

    public void broadcastPacket(Packet packet){
        this.packetQueue.add(packet);
    }

    public void stop(){
        try {
            this.running = false;
            this.socket.close();
        } catch (IOException e) {
            Logger.error("Error while shutting down server | " + e.getMessage(), Logger.Side.SERVER);
            e.printStackTrace();
        }
    }

}
