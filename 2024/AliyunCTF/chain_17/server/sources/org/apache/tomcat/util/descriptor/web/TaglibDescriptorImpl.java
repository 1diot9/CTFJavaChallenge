package org.apache.tomcat.util.descriptor.web;

import jakarta.servlet.descriptor.TaglibDescriptor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/TaglibDescriptorImpl.class */
public class TaglibDescriptorImpl implements TaglibDescriptor {
    private final String location;
    private final String uri;

    public TaglibDescriptorImpl(String location, String uri) {
        this.location = location;
        this.uri = uri;
    }

    @Override // jakarta.servlet.descriptor.TaglibDescriptor
    public String getTaglibLocation() {
        return this.location;
    }

    @Override // jakarta.servlet.descriptor.TaglibDescriptor
    public String getTaglibURI() {
        return this.uri;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.location == null ? 0 : this.location.hashCode());
        return (31 * result) + (this.uri == null ? 0 : this.uri.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TaglibDescriptorImpl)) {
            return false;
        }
        TaglibDescriptorImpl other = (TaglibDescriptorImpl) obj;
        if (this.location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!this.location.equals(other.location)) {
            return false;
        }
        if (this.uri == null) {
            if (other.uri != null) {
                return false;
            }
            return true;
        }
        if (!this.uri.equals(other.uri)) {
            return false;
        }
        return true;
    }
}
