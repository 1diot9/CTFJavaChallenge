package cn.hutool.core.io.resource;

import cn.hutool.core.collection.EnumerationIter;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/resource/ResourceUtil.class */
public class ResourceUtil {
    public static String readUtf8Str(String resource) {
        return getResourceObj(resource).readUtf8Str();
    }

    public static String readStr(String resource, Charset charset) {
        return getResourceObj(resource).readStr(charset);
    }

    public static byte[] readBytes(String resource) {
        return getResourceObj(resource).readBytes();
    }

    public static InputStream getStream(String resource) throws NoResourceException {
        return getResourceObj(resource).getStream();
    }

    public static InputStream getStreamSafe(String resource) {
        try {
            return getResourceObj(resource).getStream();
        } catch (NoResourceException e) {
            return null;
        }
    }

    public static BufferedReader getUtf8Reader(String resource) {
        return getReader(resource, CharsetUtil.CHARSET_UTF_8);
    }

    public static BufferedReader getReader(String resource, Charset charset) {
        return getResourceObj(resource).getReader(charset);
    }

    public static URL getResource(String resource) throws IORuntimeException {
        return getResource(resource, null);
    }

    public static List<URL> getResources(String resource) {
        return getResources(resource, null);
    }

    public static List<URL> getResources(String resource, Filter<URL> filter) {
        return IterUtil.filterToList(getResourceIter(resource), filter);
    }

    public static EnumerationIter<URL> getResourceIter(String resource) {
        return getResourceIter(resource, null);
    }

    public static EnumerationIter<URL> getResourceIter(String resource, ClassLoader classLoader) {
        try {
            Enumeration<URL> resources = ((ClassLoader) ObjUtil.defaultIfNull(classLoader, (Supplier<? extends ClassLoader>) ClassLoaderUtil::getClassLoader)).getResources(resource);
            return new EnumerationIter<>(resources);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static URL getResource(String resource, Class<?> baseClass) {
        String resource2 = StrUtil.nullToEmpty(resource);
        return null != baseClass ? baseClass.getResource(resource2) : ClassLoaderUtil.getClassLoader().getResource(resource2);
    }

    public static Resource getResourceObj(String path) {
        if (StrUtil.isNotBlank(path) && (path.startsWith("file:") || FileUtil.isAbsolutePath(path))) {
            return new FileResource(path);
        }
        return new ClassPathResource(path);
    }
}
