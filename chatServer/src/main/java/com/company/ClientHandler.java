package com.company;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Klasa odpowieadająca za obsługe poszczególnych klientów
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;

    public String getUsername() {
        return username;
    }

    /**
     * @param socket Socket klienta którego mamy obsłużyć
     */
    ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.br = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new
                    OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Funkcja która obsługuje wiadomości przyszyłane przez klienta
     */
    @Override
    public void run() {
        while (true) {
            try {
                if (this.br.ready()) {
                    JSONObject unparsedMessage = new JSONObject(this.br.readLine());
                    String signature = unparsedMessage.getString("signature");
                    String user = unparsedMessage.getString("username");
                    String roomName = unparsedMessage.getString("room");
                    Room room = Main.roomMap.get(roomName);
                    if (signature.equals("Message")) {
                        resendMessages(unparsedMessage, user, room);
                    } else if (signature.equals("Logout")) {
                        logoutUser(user);
                        break;
                    } else if (signature.equals("Login")) {
                        loginUser(user, roomName, room);
                        joinRoom(roomName, room);
                    } else if (signature.equals("Join")) {
                        joinRoom(roomName, room);
                    } else if (signature.equals("Quit")) {
                        quitRoom(roomName, room);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param roomName Nazwa pokoju z którego wychodzimy
     * @param room Obiekt pokoju z którego wychodzimy
     * @throws IOException
     */
    private void quitRoom(String roomName, Room room) throws IOException {
        room.removeClient(this);
        if (room.isEmpty()) {
            System.out.println("DELETING ROOM " + roomName);
            Main.roomMap.remove(roomName);
        } else {
            sendUserlist(room);
        }
    }

    /**
     * @param user Nazwa użytkownika który się loguje
     * @param roomName Nazwa pierwszego pokoju do którego się loguje
     * @param room obiekt pierwszego pokoju do którego się loguje
     * @throws IOException
     */
    private void loginUser(String user, String roomName, Room room) throws IOException {
        System.out.println("LOGIN " + user);
        this.username = user;
        Main.usersMap.put(this.username, this);
    }

    /**
     * @param user Nazwa użytkownika
     * @throws IOException
     */
    private void logoutUser(String user) throws IOException {
        System.out.println("LOGOUT " + user);
        Main.usersMap.remove(this.username);
        //System.out.println(Main.roomMap.values());
        ArrayList<Room> rooms = new ArrayList<Room>(Main.roomMap.values());
        for (Room room : rooms) {
            if (room.hasClient(this)) {
                room.removeClient(this);
                sendUserlist(room);
            }
            if (room.isEmpty()) {
                System.out.println("DELETING ROOM " + room.getName());
                Main.roomMap.remove(room.getName());
            } else {
                sendUserlist(room);
            }

        }
        this.br.close();
        this.bw.close();
    }

    /**
     * @param unparsedMessage Wiadomość w formacie JSON prosto od poprzedniego użytkownika, serwer ją tylko przekazuje
     * @param user Nazwa użytkownika który wysłał wiadomość
     * @param room Obiekt pokoju do którego wiadomość jest kierowana
     * @throws IOException
     */
    private void resendMessages(JSONObject unparsedMessage, String user, Room room) throws IOException {
        String message = unparsedMessage.getString("message");
        System.out.println("MESSAGE " + room.getName() + " " + user + ": " + message);
        for (ClientHandler client : room.getRoomClients()) {
            client.bw.write(unparsedMessage.toString());
            client.bw.newLine();
            client.bw.flush();
        }
    }

    /**
     * @param roomName Nazwa pokoju do którego dołączamy
     * @param room Obiekt pokoju do którego dołączamy
     * @throws IOException
     */
    private void joinRoom(String roomName, Room room) throws IOException {
        if (room == null) {
            System.out.println("CREATING ROOM " + roomName);
            Room newRoom = new Room(roomName);
            newRoom.addClient(this);
            Main.roomMap.put(roomName, newRoom);
            sendUserlist(newRoom);
        } else {
            System.out.println("JOINING ROOM USER: " + this.username + " ROOM: " + roomName);
            room.addClient(this);
            sendUserlist(room);
        }
    }

    /**
     * @param room Obiekt pokoju do którego jest rozesłana informacja o liście użytkowników
     * @throws IOException
     */
    private void sendUserlist(Room room) throws IOException {
        JSONObject userlistMessage = new JSONObject();
        userlistMessage.put("signature", "Userlist");
        userlistMessage.put("room", room.getName());
        JSONArray userlist = new JSONArray();
        //System.out.println("here");
        ArrayList<ClientHandler> roomClients = room.getRoomClients();
        for (ClientHandler client : roomClients) {
            String name = client.getUsername();
            userlist.put(name);
        }
        userlistMessage.put("userlist", userlist);
        for (ClientHandler client : roomClients) {
            //System.out.println(userlistMessage.toString());
            client.bw.write(userlistMessage.toString());
            client.bw.newLine();
            client.bw.flush();
        }
    }
}
