module live.notjacob.chatappclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens live.notjacob.chatappclient to javafx.fxml;
    exports live.notjacob.chatappclient;
    exports live.notjacob.chatappclient.client;
    opens live.notjacob.chatappclient.client to javafx.fxml;
}