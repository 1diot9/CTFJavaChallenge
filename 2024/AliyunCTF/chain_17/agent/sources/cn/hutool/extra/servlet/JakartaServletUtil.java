package cn.hutool.extra.servlet;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.net.multipart.MultipartFormData;
import cn.hutool.core.net.multipart.UploadSetting;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.http.HttpHeaders;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/servlet/JakartaServletUtil.class */
public class JakartaServletUtil {
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_TRACE = "TRACE";

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -75308287:
                if (implMethodName.equals("getName")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("jakarta/servlet/http/Cookie") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
                    return (v0) -> {
                        return v0.getName();
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static Map<String, String[]> getParams(ServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }

    public static Map<String, String> getParamMap(ServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParams(request).entrySet()) {
            params.put(entry.getKey(), ArrayUtil.join((Object[]) entry.getValue(), (CharSequence) ","));
        }
        return params;
    }

    public static String getBody(ServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            Throwable th = null;
            try {
                String read = IoUtil.read(reader);
                if (reader != null) {
                    if (0 != 0) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        reader.close();
                    }
                }
                return read;
            } finally {
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static byte[] getBodyBytes(ServletRequest request) {
        try {
            return IoUtil.readBytes(request.getInputStream());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static <T> T fillBean(final ServletRequest servletRequest, T t, CopyOptions copyOptions) {
        final String lowerFirst = StrUtil.lowerFirst(t.getClass().getSimpleName());
        return (T) BeanUtil.fillBean(t, new ValueProvider<String>() { // from class: cn.hutool.extra.servlet.JakartaServletUtil.1
            @Override // cn.hutool.core.bean.copier.ValueProvider
            public Object value(String key, Type valueType) {
                String[] values = ServletRequest.this.getParameterValues(key);
                if (ArrayUtil.isEmpty((Object[]) values)) {
                    values = ServletRequest.this.getParameterValues(lowerFirst + "." + key);
                    if (ArrayUtil.isEmpty((Object[]) values)) {
                        return null;
                    }
                }
                if (1 == values.length) {
                    return values[0];
                }
                return values;
            }

            @Override // cn.hutool.core.bean.copier.ValueProvider
            public boolean containsKey(String key) {
                return (null == ServletRequest.this.getParameter(key) && null == ServletRequest.this.getParameter(new StringBuilder().append(lowerFirst).append(".").append(key).toString())) ? false : true;
            }
        }, copyOptions);
    }

    public static <T> T fillBean(ServletRequest servletRequest, T t, boolean z) {
        return (T) fillBean(servletRequest, t, CopyOptions.create().setIgnoreError(z));
    }

    public static <T> T toBean(ServletRequest servletRequest, Class<T> cls, boolean z) {
        return (T) fillBean(servletRequest, ReflectUtil.newInstanceIfPossible(cls), z);
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [java.lang.Object[][], java.lang.String[]] */
    public static String getClientIP(HttpServletRequest request, String... otherHeaderNames) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        if (ArrayUtil.isNotEmpty((Object[]) otherHeaderNames)) {
            headers = (String[]) ArrayUtil.addAll((Object[][]) new String[]{headers, otherHeaderNames});
        }
        return getClientIPByHeader(request, headers);
    }

    public static String getClientIPByHeader(HttpServletRequest request, String... headerNames) {
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (false == NetUtil.isUnknown(ip)) {
                return NetUtil.getMultistageReverseProxyIp(ip);
            }
        }
        return NetUtil.getMultistageReverseProxyIp(request.getRemoteAddr());
    }

    public static MultipartFormData getMultipart(ServletRequest request) throws IORuntimeException {
        return getMultipart(request, new UploadSetting());
    }

    public static MultipartFormData getMultipart(ServletRequest request, UploadSetting uploadSetting) throws IORuntimeException {
        MultipartFormData formData = new MultipartFormData(uploadSetting);
        try {
            formData.parseRequestStream(request.getInputStream(), CharsetUtil.charset(request.getCharacterEncoding()));
            return formData;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headerMap.put(name, request.getHeader(name));
        }
        return headerMap;
    }

    public static Map<String, List<String>> getHeadersMap(HttpServletRequest request) {
        Map<String, List<String>> headerMap = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headerMap.put(name, ListUtil.list(false, (Enumeration) request.getHeaders(name)));
        }
        return headerMap;
    }

    public static Map<String, Collection<String>> getHeadersMap(HttpServletResponse response) {
        Map<String, Collection<String>> headerMap = new HashMap<>();
        Collection<String> names = response.getHeaderNames();
        for (String name : names) {
            headerMap.put(name, response.getHeaders(name));
        }
        return headerMap;
    }

    public static String getHeaderIgnoreCase(HttpServletRequest request, String nameIgnoreCase) {
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name != null && name.equalsIgnoreCase(nameIgnoreCase)) {
                return request.getHeader(name);
            }
        }
        return null;
    }

    public static String getHeader(HttpServletRequest request, String name, String charsetName) {
        return getHeader(request, name, CharsetUtil.charset(charsetName));
    }

    public static String getHeader(HttpServletRequest request, String name, Charset charset) {
        String header = request.getHeader(name);
        if (null != header) {
            return CharsetUtil.convert(header, CharsetUtil.CHARSET_ISO_8859_1, charset);
        }
        return null;
    }

    public static boolean isIE(HttpServletRequest request) {
        String userAgent = getHeaderIgnoreCase(request, HttpHeaders.USER_AGENT);
        if (StrUtil.isNotBlank(userAgent)) {
            String userAgent2 = userAgent.toUpperCase();
            return userAgent2.contains("MSIE") || userAgent2.contains("TRIDENT");
        }
        return false;
    }

    public static boolean isGetMethod(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod());
    }

    public static boolean isPostMethod(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod());
    }

    public static boolean isMultipart(HttpServletRequest request) {
        if (false == isPostMethod(request)) {
            return false;
        }
        String contentType = request.getContentType();
        if (StrUtil.isBlank(contentType)) {
            return false;
        }
        return contentType.toLowerCase().startsWith(FileUploadBase.MULTIPART);
    }

    public static Cookie getCookie(HttpServletRequest httpServletRequest, String name) {
        return readCookieMap(httpServletRequest).get(name);
    }

    public static Map<String, Cookie> readCookieMap(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (ArrayUtil.isEmpty((Object[]) cookies)) {
            return MapUtil.empty();
        }
        return IterUtil.toMap(new ArrayIter((Object[]) httpServletRequest.getCookies()), new CaseInsensitiveMap(), (v0) -> {
            return v0.getName();
        });
    }

    public static void addCookie(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, String name, String value) {
        response.addCookie(new Cookie(name, value));
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds, String path, String domain) {
        Cookie cookie = new Cookie(name, value);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(path);
        addCookie(response, cookie);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        addCookie(response, name, value, maxAgeInSeconds, "/", null);
    }

    public static PrintWriter getWriter(HttpServletResponse response) throws IORuntimeException {
        try {
            return response.getWriter();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static void write(HttpServletResponse response, String text, String contentType) {
        response.setContentType(contentType);
        Writer writer = null;
        try {
            try {
                writer = response.getWriter();
                writer.write(text);
                writer.flush();
                IoUtil.close((Closeable) writer);
            } catch (IOException e) {
                throw new UtilException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) writer);
            throw th;
        }
    }

    public static void write(HttpServletResponse response, File file) {
        String fileName = file.getName();
        String contentType = (String) ObjectUtil.defaultIfNull(FileUtil.getMimeType(fileName), "application/octet-stream");
        BufferedInputStream in = null;
        try {
            in = FileUtil.getInputStream(file);
            write(response, in, contentType, fileName);
            IoUtil.close((Closeable) in);
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public static void write(HttpServletResponse response, InputStream in, String contentType, String fileName) {
        String charset = (String) ObjectUtil.defaultIfNull(response.getCharacterEncoding(), CharsetUtil.UTF_8);
        String encodeText = URLUtil.encodeAll(fileName, CharsetUtil.charset(charset));
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, StrUtil.format("attachment;filename=\"{}\";filename*={}''{}", encodeText, charset, encodeText));
        response.setContentType(contentType);
        write(response, in);
    }

    public static void write(HttpServletResponse response, InputStream in, String contentType) {
        response.setContentType(contentType);
        write(response, in);
    }

    public static void write(HttpServletResponse response, InputStream in) {
        write(response, in, 8192);
    }

    public static void write(HttpServletResponse response, InputStream in, int bufferSize) {
        ServletOutputStream out = null;
        try {
            try {
                out = response.getOutputStream();
                IoUtil.copy(in, out, bufferSize);
                IoUtil.close((Closeable) out);
                IoUtil.close((Closeable) in);
            } catch (IOException e) {
                throw new UtilException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) out);
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public static void setHeader(HttpServletResponse response, String name, Object value) {
        if (value instanceof String) {
            response.setHeader(name, (String) value);
            return;
        }
        if (Date.class.isAssignableFrom(value.getClass())) {
            response.setDateHeader(name, ((Date) value).getTime());
        } else if ((value instanceof Integer) || "int".equalsIgnoreCase(value.getClass().getSimpleName())) {
            response.setIntHeader(name, ((Integer) value).intValue());
        } else {
            response.setHeader(name, value.toString());
        }
    }
}
