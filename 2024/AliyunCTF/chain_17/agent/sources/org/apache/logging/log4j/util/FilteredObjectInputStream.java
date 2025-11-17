package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/FilteredObjectInputStream.class */
public class FilteredObjectInputStream extends ObjectInputStream {
    private static final Set<String> REQUIRED_JAVA_CLASSES = new HashSet(Arrays.asList("java.math.BigDecimal", "java.math.BigInteger", "java.rmi.MarshalledObject", "[B"));
    private static final Set<String> REQUIRED_JAVA_PACKAGES = new HashSet(Arrays.asList("java.lang.", "java.time.", "java.util.", "org.apache.logging.log4j.", "[Lorg.apache.logging.log4j."));
    private final Collection<String> allowedExtraClasses;

    public FilteredObjectInputStream() throws IOException, SecurityException {
        this.allowedExtraClasses = Collections.emptySet();
    }

    public FilteredObjectInputStream(final InputStream inputStream) throws IOException {
        super(inputStream);
        this.allowedExtraClasses = Collections.emptySet();
    }

    public FilteredObjectInputStream(final Collection<String> allowedExtraClasses) throws IOException, SecurityException {
        this.allowedExtraClasses = allowedExtraClasses;
    }

    public FilteredObjectInputStream(final InputStream inputStream, final Collection<String> allowedExtraClasses) throws IOException {
        super(inputStream);
        this.allowedExtraClasses = allowedExtraClasses;
    }

    public Collection<String> getAllowedClasses() {
        return this.allowedExtraClasses;
    }

    @Override // java.io.ObjectInputStream
    protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        if (!isAllowedByDefault(name) && !this.allowedExtraClasses.contains(name)) {
            throw new InvalidObjectException("Class is not allowed for deserialization: " + name);
        }
        return super.resolveClass(desc);
    }

    private static boolean isAllowedByDefault(final String name) {
        return isRequiredPackage(name) || REQUIRED_JAVA_CLASSES.contains(name);
    }

    private static boolean isRequiredPackage(final String name) {
        for (String packageName : REQUIRED_JAVA_PACKAGES) {
            if (name.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }
}
