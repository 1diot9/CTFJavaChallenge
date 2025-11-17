package jakarta.el;

import java.util.EventListener;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ELContextListener.class */
public interface ELContextListener extends EventListener {
    void contextCreated(ELContextEvent eLContextEvent);
}
