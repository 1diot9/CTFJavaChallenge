package cn.hutool.core.compiler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/compiler/CompilerUtil.class */
public class CompilerUtil {
    public static final JavaCompiler SYSTEM_COMPILER = ToolProvider.getSystemJavaCompiler();

    public static boolean compile(String... sourceFiles) {
        return 0 == SYSTEM_COMPILER.run((InputStream) null, (OutputStream) null, (OutputStream) null, sourceFiles);
    }

    public static StandardJavaFileManager getFileManager() {
        return getFileManager(null);
    }

    public static StandardJavaFileManager getFileManager(DiagnosticListener<? super JavaFileObject> diagnosticListener) {
        return SYSTEM_COMPILER.getStandardFileManager(diagnosticListener, (Locale) null, (Charset) null);
    }

    public static JavaCompiler.CompilationTask getTask(JavaFileManager fileManager, DiagnosticListener<? super JavaFileObject> diagnosticListener, Iterable<String> options, Iterable<? extends JavaFileObject> compilationUnits) {
        return SYSTEM_COMPILER.getTask((Writer) null, fileManager, diagnosticListener, options, (Iterable) null, compilationUnits);
    }

    public static JavaSourceCompiler getCompiler(ClassLoader parent) {
        return JavaSourceCompiler.create(parent);
    }
}
