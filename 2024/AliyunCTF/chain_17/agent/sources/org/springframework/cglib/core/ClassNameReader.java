package org.springframework.cglib.core;

import java.util.ArrayList;
import java.util.List;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ClassNameReader.class */
public class ClassNameReader {
    private static final EarlyExitException EARLY_EXIT = new EarlyExitException();

    private ClassNameReader() {
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ClassNameReader$EarlyExitException.class */
    private static class EarlyExitException extends RuntimeException {
        private EarlyExitException() {
        }
    }

    public static String getClassName(ClassReader r) {
        return getClassInfo(r)[0];
    }

    public static String[] getClassInfo(ClassReader r) {
        final List<String> array = new ArrayList<>();
        try {
            r.accept(new ClassVisitor(Constants.ASM_API, null) { // from class: org.springframework.cglib.core.ClassNameReader.1
                @Override // org.springframework.asm.ClassVisitor
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    array.add(name.replace('/', '.'));
                    if (superName != null) {
                        array.add(superName.replace('/', '.'));
                    }
                    for (String element : interfaces) {
                        array.add(element.replace('/', '.'));
                    }
                    throw ClassNameReader.EARLY_EXIT;
                }
            }, 6);
        } catch (EarlyExitException e) {
        }
        return (String[]) array.toArray(new String[0]);
    }
}
