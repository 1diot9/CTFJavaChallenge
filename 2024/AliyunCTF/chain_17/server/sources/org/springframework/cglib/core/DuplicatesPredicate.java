package org.springframework.cglib.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/DuplicatesPredicate.class */
public class DuplicatesPredicate implements Predicate {
    private final Set unique;
    private final Set rejected;

    public DuplicatesPredicate() {
        this.unique = new HashSet();
        this.rejected = Collections.emptySet();
    }

    public DuplicatesPredicate(List allMethods) {
        InputStream is;
        this.rejected = new HashSet();
        this.unique = new HashSet();
        Map scanned = new HashMap();
        Map suspects = new HashMap();
        for (Object o : allMethods) {
            Method method = (Method) o;
            Object sig = MethodWrapper.create(method);
            Method existing = (Method) scanned.get(sig);
            if (existing == null) {
                scanned.put(sig, method);
            } else if (!suspects.containsKey(sig) && existing.isBridge() && !method.isBridge()) {
                suspects.put(sig, existing);
            }
        }
        if (!suspects.isEmpty()) {
            Set classes = new HashSet();
            UnnecessaryBridgeFinder finder = new UnnecessaryBridgeFinder(this.rejected);
            for (Object o2 : suspects.values()) {
                Method m = (Method) o2;
                classes.add(m.getDeclaringClass());
                finder.addSuspectMethod(m);
            }
            for (Object o3 : classes) {
                Class c = (Class) o3;
                try {
                    ClassLoader cl = getClassLoader(c);
                    if (cl != null && (is = cl.getResourceAsStream(c.getName().replace('.', '/') + ".class")) != null) {
                        try {
                            new ClassReader(is).accept(finder, 6);
                            is.close();
                        } catch (Throwable th) {
                            is.close();
                            throw th;
                            break;
                        }
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    @Override // org.springframework.cglib.core.Predicate
    public boolean evaluate(Object arg) {
        return !this.rejected.contains(arg) && this.unique.add(MethodWrapper.create((Method) arg));
    }

    private static ClassLoader getClassLoader(Class c) {
        ClassLoader cl = c.getClassLoader();
        if (cl == null) {
            cl = DuplicatesPredicate.class.getClassLoader();
        }
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        return cl;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/DuplicatesPredicate$UnnecessaryBridgeFinder.class */
    private static class UnnecessaryBridgeFinder extends ClassVisitor {
        private final Set rejected;
        private Signature currentMethodSig;
        private Map methods;

        UnnecessaryBridgeFinder(Set rejected) {
            super(Constants.ASM_API);
            this.currentMethodSig = null;
            this.methods = new HashMap();
            this.rejected = rejected;
        }

        void addSuspectMethod(Method m) {
            this.methods.put(ReflectUtils.getSignature(m), m);
        }

        @Override // org.springframework.asm.ClassVisitor
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        }

        @Override // org.springframework.asm.ClassVisitor
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            Signature sig = new Signature(name, desc);
            final Method currentMethod = (Method) this.methods.remove(sig);
            if (currentMethod != null) {
                this.currentMethodSig = sig;
                return new MethodVisitor(Constants.ASM_API) { // from class: org.springframework.cglib.core.DuplicatesPredicate.UnnecessaryBridgeFinder.1
                    @Override // org.springframework.asm.MethodVisitor
                    public void visitMethodInsn(int opcode, String owner, String name2, String desc2, boolean itf) {
                        if (opcode == 183 && UnnecessaryBridgeFinder.this.currentMethodSig != null) {
                            Signature target = new Signature(name2, desc2);
                            if (target.equals(UnnecessaryBridgeFinder.this.currentMethodSig)) {
                                UnnecessaryBridgeFinder.this.rejected.add(currentMethod);
                            }
                            UnnecessaryBridgeFinder.this.currentMethodSig = null;
                        }
                    }
                };
            }
            return null;
        }
    }
}
