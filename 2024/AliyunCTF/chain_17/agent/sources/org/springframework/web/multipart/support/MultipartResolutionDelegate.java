package org.springframework.web.multipart.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/support/MultipartResolutionDelegate.class */
public final class MultipartResolutionDelegate {
    public static final Object UNRESOLVABLE = new Object();

    private MultipartResolutionDelegate() {
    }

    @Nullable
    public static MultipartRequest resolveMultipartRequest(NativeWebRequest webRequest) {
        MultipartRequest multipartRequest = (MultipartRequest) webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest != null) {
            return multipartRequest;
        }
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null && isMultipartContent(servletRequest)) {
            return new StandardMultipartHttpServletRequest(servletRequest);
        }
        return null;
    }

    public static boolean isMultipartRequest(HttpServletRequest request) {
        return WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null || isMultipartContent(request);
    }

    private static boolean isMultipartContent(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith(FileUploadBase.MULTIPART);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MultipartHttpServletRequest asMultipartHttpServletRequest(HttpServletRequest request) {
        MultipartHttpServletRequest unwrapped = (MultipartHttpServletRequest) WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        if (unwrapped != null) {
            return unwrapped;
        }
        return new StandardMultipartHttpServletRequest(request);
    }

    public static boolean isMultipartArgument(MethodParameter parameter) {
        Class<?> paramType = parameter.getNestedParameterType();
        return MultipartFile.class == paramType || isMultipartFileCollection(parameter) || isMultipartFileArray(parameter) || Part.class == paramType || isPartCollection(parameter) || isPartArray(parameter);
    }

    @Nullable
    public static Object resolveMultipartArgument(String name, MethodParameter parameter, HttpServletRequest request) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        boolean isMultipart = multipartRequest != null || isMultipartContent(request);
        if (MultipartFile.class == parameter.getNestedParameterType()) {
            if (!isMultipart) {
                return null;
            }
            if (multipartRequest == null) {
                multipartRequest = new StandardMultipartHttpServletRequest(request);
            }
            return multipartRequest.getFile(name);
        }
        if (isMultipartFileCollection(parameter)) {
            if (!isMultipart) {
                return null;
            }
            if (multipartRequest == null) {
                multipartRequest = new StandardMultipartHttpServletRequest(request);
            }
            List<MultipartFile> files = multipartRequest.getFiles(name);
            if (files.isEmpty()) {
                return null;
            }
            return files;
        }
        if (isMultipartFileArray(parameter)) {
            if (!isMultipart) {
                return null;
            }
            if (multipartRequest == null) {
                multipartRequest = new StandardMultipartHttpServletRequest(request);
            }
            List<MultipartFile> files2 = multipartRequest.getFiles(name);
            if (files2.isEmpty()) {
                return null;
            }
            return files2.toArray(new MultipartFile[0]);
        }
        if (Part.class == parameter.getNestedParameterType()) {
            if (!isMultipart) {
                return null;
            }
            return request.getPart(name);
        }
        if (isPartCollection(parameter)) {
            if (!isMultipart) {
                return null;
            }
            List<Part> parts = resolvePartList(request, name);
            if (parts.isEmpty()) {
                return null;
            }
            return parts;
        }
        if (isPartArray(parameter)) {
            if (!isMultipart) {
                return null;
            }
            List<Part> parts2 = resolvePartList(request, name);
            if (parts2.isEmpty()) {
                return null;
            }
            return parts2.toArray(new Part[0]);
        }
        return UNRESOLVABLE;
    }

    private static boolean isMultipartFileCollection(MethodParameter methodParam) {
        return MultipartFile.class == getCollectionParameterType(methodParam);
    }

    private static boolean isMultipartFileArray(MethodParameter methodParam) {
        return MultipartFile.class == methodParam.getNestedParameterType().componentType();
    }

    private static boolean isPartCollection(MethodParameter methodParam) {
        return Part.class == getCollectionParameterType(methodParam);
    }

    private static boolean isPartArray(MethodParameter methodParam) {
        return Part.class == methodParam.getNestedParameterType().componentType();
    }

    @Nullable
    private static Class<?> getCollectionParameterType(MethodParameter methodParam) {
        Class<?> valueType;
        Class<?> paramType = methodParam.getNestedParameterType();
        if ((Collection.class == paramType || List.class.isAssignableFrom(paramType)) && (valueType = ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric(new int[0])) != null) {
            return valueType;
        }
        return null;
    }

    private static List<Part> resolvePartList(HttpServletRequest request, String name) throws Exception {
        Collection<Part> parts = request.getParts();
        List<Part> result = new ArrayList<>(parts.size());
        for (Part part : parts) {
            if (part.getName().equals(name)) {
                result.add(part);
            }
        }
        return result;
    }
}
