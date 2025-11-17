package jakarta.websocket;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/MessageHandler.class */
public interface MessageHandler {

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/MessageHandler$Partial.class */
    public interface Partial<T> extends MessageHandler {
        void onMessage(T t, boolean z);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/MessageHandler$Whole.class */
    public interface Whole<T> extends MessageHandler {
        void onMessage(T t);
    }
}
