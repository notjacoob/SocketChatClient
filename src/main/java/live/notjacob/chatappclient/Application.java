package live.notjacob.chatappclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import live.notjacob.chatappclient.client.Client;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("client.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnHiding(event -> {
            Client.get().disconnect();
            System.exit(0);
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}