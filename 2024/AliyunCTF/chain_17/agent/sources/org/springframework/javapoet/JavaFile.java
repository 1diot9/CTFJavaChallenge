package org.springframework.javapoet;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.StrPool;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.springframework.javapoet.CodeBlock;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/JavaFile.class */
public final class JavaFile {
    private static final Appendable NULL_APPENDABLE = new Appendable() { // from class: org.springframework.javapoet.JavaFile.1
        @Override // java.lang.Appendable
        public Appendable append(CharSequence charSequence) {
            return this;
        }

        @Override // java.lang.Appendable
        public Appendable append(CharSequence charSequence, int start, int end) {
            return this;
        }

        @Override // java.lang.Appendable
        public Appendable append(char c) {
            return this;
        }
    };
    public final CodeBlock fileComment;
    public final String packageName;
    public final TypeSpec typeSpec;
    public final boolean skipJavaLangImports;
    private final Set<String> staticImports;
    private final Set<String> alwaysQualify;
    private final String indent;

    private JavaFile(Builder builder) {
        this.fileComment = builder.fileComment.build();
        this.packageName = builder.packageName;
        this.typeSpec = builder.typeSpec;
        this.skipJavaLangImports = builder.skipJavaLangImports;
        this.staticImports = Util.immutableSet(builder.staticImports);
        this.indent = builder.indent;
        Set<String> alwaysQualifiedNames = new LinkedHashSet<>();
        fillAlwaysQualifiedNames(builder.typeSpec, alwaysQualifiedNames);
        this.alwaysQualify = Util.immutableSet(alwaysQualifiedNames);
    }

    private void fillAlwaysQualifiedNames(TypeSpec spec, Set<String> alwaysQualifiedNames) {
        alwaysQualifiedNames.addAll(spec.alwaysQualifiedNames);
        for (TypeSpec nested : spec.typeSpecs) {
            fillAlwaysQualifiedNames(nested, alwaysQualifiedNames);
        }
    }

    public void writeTo(Appendable out) throws IOException {
        CodeWriter importsCollector = new CodeWriter(NULL_APPENDABLE, this.indent, this.staticImports, this.alwaysQualify);
        emit(importsCollector);
        Map<String, ClassName> suggestedImports = importsCollector.suggestedImports();
        CodeWriter codeWriter = new CodeWriter(out, this.indent, suggestedImports, this.staticImports, this.alwaysQualify);
        emit(codeWriter);
    }

    public void writeTo(Path directory) throws IOException {
        writeToPath(directory);
    }

    public void writeTo(Path directory, Charset charset) throws IOException {
        writeToPath(directory, charset);
    }

    public Path writeToPath(Path directory) throws IOException {
        return writeToPath(directory, StandardCharsets.UTF_8);
    }

    public Path writeToPath(Path directory, Charset charset) throws IOException {
        Util.checkArgument(Files.notExists(directory, new LinkOption[0]) || Files.isDirectory(directory, new LinkOption[0]), "path %s exists but is not a directory.", directory);
        Path outputDirectory = directory;
        if (!this.packageName.isEmpty()) {
            for (String packageComponent : this.packageName.split("\\.")) {
                outputDirectory = outputDirectory.resolve(packageComponent);
            }
            Files.createDirectories(outputDirectory, new FileAttribute[0]);
        }
        Path outputPath = outputDirectory.resolve(this.typeSpec.name + FileNameUtil.EXT_JAVA);
        Writer writer = new OutputStreamWriter(Files.newOutputStream(outputPath, new OpenOption[0]), charset);
        Throwable th = null;
        try {
            try {
                writeTo(writer);
                $closeResource(null, writer);
                return outputPath;
            } finally {
            }
        } catch (Throwable th2) {
            $closeResource(th, writer);
            throw th2;
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    public void writeTo(File directory) throws IOException {
        writeTo(directory.toPath());
    }

    public File writeToFile(File directory) throws IOException {
        Path outputPath = writeToPath(directory.toPath());
        return outputPath.toFile();
    }

    public void writeTo(Filer filer) throws IOException {
        String str;
        if (this.packageName.isEmpty()) {
            str = this.typeSpec.name;
        } else {
            str = this.packageName + "." + this.typeSpec.name;
        }
        String fileName = str;
        List<Element> originatingElements = this.typeSpec.originatingElements;
        JavaFileObject filerSourceFile = filer.createSourceFile(fileName, (Element[]) originatingElements.toArray(new Element[originatingElements.size()]));
        try {
            Writer writer = filerSourceFile.openWriter();
            Throwable th = null;
            try {
                try {
                    writeTo(writer);
                    if (writer != null) {
                        $closeResource(null, writer);
                    }
                } catch (Throwable th2) {
                    if (writer != null) {
                        $closeResource(th, writer);
                    }
                    throw th2;
                }
            } finally {
            }
        } catch (Exception e) {
            try {
                filerSourceFile.delete();
            } catch (Exception e2) {
            }
            throw e;
        }
    }

    private void emit(CodeWriter codeWriter) throws IOException {
        codeWriter.pushPackage(this.packageName);
        if (!this.fileComment.isEmpty()) {
            codeWriter.emitComment(this.fileComment);
        }
        if (!this.packageName.isEmpty()) {
            codeWriter.emit("package $L;\n", this.packageName);
            codeWriter.emit(StrPool.LF);
        }
        if (!this.staticImports.isEmpty()) {
            for (String signature : this.staticImports) {
                codeWriter.emit("import static $L;\n", signature);
            }
            codeWriter.emit(StrPool.LF);
        }
        int importedTypesCount = 0;
        Iterator it = new TreeSet(codeWriter.importedTypes().values()).iterator();
        while (it.hasNext()) {
            ClassName className = (ClassName) it.next();
            if (!this.skipJavaLangImports || !className.packageName().equals("java.lang") || this.alwaysQualify.contains(className.simpleName)) {
                codeWriter.emit("import $L;\n", className.withoutAnnotations());
                importedTypesCount++;
            }
        }
        if (importedTypesCount > 0) {
            codeWriter.emit(StrPool.LF);
        }
        this.typeSpec.emit(codeWriter, null, Collections.emptySet());
        codeWriter.popPackage();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && getClass() == o.getClass()) {
            return toString().equals(o.toString());
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        try {
            StringBuilder result = new StringBuilder();
            writeTo(result);
            return result.toString();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    public JavaFileObject toJavaFileObject() {
        String str;
        StringBuilder sb = new StringBuilder();
        if (this.packageName.isEmpty()) {
            str = this.typeSpec.name;
        } else {
            str = this.packageName.replace('.', '/') + '/' + this.typeSpec.name;
        }
        URI uri = URI.create(sb.append(str).append(JavaFileObject.Kind.SOURCE.extension).toString());
        return new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) { // from class: org.springframework.javapoet.JavaFile.2
            private final long lastModified = System.currentTimeMillis();

            /* renamed from: getCharContent, reason: merged with bridge method [inline-methods] */
            public String m2586getCharContent(boolean ignoreEncodingErrors) {
                return JavaFile.this.toString();
            }

            public InputStream openInputStream() throws IOException {
                return new ByteArrayInputStream(m2586getCharContent(true).getBytes(StandardCharsets.UTF_8));
            }

            public long getLastModified() {
                return this.lastModified;
            }
        };
    }

    public static Builder builder(String packageName, TypeSpec typeSpec) {
        Util.checkNotNull(packageName, "packageName == null", new Object[0]);
        Util.checkNotNull(typeSpec, "typeSpec == null", new Object[0]);
        return new Builder(packageName, typeSpec);
    }

    public Builder toBuilder() {
        Builder builder = new Builder(this.packageName, this.typeSpec);
        builder.fileComment.add(this.fileComment);
        builder.skipJavaLangImports = this.skipJavaLangImports;
        builder.indent = this.indent;
        return builder;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/JavaFile$Builder.class */
    public static final class Builder {
        private final String packageName;
        private final TypeSpec typeSpec;
        private final CodeBlock.Builder fileComment;
        private boolean skipJavaLangImports;
        private String indent;
        public final Set<String> staticImports;

        private Builder(String packageName, TypeSpec typeSpec) {
            this.fileComment = CodeBlock.builder();
            this.indent = "  ";
            this.staticImports = new TreeSet();
            this.packageName = packageName;
            this.typeSpec = typeSpec;
        }

        public Builder addFileComment(String format, Object... args) {
            this.fileComment.add(format, args);
            return this;
        }

        public Builder addStaticImport(Enum<?> constant) {
            return addStaticImport(ClassName.get(constant.getDeclaringClass()), constant.name());
        }

        public Builder addStaticImport(Class<?> clazz, String... names) {
            return addStaticImport(ClassName.get(clazz), names);
        }

        public Builder addStaticImport(ClassName className, String... names) {
            Util.checkArgument(className != null, "className == null", new Object[0]);
            Util.checkArgument(names != null, "names == null", new Object[0]);
            Util.checkArgument(names.length > 0, "names array is empty", new Object[0]);
            int length = names.length;
            for (int i = 0; i < length; i++) {
                String name = names[i];
                Util.checkArgument(name != null, "null entry in names array: %s", Arrays.toString(names));
                this.staticImports.add(className.canonicalName + "." + name);
            }
            return this;
        }

        public Builder skipJavaLangImports(boolean skipJavaLangImports) {
            this.skipJavaLangImports = skipJavaLangImports;
            return this;
        }

        public Builder indent(String indent) {
            this.indent = indent;
            return this;
        }

        public JavaFile build() {
            return new JavaFile(this);
        }
    }
}
