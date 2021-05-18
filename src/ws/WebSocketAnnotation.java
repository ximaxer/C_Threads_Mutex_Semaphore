package ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;

@ServerEndpoint(value = "/ws")
public class WebSocketAnnotation {
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private final String username;
    private Session session;
    private static final Set<WebSocketAnnotation> users = new CopyOnWriteArraySet<>();

    public WebSocketAnnotation() {
        username = "User" + sequence.getAndIncrement();
    }

    @OnOpen
    public void start(Session session) {
        users.add(this);
        this.session = session;
        String message = "Updates in real time";
        sendMessage(message);
    }

    @OnClose
    public void end(Session session) {
        users.remove(this);
        // clean up once the WebSocket connection is closed
    }

    @OnMessage
    public void receiveMessage(String message) {
        sendMessage(message);
    }

    @OnError
    public void handleError(Throwable t) {

        System.out.printf("cookies\n");
        t.printStackTrace();
    }

    private void sendMessage(String text) {
        // uses *this* object's session to call sendText()
        try {
            for(WebSocketAnnotation var : users) {
                var.session.getBasicRemote().sendText(text);
            }
        } catch (IOException e) {
            // clean up once the WebSocket connection is closed
            try {
                this.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
