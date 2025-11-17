package jakarta.el;

import java.util.EventObject;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ELContextEvent.class */
public class ELContextEvent extends EventObject {
    private static final long serialVersionUID = 1255131906285426769L;

    public ELContextEvent(ELContext source) {
        super(source);
    }

    public ELContext getELContext() {
        return (ELContext) getSource();
    }
}
