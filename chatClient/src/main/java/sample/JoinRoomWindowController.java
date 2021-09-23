package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class JoinRoomWindowController {
    public Button joinButton;
    public TextField roomNameField;
    private ServerConnection connection;
    private String username;
    private ChatWindowController mainWindowController;

    public JoinRoomWindowController() {
    }

    /**
     * Metoda otwiera nowe okno do wpisania pokoju
     * @param event
     * @throws IOException
     */
    @FXML
    protected void joinClick(ActionEvent event) throws IOException {
        String roomName = this.roomNameField.getText();
        //System.out.println(roomName);
        this.connection.joinRoom(this.username, roomName);
        mainWindowController.addRoom(roomName);
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


    public void setConnection(ServerConnection connection) {
        this.connection = connection;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMainWindowController(ChatWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public ServerConnection getConnection() {
        return connection;
    }

    public String getUsername() {
        return username;
    }
}
