package org.springframework.boot.ssl;

import javax.net.ssl.SSLContext;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/SslBundle.class */
public interface SslBundle {
    public static final String DEFAULT_PROTOCOL = "TLS";

    SslStoreBundle getStores();

    SslBundleKey getKey();

    SslOptions getOptions();

    String getProtocol();

    SslManagerBundle getManagers();

    default SSLContext createSslContext() {
        return getManagers().createSslContext(getProtocol());
    }

    static SslBundle of(SslStoreBundle stores) {
        return of(stores, null, null);
    }

    static SslBundle of(SslStoreBundle stores, SslBundleKey key) {
        return of(stores, key, null);
    }

    static SslBundle of(SslStoreBundle stores, SslBundleKey key, SslOptions options) {
        return of(stores, key, options, null);
    }

    static SslBundle of(SslStoreBundle stores, SslBundleKey key, SslOptions options, String protocol) {
        return of(stores, key, options, protocol, null);
    }

    static SslBundle of(final SslStoreBundle stores, final SslBundleKey key, final SslOptions options, final String protocol, SslManagerBundle managers) {
        final SslManagerBundle managersToUse = managers != null ? managers : SslManagerBundle.from(stores, key);
        return new SslBundle() { // from class: org.springframework.boot.ssl.SslBundle.1
            @Override // org.springframework.boot.ssl.SslBundle
            public SslStoreBundle getStores() {
                return SslStoreBundle.this != null ? SslStoreBundle.this : SslStoreBundle.NONE;
            }

            @Override // org.springframework.boot.ssl.SslBundle
            public SslBundleKey getKey() {
                return key != null ? key : SslBundleKey.NONE;
            }

            @Override // org.springframework.boot.ssl.SslBundle
            public SslOptions getOptions() {
                return options != null ? options : SslOptions.NONE;
            }

            @Override // org.springframework.boot.ssl.SslBundle
            public String getProtocol() {
                return !StringUtils.hasText(protocol) ? "TLS" : protocol;
            }

            @Override // org.springframework.boot.ssl.SslBundle
            public SslManagerBundle getManagers() {
                return managersToUse;
            }

            public String toString() {
                ToStringCreator creator = new ToStringCreator(this);
                creator.append("key", getKey());
                creator.append("options", getOptions());
                creator.append("protocol", getProtocol());
                creator.append("stores", getStores());
                return creator.toString();
            }
        };
    }
}
