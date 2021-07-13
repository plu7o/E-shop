package shop.server.net;

import shop.common.interfaces.ShopInterface;
import shop.server.domain.Shop;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MiniShopServer {

    public final static int DEFAULT_PORT = 6789;

    protected int port;
    protected ServerSocket serverSocket;
    private ShopInterface shop;

    public MiniShopServer(int port) throws IOException {

        shop = new Shop("DATA");

        if (port == 0)
            port = DEFAULT_PORT;
        this.port = port;

        try {
            serverSocket = new ServerSocket(port);

            InetAddress ia = InetAddress.getLocalHost();
            System.out.println("Host: " + ia.getHostName());
            System.out.println("Server: " + ia.getHostAddress() + "* listens on Port: " + port);
        } catch (IOException e) {
            fail(e, "An exception occurred while creating the server socket");
        }
    }

        /**
         * Methode zur Entgegennahme von Verbindungswünschen durch Clients.
         * Die Methode fragt wiederholt ab, ob Verbindungsanfragen vorliegen
         * und erzeugt dann jeweils ein ClientRequestProcessor-Objekt mit dem
         * für diese Verbindung erzeugten Client-Socket.
         */
        public void acceptClientConnectRequests() {

            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ClientRequestProcessor c = new ClientRequestProcessor(clientSocket, shop);
                    Thread t = new Thread(c);
                    t.start();
                }
            } catch (IOException e) {
                fail(e, "Error while listening for connections");
            }
        }

        // Standard-Exit im Fehlerfall:
        private static void fail(Exception e, String msg) {
            System.err.println(msg + ": " + e);
            System.exit(1);
        }

        public static void main(String[] args) {
            int port = 0;
            if (args.length == 1) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    port = 0;
                }
            }
            try {
                MiniShopServer server = new MiniShopServer(port);
                server.acceptClientConnectRequests();
            } catch (IOException e) {
                e.printStackTrace();
                fail(e, " - MiniShopServer generation");
            }
        }
    }
