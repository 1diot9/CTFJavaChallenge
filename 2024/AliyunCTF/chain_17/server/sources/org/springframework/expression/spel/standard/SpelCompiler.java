package org.springframework.expression.spel.standard;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.cglib.core.Constants;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.CompiledExpression;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/standard/SpelCompiler.class */
public final class SpelCompiler implements Opcodes {
    private static final int CLASSES_DEFINED_LIMIT = 100;
    private static final Log logger = LogFactory.getLog((Class<?>) SpelCompiler.class);
    private static final Map<ClassLoader, SpelCompiler> compilers = new ConcurrentReferenceHashMap();
    private volatile ChildClassLoader childClassLoader;
    private final AtomicInteger suffixId = new AtomicInteger(1);

    private SpelCompiler(@Nullable ClassLoader classloader) {
        this.childClassLoader = new ChildClassLoader(classloader);
    }

    @Nullable
    public CompiledExpression compile(SpelNodeImpl expression) {
        if (expression.isCompilable()) {
            if (logger.isDebugEnabled()) {
                logger.debug("SpEL: compiling " + expression.toStringAST());
            }
            Class<? extends CompiledExpression> clazz = createExpressionClass(expression);
            if (clazz != null) {
                try {
                    return (CompiledExpression) ReflectionUtils.accessibleConstructor(clazz, new Class[0]).newInstance(new Object[0]);
                } catch (Throwable ex) {
                    throw new IllegalStateException("Failed to instantiate CompiledExpression for expression: " + expression.toStringAST(), ex);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("SpEL: unable to compile " + expression.toStringAST());
            return null;
        }
        return null;
    }

    private int getNextSuffix() {
        return this.suffixId.incrementAndGet();
    }

    @Nullable
    private Class<? extends CompiledExpression> createExpressionClass(SpelNodeImpl expressionToCompile) {
        String className = "spel/Ex" + getNextSuffix();
        ClassWriter cw = new ExpressionClassWriter();
        cw.visit(52, 1, className, null, "org/springframework/expression/spel/CompiledExpression", null);
        MethodVisitor mv = cw.visitMethod(1, Constants.CONSTRUCTOR_NAME, "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/springframework/expression/spel/CompiledExpression", Constants.CONSTRUCTOR_NAME, "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        MethodVisitor mv2 = cw.visitMethod(1, "getValue", "(Ljava/lang/Object;L" + "org/springframework/expression/EvaluationContext" + ";)Ljava/lang/Object;", null, new String[]{"org/springframework/expression/EvaluationException"});
        mv2.visitCode();
        CodeFlow cf = new CodeFlow(className, cw);
        try {
            expressionToCompile.generateCode(mv2, cf);
            CodeFlow.insertBoxIfNecessary(mv2, cf.lastDescriptor());
            if ("V".equals(cf.lastDescriptor())) {
                mv2.visitInsn(1);
            }
            mv2.visitInsn(176);
            mv2.visitMaxs(0, 0);
            mv2.visitEnd();
            cw.visitEnd();
            cf.finish();
            byte[] data = cw.toByteArray();
            return loadClass(StringUtils.replace(className, "/", "."), data);
        } catch (IllegalStateException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(expressionToCompile.getClass().getSimpleName() + ".generateCode opted out of compilation: " + ex.getMessage());
                return null;
            }
            return null;
        }
    }

    private Class<? extends CompiledExpression> loadClass(String name, byte[] bytes) {
        ChildClassLoader ccl = this.childClassLoader;
        if (ccl.getClassesDefinedCount() >= 100) {
            synchronized (this) {
                ChildClassLoader currentCcl = this.childClassLoader;
                if (ccl == currentCcl) {
                    ccl = new ChildClassLoader(ccl.getParent());
                    this.childClassLoader = ccl;
                } else {
                    ccl = currentCcl;
                }
            }
        }
        return ccl.defineClass(name, bytes);
    }

    public static SpelCompiler getCompiler(@Nullable ClassLoader classLoader) {
        SpelCompiler computeIfAbsent;
        ClassLoader clToUse = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
        SpelCompiler compiler = compilers.get(clToUse);
        if (compiler == null) {
            synchronized (compilers) {
                computeIfAbsent = compilers.computeIfAbsent(clToUse, SpelCompiler::new);
            }
            return computeIfAbsent;
        }
        return compiler;
    }

    public static boolean compile(Expression expression) {
        if (expression instanceof SpelExpression) {
            SpelExpression spelExpression = (SpelExpression) expression;
            if (spelExpression.compileExpression()) {
                return true;
            }
        }
        return false;
    }

    public static void revertToInterpreted(Expression expression) {
        if (expression instanceof SpelExpression) {
            SpelExpression spelExpression = (SpelExpression) expression;
            spelExpression.revertToInterpreted();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/standard/SpelCompiler$ChildClassLoader.class */
    public static class ChildClassLoader extends URLClassLoader {
        private static final URL[] NO_URLS = new URL[0];
        private final AtomicInteger classesDefinedCount;

        public ChildClassLoader(@Nullable ClassLoader classLoader) {
            super(NO_URLS, classLoader);
            this.classesDefinedCount = new AtomicInteger();
        }

        public Class<?> defineClass(String name, byte[] bytes) {
            Class<?> clazz = super.defineClass(name, bytes, 0, bytes.length);
            this.classesDefinedCount.incrementAndGet();
            return clazz;
        }

        public int getClassesDefinedCount() {
            return this.classesDefinedCount.get();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/standard/SpelCompiler$ExpressionClassWriter.class */
    public class ExpressionClassWriter extends ClassWriter {
        public ExpressionClassWriter() {
            super(3);
        }

        @Override // org.springframework.asm.ClassWriter
        protected ClassLoader getClassLoader() {
            return SpelCompiler.this.childClassLoader;
        }
    }
}
