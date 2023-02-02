package com.sqcred.spackets;

import com.sqcred.spackets.utils.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PacketClient {

    private final IReceiver iReceiver;
    private String host;
    private int port;

    private boolean running = false;

    private Socket socket;

    private final List<Packet> packetQueue = new ArrayList<>();

    public PacketClient(Socket socket, IReceiver iReceiver){
        this.socket = socket;
        this.iReceiver = iReceiver;
    }

    public PacketClient(String host, int port, IReceiver iReceiver){
        this.host = host;
        this.port = port;
        this.iReceiver = iReceiver;
    }

    public PacketClient(String host, int port){
        this(host, port, null);
    }

    public void start() {

        running = true;

        try {

            if(socket == null){
                socket = new Socket(host, port);
            }

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());

            // Handling outgoing data
            new Thread(() -> {
                while (running){
                    try {
                        if(!packetQueue.isEmpty()){
                            Packet packet = packetQueue.get(0);
                            packetQueue.remove(0);

                            byte[] data = packet.encode();

                            output.writeByte(Registry.getIdByPacket(packet.getClass()));
                            output.writeInt(data.length);
                            output.write(data);
                        }
                    } catch (Exception e){
                        Logger.error("Error sending packet | " + e.getMessage(), Logger.Side.CLIENT);
                        e.printStackTrace();
                    }
                }
            }).start();

            // Handling incoming data
            if(iReceiver != null){
                new Thread(() -> {
                    while (running){
                        try {
                            byte id = input.readByte();
                            int length = input.readInt();
                            byte[] data = new byte[length];
                            input.readFully(data, 0, length);

                            Class<? extends Packet> packetType = Registry.getPacketById(id);

                            if(packetType == null){
                                Logger.warn("Received unknown packet with id: " + id + ", ignoring...", Logger.Side.UNKNOWN);
                                continue;
                            }

                            try {
                                Packet packet = packetType.getDeclaredConstructor().newInstance();
                                packet.decode(data);

                                try {
                                    for(Method method : iReceiver.getClass().getMethods()){
                                        for(Class<?> type : method.getParameterTypes()){
                                            if(type == packetType && method.getParameterCount() == 1){
                                                method.setAccessible(true);
                                                method.invoke(iReceiver, packet);
                                            }
                                        }
                                    }
                                } catch (SecurityException | InvocationTargetException | IllegalArgumentException |
                                         IllegalAccessException e) {
                                    Logger.warn("Could not call method in IReceiver.", Logger.Side.UNKNOWN);
                                }
                            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                                     InvocationTargetException e) {
                                Logger.warn("Packet with id " + id + " does not have an empty constructor, please create one, ignoring...", Logger.Side.UNKNOWN);
                            }

                        } catch (IOException e){
                            Logger.error("Error receiving packet | " + e.getMessage(), Logger.Side.CLIENT);
                        }
                    }
                }).start();
            }

        } catch (Exception e){
            Logger.error("Error | " + e.getMessage(), Logger.Side.CLIENT);
            e.printStackTrace();
        }

    }

    public void sendPacket(Packet packet){
        this.packetQueue.add(packet);
    }

    public void stop(){
        try {
            this.running = false;
            this.socket.close();
        } catch (IOException e) {
            Logger.error("Error while shutting down client | " + e.getMessage(), Logger.Side.CLIENT);
            e.printStackTrace();
        }
    }

}
