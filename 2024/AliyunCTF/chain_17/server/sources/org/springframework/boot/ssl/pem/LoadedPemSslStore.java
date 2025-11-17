package org.springframework.boot.ssl.pem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.util.function.ThrowingSupplier;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/pem/LoadedPemSslStore.class */
public final class LoadedPemSslStore implements PemSslStore {
    private final PemSslStoreDetails details;
    private final Supplier<List<X509Certificate>> certificatesSupplier;
    private final Supplier<PrivateKey> privateKeySupplier;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoadedPemSslStore(PemSslStoreDetails details) {
        Assert.notNull(details, "Details must not be null");
        this.details = details;
        this.certificatesSupplier = supplier(() -> {
            return loadCertificates(details);
        });
        this.privateKeySupplier = supplier(() -> {
            return loadPrivateKey(details);
        });
    }

    private static <T> Supplier<T> supplier(ThrowingSupplier<T> supplier) {
        return SingletonSupplier.of((Supplier) supplier.throwing(LoadedPemSslStore::asUncheckedIOException));
    }

    private static UncheckedIOException asUncheckedIOException(String message, Exception cause) {
        return new UncheckedIOException(message, (IOException) cause);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<X509Certificate> loadCertificates(PemSslStoreDetails details) throws IOException {
        PemContent pemContent = PemContent.load(details.certificates());
        if (pemContent == null) {
            return null;
        }
        List<X509Certificate> certificates = pemContent.getCertificates();
        Assert.state(!CollectionUtils.isEmpty(certificates), "Loaded certificates are empty");
        return certificates;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PrivateKey loadPrivateKey(PemSslStoreDetails details) throws IOException {
        PemContent pemContent = PemContent.load(details.privateKey());
        if (pemContent != null) {
            return pemContent.getPrivateKey(details.privateKeyPassword());
        }
        return null;
    }

    @Override // org.springframework.boot.ssl.pem.PemSslStore
    public String type() {
        return this.details.type();
    }

    @Override // org.springframework.boot.ssl.pem.PemSslStore
    public String alias() {
        return this.details.alias();
    }

    @Override // org.springframework.boot.ssl.pem.PemSslStore
    public String password() {
        return this.details.password();
    }

    @Override // org.springframework.boot.ssl.pem.PemSslStore
    public List<X509Certificate> certificates() {
        return this.certificatesSupplier.get();
    }

    @Override // org.springframework.boot.ssl.pem.PemSslStore
    public PrivateKey privateKey() {
        return this.privateKeySupplier.get();
    }
}
