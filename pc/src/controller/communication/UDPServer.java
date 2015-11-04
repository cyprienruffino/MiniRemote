package controller.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by whiteshad on 27/10/15.
 * Serveur UDP servant a la reconnaissance du réseau par le client, permet au client d'optenir l'adresse IP du Serveur TCP
 * le client doit connaitre le port utilisé
 */
public class UDPServer {
    private final static int PORT=8888;
    public static final int RECEIVING_TIMEOUT = 10000;
    public static final int RECEIVING_TIMEOUT_SERVER = 30000;
   private DatagramSocket s=null;


    public UDPServer() {
        try {
            s = new DatagramSocket(PORT);
            s.setSoTimeout(RECEIVING_TIMEOUT_SERVER);
            System.out.println("serveur UDP lancé");
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void attendreRequete() {


        // On initialise les trames qui vont servir à recevoir et envoyer les paquets
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        System.out.println("En attente de Ping du client!");
        // Tant qu'on est connecté, on attend une requête et on y répond
        while (s != null && !s.isClosed()) {
            try
            {
                DatagramPacket paquetRecu = new DatagramPacket(receiveData, receiveData.length);
                s.receive(paquetRecu);
                System.out.println("packet reçu");
                String requete = new String(paquetRecu.getData());
                InetAddress IPAddress = paquetRecu.getAddress();
                int portExp = paquetRecu.getPort();
                // Si on reçoit un "ping", on répond "pong" à celui qui nous l'a envoyé
                if (requete.contains("Ping")) {
                    System.out.println("Ping reçu du client");
                    sendData = "Pong".getBytes();
                    DatagramPacket paquetRetour = new DatagramPacket(sendData, sendData.length, IPAddress, portExp);
                    s.send(paquetRetour);
                    System.out.println("reponse envoyé au client");
                    s.close();
                    System.out.println("Serveur UDP fermé");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
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