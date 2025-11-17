package org.springframework.boot.web.embedded.tomcat;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.boot.web.server.Compression;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/CompressionConnectorCustomizer.class */
public class CompressionConnectorCustomizer implements TomcatConnectorCustomizer {
    private final Compression compression;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompressionConnectorCustomizer(Compression compression) {
        this.compression = compression;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
    public void customize(Connector connector) {
        if (this.compression != null && this.compression.getEnabled()) {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol) {
                customize((AbstractHttp11Protocol<?>) handler);
            }
        }
    }

    private void customize(AbstractHttp11Protocol<?> protocol) {
        Compression compression = this.compression;
        protocol.setCompression(CustomBooleanEditor.VALUE_ON);
        protocol.setCompressionMinSize(getMinResponseSize(compression));
        protocol.setCompressibleMimeType(getMimeTypes(compression));
        if (this.compression.getExcludedUserAgents() != null) {
            protocol.setNoCompressionUserAgents(getExcludedUserAgents());
        }
    }

    private int getMinResponseSize(Compression compression) {
        return (int) compression.getMinResponseSize().toBytes();
    }

    private String getMimeTypes(Compression compression) {
        return StringUtils.arrayToCommaDelimitedString(compression.getMimeTypes());
    }

    private String getExcludedUserAgents() {
        return StringUtils.arrayToCommaDelimitedString(this.compression.getExcludedUserAgents());
    }
}
