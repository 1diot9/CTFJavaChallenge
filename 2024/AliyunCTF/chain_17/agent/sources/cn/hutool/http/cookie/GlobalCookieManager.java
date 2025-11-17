package cn.hutool.http.cookie;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpConnection;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/cookie/GlobalCookieManager.class */
public class GlobalCookieManager {
    private static CookieManager cookieManager = new CookieManager(new ThreadLocalCookieStore(), CookiePolicy.ACCEPT_ALL);

    public static void setCookieManager(CookieManager customCookieManager) {
        cookieManager = customCookieManager;
    }

    public static CookieManager getCookieManager() {
        return cookieManager;
    }

    public static List<HttpCookie> getCookies(HttpConnection conn) {
        return cookieManager.getCookieStore().get(getURI(conn));
    }

    public static void add(HttpConnection conn) {
        if (null == cookieManager) {
            return;
        }
        try {
            Map<String, List<String>> cookieHeader = cookieManager.get(getURI(conn), new HashMap(0));
            conn.header(cookieHeader, false);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static void store(HttpConnection conn) {
        if (null == cookieManager) {
            return;
        }
        try {
            cookieManager.put(getURI(conn), conn.headers());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private static URI getURI(HttpConnection conn) {
        return URLUtil.toURI(conn.getUrl());
    }
}
