package org.springframework.boot.autoconfigure.flyway;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.resource.LoadableResource;
import org.flywaydb.core.internal.resource.classpath.ClassPathResource;
import org.flywaydb.core.internal.scanner.Scanner;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.core.NativeDetector;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/NativeImageResourceProvider.class */
class NativeImageResourceProvider implements ResourceProvider {
    private final Scanner<?> scanner;
    private final ClassLoader classLoader;
    private final Collection<Location> locations;
    private final Charset encoding;
    private final boolean failOnMissingLocations;
    private final List<LocatedResource> locatedResources = new ArrayList();
    private final Lock lock = new ReentrantLock();
    private boolean initialized;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NativeImageResourceProvider(Scanner<?> scanner, ClassLoader classLoader, Collection<Location> locations, Charset encoding, boolean failOnMissingLocations) {
        this.scanner = scanner;
        this.classLoader = classLoader;
        this.locations = locations;
        this.encoding = encoding;
        this.failOnMissingLocations = failOnMissingLocations;
    }

    public LoadableResource getResource(String name) {
        if (!NativeDetector.inNativeImage()) {
            return this.scanner.getResource(name);
        }
        LoadableResource resource = this.scanner.getResource(name);
        if (resource != null) {
            return resource;
        }
        if (this.classLoader.getResource(name) == null) {
            return null;
        }
        return new ClassPathResource((Location) null, name, this.classLoader, this.encoding);
    }

    public Collection<LoadableResource> getResources(String prefix, String[] suffixes) {
        if (!NativeDetector.inNativeImage()) {
            return this.scanner.getResources(prefix, suffixes);
        }
        ensureInitialized();
        Predicate<LocatedResource> matchesPrefixAndSuffixes = locatedResource -> {
            return StringUtils.startsAndEndsWith(locatedResource.resource.getFilename(), prefix, suffixes);
        };
        List<LoadableResource> result = new ArrayList<>();
        result.addAll(this.scanner.getResources(prefix, suffixes));
        Stream<R> map = this.locatedResources.stream().filter(matchesPrefixAndSuffixes).map(this::asClassPathResource);
        Objects.requireNonNull(result);
        map.forEach((v1) -> {
            r1.add(v1);
        });
        return result;
    }

    private ClassPathResource asClassPathResource(LocatedResource locatedResource) {
        Location location = locatedResource.location();
        String fileNameWithAbsolutePath = location.getPath() + "/" + locatedResource.resource().getFilename();
        return new ClassPathResource(location, fileNameWithAbsolutePath, this.classLoader, this.encoding);
    }

    private void ensureInitialized() {
        this.lock.lock();
        try {
            if (!this.initialized) {
                initialize();
                this.initialized = true;
            }
        } finally {
            this.lock.unlock();
        }
    }

    private void initialize() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (Location location : this.locations) {
            if (location.isClassPath()) {
                Resource root = resolver.getResource(location.getDescriptor());
                if (!root.exists()) {
                    if (this.failOnMissingLocations) {
                        throw new FlywayException("Location " + location.getDescriptor() + " doesn't exist");
                    }
                } else {
                    Resource[] resources = getResources(resolver, location, root);
                    for (Resource resource : resources) {
                        this.locatedResources.add(new LocatedResource(resource, location));
                    }
                }
            }
        }
    }

    private Resource[] getResources(PathMatchingResourcePatternResolver resolver, Location location, Resource root) {
        try {
            return resolver.getResources(root.getURI() + "/*");
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to list resources for " + location.getDescriptor(), ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/NativeImageResourceProvider$LocatedResource.class */
    public static final class LocatedResource extends Record {
        private final Resource resource;
        private final Location location;

        private LocatedResource(Resource resource, Location location) {
            this.resource = resource;
            this.location = location;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, LocatedResource.class), LocatedResource.class, "resource;location", "FIELD:Lorg/springframework/boot/autoconfigure/flyway/NativeImageResourceProvider$LocatedResource;->resource:Lorg/springframework/core/io/Resource;", "FIELD:Lorg/springframework/boot/autoconfigure/flyway/NativeImageResourceProvider$LocatedResource;->location:Lorg/flywaydb/core/api/Location;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, LocatedResource.class), LocatedResource.class, "resource;location", "FIELD:Lorg/springframework/boot/autoconfigure/flyway/NativeImageResourceProvider$LocatedResource;->resource:Lorg/springframework/core/io/Resource;", "FIELD:Lorg/springframework/boot/autoconfigure/flyway/NativeImageResourceProvider$LocatedResource;->location:Lorg/flywaydb/core/api/Location;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, LocatedResource.class, Object.class), LocatedResource.class, "resource;location", "FIELD:Lorg/springframework/boot/autoconfigure/flyway/NativeImageResourceProvider$LocatedResource;->resource:Lorg/springframework/core/io/Resource;", "FIELD:Lorg/springframework/boot/autoconfigure/flyway/NativeImageResourceProvider$LocatedResource;->location:Lorg/flywaydb/core/api/Location;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Resource resource() {
            return this.resource;
        }

        public Location location() {
            return this.location;
        }
    }
}
