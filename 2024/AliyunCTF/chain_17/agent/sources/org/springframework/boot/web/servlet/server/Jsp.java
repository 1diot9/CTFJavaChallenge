package org.springframework.boot.web.servlet.server;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.core.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/Jsp.class */
public class Jsp {
    private String className = Constants.JSP_SERVLET_CLASS;
    private Map<String, String> initParameters = new HashMap();
    private boolean registered = true;

    public Jsp() {
        this.initParameters.put("development", "false");
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, String> getInitParameters() {
        return this.initParameters;
    }

    public void setInitParameters(Map<String, String> initParameters) {
        this.initParameters = initParameters;
    }

    public boolean getRegistered() {
        return this.registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}
