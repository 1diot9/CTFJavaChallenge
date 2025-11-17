package org.springframework.web.bind;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/ServletRequestDataBinder.class */
public class ServletRequestDataBinder extends WebDataBinder {
    public ServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public ServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void construct(ServletRequest request) {
        construct(createValueResolver(request));
    }

    protected ServletRequestValueResolver createValueResolver(ServletRequest request) {
        return new ServletRequestValueResolver(request, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.validation.DataBinder
    public boolean shouldConstructArgument(MethodParameter param) {
        Class<?> type = param.nestedIfOptional().getNestedParameterType();
        return (!super.shouldConstructArgument(param) || MultipartFile.class.isAssignableFrom(type) || Part.class.isAssignableFrom(type)) ? false : true;
    }

    public void bind(ServletRequest request) {
        HttpServletRequest httpServletRequest;
        if (shouldNotBindPropertyValues()) {
            return;
        }
        MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
        MultipartRequest multipartRequest = (MultipartRequest) WebUtils.getNativeRequest(request, MultipartRequest.class);
        if (multipartRequest != null) {
            bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
        } else if (isFormDataPost(request) && (httpServletRequest = (HttpServletRequest) WebUtils.getNativeRequest(request, HttpServletRequest.class)) != null && HttpMethod.POST.matches(httpServletRequest.getMethod())) {
            StandardServletPartUtils.bindParts(httpServletRequest, mpvs, isBindEmptyMultipartFiles());
        }
        addBindValues(mpvs, request);
        doBind(mpvs);
    }

    private static boolean isFormDataPost(ServletRequest request) {
        return StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/form-data");
    }

    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
    }

    public void closeNoCatch() throws ServletRequestBindingException {
        if (getBindingResult().hasErrors()) {
            throw new ServletRequestBindingException("Errors binding onto object '" + getBindingResult().getObjectName() + "'", new BindException(getBindingResult()));
        }
    }

    public static DataBinder.ValueResolver valueResolver(ServletRequest request, WebDataBinder binder) {
        return new ServletRequestValueResolver(request, binder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/ServletRequestDataBinder$ServletRequestValueResolver.class */
    public static class ServletRequestValueResolver implements DataBinder.ValueResolver {
        private final ServletRequest request;
        private final WebDataBinder dataBinder;

        @Nullable
        private Set<String> parameterNames;

        /* JADX INFO: Access modifiers changed from: protected */
        public ServletRequestValueResolver(ServletRequest request, WebDataBinder dataBinder) {
            this.request = request;
            this.dataBinder = dataBinder;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public ServletRequest getRequest() {
            return this.request;
        }

        @Override // org.springframework.validation.DataBinder.ValueResolver
        @Nullable
        public final Object resolveValue(String name, Class<?> paramType) {
            Object value = getRequestParameter(name, paramType);
            if (value == null) {
                value = this.dataBinder.resolvePrefixValue(name, paramType, this::getRequestParameter);
            }
            if (value == null) {
                value = getMultipartValue(name);
            }
            return value;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Nullable
        public Object getRequestParameter(String name, Class<?> type) {
            Object value = this.request.getParameterValues(name);
            return (ObjectUtils.isArray(value) && Array.getLength(value) == 1) ? Array.get(value, 0) : value;
        }

        @Nullable
        private Object getMultipartValue(String name) {
            HttpServletRequest httpRequest;
            MultipartRequest multipartRequest = (MultipartRequest) WebUtils.getNativeRequest(this.request, MultipartRequest.class);
            if (multipartRequest != null) {
                List<MultipartFile> files = multipartRequest.getFiles(name);
                if (files.isEmpty()) {
                    return null;
                }
                return files.size() == 1 ? files.get(0) : files;
            }
            if (ServletRequestDataBinder.isFormDataPost(this.request) && (httpRequest = (HttpServletRequest) WebUtils.getNativeRequest(this.request, HttpServletRequest.class)) != null && HttpMethod.POST.matches(httpRequest.getMethod())) {
                List<Part> parts = StandardServletPartUtils.getParts(httpRequest, name);
                if (parts.isEmpty()) {
                    return null;
                }
                return parts.size() == 1 ? parts.get(0) : parts;
            }
            return null;
        }

        @Override // org.springframework.validation.DataBinder.ValueResolver
        public Set<String> getNames() {
            if (this.parameterNames == null) {
                this.parameterNames = initParameterNames(this.request);
            }
            return this.parameterNames;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Set<String> initParameterNames(ServletRequest request) {
            Set<String> set = new LinkedHashSet<>();
            Enumeration<String> enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements()) {
                set.add(enumeration.nextElement());
            }
            return set;
        }
    }
}
