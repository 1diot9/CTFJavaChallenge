package org.springframework.cglib.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.Signature;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/BridgeMethodResolver.class */
class BridgeMethodResolver {
    private final Map declToBridge;
    private final ClassLoader classLoader;

    public BridgeMethodResolver(Map declToBridge, ClassLoader classLoader) {
        this.declToBridge = declToBridge;
        this.classLoader = classLoader;
    }

    public Map resolveAll() {
        InputStream is;
        Map resolved = new HashMap();
        for (Map.Entry entry : this.declToBridge.entrySet()) {
            Class owner = (Class) entry.getKey();
            Set bridges = (Set) entry.getValue();
            try {
                is = this.classLoader.getResourceAsStream(owner.getName().replace('.', '/') + ".class");
            } catch (IOException e) {
            }
            if (is == null) {
                return resolved;
            }
            try {
                new ClassReader(is).accept(new BridgedFinder(bridges, resolved), 6);
                is.close();
            } catch (Throwable th) {
                is.close();
                throw th;
                break;
            }
        }
        return resolved;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/BridgeMethodResolver$BridgedFinder.class */
    private static class BridgedFinder extends ClassVisitor {
        private Map resolved;
        private Set eligibleMethods;
        private Signature currentMethod;

        BridgedFinder(Set eligibleMethods, Map resolved) {
            super(Constants.ASM_API);
            this.currentMethod = null;
            this.resolved = resolved;
            this.eligibleMethods = eligibleMethods;
        }

        @Override // org.springframework.asm.ClassVisitor
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        }

        @Override // org.springframework.asm.ClassVisitor
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            Signature sig = new Signature(name, desc);
            if (this.eligibleMethods.remove(sig)) {
                this.currentMethod = sig;
                return new MethodVisitor(Constants.ASM_API) { // from class: org.springframework.cglib.proxy.BridgeMethodResolver.BridgedFinder.1
                    @Override // org.springframework.asm.MethodVisitor
                    public void visitMethodInsn(int opcode, String owner, String name2, String desc2, boolean itf) {
                        if ((opcode == 183 || (itf && opcode == 185)) && BridgedFinder.this.currentMethod != null) {
                            Signature target = new Signature(name2, desc2);
                            if (!target.equals(BridgedFinder.this.currentMethod)) {
                                BridgedFinder.this.resolved.put(BridgedFinder.this.currentMethod, target);
                            }
                            BridgedFinder.this.currentMethod = null;
                        }
                    }
                };
            }
            return null;
        }
    }
}
