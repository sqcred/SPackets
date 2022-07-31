# SimplePacketSystem

SimplePacketSystem is a simple Java packet system using websockets.

## Packet Format v1.0.0

| 1 byte (Packet Id) | byte[] (Packet data) |
|--------------------|----------------------|


## Usage

Make sure to register the packet on both sides (Client + Server)
```
Packets.registerPacket(PacketTest.class, (byte) 1);
```

### Client

```java

PacketSender sender = new PacketSender("127.0.0.1", 1234);

PacketTest packet = new PacketTest((byte) 3);

sender.sendPacket(packet)

```

### Server

```java

PacketReceiver receiver = new PacketReceiver("127.0.0.1",1234);

public class PacketListener implements IReceiver {

    public void onTest(PacketTest test){
        System.out.println("Byte 1: " + test.byteOne);
        System.out.println("Byte 2: " + test.byteTwo);
        System.out.println("Byte 3: " + test.byteThree);
    }
    
}

```

