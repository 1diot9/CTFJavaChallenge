package org.apache.catalina.core;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletResponseWrapper;
import java.util.Locale;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationResponse.class */
class ApplicationResponse extends ServletResponseWrapper {
    protected boolean included;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationResponse(ServletResponse response, boolean included) {
        super(response);
        this.included = false;
        setIncluded(included);
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void reset() {
        if (!this.included || getResponse().isCommitted()) {
            getResponse().reset();
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setContentLength(int len) {
        if (!this.included) {
            getResponse().setContentLength(len);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setContentLengthLong(long len) {
        if (!this.included) {
            getResponse().setContentLengthLong(len);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setContentType(String type) {
        if (!this.included) {
            getResponse().setContentType(type);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setLocale(Locale loc) {
        if (!this.included) {
            getResponse().setLocale(loc);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setBufferSize(int size) {
        if (!this.included) {
            getResponse().setBufferSize(size);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper
    public void setResponse(ServletResponse response) {
        super.setResponse(response);
    }

    void setIncluded(boolean included) {
        this.included = included;
    }
}
