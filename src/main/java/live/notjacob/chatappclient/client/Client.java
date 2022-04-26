package live.notjacob.chatappclient.client;

import live.notjacob.chatappclient.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Client {

    private static Client instance;
    public Client() {
        instance=this;
    }
    public static Client get() {
        return instance;
    }

    private PrintWriter out;
    private BufferedReader in;
    String username = "null";
    String otherUser = "null";
    private boolean continueReadLoop = true;
    private volatile boolean canDisconnect = false;
    private final Thread readThread = new Thread(() -> {
        try {
            readLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    public void start(String ip, int port) throws IOException, InterruptedException {
        try {
            Socket client = new Socket(ip, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                out.println(new Packet("disconnected").addValue("user", username));
                while (!canDisconnect) {
                    Thread.onSpinWait();
                }
            }));
        } catch (Exception e) {
            FXMLController.get().couldNotConnect();
            continueReadLoop = false;
        }
        if (continueReadLoop) {
            while (!in.ready()) {
                Thread.onSpinWait();
            }
            readThread.start();
            Thread.currentThread().join();
            readLoop();
        }
    }

    public void readLoop() throws IOException {
        Packet p = Packet.from(in.readLine());
        System.out.println("Received packet: " + p);
        switch (p.topic()) {
            case "connection success" -> {
                FXMLController.get().addToMessages("System: Waiting for " + otherUser);
                FXMLController.get().setConnected(true);
            }
            case "chat start" -> {
                out.println(new Packet("connection confirm"));
                FXMLController.get().addToMessages("System: " + otherUser + " has connected");
                FXMLController.get().enableMessaging();
            }
            case "message" -> FXMLController.get().addToMessages(otherUser + ": " + new String(Base64.getDecoder().decode(p.getValue("message"))));
            case "disconnect safe" -> {
                canDisconnect = true;
                continueReadLoop = false;
                FXMLController.get().addToMessages("System: disconnected");
                FXMLController.get().setConnected(false);
            }
            case "other disconnect" -> {
                FXMLController.get().addToMessages("System: " + otherUser + " disconnected");
                out.println(new Packet("disconnected"));
            }
        }
        if (continueReadLoop) readLoop();
    }

    public void write(String msg) {
        out.println(new Packet("message").addValue("message", Base64.getEncoder().encodeToString(msg.getBytes(StandardCharsets.UTF_8))));
    }
    public void disconnect() {
        out.println(new Packet("disconnected").addValue("user", username));
        while (!canDisconnect) {
            Thread.onSpinWait();
        }
    }
    public void submitConnection(String username, String friendUsername) {
        this.username=username;
        this.otherUser=friendUsername;
        out.println(new Packet("connection info")
                .addValue("username", username)
                .addValue("searchingFor", friendUsername)
                .toString());
    }
}