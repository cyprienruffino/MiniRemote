package controller.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

/**
 * Created by whiteshad on 27/10/15.
 * Server UDP servant a la reconnaissance du réseau par le client, permet au client d'optenir l'adresse IP du Serveur TCP
 * le client doit connaitre le port utilisé
 */
public class UDPServer {
    private final static int PORT=8888;
   private DatagramSocket s=null;
   private ServerSocket tcpServer=null;

    public UDPServer(ServerSocket tcpServer) {
        this.tcpServer = tcpServer;
        try {
            s = new DatagramSocket(PORT);
            System.out.println("serveur UDP en attente de communication");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    
    public void start() {
        byte[] helloBuff = new byte[5];
        DatagramPacket packet = new DatagramPacket(helloBuff, helloBuff.length);

        boolean wait = true;
        while (wait) {
            try {
                s.receive(packet);

                if ("hello".equals(new String(packet.getData()))) {
                    wait = false;
                    System.out.println("packet recut du client");
                } else {
                    System.out.println("le packet recu ne correspond pas au attente du serveur");
                }
               
                DatagramPacket data = buildDataPacket(packet);

                s.send(data);

                System.out.println("reponse envoyé au client");
                System.out.println("en attente confirmation client");

                s.setSoTimeout(2000);
                wait = true;
                byte[] okBuff = new byte[2];
                while (wait) {
                    DatagramPacket getack = new DatagramPacket(okBuff, okBuff.length);
                    try {
                        s.receive(getack);
                    } catch (SocketTimeoutException e) {
                        // resend
                        System.out.println("pas de réponse du client nouvelle tentative");
                        s.send(data);
                        continue;
                    }
                    // check received data...
                    if ("ok".equals(new String(getack.getData()))) {
                        wait = false;
                        System.out.println("packet recut du client");
                    } else {
                        if ("hello".equals(new String(packet.getData()))) {
                            System.out.println("le client n'a pas recu le packet precedent");
                            s.send(data);
                        } else {
                            System.out.println("le packet recu ne correspond pas au attente du serveur");
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //construit un datapacket contenant l'ip et le port d'écoute du server TCP
    private DatagramPacket buildDataPacket(DatagramPacket packet){
        byte[] serverIp = tcpServer.getInetAddress().getAddress();
        ByteBuffer c = ByteBuffer.allocate(4 + serverIp.length);
        c.put(serverIp);
        c.putInt(tcpServer.getLocalPort());
        byte[] rep = c.array();
        return new DatagramPacket(rep, rep.length, packet.getAddress(), packet.getPort());
    }


}
/*
Echange UDP pour préparer la connection

(1)server listens on a pre-arranged port

DatagramSocket s = new DatagramSocket(8888);
s.receive  //(1)
s.send     //(2)

(3)client sends a message to the port, on the broadcast IP, 255.255.255.255

DatagramSocket c = new DatagramSocket();
c.send(255.255.255.255:8888,msg)     //(3)
c.receive  //(4)

the client binds to a port too. we didn't specify it, so it's random chosen for us.

(3) will broadcast the message to all local machines, server at (1) receives message, with the client IP:port.

(2) server sends response message to client IP:port

(4) client gets the reponse message from server.


 */