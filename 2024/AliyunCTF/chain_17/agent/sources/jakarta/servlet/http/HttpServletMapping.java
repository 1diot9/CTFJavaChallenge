package jakarta.servlet.http;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServletMapping.class */
public interface HttpServletMapping {
    String getMatchValue();

    String getPattern();

    String getServletName();

    MappingMatch getMappingMatch();
}
