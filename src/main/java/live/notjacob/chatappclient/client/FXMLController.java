package live.notjacob.chatappclient.client;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class FXMLController {

    private static FXMLController instance;
    private boolean connectError = false;
    private boolean connected = false;
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
    public void setConnected(boolean connected) {
        this.connected=connected;
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
    private VBox Window;

    @FXML
    private Button SendButton;

    @FXML
    private ImageView Settings;

    private  Timeline settingsAnimation;
    private Timeline settingsAnimationReverse;

    private void notifyConnections() {
        if (connectError) {
            addToMessages("System: could not connect to server");
        } else {
            addToMessages("System: connected to server");
        }
    }
    private void addEnterKeyEvent() {
        Window.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (connected) {
                    if (!Objects.equals(Message.getText(), "")) {
                        String text = Message.getText();
                        Message.setText("");
                        addToMessages(YourUsername.getText() + ": " + text);
                        client.write(text);
                    }
                } else {
                    if (!Objects.equals(YourUsername.getText(), "") && !Objects.equals(FriendUsername.getText(), "")) {
                        YourUsername.setDisable(true);
                        FriendUsername.setDisable(true);
                        ConnectButton.setDisable(true);
                        client.submitConnection(YourUsername.getText(), FriendUsername.getText());
                    } else {
                        addToMessages("\nSystem: Please enter usernames");
                    }
                }
            }
        });
    }
    private void addSettingsHoverEvent() {
        Settings.setOnMouseEntered(e -> {
            settingsAnimation.play();
        });
        Settings.setOnMouseExited(e -> {
            settingsAnimationReverse.play();
        });
    }
    private void createAnimations() {
        settingsAnimationReverse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(Settings.rotateProperty(), 90)),
                new KeyFrame(Duration.millis(100), new KeyValue(Settings.rotateProperty(), 0))
        );
        settingsAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(Settings.rotateProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(Settings.rotateProperty(), 90))
        );
    }
    @FXML
    private void initialize() {
        notifyConnections();
        addEnterKeyEvent();
        createAnimations();
        addSettingsHoverEvent();
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
    private void onSettingsPress() {

    }

    public void enableMessaging() {
        Message.setDisable(false);
        SendButton.setDisable(false);
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