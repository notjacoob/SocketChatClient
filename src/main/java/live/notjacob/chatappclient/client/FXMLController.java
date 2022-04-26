package live.notjacob.chatappclient.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.util.Objects;

public class FXMLController {

    private static FXMLController instance;
    private boolean connectError = false;
    private final Client client;
    public FXMLController() {
        instance=this;
        client = new Client();
        new Thread(() -> {
            try {
                client.start("127.0.0.1", 4321);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static FXMLController get() {
        return instance;
    }

    @FXML
    private TextField Message;

    @FXML
    private TextField YourUsername;

    @FXML
    private TextField FriendUsername;

    @FXML
    private Button ConnectButton;

    @FXML
    private TextArea MessageBox;

    @FXML
    private void initialize() {
        if (connectError) {
            addToMessages("System: could not connect to server");
        } else {
            addToMessages("System: connected to server");
        }
        Message.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (!Objects.equals(Message.getText(), "")) {
                    String text = Message.getText();
                    Message.setText("");
                    addToMessages(YourUsername.getText() + ": " + text);
                    client.write(text);
                }
            }
        });
    }

    @FXML
    private void onConnectPress() {
        if (!Objects.equals(YourUsername.getText(), "") && !Objects.equals(FriendUsername.getText(), "")) {
            YourUsername.setDisable(true);
            FriendUsername.setDisable(true);
            ConnectButton.setDisable(true);
            client.submitConnection(YourUsername.getText(), FriendUsername.getText());
        } else {
            addToMessages("\nSystem: Please enter usernames");
        }
    }

    @FXML
    private void onSendPress() {
        if (!Objects.equals(Message.getText(), "")) {
            String text = Message.getText();
            Message.setText("");
            addToMessages(YourUsername.getText() + ": " + text);
            client.write(text);
        }
    }

    public void addToMessages(String msg) {
        MessageBox.setText(MessageBox.getText() + "\n" + msg);
    }
    public void couldNotConnect() {
        connectError = true;
    }

}