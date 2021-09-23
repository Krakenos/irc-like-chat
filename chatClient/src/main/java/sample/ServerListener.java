package sample;

import javafx.scene.control.TextArea;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Klasa wątku odpowiedzialnego za nasłuchiwanie serwera
 */
public class ServerListener extends Thread {
    private ChatWindowController controller;
    private BufferedReader br;

    ServerListener(ChatWindowController controller, BufferedReader br) {
        this.br = br;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (this.br.ready()) {
                    JSONObject message = new JSONObject(this.br.readLine());
                    String signature = message.getString("signature");
                    String roomName = message.getString("room");
                    if (signature.equals("Message")) {
                        String user = message.getString("username");
                        String text = message.getString("message");
                        String line = user + ": " + text + "\n";
                        //System.out.println(line);
                        Room room = this.controller.getRoomHashMap().get(roomName);
                        TextArea roomMessages = room.getRoomMessages();
                        roomMessages.appendText(line);
                        //this.controller.displayedMessages.appendText(line);
                        room.setRoomMessages(roomMessages);
                        this.controller.refreshWindow();
                    } else if (signature.equals("Userlist")) {
                        Room room = this.controller.getRoomHashMap().get(roomName);
                        TextArea userlist = room.getRoomUserlist();
                        userlist.clear();
                        //this.controller.userlist.clear();
                        //System.out.println("here");
                        JSONArray activeUsers = message.getJSONArray("userlist");
                        for (int i = 0; i < activeUsers.length(); i++) {
                            String name = activeUsers.getString(i) + "\n";
                            userlist.appendText(name);
                            this.controller.refreshWindow();
                            //this.controller.userlist.appendText(name);

                        }

                        room.setRoomUserlist(userlist);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
