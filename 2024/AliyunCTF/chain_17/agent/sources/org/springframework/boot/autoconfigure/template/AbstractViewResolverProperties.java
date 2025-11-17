package org.springframework.boot.autoconfigure.template;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.util.MimeType;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/template/AbstractViewResolverProperties.class */
public abstract class AbstractViewResolverProperties {
    private static final MimeType DEFAULT_CONTENT_TYPE = MimeType.valueOf("text/html");
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private boolean cache;
    private String[] viewNames;
    private boolean enabled = true;
    private MimeType contentType = DEFAULT_CONTENT_TYPE;
    private Charset charset = DEFAULT_CHARSET;
    private boolean checkTemplateLocation = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setCheckTemplateLocation(boolean checkTemplateLocation) {
        this.checkTemplateLocation = checkTemplateLocation;
    }

    public boolean isCheckTemplateLocation() {
        return this.checkTemplateLocation;
    }

    public String[] getViewNames() {
        return this.viewNames;
    }

    public void setViewNames(String[] viewNames) {
        this.viewNames = viewNames;
    }

    public boolean isCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public MimeType getContentType() {
        if (this.contentType.getCharset() == null) {
            Map<String, String> parameters = new LinkedHashMap<>();
            parameters.put(BasicAuthenticator.charsetparam, this.charset.name());
            parameters.putAll(this.contentType.getParameters());
            return new MimeType(this.contentType, parameters);
        }
        return this.contentType;
    }

    public void setContentType(MimeType contentType) {
        this.contentType = contentType;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getCharsetName() {
        if (this.charset != null) {
            return this.charset.name();
        }
        return null;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
