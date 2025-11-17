package org.springframework.web.context.support;

import jakarta.servlet.ServletContext;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletContextAware;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletContextAttributeExporter.class */
public class ServletContextAttributeExporter implements ServletContextAware {
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private Map<String, Object> attributes;

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        if (this.attributes != null) {
            for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
                String attributeName = entry.getKey();
                if (this.logger.isDebugEnabled() && servletContext.getAttribute(attributeName) != null) {
                    this.logger.debug("Replacing existing ServletContext attribute with name '" + attributeName + "'");
                }
                servletContext.setAttribute(attributeName, entry.getValue());
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Exported ServletContext attribute with name '" + attributeName + "'");
                }
            }
        }
    }
}
