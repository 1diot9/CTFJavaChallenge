package org.apache.tomcat.util.descriptor.web;

import jakarta.servlet.descriptor.JspPropertyGroupDescriptor;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/JspPropertyGroupDescriptorImpl.class */
public class JspPropertyGroupDescriptorImpl implements JspPropertyGroupDescriptor {
    private final JspPropertyGroup jspPropertyGroup;

    public JspPropertyGroupDescriptorImpl(JspPropertyGroup jspPropertyGroup) {
        this.jspPropertyGroup = jspPropertyGroup;
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getBuffer() {
        return this.jspPropertyGroup.getBuffer();
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getDefaultContentType() {
        return this.jspPropertyGroup.getDefaultContentType();
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getDeferredSyntaxAllowedAsLiteral() {
        String result = null;
        if (this.jspPropertyGroup.getDeferredSyntax() != null) {
            result = this.jspPropertyGroup.getDeferredSyntax().toString();
        }
        return result;
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getElIgnored() {
        String result = null;
        if (this.jspPropertyGroup.getElIgnored() != null) {
            result = this.jspPropertyGroup.getElIgnored().toString();
        }
        return result;
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getErrorOnELNotFound() {
        String result = null;
        if (this.jspPropertyGroup.getErrorOnELNotFound() != null) {
            result = this.jspPropertyGroup.getErrorOnELNotFound().toString();
        }
        return result;
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getErrorOnUndeclaredNamespace() {
        String result = null;
        if (this.jspPropertyGroup.getErrorOnUndeclaredNamespace() != null) {
            result = this.jspPropertyGroup.getErrorOnUndeclaredNamespace().toString();
        }
        return result;
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public Collection<String> getIncludeCodas() {
        return new ArrayList(this.jspPropertyGroup.getIncludeCodas());
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public Collection<String> getIncludePreludes() {
        return new ArrayList(this.jspPropertyGroup.getIncludePreludes());
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getIsXml() {
        String result = null;
        if (this.jspPropertyGroup.getIsXml() != null) {
            result = this.jspPropertyGroup.getIsXml().toString();
        }
        return result;
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getPageEncoding() {
        return this.jspPropertyGroup.getPageEncoding();
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getScriptingInvalid() {
        String result = null;
        if (this.jspPropertyGroup.getScriptingInvalid() != null) {
            result = this.jspPropertyGroup.getScriptingInvalid().toString();
        }
        return result;
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public String getTrimDirectiveWhitespaces() {
        String result = null;
        if (this.jspPropertyGroup.getTrimWhitespace() != null) {
            result = this.jspPropertyGroup.getTrimWhitespace().toString();
        }
        return result;
    }

    @Override // jakarta.servlet.descriptor.JspPropertyGroupDescriptor
    public Collection<String> getUrlPatterns() {
        return new ArrayList(this.jspPropertyGroup.getUrlPatterns());
    }
}
