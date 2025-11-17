package org.h2.util;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.CharsetUtil;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.h2.api.ErrorCode;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.springframework.web.context.support.GroovyWebApplicationContext;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SourceCompiler.class */
public class SourceCompiler {
    static final JavaCompiler JAVA_COMPILER;
    private static final Class<?> JAVAC_SUN;
    private static final String COMPILE_DIR = Utils.getProperty("java.io.tmpdir", ".");
    final HashMap<String, String> sources = new HashMap<>();
    final HashMap<String, Class<?>> compiled = new HashMap<>();
    final Map<String, CompiledScript> compiledScripts = new HashMap();
    boolean useJavaSystemCompiler = SysProperties.JAVA_SYSTEM_COMPILER;

    static {
        JavaCompiler javaCompiler;
        Class<?> cls;
        try {
            javaCompiler = ToolProvider.getSystemJavaCompiler();
        } catch (Exception e) {
            javaCompiler = null;
        }
        JAVA_COMPILER = javaCompiler;
        try {
            cls = Class.forName("com.sun.tools.javac.Main");
        } catch (Exception e2) {
            cls = null;
        }
        JAVAC_SUN = cls;
    }

    public void setSource(String str, String str2) {
        this.sources.put(str, str2);
        this.compiled.clear();
    }

    public void setJavaSystemCompiler(boolean z) {
        this.useJavaSystemCompiler = z;
    }

    public Class<?> getClass(String str) throws ClassNotFoundException {
        Class<?> cls = this.compiled.get(str);
        if (cls != null) {
            return cls;
        }
        String str2 = this.sources.get(str);
        if (isGroovySource(str2)) {
            Class<?> parseClass = GroovyCompiler.parseClass(str2, str);
            this.compiled.put(str, parseClass);
            return parseClass;
        }
        return new ClassLoader(getClass().getClassLoader()) { // from class: org.h2.util.SourceCompiler.1
            @Override // java.lang.ClassLoader
            public Class<?> findClass(String str3) throws ClassNotFoundException {
                String str4;
                Class<?> cls2 = SourceCompiler.this.compiled.get(str3);
                if (cls2 == null) {
                    String str5 = SourceCompiler.this.sources.get(str3);
                    String str6 = null;
                    int lastIndexOf = str3.lastIndexOf(46);
                    if (lastIndexOf >= 0) {
                        str6 = str3.substring(0, lastIndexOf);
                        str4 = str3.substring(lastIndexOf + 1);
                    } else {
                        str4 = str3;
                    }
                    String completeSourceCode = SourceCompiler.getCompleteSourceCode(str6, str4, str5);
                    if (SourceCompiler.JAVA_COMPILER != null && SourceCompiler.this.useJavaSystemCompiler) {
                        cls2 = SourceCompiler.this.javaxToolsJavac(str6, str4, completeSourceCode);
                    } else {
                        byte[] javacCompile = SourceCompiler.this.javacCompile(str6, str4, completeSourceCode);
                        if (javacCompile == null) {
                            cls2 = findSystemClass(str3);
                        } else {
                            cls2 = defineClass(str3, javacCompile, 0, javacCompile.length);
                        }
                    }
                    SourceCompiler.this.compiled.put(str3, cls2);
                }
                return cls2;
            }
        }.loadClass(str);
    }

    private static boolean isGroovySource(String str) {
        return str.startsWith("//groovy") || str.startsWith("@groovy");
    }

    private static boolean isJavascriptSource(String str) {
        return str.startsWith("//javascript");
    }

    private static boolean isRubySource(String str) {
        return str.startsWith("#ruby");
    }

    public static boolean isJavaxScriptSource(String str) {
        return isJavascriptSource(str) || isRubySource(str);
    }

    public CompiledScript getCompiledScript(String str) throws ScriptException {
        String str2;
        CompiledScript compiledScript = this.compiledScripts.get(str);
        if (compiledScript == null) {
            String str3 = this.sources.get(str);
            if (isJavascriptSource(str3)) {
                str2 = "javascript";
            } else if (isRubySource(str3)) {
                str2 = "ruby";
            } else {
                throw new IllegalStateException("Unknown language for " + str3);
            }
            Compilable engineByName = new ScriptEngineManager().getEngineByName(str2);
            if (engineByName.getClass().getName().equals("com.oracle.truffle.js.scriptengine.GraalJSScriptEngine")) {
                Bindings bindings = engineByName.getBindings(100);
                bindings.put("polyglot.js.allowHostAccess", true);
                bindings.put("polyglot.js.allowHostClassLookup", str4 -> {
                    return true;
                });
            }
            compiledScript = engineByName.compile(str3);
            this.compiledScripts.put(str, compiledScript);
        }
        return compiledScript;
    }

    public Method getMethod(String str) throws ClassNotFoundException {
        for (Method method : getClass(str).getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && !method.getName().startsWith("_") && !method.getName().equals("main")) {
                return method;
            }
        }
        return null;
    }

    byte[] javacCompile(String str, String str2, String str3) {
        Path path = Paths.get(COMPILE_DIR, new String[0]);
        if (str != null) {
            path = path.resolve(str.replace('.', '/'));
            try {
                Files.createDirectories(path, new FileAttribute[0]);
            } catch (Exception e) {
                throw DbException.convert(e);
            }
        }
        Path resolve = path.resolve(str2 + FileNameUtil.EXT_JAVA);
        resolve = path.resolve(str2 + ".class");
        try {
            try {
                Files.write(resolve, str3.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
                Files.deleteIfExists(resolve);
                if (JAVAC_SUN != null) {
                    javacSun(resolve);
                } else {
                    javacProcess(resolve);
                }
                byte[] readAllBytes = Files.readAllBytes(resolve);
                try {
                    Files.deleteIfExists(resolve);
                } catch (IOException e2) {
                }
                return readAllBytes;
            } catch (Exception e3) {
                throw DbException.convert(e3);
            }
        } finally {
            try {
                Files.deleteIfExists(resolve);
            } catch (IOException e4) {
            }
            try {
                Files.deleteIfExists(resolve);
            } catch (IOException e5) {
            }
        }
    }

    static String getCompleteSourceCode(String str, String str2, String str3) {
        if (str3.startsWith("package ")) {
            return str3;
        }
        StringBuilder sb = new StringBuilder();
        if (str != null) {
            sb.append("package ").append(str).append(";\n");
        }
        int indexOf = str3.indexOf("@CODE");
        String str4 = "import java.util.*;\nimport java.math.*;\nimport java.sql.*;\n";
        if (indexOf >= 0) {
            str4 = str3.substring(0, indexOf);
            str3 = str3.substring("@CODE".length() + indexOf);
        }
        sb.append(str4);
        sb.append("public class ").append(str2).append(" {\n    public static ").append(str3).append("\n}\n");
        return sb.toString();
    }

    /* JADX WARN: Failed to calculate best type for var: r14v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r15v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Multi-variable type inference failed. Error: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.RegisterArg.getSVar()" because the return value of "jadx.core.dex.nodes.InsnNode.getResult()" is null
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.collectRelatedVars(AbstractTypeConstraint.java:31)
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.<init>(AbstractTypeConstraint.java:19)
    	at jadx.core.dex.visitors.typeinference.TypeSearch$1.<init>(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeMoveConstraint(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeConstraint(TypeSearch.java:361)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.collectConstraints(TypeSearch.java:341)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:60)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.runMultiVariableSearch(FixTypesVisitor.java:116)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Not initialized variable reg: 14, insn: 0x00dc: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r14 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:41:0x00dc */
    /* JADX WARN: Not initialized variable reg: 15, insn: 0x00e1: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r15 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:43:0x00e1 */
    /* JADX WARN: Type inference failed for: r14v0, types: [javax.tools.JavaFileManager] */
    /* JADX WARN: Type inference failed for: r15v0, types: [java.lang.Throwable] */
    Class<?> javaxToolsJavac(String str, String str2, String str3) {
        ?? r14;
        ?? r15;
        boolean booleanValue;
        String str4 = str + "." + str2;
        StringWriter stringWriter = new StringWriter();
        try {
            try {
                ClassFileManager classFileManager = new ClassFileManager(JAVA_COMPILER.getStandardFileManager((DiagnosticListener) null, (Locale) null, (Charset) null));
                Throwable th = null;
                ArrayList arrayList = new ArrayList();
                arrayList.add(new StringJavaFileObject(str4, str3));
                synchronized (JAVA_COMPILER) {
                    booleanValue = JAVA_COMPILER.getTask(stringWriter, classFileManager, (DiagnosticListener) null, (Iterable) null, (Iterable) null, arrayList).call().booleanValue();
                }
                handleSyntaxError(stringWriter.toString(), booleanValue ? 0 : 1);
                Class<?> loadClass = classFileManager.getClassLoader((JavaFileManager.Location) null).loadClass(str4);
                if (classFileManager != null) {
                    if (0 != 0) {
                        try {
                            classFileManager.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        classFileManager.close();
                    }
                }
                return loadClass;
            } catch (Throwable th3) {
                if (r14 != 0) {
                    if (r15 != 0) {
                        try {
                            r14.close();
                        } catch (Throwable th4) {
                            r15.addSuppressed(th4);
                        }
                    } else {
                        r14.close();
                    }
                }
                throw th3;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw DbException.convert(e);
        }
    }

    private static void javacProcess(Path path) {
        exec("javac", "-sourcepath", COMPILE_DIR, "-d", COMPILE_DIR, "-encoding", CharsetUtil.UTF_8, path.toAbsolutePath().toString());
    }

    private static int exec(String... strArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
            processBuilder.environment().remove("JAVA_TOOL_OPTIONS");
            processBuilder.command(strArr);
            Process start = processBuilder.start();
            copyInThread(start.getInputStream(), byteArrayOutputStream);
            copyInThread(start.getErrorStream(), byteArrayOutputStream);
            start.waitFor();
            handleSyntaxError(Utils10.byteArrayOutputStreamToString(byteArrayOutputStream, StandardCharsets.UTF_8), start.exitValue());
            return start.exitValue();
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }

    private static void copyInThread(final InputStream inputStream, final OutputStream outputStream) {
        new Task() { // from class: org.h2.util.SourceCompiler.2
            @Override // org.h2.util.Task
            public void call() throws IOException {
                IOUtils.copy(inputStream, outputStream);
            }
        }.execute();
    }

    private static synchronized void javacSun(Path path) {
        PrintStream printStream = System.err;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            try {
                System.setErr(new PrintStream((OutputStream) byteArrayOutputStream, false, CharsetUtil.UTF_8));
                handleSyntaxError(Utils10.byteArrayOutputStreamToString(byteArrayOutputStream, StandardCharsets.UTF_8), ((Integer) JAVAC_SUN.getMethod("compile", String[].class).invoke(JAVAC_SUN.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]), new String[]{"-sourcepath", COMPILE_DIR, "-d", COMPILE_DIR, "-encoding", CharsetUtil.UTF_8, path.toAbsolutePath().toString()})).intValue());
                System.setErr(printStream);
            } catch (Exception e) {
                throw DbException.convert(e);
            }
        } catch (Throwable th) {
            System.setErr(printStream);
            throw th;
        }
    }

    private static void handleSyntaxError(String str, int i) {
        if (0 == i) {
            return;
        }
        boolean z = false;
        BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    if (!readLine.endsWith("warning") && !readLine.endsWith("warnings") && !readLine.startsWith("Note:") && !readLine.startsWith("warning:")) {
                        z = true;
                        break;
                    }
                } else {
                    break;
                }
            } catch (IOException e) {
            }
        }
        if (z) {
            throw DbException.get(ErrorCode.SYNTAX_ERROR_1, StringUtils.replaceAll(str, COMPILE_DIR, ""));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SourceCompiler$GroovyCompiler.class */
    public static final class GroovyCompiler {
        private static final Object LOADER;
        private static final Throwable INIT_FAIL_EXCEPTION;

        private GroovyCompiler() {
        }

        static {
            Object obj = null;
            Exception exc = null;
            try {
                Class<?> cls = Class.forName("org.codehaus.groovy.control.customizers.ImportCustomizer");
                Object newInstance = Utils.newInstance("org.codehaus.groovy.control.customizers.ImportCustomizer", new Object[0]);
                Utils.callMethod(newInstance, "addImports", new String[]{"java.sql.Connection", "java.sql.Types", "java.sql.ResultSet", "groovy.sql.Sql", "org.h2.tools.SimpleResultSet"});
                Object newInstance2 = Array.newInstance(cls, 1);
                Array.set(newInstance2, 0, newInstance);
                Object newInstance3 = Utils.newInstance("org.codehaus.groovy.control.CompilerConfiguration", new Object[0]);
                Utils.callMethod(newInstance3, "addCompilationCustomizers", newInstance2);
                obj = Utils.newInstance("groovy.lang.GroovyClassLoader", GroovyCompiler.class.getClassLoader(), newInstance3);
            } catch (Exception e) {
                exc = e;
            }
            LOADER = obj;
            INIT_FAIL_EXCEPTION = exc;
        }

        public static Class<?> parseClass(String str, String str2) {
            if (LOADER == null) {
                throw new RuntimeException("Compile fail: no Groovy jar in the classpath", INIT_FAIL_EXCEPTION);
            }
            try {
                Object newInstance = Utils.newInstance("groovy.lang.GroovyCodeSource", str, str2 + GroovyWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX, CharsetUtil.UTF_8);
                Utils.callMethod(newInstance, "setCachable", false);
                return (Class) Utils.callMethod(LOADER, "parseClass", newInstance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SourceCompiler$StringJavaFileObject.class */
    public static class StringJavaFileObject extends SimpleJavaFileObject {
        private final String sourceCode;

        public StringJavaFileObject(String str, String str2) {
            super(URI.create("string:///" + str.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.sourceCode = str2;
        }

        public CharSequence getCharContent(boolean z) {
            return this.sourceCode;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SourceCompiler$JavaClassObject.class */
    static class JavaClassObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream out;

        public JavaClassObject(String str, JavaFileObject.Kind kind) {
            super(URI.create("string:///" + str.replace('.', '/') + kind.extension), kind);
            this.out = new ByteArrayOutputStream();
        }

        public byte[] getBytes() {
            return this.out.toByteArray();
        }

        public OutputStream openOutputStream() throws IOException {
            return this.out;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SourceCompiler$ClassFileManager.class */
    public static class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        Map<String, JavaClassObject> classObjectsByName;
        private SecureClassLoader classLoader;

        public ClassFileManager(StandardJavaFileManager standardJavaFileManager) {
            super(standardJavaFileManager);
            this.classObjectsByName = new HashMap();
            this.classLoader = new SecureClassLoader() { // from class: org.h2.util.SourceCompiler.ClassFileManager.1
                @Override // java.lang.ClassLoader
                protected Class<?> findClass(String str) throws ClassNotFoundException {
                    byte[] bytes = ClassFileManager.this.classObjectsByName.get(str).getBytes();
                    return super.defineClass(str, bytes, 0, bytes.length);
                }
            };
        }

        public ClassLoader getClassLoader(JavaFileManager.Location location) {
            return this.classLoader;
        }

        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String str, JavaFileObject.Kind kind, FileObject fileObject) throws IOException {
            JavaClassObject javaClassObject = new JavaClassObject(str, kind);
            this.classObjectsByName.put(str, javaClassObject);
            return javaClassObject;
        }
    }
}
