package org.springframework.boot.ssl.pem;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/pem/PemSslStoreDetails.class */
public final class PemSslStoreDetails extends Record {
    private final String type;
    private final String alias;
    private final String password;
    private final String certificates;
    private final String privateKey;
    private final String privateKeyPassword;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, PemSslStoreDetails.class), PemSslStoreDetails.class, "type;alias;password;certificates;privateKey;privateKeyPassword", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->type:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->alias:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->password:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->certificates:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->privateKey:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->privateKeyPassword:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, PemSslStoreDetails.class), PemSslStoreDetails.class, "type;alias;password;certificates;privateKey;privateKeyPassword", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->type:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->alias:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->password:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->certificates:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->privateKey:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->privateKeyPassword:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, PemSslStoreDetails.class, Object.class), PemSslStoreDetails.class, "type;alias;password;certificates;privateKey;privateKeyPassword", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->type:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->alias:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->password:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->certificates:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->privateKey:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/pem/PemSslStoreDetails;->privateKeyPassword:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public String type() {
        return this.type;
    }

    public String alias() {
        return this.alias;
    }

    public String password() {
        return this.password;
    }

    public String certificates() {
        return this.certificates;
    }

    public String privateKey() {
        return this.privateKey;
    }

    public String privateKeyPassword() {
        return this.privateKeyPassword;
    }

    public PemSslStoreDetails(String type, String alias, String password, String certificates, String privateKey, String privateKeyPassword) {
        this.type = type;
        this.alias = alias;
        this.password = password;
        this.certificates = certificates;
        this.privateKey = privateKey;
        this.privateKeyPassword = privateKeyPassword;
    }

    public PemSslStoreDetails(String type, String certificate, String privateKey, String privateKeyPassword) {
        this(type, null, null, certificate, privateKey, privateKeyPassword);
    }

    public PemSslStoreDetails(String type, String certificate, String privateKey) {
        this(type, certificate, privateKey, null);
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public String certificate() {
        return certificates();
    }

    public PemSslStoreDetails withAlias(String alias) {
        return new PemSslStoreDetails(this.type, alias, this.password, this.certificates, this.privateKey, this.privateKeyPassword);
    }

    public PemSslStoreDetails withPassword(String password) {
        return new PemSslStoreDetails(this.type, this.alias, password, this.certificates, this.privateKey, this.privateKeyPassword);
    }

    public PemSslStoreDetails withPrivateKey(String privateKey) {
        return new PemSslStoreDetails(this.type, this.alias, this.password, this.certificates, privateKey, this.privateKeyPassword);
    }

    public PemSslStoreDetails withPrivateKeyPassword(String privateKeyPassword) {
        return new PemSslStoreDetails(this.type, this.alias, this.password, this.certificates, this.privateKey, privateKeyPassword);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmpty() {
        return isEmpty(this.type) && isEmpty(this.certificates) && isEmpty(this.privateKey);
    }

    private boolean isEmpty(String value) {
        return !StringUtils.hasText(value);
    }

    public static PemSslStoreDetails forCertificate(String certificate) {
        return forCertificates(certificate);
    }

    public static PemSslStoreDetails forCertificates(String certificates) {
        return new PemSslStoreDetails(null, certificates, null);
    }
}
