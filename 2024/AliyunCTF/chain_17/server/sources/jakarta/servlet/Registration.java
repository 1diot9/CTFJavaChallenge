package jakarta.servlet;

import java.util.Map;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/Registration.class */
public interface Registration {

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/Registration$Dynamic.class */
    public interface Dynamic extends Registration {
        void setAsyncSupported(boolean z);
    }

    String getName();

    String getClassName();

    boolean setInitParameter(String str, String str2);

    String getInitParameter(String str);

    Set<String> setInitParameters(Map<String, String> map);

    Map<String, String> getInitParameters();
}
