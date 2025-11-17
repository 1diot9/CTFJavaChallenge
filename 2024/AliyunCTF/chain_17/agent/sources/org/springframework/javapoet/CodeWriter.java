package org.springframework.javapoet;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/CodeWriter.class */
public final class CodeWriter {
    private static final String NO_PACKAGE = new String();
    private final String indent;
    private final LineWrapper out;
    private int indentLevel;
    private boolean javadoc;
    private boolean comment;
    private String packageName;
    private final List<TypeSpec> typeSpecStack;
    private final Set<String> staticImportClassNames;
    private final Set<String> staticImports;
    private final Set<String> alwaysQualify;
    private final Map<String, ClassName> importedTypes;
    private final Map<String, ClassName> importableTypes;
    private final Set<String> referencedNames;
    private final Multiset<String> currentTypeVariables;
    private boolean trailingNewline;
    int statementLine;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeWriter(Appendable out) {
        this(out, "  ", Collections.emptySet(), Collections.emptySet());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeWriter(Appendable out, String indent, Set<String> staticImports, Set<String> alwaysQualify) {
        this(out, indent, Collections.emptyMap(), staticImports, alwaysQualify);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeWriter(Appendable out, String indent, Map<String, ClassName> importedTypes, Set<String> staticImports, Set<String> alwaysQualify) {
        this.javadoc = false;
        this.comment = false;
        this.packageName = NO_PACKAGE;
        this.typeSpecStack = new ArrayList();
        this.importableTypes = new LinkedHashMap();
        this.referencedNames = new LinkedHashSet();
        this.currentTypeVariables = new Multiset<>();
        this.statementLine = -1;
        this.out = new LineWrapper(out, indent, 100);
        this.indent = (String) Util.checkNotNull(indent, "indent == null", new Object[0]);
        this.importedTypes = (Map) Util.checkNotNull(importedTypes, "importedTypes == null", new Object[0]);
        this.staticImports = (Set) Util.checkNotNull(staticImports, "staticImports == null", new Object[0]);
        this.alwaysQualify = (Set) Util.checkNotNull(alwaysQualify, "alwaysQualify == null", new Object[0]);
        this.staticImportClassNames = new LinkedHashSet();
        for (String signature : staticImports) {
            this.staticImportClassNames.add(signature.substring(0, signature.lastIndexOf(46)));
        }
    }

    public Map<String, ClassName> importedTypes() {
        return this.importedTypes;
    }

    public CodeWriter indent() {
        return indent(1);
    }

    public CodeWriter indent(int levels) {
        this.indentLevel += levels;
        return this;
    }

    public CodeWriter unindent() {
        return unindent(1);
    }

    public CodeWriter unindent(int levels) {
        Util.checkArgument(this.indentLevel - levels >= 0, "cannot unindent %s from %s", Integer.valueOf(levels), Integer.valueOf(this.indentLevel));
        this.indentLevel -= levels;
        return this;
    }

    public CodeWriter pushPackage(String packageName) {
        Util.checkState(this.packageName == NO_PACKAGE, "package already set: %s", this.packageName);
        this.packageName = (String) Util.checkNotNull(packageName, "packageName == null", new Object[0]);
        return this;
    }

    public CodeWriter popPackage() {
        Util.checkState(this.packageName != NO_PACKAGE, "package not set", new Object[0]);
        this.packageName = NO_PACKAGE;
        return this;
    }

    public CodeWriter pushType(TypeSpec type) {
        this.typeSpecStack.add(type);
        return this;
    }

    public CodeWriter popType() {
        this.typeSpecStack.remove(this.typeSpecStack.size() - 1);
        return this;
    }

    public void emitComment(CodeBlock codeBlock) throws IOException {
        this.trailingNewline = true;
        this.comment = true;
        try {
            emit(codeBlock);
            emit(StrPool.LF);
        } finally {
            this.comment = false;
        }
    }

    public void emitJavadoc(CodeBlock javadocCodeBlock) throws IOException {
        if (javadocCodeBlock.isEmpty()) {
            return;
        }
        emit("/**\n");
        this.javadoc = true;
        try {
            emit(javadocCodeBlock, true);
            emit(" */\n");
        } finally {
            this.javadoc = false;
        }
    }

    public void emitAnnotations(List<AnnotationSpec> annotations, boolean inline) throws IOException {
        for (AnnotationSpec annotationSpec : annotations) {
            annotationSpec.emit(this, inline);
            emit(inline ? CharSequenceUtil.SPACE : StrPool.LF);
        }
    }

    public void emitModifiers(Set<Modifier> modifiers, Set<Modifier> implicitModifiers) throws IOException {
        if (modifiers.isEmpty()) {
            return;
        }
        Iterator it = EnumSet.copyOf((Collection) modifiers).iterator();
        while (it.hasNext()) {
            Modifier modifier = (Modifier) it.next();
            if (!implicitModifiers.contains(modifier)) {
                emitAndIndent(modifier.name().toLowerCase(Locale.US));
                emitAndIndent(CharSequenceUtil.SPACE);
            }
        }
    }

    public void emitModifiers(Set<Modifier> modifiers) throws IOException {
        emitModifiers(modifiers, Collections.emptySet());
    }

    public void emitTypeVariables(List<TypeVariableName> typeVariables) throws IOException {
        if (typeVariables.isEmpty()) {
            return;
        }
        typeVariables.forEach(typeVariable -> {
            this.currentTypeVariables.add(typeVariable.name);
        });
        emit("<");
        boolean firstTypeVariable = true;
        for (TypeVariableName typeVariable2 : typeVariables) {
            if (!firstTypeVariable) {
                emit(", ");
            }
            emitAnnotations(typeVariable2.annotations, true);
            emit("$L", typeVariable2.name);
            boolean firstBound = true;
            for (TypeName bound : typeVariable2.bounds) {
                emit(firstBound ? " extends $T" : " & $T", bound);
                firstBound = false;
            }
            firstTypeVariable = false;
        }
        emit(">");
    }

    public void popTypeVariables(List<TypeVariableName> typeVariables) throws IOException {
        typeVariables.forEach(typeVariable -> {
            this.currentTypeVariables.remove(typeVariable.name);
        });
    }

    public CodeWriter emit(String s) throws IOException {
        return emitAndIndent(s);
    }

    public CodeWriter emit(String format, Object... args) throws IOException {
        return emit(CodeBlock.of(format, args));
    }

    public CodeWriter emit(CodeBlock codeBlock) throws IOException {
        return emit(codeBlock, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x02c9 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:103:0x02d9 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:106:0x02e9 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:122:0x018c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x01a0 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x01b8 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x01e6 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0263 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x026e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0276 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x027e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x029d A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.springframework.javapoet.CodeWriter emit(org.springframework.javapoet.CodeBlock r5, boolean r6) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 826
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.javapoet.CodeWriter.emit(org.springframework.javapoet.CodeBlock, boolean):org.springframework.javapoet.CodeWriter");
    }

    public CodeWriter emitWrappingSpace() throws IOException {
        this.out.wrappingSpace(this.indentLevel + 2);
        return this;
    }

    private static String extractMemberName(String part) {
        Util.checkArgument(Character.isJavaIdentifierStart(part.charAt(0)), "not an identifier: %s", part);
        for (int i = 1; i <= part.length(); i++) {
            if (!SourceVersion.isIdentifier(part.substring(0, i))) {
                return part.substring(0, i - 1);
            }
        }
        return part;
    }

    private boolean emitStaticImportMember(String canonical, String part) throws IOException {
        String partWithoutLeadingDot = part.substring(1);
        if (partWithoutLeadingDot.isEmpty()) {
            return false;
        }
        char first = partWithoutLeadingDot.charAt(0);
        if (!Character.isJavaIdentifierStart(first)) {
            return false;
        }
        String explicit = canonical + "." + extractMemberName(partWithoutLeadingDot);
        String wildcard = canonical + ".*";
        if (this.staticImports.contains(explicit) || this.staticImports.contains(wildcard)) {
            emitAndIndent(partWithoutLeadingDot);
            return true;
        }
        return false;
    }

    private void emitLiteral(Object o) throws IOException {
        if (o instanceof TypeSpec) {
            TypeSpec typeSpec = (TypeSpec) o;
            typeSpec.emit(this, null, Collections.emptySet());
        } else if (o instanceof AnnotationSpec) {
            AnnotationSpec annotationSpec = (AnnotationSpec) o;
            annotationSpec.emit(this, true);
        } else if (o instanceof CodeBlock) {
            CodeBlock codeBlock = (CodeBlock) o;
            emit(codeBlock);
        } else {
            emitAndIndent(String.valueOf(o));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String lookupName(ClassName className) {
        String topLevelSimpleName = className.topLevelClassName().simpleName();
        if (this.currentTypeVariables.contains(topLevelSimpleName)) {
            return className.canonicalName;
        }
        boolean nameResolved = false;
        ClassName className2 = className;
        while (true) {
            ClassName c = className2;
            if (c != null) {
                ClassName resolved = resolve(c.simpleName());
                nameResolved = resolved != null;
                if (resolved == null || !Objects.equals(resolved.canonicalName, c.canonicalName)) {
                    className2 = c.enclosingClassName();
                } else {
                    int suffixOffset = c.simpleNames().size() - 1;
                    return String.join(".", className.simpleNames().subList(suffixOffset, className.simpleNames().size()));
                }
            } else {
                if (nameResolved) {
                    return className.canonicalName;
                }
                if (Objects.equals(this.packageName, className.packageName())) {
                    this.referencedNames.add(topLevelSimpleName);
                    return String.join(".", className.simpleNames());
                }
                if (!this.javadoc) {
                    importableType(className);
                }
                return className.canonicalName;
            }
        }
    }

    private void importableType(ClassName className) {
        ClassName topLevelClassName;
        String simpleName;
        ClassName replaced;
        if (!className.packageName().isEmpty() && !this.alwaysQualify.contains(className.simpleName) && (replaced = this.importableTypes.put((simpleName = (topLevelClassName = className.topLevelClassName()).simpleName()), topLevelClassName)) != null) {
            this.importableTypes.put(simpleName, replaced);
        }
    }

    private ClassName resolve(String simpleName) {
        for (int i = this.typeSpecStack.size() - 1; i >= 0; i--) {
            TypeSpec typeSpec = this.typeSpecStack.get(i);
            if (typeSpec.nestedTypesSimpleNames.contains(simpleName)) {
                return stackClassName(i, simpleName);
            }
        }
        if (this.typeSpecStack.size() > 0 && Objects.equals(this.typeSpecStack.get(0).name, simpleName)) {
            return ClassName.get(this.packageName, simpleName, new String[0]);
        }
        ClassName importedType = this.importedTypes.get(simpleName);
        if (importedType != null) {
            return importedType;
        }
        return null;
    }

    private ClassName stackClassName(int stackDepth, String simpleName) {
        ClassName className = ClassName.get(this.packageName, this.typeSpecStack.get(0).name, new String[0]);
        for (int i = 1; i <= stackDepth; i++) {
            className = className.nestedClass(this.typeSpecStack.get(i).name);
        }
        return className.nestedClass(simpleName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeWriter emitAndIndent(String s) throws IOException {
        boolean first = true;
        for (String line : s.split("\\R", -1)) {
            if (!first) {
                if ((this.javadoc || this.comment) && this.trailingNewline) {
                    emitIndentation();
                    this.out.append(this.javadoc ? " *" : "//");
                }
                this.out.append(StrPool.LF);
                this.trailingNewline = true;
                if (this.statementLine != -1) {
                    if (this.statementLine == 0) {
                        indent(2);
                    }
                    this.statementLine++;
                }
            }
            first = false;
            if (!line.isEmpty()) {
                if (this.trailingNewline) {
                    emitIndentation();
                    if (this.javadoc) {
                        this.out.append(" * ");
                    } else if (this.comment) {
                        this.out.append("// ");
                    }
                }
                this.out.append(line);
                this.trailingNewline = false;
            }
        }
        return this;
    }

    private void emitIndentation() throws IOException {
        for (int j = 0; j < this.indentLevel; j++) {
            this.out.append(this.indent);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<String, ClassName> suggestedImports() {
        Map<String, ClassName> result = new LinkedHashMap<>(this.importableTypes);
        result.keySet().removeAll(this.referencedNames);
        return result;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/CodeWriter$Multiset.class */
    private static final class Multiset<T> {
        private final Map<T, Integer> map;

        private Multiset() {
            this.map = new LinkedHashMap();
        }

        void add(T t) {
            int count = this.map.getOrDefault(t, 0).intValue();
            this.map.put(t, Integer.valueOf(count + 1));
        }

        void remove(T t) {
            int count = this.map.getOrDefault(t, 0).intValue();
            if (count == 0) {
                throw new IllegalStateException(t + " is not in the multiset");
            }
            this.map.put(t, Integer.valueOf(count - 1));
        }

        boolean contains(T t) {
            return this.map.getOrDefault(t, 0).intValue() > 0;
        }
    }
}
