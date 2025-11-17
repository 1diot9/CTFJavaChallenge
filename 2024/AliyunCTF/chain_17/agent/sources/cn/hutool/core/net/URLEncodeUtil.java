package cn.hutool.core.net;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/URLEncodeUtil.class */
public class URLEncodeUtil {
    public static String encodeAll(String url) {
        return encodeAll(url, CharsetUtil.CHARSET_UTF_8);
    }

    public static String encodeAll(String url, Charset charset) throws UtilException {
        return RFC3986.UNRESERVED.encode(url, charset, new char[0]);
    }

    public static String encode(String url) throws UtilException {
        return encode(url, CharsetUtil.CHARSET_UTF_8);
    }

    public static String encode(String url, Charset charset) {
        return RFC3986.PATH.encode(url, charset, new char[0]);
    }

    public static String encodeQuery(String url) throws UtilException {
        return encodeQuery(url, CharsetUtil.CHARSET_UTF_8);
    }

    public static String encodeQuery(String url, Charset charset) {
        return RFC3986.QUERY.encode(url, charset, new char[0]);
    }

    public static String encodePathSegment(String url) throws UtilException {
        return encodePathSegment(url, CharsetUtil.CHARSET_UTF_8);
    }

    public static String encodePathSegment(String url, Charset charset) {
        if (StrUtil.isEmpty(url)) {
            return url;
        }
        return RFC3986.SEGMENT.encode(url, charset, new char[0]);
    }

    public static String encodeFragment(String url) throws UtilException {
        return encodeFragment(url, CharsetUtil.CHARSET_UTF_8);
    }

    public static String encodeFragment(String url, Charset charset) {
        if (StrUtil.isEmpty(url)) {
            return url;
        }
        return RFC3986.FRAGMENT.encode(url, charset, new char[0]);
    }
}
