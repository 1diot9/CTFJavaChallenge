package org.springframework.boot.ssl.jks;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/jks/JksSslStoreDetails.class */
public final class JksSslStoreDetails extends Record {
    private final String type;
    private final String provider;
    private final String location;
    private final String password;

    public JksSslStoreDetails(String type, String provider, String location, String password) {
        this.type = type;
        this.provider = provider;
        this.location = location;
        this.password = password;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, JksSslStoreDetails.class), JksSslStoreDetails.class, "type;provider;location;password", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->type:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->provider:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->location:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->password:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, JksSslStoreDetails.class), JksSslStoreDetails.class, "type;provider;location;password", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->type:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->provider:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->location:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->password:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, JksSslStoreDetails.class, Object.class), JksSslStoreDetails.class, "type;provider;location;password", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->type:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->provider:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->location:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/ssl/jks/JksSslStoreDetails;->password:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public String type() {
        return this.type;
    }

    public String provider() {
        return this.provider;
    }

    public String location() {
        return this.location;
    }

    public String password() {
        return this.password;
    }

    public JksSslStoreDetails withPassword(String password) {
        return new JksSslStoreDetails(this.type, this.provider, this.location, password);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmpty() {
        return isEmpty(this.type) && isEmpty(this.provider) && isEmpty(this.location);
    }

    private boolean isEmpty(String value) {
        return !StringUtils.hasText(value);
    }

    public static JksSslStoreDetails forLocation(String location) {
        return new JksSslStoreDetails(null, null, location, null);
    }
}
