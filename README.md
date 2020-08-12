[![Build Status](https://ci.exceptionflug.de/job/Protocolize/badge/icon)](https://ci.exceptionflug.de/job/Protocolize/) 
[![GH Downloads](https://img.shields.io/github/downloads/Exceptionflug/Protocolize/total?color=GREEN&label=Downloads)](https://github.com/Exceptionflug/Protocolize/releases)
[![Twitter](https://img.shields.io/twitter/follow/Exceptionflug?label=Twitter&style=social)](https://twitter.com/Exceptionflug)

# Protocolize
A lightweight BungeeCord protocol framework supporting items.
## Purpose
Protocolize had been developed to raise the amount of game changing possibilities on BungeeCord platforms. With Protocolize it is possible to create custom inventories or interact with the player inventory itself. Protocolize is also ready for future upscaling! It is easy to self-implement custom packets using the *de.exceptionflug.protocolize.AbstractPacket* class.
## Compatibility
Protocolize has no currently known compatibility issues yet.
## How To Use
Please take a look at the examples which I made: https://github.com/Exceptionflug/protocolize/tree/master/protocolize-examples/src/main/java/de/exceptionflug/protocolize/example

### Maven
Add the following repository to your pom.xml:
```xml
<repository>
  <id>exceptionflug</id>
  <url>https://mvn.exceptionflug.de/repository/exceptionflug-public/</url>
</repository>
```
This is the full portfolio of protocolize dependencies:
```xml
<dependency>
  <groupId>de.exceptionflug</groupId>
  <artifactId>protocolize-api</artifactId>
  <version>1.6.2-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>de.exceptionflug</groupId>
  <artifactId>protocolize-items</artifactId>
  <version>1.6.2-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>de.exceptionflug</groupId>
  <artifactId>protocolize-inventory</artifactId>
  <version>1.6.2-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>de.exceptionflug</groupId>
  <artifactId>protocolize-world</artifactId>
  <version>1.6.2-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
```

### Listen for incoming and outgoing packets
At first you need to create a new PacketListener (eg. when you want to interact with chat messages)
```java
public class MyPacketAdapter extends PacketAdapter<Chat> {  
  public MyPacketListener() {  
    super(Stream.UPSTREAM, Chat.class);  
  }
}
```
and register it.
```java
ProtocolAPI.getEventManager().registerListener(new MyPacketAdapter());
```
Now you can override the following methods to access the packets.
```java
@Override  
public void receive(PacketReceiveEvent<Chat> event) {  
  // Receive on UPSTREAM means receiving a packet from a player  
  // Receive on DOWNSTREAM means receiving a packet from a server a player is connected to
}  
  
@Override  
public void send(PacketSendEvent<Chat> event) {  
  // Sending on UPSTREAM means sending a packet to a player  
  // Sending on DOWNSTREAM means sending a packet to a server a player is connected with
}
 ```
 For example if we want all actionbar messages printing in the chat instead we can use the following logic on `Stream.DOWNSTREAM`:
 ```java
public class MyPacketListener extends PacketAdapter<Chat> {  
  
   public MyPacketListener() {  
     super(Stream.DOWNSTREAM, Chat.class);  
   }  
  
   @Override  
   public void receive(PacketReceiveEvent<Chat> event) {  
     Chat packet = event.getPacket();  
     if(packet.getPosition() == 2) { // Position 2 means actionbar  
       packet.setPosition((byte) 0); // Set to normal chat  
       event.markForRewrite(); // We need to mark the packet for rewriting after we changed fields in the packet class. This is only necessary when receiving packets.  
     }  
   }  
}
 ```
