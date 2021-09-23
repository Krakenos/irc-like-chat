package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class ChatWindowController {
    public TextArea userlist;
    public Button sendButton;
    public TreeView<String> roomView;
    public Button joinRoomButton;
    public Label usernameLabel;
    private String username;
    private ServerConnection connection;
    @FXML
    private TextField inputText;
    @FXML
    public TextArea displayedMessages;
    private String currentRoomName;
    private HashMap<String, Room> roomHashMap = new HashMap<String, Room>();

    public HashMap<String, Room> getRoomHashMap() {
        return roomHashMap;
    }

    public void setCurrentRoomName(String currentRoomName) {
        this.currentRoomName = currentRoomName;
    }

    void setUsername(String username) {
        this.username = username;
        this.usernameLabel.setText(username + ":");
    }

    void setConnection(ServerConnection connection) {
        this.connection = connection;
    }

    /**
     * Metoda inicjująca widok drzewa pokoi
     * @param initialRoom Nazwa pierwszego pokoju do którego dołącza użytkownik
     */
    public void initRoomView(String initialRoom) {
        TreeItem<String> root, roomBranch;
        root = new TreeItem<String>();
        root.setExpanded(true);
        roomBranch = makeBranch("Rooms", root);
        makeBranch(initialRoom, roomBranch);
        Room newRoom = new Room(initialRoom);
        this.roomHashMap.put(initialRoom, newRoom);
        //System.out.println("hello there");
        this.roomView.setRoot(root);
        this.roomView.setShowRoot(false);
        this.roomView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem<String>>() {
                    public void changed(ObservableValue<? extends TreeItem<String>> v, TreeItem<String> oldValue, TreeItem<String> newValue) {
                        if (newValue != null && !newValue.getValue().equals("Rooms")) {
                            currentRoomName = newValue.getValue();
                            changeRoom(newValue.getValue());
                        }
                    }
                }
        );
        MultipleSelectionModel msm = this.roomView.getSelectionModel();
        msm.select(1);
        this.currentRoomName = initialRoom;
    }

    /**
     * Funkcja dodająca potomka do rodzica w drzewie
     * @param title Tekst potomka
     * @param parent Rodzic
     * @return
     */
    private TreeItem<String> makeBranch(String title, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem<String>(title);
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }

    /**
     * Funkcja wysyłająca wiadomość do serwera, jest pośrednikiem między kontrolerem a klasą ServerConnection
     * @throws IOException
     */
    @FXML
    protected void sendMessage() throws IOException {
        String message = inputText.getText();
        if (!message.equals("")) {
            inputText.clear();
            this.connection.sendMessage(this.username, message, this.currentRoomName);
        }
    }

    /**
     * Funkcja rozpoczynająca nasłuchiwanie serwera, pośrednik między kontrolerem a klasą ServerConnection
     * @throws IOException
     */
    @FXML
    void startListening() throws IOException {
        this.connection.listener(this);
    }

    @FXML
    protected void onEnter() throws IOException {
        sendMessage();
    }

    public void addRoom(String name) {
        Room newRoom = new Room(name);
        this.roomHashMap.put(name, newRoom);
        updateRoomView();
    }

    /**
     * Funkcja odświeżająca widok pokoi
     */
    private void updateRoomView() {
        TreeItem<String> root, roomBranch, last_item;
        root = new TreeItem<String>();
        last_item = new TreeItem<String>();
        root.setExpanded(true);
        roomBranch = makeBranch("Rooms", root);
        for (String roomName : this.roomHashMap.keySet()) {
            last_item = makeBranch(roomName, roomBranch);
        }
        this.roomView.setRoot(root);
        MultipleSelectionModel msm = roomView.getSelectionModel();
        int row = roomView.getRow(last_item);
        msm.select(row);
    }

    @FXML
    protected void joinRoomClick() throws IOException {
        Stage secondaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = loader.load(getClass().getResource("/JoinRoomWindow.fxml").openStream());
        JoinRoomWindowController controller = loader.getController();
        controller.setConnection(this.connection);
        controller.setUsername(this.username);
        controller.setMainWindowController(this);
        secondaryStage.setTitle("Join new room");
        secondaryStage.setScene(new Scene(root, 208, 145));
        secondaryStage.show();
    }

    private void changeRoom(String roomName) {
        String roomText = this.roomHashMap.get(roomName).getRoomMessages().getText();
        String userlistText = this.roomHashMap.get(roomName).getRoomUserlist().getText();
        this.currentRoomName = roomName;
        this.displayedMessages.setText(roomText);
        this.userlist.setText(userlistText);
    }

    @FXML
    protected void quitRoomClick() throws IOException {
        if (this.roomHashMap.size() > 1) {
            this.connection.quitRoom(this.username, this.currentRoomName);
            this.roomHashMap.remove(this.currentRoomName);
            this.updateRoomView();
            MultipleSelectionModel msm = this.roomView.getSelectionModel();
            msm.select(1);
            String roomName = this.roomView.getSelectionModel().getSelectedItem().getValue();
            this.displayedMessages.setText(roomHashMap.get(roomName).getRoomMessages().getText());
            this.userlist.setText(roomHashMap.get(roomName).getRoomUserlist().getText());
        }
    }


    /**
     * Funkcja która odświeża okno
     */
    void refreshWindow() {
        changeRoom(currentRoomName);
    }
}
