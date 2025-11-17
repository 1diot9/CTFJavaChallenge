package cn.hutool.http.cookie;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/cookie/ThreadLocalCookieStore.class */
public class ThreadLocalCookieStore implements CookieStore {
    private static final ThreadLocal<CookieStore> STORES = new ThreadLocal<CookieStore>() { // from class: cn.hutool.http.cookie.ThreadLocalCookieStore.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public synchronized CookieStore initialValue() {
            return new CookieManager().getCookieStore();
        }
    };

    public CookieStore getCookieStore() {
        return STORES.get();
    }

    public ThreadLocalCookieStore removeCurrent() {
        STORES.remove();
        return this;
    }

    @Override // java.net.CookieStore
    public void add(URI uri, HttpCookie cookie) {
        getCookieStore().add(uri, cookie);
    }

    @Override // java.net.CookieStore
    public List<HttpCookie> get(URI uri) {
        return getCookieStore().get(uri);
    }

    @Override // java.net.CookieStore
    public List<HttpCookie> getCookies() {
        return getCookieStore().getCookies();
    }

    @Override // java.net.CookieStore
    public List<URI> getURIs() {
        return getCookieStore().getURIs();
    }

    @Override // java.net.CookieStore
    public boolean remove(URI uri, HttpCookie cookie) {
        return getCookieStore().remove(uri, cookie);
    }

    @Override // java.net.CookieStore
    public boolean removeAll() {
        return getCookieStore().removeAll();
    }
}
