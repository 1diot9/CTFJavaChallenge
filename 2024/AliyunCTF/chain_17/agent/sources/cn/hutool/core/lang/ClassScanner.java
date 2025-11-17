package cn.hutool.core.lang;

import cn.hutool.core.collection.EnumerationIter;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/ClassScanner.class */
public class ClassScanner implements Serializable {
    private static final long serialVersionUID = 1;
    private final String packageName;
    private final String packageNameWithDot;
    private final String packageDirName;
    private final String packagePath;
    private final Filter<Class<?>> classFilter;
    private final Charset charset;
    private ClassLoader classLoader;
    private boolean initialize;
    private final Set<Class<?>> classes;
    private boolean ignoreLoadError;
    private final Set<String> classesOfLoadError;

    public static Set<Class<?>> scanAllPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        return scanAllPackage(packageName, clazz -> {
            return clazz.isAnnotationPresent(annotationClass);
        });
    }

    public static Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        return scanPackage(packageName, clazz -> {
            return clazz.isAnnotationPresent(annotationClass);
        });
    }

    public static Set<Class<?>> scanAllPackageBySuper(String packageName, Class<?> superClass) {
        return scanAllPackage(packageName, clazz -> {
            return superClass.isAssignableFrom(clazz) && !superClass.equals(clazz);
        });
    }

    public static Set<Class<?>> scanPackageBySuper(String packageName, Class<?> superClass) {
        return scanPackage(packageName, clazz -> {
            return superClass.isAssignableFrom(clazz) && !superClass.equals(clazz);
        });
    }

    public static Set<Class<?>> scanAllPackage() {
        return scanAllPackage("", null);
    }

    public static Set<Class<?>> scanPackage() {
        return scanPackage("", null);
    }

    public static Set<Class<?>> scanPackage(String packageName) {
        return scanPackage(packageName, null);
    }

    public static Set<Class<?>> scanAllPackage(String packageName, Filter<Class<?>> classFilter) {
        return new ClassScanner(packageName, classFilter).scan(true);
    }

    public static Set<Class<?>> scanPackage(String packageName, Filter<Class<?>> classFilter) {
        return new ClassScanner(packageName, classFilter).scan();
    }

    public ClassScanner() {
        this(null);
    }

    public ClassScanner(String packageName) {
        this(packageName, null);
    }

    public ClassScanner(String packageName, Filter<Class<?>> classFilter) {
        this(packageName, classFilter, CharsetUtil.CHARSET_UTF_8);
    }

    public ClassScanner(String packageName, Filter<Class<?>> classFilter, Charset charset) {
        this.classes = new HashSet();
        this.ignoreLoadError = false;
        this.classesOfLoadError = new HashSet();
        String packageName2 = StrUtil.nullToEmpty(packageName);
        this.packageName = packageName2;
        this.packageNameWithDot = StrUtil.addSuffixIfNot(packageName2, ".");
        this.packageDirName = packageName2.replace('.', File.separatorChar);
        this.packagePath = packageName2.replace('.', '/');
        this.classFilter = classFilter;
        this.charset = charset;
    }

    public ClassScanner setIgnoreLoadError(boolean ignoreLoadError) {
        this.ignoreLoadError = ignoreLoadError;
        return this;
    }

    public Set<Class<?>> scan() {
        return scan(false);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0094 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:18:0x00b1 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0021 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.Set<java.lang.Class<?>> scan(boolean r7) {
        /*
            r6 = this;
            r0 = r6
            java.util.Set<java.lang.Class<?>> r0 = r0.classes
            r0.clear()
            r0 = r6
            java.util.Set<java.lang.String> r0 = r0.classesOfLoadError
            r0.clear()
            r0 = r6
            java.lang.String r0 = r0.packagePath
            r1 = r6
            java.lang.ClassLoader r1 = r1.classLoader
            cn.hutool.core.collection.EnumerationIter r0 = cn.hutool.core.io.resource.ResourceUtil.getResourceIter(r0, r1)
            java.util.Iterator r0 = r0.iterator()
            r8 = r0
        L21:
            r0 = r8
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto Lbc
            r0 = r8
            java.lang.Object r0 = r0.next()
            java.net.URL r0 = (java.net.URL) r0
            r9 = r0
            r0 = r9
            java.lang.String r0 = r0.getProtocol()
            r10 = r0
            r0 = -1
            r11 = r0
            r0 = r10
            int r0 = r0.hashCode()
            switch(r0) {
                case 104987: goto L6c;
                case 3143036: goto L5c;
                default: goto L79;
            }
        L5c:
            r0 = r10
            java.lang.String r1 = "file"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L79
            r0 = 0
            r11 = r0
            goto L79
        L6c:
            r0 = r10
            java.lang.String r1 = "jar"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L79
            r0 = 1
            r11 = r0
        L79:
            r0 = r11
            switch(r0) {
                case 0: goto L94;
                case 1: goto Lb1;
                default: goto Lb9;
            }
        L94:
            r0 = r6
            java.io.File r1 = new java.io.File
            r2 = r1
            r3 = r9
            java.lang.String r3 = r3.getFile()
            r4 = r6
            java.nio.charset.Charset r4 = r4.charset
            java.lang.String r4 = r4.name()
            java.lang.String r3 = cn.hutool.core.util.URLUtil.decode(r3, r4)
            r2.<init>(r3)
            r2 = 0
            r0.scanFile(r1, r2)
            goto Lb9
        Lb1:
            r0 = r6
            r1 = r9
            java.util.jar.JarFile r1 = cn.hutool.core.util.URLUtil.getJarFile(r1)
            r0.scanJar(r1)
        Lb9:
            goto L21
        Lbc:
            r0 = r7
            if (r0 != 0) goto Lca
            r0 = r6
            java.util.Set<java.lang.Class<?>> r0 = r0.classes
            boolean r0 = cn.hutool.core.collection.CollUtil.isEmpty(r0)
            if (r0 == 0) goto Lce
        Lca:
            r0 = r6
            r0.scanJavaClassPaths()
        Lce:
            r0 = r6
            java.util.Set<java.lang.Class<?>> r0 = r0.classes
            java.util.Set r0 = java.util.Collections.unmodifiableSet(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.hutool.core.lang.ClassScanner.scan(boolean):java.util.Set");
    }

    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Set<String> getClassesOfLoadError() {
        return Collections.unmodifiableSet(this.classesOfLoadError);
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void scanJavaClassPaths() {
        String[] javaClassPaths = ClassUtil.getJavaClassPaths();
        for (String classPath : javaClassPaths) {
            scanFile(new File(URLUtil.decode(classPath, CharsetUtil.systemCharsetName())), null);
        }
    }

    private void scanFile(File file, String rootDir) {
        File[] files;
        if (file.isFile()) {
            String fileName = file.getAbsolutePath();
            if (fileName.endsWith(".class")) {
                String className = fileName.substring(rootDir.length(), fileName.length() - 6).replace(File.separatorChar, '.');
                addIfAccept(className);
                return;
            } else {
                if (fileName.endsWith(".jar")) {
                    try {
                        scanJar(new JarFile(file));
                        return;
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }
                return;
            }
        }
        if (file.isDirectory() && null != (files = file.listFiles())) {
            for (File subFile : files) {
                scanFile(subFile, null == rootDir ? subPathBeforePackage(file) : rootDir);
            }
        }
    }

    private void scanJar(JarFile jar) {
        Iterator<E> it = new EnumerationIter(jar.entries()).iterator();
        while (it.hasNext()) {
            JarEntry entry = (JarEntry) it.next();
            String name = StrUtil.removePrefix(entry.getName(), "/");
            if (StrUtil.isEmpty(this.packagePath) || name.startsWith(this.packagePath)) {
                if (name.endsWith(".class") && false == entry.isDirectory()) {
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    addIfAccept(loadClass(className));
                }
            }
        }
    }

    protected Class<?> loadClass(String className) {
        ClassLoader loader = this.classLoader;
        if (null == loader) {
            loader = ClassLoaderUtil.getClassLoader();
            this.classLoader = loader;
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className, this.initialize, loader);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            this.classesOfLoadError.add(className);
        } catch (UnsupportedClassVersionError e2) {
            this.classesOfLoadError.add(className);
        } catch (Throwable e3) {
            if (false == this.ignoreLoadError) {
                throw new RuntimeException(e3);
            }
            this.classesOfLoadError.add(className);
        }
        return clazz;
    }

    private void addIfAccept(String className) {
        if (StrUtil.isBlank(className)) {
            return;
        }
        int classLen = className.length();
        int packageLen = this.packageName.length();
        if (classLen == packageLen) {
            if (className.equals(this.packageName)) {
                addIfAccept(loadClass(className));
            }
        } else if (classLen > packageLen) {
            if (".".equals(this.packageNameWithDot) || className.startsWith(this.packageNameWithDot)) {
                addIfAccept(loadClass(className));
            }
        }
    }

    private void addIfAccept(Class<?> clazz) {
        if (null != clazz) {
            Filter<Class<?>> classFilter = this.classFilter;
            if (classFilter == null || classFilter.accept(clazz)) {
                this.classes.add(clazz);
            }
        }
    }

    private String subPathBeforePackage(File file) {
        String filePath = file.getAbsolutePath();
        if (StrUtil.isNotEmpty(this.packageDirName)) {
            filePath = StrUtil.subBefore((CharSequence) filePath, (CharSequence) this.packageDirName, true);
        }
        return StrUtil.addSuffixIfNot(filePath, File.separator);
    }
}
