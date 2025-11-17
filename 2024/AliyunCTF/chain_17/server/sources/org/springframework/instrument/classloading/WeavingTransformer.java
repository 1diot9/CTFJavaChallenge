package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/instrument/classloading/WeavingTransformer.class */
public class WeavingTransformer {

    @Nullable
    private final ClassLoader classLoader;
    private final List<ClassFileTransformer> transformers = new ArrayList();

    public WeavingTransformer(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void addTransformer(ClassFileTransformer transformer) {
        Assert.notNull(transformer, "Transformer must not be null");
        this.transformers.add(transformer);
    }

    public byte[] transformIfNecessary(String className, byte[] bytes) {
        String internalName = StringUtils.replace(className, ".", "/");
        return transformIfNecessary(className, internalName, bytes, null);
    }

    public byte[] transformIfNecessary(String className, String internalName, byte[] bytes, @Nullable ProtectionDomain pd) {
        byte[] result = bytes;
        for (ClassFileTransformer cft : this.transformers) {
            try {
                byte[] transformed = cft.transform(this.classLoader, internalName, (Class) null, pd, result);
                if (transformed != null) {
                    result = transformed;
                }
            } catch (IllegalClassFormatException ex) {
                throw new IllegalStateException("Class file transformation failed", ex);
            }
        }
        return result;
    }
}
