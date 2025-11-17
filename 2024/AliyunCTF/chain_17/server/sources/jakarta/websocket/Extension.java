package jakarta.websocket;

import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Extension.class */
public interface Extension {

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Extension$Parameter.class */
    public interface Parameter {
        String getName();

        String getValue();
    }

    String getName();

    List<Parameter> getParameters();
}
