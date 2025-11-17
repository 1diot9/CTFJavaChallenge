package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractCookieValueMethodArgumentResolver;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ServletCookieValueMethodArgumentResolver.class */
public class ServletCookieValueMethodArgumentResolver extends AbstractCookieValueMethodArgumentResolver {
    private UrlPathHelper urlPathHelper;

    public ServletCookieValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
        this.urlPathHelper = new UrlPathHelper();
    }

    public void setUrlDecode(boolean urlDecode) {
        this.urlPathHelper.setUrlDecode(urlDecode);
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    @Nullable
    protected Object resolveName(String cookieName, MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "No HttpServletRequest");
        Cookie cookieValue = WebUtils.getCookie(servletRequest, cookieName);
        if (Cookie.class.isAssignableFrom(parameter.getNestedParameterType())) {
            return cookieValue;
        }
        if (cookieValue != null) {
            return this.urlPathHelper.decodeRequestString(servletRequest, cookieValue.getValue());
        }
        return null;
    }
}
