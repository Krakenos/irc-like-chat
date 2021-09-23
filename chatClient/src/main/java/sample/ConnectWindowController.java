package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


public class ConnectWindowController {
    public TextField roomField;
    public Label errorLabel;
    @FXML
    private TextField usernameField;

    /**
     * Funkcja nawiązuje połączenie z serwerem, przekazuje dane do następnego kontrolera, oraz otwiera nowe okno
     * @param event
     */
    @FXML
    protected void connectClick(ActionEvent event) {
        final String username = usernameField.getText();
        final String room = roomField.getText();
        //System.out.println("Sending username " + username + " to the server");
        if (verifyFields()) {
            final ServerConnection connection;
            try {
                connection = new ServerConnection();
                connection.connect(username, room);
                Stage secondaryStage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                Pane root = loader.load(getClass().getResource("/ChatWindow.fxml").openStream());
                ChatWindowController controller = loader.getController();
                controller.setUsername(username);
                controller.setConnection(connection);
                controller.setCurrentRoomName(room);
                controller.initRoomView(room);
                controller.startListening();
                secondaryStage.setTitle("Chat Window");
                secondaryStage.setScene(new Scene(root, 740, 400));
                secondaryStage.show();
                secondaryStage.setOnHiding(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent event) {
                        try {
                            connection.disconnect(username, room);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ((Node) (event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                errorLabel.setText("Cannot connect to the server");
                errorLabel.setTextFill(Color.RED);
            }
        }
    }

    @FXML
    protected void onEnter(ActionEvent ae) throws IOException {
        connectClick(ae);
    }

    /**
     * @return Zwraca czy pola są wypełnione prawidłowo
     */
    protected boolean verifyFields() {
        if (usernameField.getText().equals("") || roomField.getText().equals("")) {
            errorLabel.setText("Username and Room fields \n can't be empty!");
            errorLabel.setTextFill(Color.RED);
            return false;
        } else {
            return true;
        }
    }

}
