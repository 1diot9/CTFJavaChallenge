package org.springframework.cglib.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassWriter;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/DebuggingClassWriter.class */
public class DebuggingClassWriter extends ClassVisitor {
    public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";
    private static String debugLocation = System.getProperty(DEBUG_LOCATION_PROPERTY);
    private static Constructor traceCtor;
    private String className;
    private String superName;

    static {
        if (debugLocation != null) {
            System.err.println("CGLIB debugging enabled, writing to '" + debugLocation + "'");
            try {
                Class clazz = Class.forName("org.springframework.asm.util.TraceClassVisitor");
                traceCtor = clazz.getConstructor(ClassVisitor.class, PrintWriter.class);
            } catch (Throwable th) {
            }
        }
    }

    public DebuggingClassWriter(int flags) {
        super(Constants.ASM_API, new ClassWriter(flags));
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name.replace('/', '.');
        this.superName = superName.replace('/', '.');
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public String getClassName() {
        return this.className;
    }

    public String getSuperName() {
        return this.superName;
    }

    public byte[] toByteArray() {
        byte[] b = ((ClassWriter) this.cv).toByteArray();
        if (debugLocation != null) {
            String dirs = this.className.replace('.', File.separatorChar);
            try {
                new File(debugLocation + File.separatorChar + dirs).getParentFile().mkdirs();
                File file = new File(new File(debugLocation), dirs + ".class");
                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                try {
                    out.write(b);
                    out.close();
                    if (traceCtor != null) {
                        File file2 = new File(new File(debugLocation), dirs + ".asm");
                        out = new BufferedOutputStream(new FileOutputStream(file2));
                        try {
                            ClassReader cr = new ClassReader(b);
                            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                            ClassVisitor tcv = (ClassVisitor) traceCtor.newInstance(null, pw);
                            cr.accept(tcv, 0);
                            pw.flush();
                            out.close();
                        } finally {
                        }
                    }
                } finally {
                }
            } catch (Exception e) {
                throw new CodeGenerationException(e);
            }
        }
        return b;
    }
}
