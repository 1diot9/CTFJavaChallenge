package org.springframework.javapoet;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/ClassName.class */
public final class ClassName extends TypeName implements Comparable<ClassName> {
    public static final ClassName OBJECT = get((Class<?>) Object.class);
    private static final String NO_PACKAGE = "";
    final String packageName;
    final ClassName enclosingClassName;
    final String simpleName;
    private List<String> simpleNames;
    final String canonicalName;

    @Override // org.springframework.javapoet.TypeName
    public /* bridge */ /* synthetic */ TypeName annotated(List list) {
        return annotated((List<AnnotationSpec>) list);
    }

    private ClassName(String packageName, ClassName enclosingClassName, String simpleName) {
        this(packageName, enclosingClassName, simpleName, (List<AnnotationSpec>) Collections.emptyList());
    }

    private ClassName(String packageName, ClassName enclosingClassName, String simpleName, List<AnnotationSpec> annotations) {
        super(annotations);
        String str;
        this.packageName = (String) Objects.requireNonNull(packageName, "packageName == null");
        this.enclosingClassName = enclosingClassName;
        this.simpleName = simpleName;
        if (enclosingClassName != null) {
            str = enclosingClassName.canonicalName + '.' + simpleName;
        } else {
            str = packageName.isEmpty() ? simpleName : packageName + '.' + simpleName;
        }
        this.canonicalName = str;
    }

    @Override // org.springframework.javapoet.TypeName
    public ClassName annotated(List<AnnotationSpec> annotations) {
        return new ClassName(this.packageName, this.enclosingClassName, this.simpleName, concatAnnotations(annotations));
    }

    @Override // org.springframework.javapoet.TypeName
    public ClassName withoutAnnotations() {
        ClassName className;
        if (!isAnnotated()) {
            return this;
        }
        if (this.enclosingClassName != null) {
            className = this.enclosingClassName.withoutAnnotations();
        } else {
            className = null;
        }
        ClassName resultEnclosingClassName = className;
        return new ClassName(this.packageName, resultEnclosingClassName, this.simpleName);
    }

    @Override // org.springframework.javapoet.TypeName
    public boolean isAnnotated() {
        return super.isAnnotated() || (this.enclosingClassName != null && this.enclosingClassName.isAnnotated());
    }

    public String packageName() {
        return this.packageName;
    }

    public ClassName enclosingClassName() {
        return this.enclosingClassName;
    }

    public ClassName topLevelClassName() {
        return this.enclosingClassName != null ? this.enclosingClassName.topLevelClassName() : this;
    }

    public String reflectionName() {
        if (this.enclosingClassName != null) {
            return this.enclosingClassName.reflectionName() + '$' + this.simpleName;
        }
        return this.packageName.isEmpty() ? this.simpleName : this.packageName + '.' + this.simpleName;
    }

    public List<String> simpleNames() {
        if (this.simpleNames != null) {
            return this.simpleNames;
        }
        if (this.enclosingClassName == null) {
            this.simpleNames = Collections.singletonList(this.simpleName);
        } else {
            List<String> mutableNames = new ArrayList<>();
            mutableNames.addAll(enclosingClassName().simpleNames());
            mutableNames.add(this.simpleName);
            this.simpleNames = Collections.unmodifiableList(mutableNames);
        }
        return this.simpleNames;
    }

    public ClassName peerClass(String name) {
        return new ClassName(this.packageName, this.enclosingClassName, name);
    }

    public ClassName nestedClass(String name) {
        return new ClassName(this.packageName, this, name);
    }

    public String simpleName() {
        return this.simpleName;
    }

    public String canonicalName() {
        return this.canonicalName;
    }

    public static ClassName get(Class<?> clazz) {
        Util.checkNotNull(clazz, "clazz == null", new Object[0]);
        Util.checkArgument(!clazz.isPrimitive(), "primitive types cannot be represented as a ClassName", new Object[0]);
        Util.checkArgument(!Void.TYPE.equals(clazz), "'void' type cannot be represented as a ClassName", new Object[0]);
        Util.checkArgument(!clazz.isArray(), "array types cannot be represented as a ClassName", new Object[0]);
        String anonymousSuffix = "";
        while (clazz.isAnonymousClass()) {
            int lastDollar = clazz.getName().lastIndexOf(36);
            anonymousSuffix = clazz.getName().substring(lastDollar) + anonymousSuffix;
            clazz = clazz.getEnclosingClass();
        }
        String name = clazz.getSimpleName() + anonymousSuffix;
        if (clazz.getEnclosingClass() == null) {
            int lastDot = clazz.getName().lastIndexOf(46);
            String packageName = lastDot != -1 ? clazz.getName().substring(0, lastDot) : "";
            return new ClassName(packageName, null, name);
        }
        return get(clazz.getEnclosingClass()).nestedClass(name);
    }

    public static ClassName bestGuess(String classNameString) {
        int p = 0;
        while (p < classNameString.length() && Character.isLowerCase(classNameString.codePointAt(p))) {
            p = classNameString.indexOf(46, p) + 1;
            Util.checkArgument(p != 0, "couldn't make a guess for %s", classNameString);
        }
        String packageName = p == 0 ? "" : classNameString.substring(0, p - 1);
        ClassName className = null;
        for (String simpleName : classNameString.substring(p).split("\\.", -1)) {
            Util.checkArgument(!simpleName.isEmpty() && Character.isUpperCase(simpleName.codePointAt(0)), "couldn't make a guess for %s", classNameString);
            className = new ClassName(packageName, className, simpleName);
        }
        return className;
    }

    public static ClassName get(String packageName, String simpleName, String... simpleNames) {
        ClassName className = new ClassName(packageName, null, simpleName);
        for (String name : simpleNames) {
            className = className.nestedClass(name);
        }
        return className;
    }

    public static ClassName get(final TypeElement element) {
        Util.checkNotNull(element, "element == null", new Object[0]);
        final String simpleName = element.getSimpleName().toString();
        return (ClassName) element.getEnclosingElement().accept(new SimpleElementVisitor8<ClassName, Void>() { // from class: org.springframework.javapoet.ClassName.1
            public ClassName visitPackage(PackageElement packageElement, Void p) {
                return new ClassName(packageElement.getQualifiedName().toString(), (ClassName) null, simpleName);
            }

            public ClassName visitType(TypeElement enclosingClass, Void p) {
                return ClassName.get(enclosingClass).nestedClass(simpleName);
            }

            public ClassName visitUnknown(Element unknown, Void p) {
                return ClassName.get("", simpleName, new String[0]);
            }

            public ClassName defaultAction(Element enclosingElement, Void p) {
                throw new IllegalArgumentException("Unexpected type nesting: " + element);
            }
        }, (Object) null);
    }

    @Override // java.lang.Comparable
    public int compareTo(ClassName o) {
        return this.canonicalName.compareTo(o.canonicalName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.springframework.javapoet.TypeName
    public CodeWriter emit(CodeWriter out) throws IOException {
        String simpleName;
        boolean charsEmitted = false;
        for (ClassName className : enclosingClasses()) {
            if (charsEmitted) {
                out.emit(".");
                simpleName = className.simpleName;
            } else if (className.isAnnotated() || className == this) {
                String qualifiedName = out.lookupName(className);
                int dot = qualifiedName.lastIndexOf(46);
                if (dot != -1) {
                    out.emitAndIndent(qualifiedName.substring(0, dot + 1));
                    simpleName = qualifiedName.substring(dot + 1);
                    charsEmitted = true;
                } else {
                    simpleName = qualifiedName;
                }
            }
            if (className.isAnnotated()) {
                if (charsEmitted) {
                    out.emit(CharSequenceUtil.SPACE);
                }
                className.emitAnnotations(out);
            }
            out.emit(simpleName);
            charsEmitted = true;
        }
        return out;
    }

    private List<ClassName> enclosingClasses() {
        List<ClassName> result = new ArrayList<>();
        ClassName className = this;
        while (true) {
            ClassName c = className;
            if (c != null) {
                result.add(c);
                className = c.enclosingClassName;
            } else {
                Collections.reverse(result);
                return result;
            }
        }
    }
}
