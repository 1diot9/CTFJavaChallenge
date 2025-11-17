package jakarta.servlet;

import jakarta.servlet.Registration;
import java.util.Collection;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletRegistration.class */
public interface ServletRegistration extends Registration {

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletRegistration$Dynamic.class */
    public interface Dynamic extends ServletRegistration, Registration.Dynamic {
        void setLoadOnStartup(int i);

        Set<String> setServletSecurity(ServletSecurityElement servletSecurityElement);

        void setMultipartConfig(MultipartConfigElement multipartConfigElement);

        void setRunAsRole(String str);
    }

    Set<String> addMapping(String... strArr);

    Collection<String> getMappings();

    String getRunAsRole();
}
