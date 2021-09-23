package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Main {
    static HashMap<String, ClientHandler> usersMap = new HashMap<String, ClientHandler>();
    static HashMap<String, Room> roomMap = new HashMap<String, Room>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(2555);
        while (true) {
            Socket socket = server.accept();
            System.out.println("Accepted new request " + socket);
            ClientHandler client = new ClientHandler(socket);
            client.start();
        }
    }
}
