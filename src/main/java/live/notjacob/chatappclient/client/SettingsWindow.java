package live.notjacob.chatappclient.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.prefs.Preferences;


public class SettingsWindow {

    private static final String green = "#00eb3f";
    private static final String red = "#ed1313";
    private Preferences prefs;
    @FXML
    public void initialize() {
        prefs = Preferences.userRoot().node("ChatAppClient");
    }

    @FXML
    private Button DarkModeButton;

}
