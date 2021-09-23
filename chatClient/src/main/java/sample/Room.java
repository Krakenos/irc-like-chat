package sample;

import javafx.scene.control.TextArea;

/**
 * Klasa pojedynczego pokoju
 */
public class Room {
    private TextArea roomMessages;
    private String roomName;
    private TextArea roomUserlist;

    public Room(String roomName, TextArea roomUserlist) {
        this.roomName = roomName;
        this.roomUserlist = roomUserlist;
        this.roomMessages = new TextArea();
    }

    public Room(TextArea roomMessages, String roomName, TextArea roomUserlist) {
        this.roomMessages = roomMessages;
        this.roomName = roomName;
        this.roomUserlist = roomUserlist;
    }

    public Room(String roomName) {
        this.roomName = roomName;
        this.roomMessages = new TextArea();
        this.roomUserlist = new TextArea();
    }

    TextArea getRoomMessages() {
        return roomMessages;
    }

    void setRoomMessages(TextArea roomMessages) {
        this.roomMessages = roomMessages;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    TextArea getRoomUserlist() {
        return roomUserlist;
    }

    void setRoomUserlist(TextArea roomUserlist) {
        this.roomUserlist = roomUserlist;
    }
}
