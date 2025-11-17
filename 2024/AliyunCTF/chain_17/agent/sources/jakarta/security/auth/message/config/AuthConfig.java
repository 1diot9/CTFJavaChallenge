package jakarta.security.auth.message.config;

import jakarta.security.auth.message.MessageInfo;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/config/AuthConfig.class */
public interface AuthConfig {
    String getMessageLayer();

    String getAppContext();

    String getAuthContextID(MessageInfo messageInfo);

    void refresh();

    boolean isProtected();
}
