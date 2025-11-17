package org.springframework.web.multipart.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/support/StandardServletMultipartResolver.class */
public class StandardServletMultipartResolver implements MultipartResolver {
    private boolean resolveLazily = false;
    private boolean strictServletCompliance = false;

    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    public void setStrictServletCompliance(boolean strictServletCompliance) {
        this.strictServletCompliance = strictServletCompliance;
    }

    @Override // org.springframework.web.multipart.MultipartResolver
    public boolean isMultipart(HttpServletRequest request) {
        return StringUtils.startsWithIgnoreCase(request.getContentType(), this.strictServletCompliance ? "multipart/form-data" : FileUploadBase.MULTIPART);
    }

    @Override // org.springframework.web.multipart.MultipartResolver
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
        return new StandardMultipartHttpServletRequest(request, this.resolveLazily);
    }

    @Override // org.springframework.web.multipart.MultipartResolver
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        if (request instanceof AbstractMultipartHttpServletRequest) {
            AbstractMultipartHttpServletRequest abstractMultipartHttpServletRequest = (AbstractMultipartHttpServletRequest) request;
            if (!abstractMultipartHttpServletRequest.isResolved()) {
                return;
            }
        }
        try {
            for (Part part : request.getParts()) {
                if (request.getFile(part.getName()) != null) {
                    part.delete();
                }
            }
        } catch (Throwable ex) {
            LogFactory.getLog(getClass()).warn("Failed to perform cleanup of multipart items", ex);
        }
    }
}
