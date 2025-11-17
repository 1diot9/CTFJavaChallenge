package org.springframework.boot.autoconfigure.ssl;

import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Supplier;
import org.springframework.boot.ssl.pem.PemContent;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/BundleContentProperty.class */
final class BundleContentProperty extends Record {
    private final String name;
    private final String value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BundleContentProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, BundleContentProperty.class), BundleContentProperty.class, "name;value", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/BundleContentProperty;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/BundleContentProperty;->value:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, BundleContentProperty.class), BundleContentProperty.class, "name;value", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/BundleContentProperty;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/BundleContentProperty;->value:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, BundleContentProperty.class, Object.class), BundleContentProperty.class, "name;value", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/BundleContentProperty;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/BundleContentProperty;->value:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public String name() {
        return this.name;
    }

    public String value() {
        return this.value;
    }

    boolean isPemContent() {
        return PemContent.isPresentInText(this.value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasValue() {
        return StringUtils.hasText(this.value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Path toWatchPath() {
        return toPath();
    }

    private Path toPath() {
        try {
            URL url = toUrl();
            Assert.state(isFileUrl(url), (Supplier<String>) () -> {
                return "Value '%s' is not a file URL".formatted(url);
            });
            return Path.of(url.toURI()).toAbsolutePath();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to convert value of property '%s' to a path".formatted(this.name), ex);
        }
    }

    private URL toUrl() throws FileNotFoundException {
        Assert.state(!isPemContent(), "Value contains PEM content");
        return ResourceUtils.getURL(this.value);
    }

    private boolean isFileUrl(URL url) {
        return "file".equalsIgnoreCase(url.getProtocol());
    }
}
