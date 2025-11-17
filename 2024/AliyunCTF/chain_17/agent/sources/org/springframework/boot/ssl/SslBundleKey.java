package org.springframework.boot.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.function.Supplier;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/SslBundleKey.class */
public interface SslBundleKey {
    public static final SslBundleKey NONE = of(null, null);

    String getPassword();

    String getAlias();

    default void assertContainsAlias(KeyStore keyStore) {
        String alias = getAlias();
        if (StringUtils.hasLength(alias) && keyStore != null) {
            try {
                Assert.state(keyStore.containsAlias(alias), (Supplier<String>) () -> {
                    return String.format("Keystore does not contain alias '%s'", alias);
                });
            } catch (KeyStoreException ex) {
                throw new IllegalStateException(String.format("Could not determine if keystore contains alias '%s'", alias), ex);
            }
        }
    }

    static SslBundleKey of(String password) {
        return of(password, null);
    }

    static SslBundleKey of(final String password, final String alias) {
        return new SslBundleKey() { // from class: org.springframework.boot.ssl.SslBundleKey.1
            @Override // org.springframework.boot.ssl.SslBundleKey
            public String getPassword() {
                return password;
            }

            @Override // org.springframework.boot.ssl.SslBundleKey
            public String getAlias() {
                return alias;
            }

            public String toString() {
                ToStringCreator creator = new ToStringCreator(this);
                creator.append("alias", alias);
                creator.append("password", password != null ? "******" : null);
                return creator.toString();
            }
        };
    }
}
