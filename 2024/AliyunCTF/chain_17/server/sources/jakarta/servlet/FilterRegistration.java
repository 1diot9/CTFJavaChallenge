package jakarta.servlet;

import jakarta.servlet.Registration;
import java.util.Collection;
import java.util.EnumSet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/FilterRegistration.class */
public interface FilterRegistration extends Registration {

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/FilterRegistration$Dynamic.class */
    public interface Dynamic extends FilterRegistration, Registration.Dynamic {
    }

    void addMappingForServletNames(EnumSet<DispatcherType> enumSet, boolean z, String... strArr);

    Collection<String> getServletNameMappings();

    void addMappingForUrlPatterns(EnumSet<DispatcherType> enumSet, boolean z, String... strArr);

    Collection<String> getUrlPatternMappings();
}
