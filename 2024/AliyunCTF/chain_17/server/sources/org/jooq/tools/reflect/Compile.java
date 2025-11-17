package org.jooq.tools.reflect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.StackWalker;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/Compile.class */
class Compile {

    /* JADX INFO: Access modifiers changed from: package-private */
    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/Compile$ThrowingBiFunction.class */
    public interface ThrowingBiFunction<T, U, R> {
        R apply(T t, U u) throws Exception;
    }

    Compile() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Class<?> compile(String className, String content, CompileOptions compileOptions) {
        return compile(className, content, compileOptions, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Class<?> compile(String className, String content, CompileOptions compileOptions, boolean expectResult) {
        ClassLoader classLoader;
        Class<?> result;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        if (compileOptions.classLoader != null) {
            classLoader = compileOptions.classLoader;
        } else {
            classLoader = lookup.lookupClass().getClassLoader();
        }
        ClassLoader cl = classLoader;
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new ReflectException("No compiler was provided by ToolProvider.getSystemJavaCompiler(). Make sure the jdk.compiler module is available.");
            }
            try {
                ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager((DiagnosticListener) null, (Locale) null, (Charset) null));
                List<CharSequenceJavaFileObject> files = new ArrayList<>();
                files.add(new CharSequenceJavaFileObject(className, content));
                StringWriter out = new StringWriter();
                List<String> options = new ArrayList<>(compileOptions.options);
                if (!options.contains("-classpath")) {
                    StringBuilder classpath = new StringBuilder();
                    String separator = System.getProperty("path.separator");
                    String cp = System.getProperty("java.class.path");
                    String mp = System.getProperty("jdk.module.path");
                    if (cp != null && !"".equals(cp)) {
                        classpath.append(cp);
                    }
                    if (mp != null && !"".equals(mp)) {
                        classpath.append(mp);
                    }
                    if (cl instanceof URLClassLoader) {
                        for (URL url : ((URLClassLoader) cl).getURLs()) {
                            if (classpath.length() > 0) {
                                classpath.append(separator);
                            }
                            if ("file".equals(url.getProtocol())) {
                                classpath.append(new File(url.toURI()));
                            }
                        }
                    }
                    options.addAll(Arrays.asList("-classpath", classpath.toString()));
                }
                JavaCompiler.CompilationTask task = compiler.getTask(out, fileManager, (DiagnosticListener) null, options, (Iterable) null, files);
                if (!compileOptions.processors.isEmpty()) {
                    task.setProcessors(compileOptions.processors);
                }
                task.call();
                if (fileManager.isEmpty()) {
                    if (!expectResult) {
                        return null;
                    }
                    throw new ReflectException("Compilation error: " + String.valueOf(out));
                }
                if (Reflect.CACHED_LOOKUP_CONSTRUCTOR != null) {
                    result = fileManager.loadAndReturnMainClass(className, (name, bytes) -> {
                        return (Class) Reflect.on(cl).call("defineClass", name, bytes, 0, Integer.valueOf(bytes.length)).get();
                    });
                } else {
                    Class<?> caller = (Class) StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(s -> {
                        return ((StackWalker.StackFrame) s.skip(2L).findFirst().get()).getDeclaringClass();
                    });
                    if (className.startsWith(caller.getPackageName() + ".") && Character.isUpperCase(className.charAt(caller.getPackageName().length() + 1))) {
                        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(caller, lookup);
                        result = fileManager.loadAndReturnMainClass(className, (name2, bytes2) -> {
                            return privateLookup.defineClass(bytes2);
                        });
                    } else {
                        ByteArrayClassLoader c = new ByteArrayClassLoader(fileManager.classes());
                        result = fileManager.loadAndReturnMainClass(className, (name3, bytes3) -> {
                            return c.loadClass(name3);
                        });
                    }
                }
                return result;
            } catch (ReflectException e2) {
                throw e2;
            } catch (Exception e3) {
                throw new ReflectException("Error while compiling " + className, e3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/Compile$ByteArrayClassLoader.class */
    public static final class ByteArrayClassLoader extends ClassLoader {
        private final Map<String, byte[]> classes;

        ByteArrayClassLoader(Map<String, byte[]> classes) {
            super(ByteArrayClassLoader.class.getClassLoader());
            this.classes = classes;
        }

        @Override // java.lang.ClassLoader
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = this.classes.get(name);
            if (bytes == null) {
                return super.findClass(name);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/Compile$JavaFileObject.class */
    public static final class JavaFileObject extends SimpleJavaFileObject {
        final ByteArrayOutputStream os;

        JavaFileObject(String name, JavaFileObject.Kind kind) {
            super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
            this.os = new ByteArrayOutputStream();
        }

        byte[] getBytes() {
            return this.os.toByteArray();
        }

        public OutputStream openOutputStream() {
            return this.os;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return new String(this.os.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/Compile$ClassFileManager.class */
    public static final class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final Map<String, JavaFileObject> fileObjectMap;
        private Map<String, byte[]> classes;

        ClassFileManager(StandardJavaFileManager standardManager) {
            super(standardManager);
            this.fileObjectMap = new LinkedHashMap();
        }

        /* renamed from: getJavaFileForOutput, reason: merged with bridge method [inline-methods] */
        public JavaFileObject m1799getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
            JavaFileObject result = new JavaFileObject(className, kind);
            this.fileObjectMap.put(className, result);
            return result;
        }

        boolean isEmpty() {
            return this.fileObjectMap.isEmpty();
        }

        Map<String, byte[]> classes() {
            if (this.classes == null) {
                this.classes = new LinkedHashMap();
                for (Map.Entry<String, JavaFileObject> entry : this.fileObjectMap.entrySet()) {
                    this.classes.put(entry.getKey(), entry.getValue().getBytes());
                }
            }
            return this.classes;
        }

        Class<?> loadAndReturnMainClass(String mainClassName, ThrowingBiFunction<String, byte[], Class<?>> definer) throws Exception {
            Class<?> result = null;
            Deque<Map.Entry<String, byte[]>> queue = new ArrayDeque<>(classes().entrySet());
            int n1 = queue.size();
            for (int i1 = 0; i1 < n1 && !queue.isEmpty(); i1++) {
                int n2 = queue.size();
                for (int i2 = 0; i2 < n2; i2++) {
                    Map.Entry<String, byte[]> entry = queue.pop();
                    try {
                        Class<?> c = definer.apply(entry.getKey(), entry.getValue());
                        if (mainClassName.equals(entry.getKey())) {
                            result = c;
                        }
                    } catch (ReflectException e) {
                        queue.offer(entry);
                    }
                }
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/Compile$CharSequenceJavaFileObject.class */
    public static final class CharSequenceJavaFileObject extends SimpleJavaFileObject {
        final CharSequence content;

        public CharSequenceJavaFileObject(String className, CharSequence content) {
            super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.content = content;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return this.content;
        }
    }
}
