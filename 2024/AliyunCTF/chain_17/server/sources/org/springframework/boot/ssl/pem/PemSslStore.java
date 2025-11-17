package org.springframework.boot.ssl.pem;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/pem/PemSslStore.class */
public interface PemSslStore {
    String type();

    String alias();

    String password();

    List<X509Certificate> certificates();

    PrivateKey privateKey();

    default PemSslStore withAlias(String alias) {
        return of(type(), alias, password(), certificates(), privateKey());
    }

    default PemSslStore withPassword(String password) {
        return of(type(), alias(), password, certificates(), privateKey());
    }

    static PemSslStore load(PemSslStoreDetails details) {
        if (details == null || details.isEmpty()) {
            return null;
        }
        return new LoadedPemSslStore(details);
    }

    static PemSslStore of(String type, List<X509Certificate> certificates, PrivateKey privateKey) {
        return of(type, null, null, certificates, privateKey);
    }

    static PemSslStore of(List<X509Certificate> certificates, PrivateKey privateKey) {
        return of(null, null, null, certificates, privateKey);
    }

    static PemSslStore of(final String type, final String alias, final String password, final List<X509Certificate> certificates, final PrivateKey privateKey) {
        Assert.notEmpty(certificates, "Certificates must not be empty");
        return new PemSslStore() { // from class: org.springframework.boot.ssl.pem.PemSslStore.1
            @Override // org.springframework.boot.ssl.pem.PemSslStore
            public String type() {
                return type;
            }

            @Override // org.springframework.boot.ssl.pem.PemSslStore
            public String alias() {
                return alias;
            }

            @Override // org.springframework.boot.ssl.pem.PemSslStore
            public String password() {
                return password;
            }

            @Override // org.springframework.boot.ssl.pem.PemSslStore
            public List<X509Certificate> certificates() {
                return certificates;
            }

            @Override // org.springframework.boot.ssl.pem.PemSslStore
            public PrivateKey privateKey() {
                return privateKey;
            }
        };
    }
}
