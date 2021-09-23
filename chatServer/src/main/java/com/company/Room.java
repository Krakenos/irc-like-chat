package com.company;

import java.util.ArrayList;

/**
 * Klasa pojedynczego pokoju
 */
public class Room {
    private ArrayList<ClientHandler> roomClients;
    private String name;

    public Room() {
        this.roomClients = new ArrayList<ClientHandler>();
    }

    public Room(String name) {
        this.roomClients = new ArrayList<ClientHandler>();
        this.name = name;
    }

    public ArrayList<ClientHandler> getRoomClients() {
        return roomClients;
    }

    public void setRoomClients(ArrayList<ClientHandler> roomClients) {
        this.roomClients = roomClients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addClient(ClientHandler handler) {
        this.roomClients.add(handler);
    }

    public void removeClient(int i) {
        this.roomClients.remove(i);
    }

    public void removeClient(ClientHandler handler) {
        this.roomClients.remove(handler);
    }

    public boolean isEmpty() {
        return this.roomClients.isEmpty();
    }

    public boolean hasClient(ClientHandler client) {
        return roomClients.contains(client);
    }
}
