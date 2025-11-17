package org.springframework.cglib.transform;

import java.io.IOException;
import java.io.InputStream;
import java.security.ProtectionDomain;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassReader;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.DebuggingClassWriter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/AbstractClassLoader.class */
public abstract class AbstractClassLoader extends ClassLoader {
    private ClassFilter filter;
    private ClassLoader classPath;
    private static ProtectionDomain DOMAIN = AbstractClassLoader.class.getProtectionDomain();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractClassLoader(ClassLoader parent, ClassLoader classPath, ClassFilter filter) {
        super(parent);
        this.filter = filter;
        this.classPath = classPath;
    }

    @Override // java.lang.ClassLoader
    public Class loadClass(String name) throws ClassNotFoundException {
        Class loaded = findLoadedClass(name);
        if (loaded != null && loaded.getClassLoader() == this) {
            return loaded;
        }
        if (!this.filter.accept(name)) {
            return super.loadClass(name);
        }
        try {
            InputStream is = this.classPath.getResourceAsStream(name.replace('.', '/') + ".class");
            if (is == null) {
                throw new ClassNotFoundException(name);
            }
            try {
                ClassReader r = new ClassReader(is);
                is.close();
                try {
                    DebuggingClassWriter w = new DebuggingClassWriter(2);
                    getGenerator(r).generateClass(w);
                    byte[] b = w.toByteArray();
                    Class c = super.defineClass(name, b, 0, b.length, DOMAIN);
                    postProcess(c);
                    return c;
                } catch (Error | RuntimeException e) {
                    throw e;
                } catch (Exception e2) {
                    throw new CodeGenerationException(e2);
                }
            } catch (Throwable th) {
                is.close();
                throw th;
            }
        } catch (IOException e3) {
            throw new ClassNotFoundException(name + ":" + e3.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ClassGenerator getGenerator(ClassReader r) {
        return new ClassReaderGenerator(r, attributes(), getFlags());
    }

    protected int getFlags() {
        return 0;
    }

    protected Attribute[] attributes() {
        return null;
    }

    protected void postProcess(Class c) {
    }
}
