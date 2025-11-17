package org.springframework.core.io.support;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/PropertySourceDescriptor.class */
public final class PropertySourceDescriptor extends Record {
    private final List<String> locations;
    private final boolean ignoreResourceNotFound;

    @Nullable
    private final String name;

    @Nullable
    private final Class<? extends PropertySourceFactory> propertySourceFactory;

    @Nullable
    private final String encoding;

    public PropertySourceDescriptor(List<String> locations, boolean ignoreResourceNotFound, @Nullable String name, @Nullable Class<? extends PropertySourceFactory> propertySourceFactory, @Nullable String encoding) {
        this.locations = locations;
        this.ignoreResourceNotFound = ignoreResourceNotFound;
        this.name = name;
        this.propertySourceFactory = propertySourceFactory;
        this.encoding = encoding;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, PropertySourceDescriptor.class), PropertySourceDescriptor.class, "locations;ignoreResourceNotFound;name;propertySourceFactory;encoding", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->locations:Ljava/util/List;", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->ignoreResourceNotFound:Z", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->propertySourceFactory:Ljava/lang/Class;", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->encoding:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, PropertySourceDescriptor.class), PropertySourceDescriptor.class, "locations;ignoreResourceNotFound;name;propertySourceFactory;encoding", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->locations:Ljava/util/List;", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->ignoreResourceNotFound:Z", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->propertySourceFactory:Ljava/lang/Class;", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->encoding:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, PropertySourceDescriptor.class, Object.class), PropertySourceDescriptor.class, "locations;ignoreResourceNotFound;name;propertySourceFactory;encoding", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->locations:Ljava/util/List;", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->ignoreResourceNotFound:Z", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->propertySourceFactory:Ljava/lang/Class;", "FIELD:Lorg/springframework/core/io/support/PropertySourceDescriptor;->encoding:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public List<String> locations() {
        return this.locations;
    }

    public boolean ignoreResourceNotFound() {
        return this.ignoreResourceNotFound;
    }

    @Nullable
    public String name() {
        return this.name;
    }

    @Nullable
    public Class<? extends PropertySourceFactory> propertySourceFactory() {
        return this.propertySourceFactory;
    }

    @Nullable
    public String encoding() {
        return this.encoding;
    }

    public PropertySourceDescriptor(String... locations) {
        this(Arrays.asList(locations), false, null, null, null);
    }
}
