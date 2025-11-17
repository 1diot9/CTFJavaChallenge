package org.apache.tomcat.util.descriptor.web;

import jakarta.servlet.descriptor.JspConfigDescriptor;
import jakarta.servlet.descriptor.JspPropertyGroupDescriptor;
import jakarta.servlet.descriptor.TaglibDescriptor;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/JspConfigDescriptorImpl.class */
public class JspConfigDescriptorImpl implements JspConfigDescriptor {
    private final Collection<JspPropertyGroupDescriptor> jspPropertyGroups;
    private final Collection<TaglibDescriptor> taglibs;

    public JspConfigDescriptorImpl(Collection<JspPropertyGroupDescriptor> jspPropertyGroups, Collection<TaglibDescriptor> taglibs) {
        this.jspPropertyGroups = jspPropertyGroups;
        this.taglibs = taglibs;
    }

    @Override // jakarta.servlet.descriptor.JspConfigDescriptor
    public Collection<JspPropertyGroupDescriptor> getJspPropertyGroups() {
        return new ArrayList(this.jspPropertyGroups);
    }

    @Override // jakarta.servlet.descriptor.JspConfigDescriptor
    public Collection<TaglibDescriptor> getTaglibs() {
        return new ArrayList(this.taglibs);
    }
}
