/* (C) 2021 DragonSkulle */
package org.dragonskulle.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * SocketStore is for storing all the information about connected clients. It stores all of the
 * sockets and can close and broadcast to all clients. It should be used alongside Server, it is the
 * backbone of the server functions.
 */
public class SocketStore {
    private ServerSocket server;
    private final ArrayList<Socket> store;
    PrintWriter printWriter;
    static final int SO_TIMEOUT = 3000;

    public SocketStore() {
        this.store = new ArrayList<>();
    }

    public void broadcast(byte[] buf) {
        System.out.println("Broadcasting bytes");
        DataOutputStream dOut;
        for (Socket connection : store) {
            try {
                if (connection.isClosed()) {
                    System.out.println("Client socket output has closed");
                }
                System.out.println("--broadcasting to client " + connection.toString());
                dOut = new DataOutputStream(connection.getOutputStream());
                dOut.write(buf);
                System.out.println("--broadcast success");

            } catch (IOException e) {
                System.out.println("Error in broadcasting");
                System.out.println(e.toString());
            }
        }
    }

    public void initServer(ServerSocket serverSocket) {
        try {
            this.server = serverSocket;
            this.server.setSoTimeout(SO_TIMEOUT);
            System.out.println("[SS] Server created @ " + serverSocket.getLocalSocketAddress());
        } catch (SocketException e) {
            System.out.println("Failed to create server");
            e.printStackTrace();
        }
    }

    public void addClient(Socket sock) {
        // TODO add check for invalid socket
        System.out.println("Adding client");
        System.out.println("Socket :" + sock.toString());
        this.store.add(sock);
    }

    public int getServerPort() {
        return this.server.getLocalPort();
    }

    public void close() {
        try {
            this.server.close();
        } catch (Exception ignored) {
        }

        this.store.clear();
        this.server = null;
    }

    private void shutdownSocket(Socket socket) {
        try {
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerIp() {
        try {
            this.server.getInetAddress();
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Socket acceptClient() {
        try {
            return this.server.accept();
        } catch (IOException ignored) {
        }
        return null;
    }

    public boolean terminateClient(Socket sock) {
        // if client connection failed, close the socket and remove
        this.shutdownSocket(sock);
        this.store.remove(sock);
        return true;
    }

    public void removeClient(Socket sock) {
        // only remove client from store as socket has closed
        if (!sock.isClosed()) {
            this.terminateClient(sock);
        } else {
            this.store.remove(sock);
        }
    }

    public void sendBytesToClient(ClientInstance client, byte[] response_bytes) {
        for (Socket sock : this.store) {
            if (sock.getPort() == client.PORT && sock.getInetAddress() == client.IP) {
                System.out.println("Sending bytes to client");
                try {
                    DataOutputStream dOut = new DataOutputStream(sock.getOutputStream());
                    dOut.write(response_bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
