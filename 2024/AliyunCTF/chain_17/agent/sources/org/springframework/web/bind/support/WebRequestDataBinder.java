package org.springframework.web.bind.support;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/WebRequestDataBinder.class */
public class WebRequestDataBinder extends WebDataBinder {
    public WebRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public WebRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void construct(WebRequest request) {
        if (request instanceof NativeWebRequest) {
            NativeWebRequest nativeRequest = (NativeWebRequest) request;
            ServletRequest servletRequest = (ServletRequest) nativeRequest.getNativeRequest(ServletRequest.class);
            if (servletRequest != null) {
                construct(ServletRequestDataBinder.valueResolver(servletRequest, this));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.validation.DataBinder
    public boolean shouldConstructArgument(MethodParameter param) {
        Class<?> type = param.nestedIfOptional().getNestedParameterType();
        return (!super.shouldConstructArgument(param) || MultipartFile.class.isAssignableFrom(type) || Part.class.isAssignableFrom(type)) ? false : true;
    }

    public void bind(WebRequest request) {
        HttpServletRequest servletRequest;
        if (shouldNotBindPropertyValues()) {
            return;
        }
        MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
        if (request instanceof NativeWebRequest) {
            NativeWebRequest nativeRequest = (NativeWebRequest) request;
            MultipartRequest multipartRequest = (MultipartRequest) nativeRequest.getNativeRequest(MultipartRequest.class);
            if (multipartRequest != null) {
                bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
            } else if (StringUtils.startsWithIgnoreCase(request.getHeader(HttpHeaders.CONTENT_TYPE), "multipart/form-data") && (servletRequest = (HttpServletRequest) nativeRequest.getNativeRequest(HttpServletRequest.class)) != null && HttpMethod.POST.matches(servletRequest.getMethod())) {
                StandardServletPartUtils.bindParts(servletRequest, mpvs, isBindEmptyMultipartFiles());
            }
        }
        doBind(mpvs);
    }

    public void closeNoCatch() throws BindException {
        if (getBindingResult().hasErrors()) {
            throw new BindException(getBindingResult());
        }
    }
}
