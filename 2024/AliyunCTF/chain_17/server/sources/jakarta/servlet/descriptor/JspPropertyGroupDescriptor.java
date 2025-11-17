package jakarta.servlet.descriptor;

import java.util.Collection;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/descriptor/JspPropertyGroupDescriptor.class */
public interface JspPropertyGroupDescriptor {
    Collection<String> getUrlPatterns();

    String getElIgnored();

    String getErrorOnELNotFound();

    String getPageEncoding();

    String getScriptingInvalid();

    String getIsXml();

    Collection<String> getIncludePreludes();

    Collection<String> getIncludeCodas();

    String getDeferredSyntaxAllowedAsLiteral();

    String getTrimDirectiveWhitespaces();

    String getDefaultContentType();

    String getBuffer();

    String getErrorOnUndeclaredNamespace();
}
