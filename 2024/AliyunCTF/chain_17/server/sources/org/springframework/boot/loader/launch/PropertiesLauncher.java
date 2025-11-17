package org.springframework.boot.loader.launch;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.runtime.ObjectMethods;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.log.DebugLogger;
import org.springframework.boot.loader.net.protocol.jar.JarUrl;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: server.jar:org/springframework/boot/loader/launch/PropertiesLauncher.class */
public class PropertiesLauncher extends Launcher {
    public static final String MAIN = "loader.main";
    public static final String PATH = "loader.path";
    public static final String HOME = "loader.home";
    public static final String ARGS = "loader.args";
    public static final String CONFIG_NAME = "loader.config.name";
    public static final String CONFIG_LOCATION = "loader.config.location";
    public static final String SET_SYSTEM_PROPERTIES = "loader.system";
    private static final String JAR_FILE_PREFIX = "jar:file:";
    private final Archive archive;
    private final File homeDirectory;
    private final List<String> paths;
    private final Properties properties;
    private static final URL[] NO_URLS = new URL[0];
    private static final Pattern WORD_SEPARATOR = Pattern.compile("\\W+");
    private static final String NESTED_ARCHIVE_SEPARATOR = "!" + File.separator;
    private static final DebugLogger debug = DebugLogger.get(PropertiesLauncher.class);

    public PropertiesLauncher() throws Exception {
        this(Archive.create((Class<?>) Launcher.class));
    }

    PropertiesLauncher(Archive archive) throws Exception {
        this.properties = new Properties();
        this.archive = archive;
        this.homeDirectory = getHomeDirectory();
        initializeProperties();
        this.paths = getPaths();
    }

    protected File getHomeDirectory() throws Exception {
        return new File(getPropertyWithDefault(HOME, "${user.dir}"));
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x00df A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void initializeProperties() throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 244
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.boot.loader.launch.PropertiesLauncher.initializeProperties():void");
    }

    private InputStream getResource(String config) throws Exception {
        if (config.startsWith("classpath:")) {
            return getClasspathResource(config.substring("classpath:".length()));
        }
        String config2 = handleUrl(config);
        if (isUrl(config2)) {
            return getURLResource(config2);
        }
        return getFileResource(config2);
    }

    private InputStream getClasspathResource(String config) {
        String config2 = "/" + stripLeadingSlashes(config);
        debug.log("Trying classpath: %s", config2);
        return getClass().getResourceAsStream(config2);
    }

    private String handleUrl(String path) {
        if (path.startsWith(JAR_FILE_PREFIX) || path.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            path = URLDecoder.decode(path, StandardCharsets.UTF_8);
            if (path.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
                path = path.substring(ResourceUtils.FILE_URL_PREFIX.length());
                if (path.startsWith("//")) {
                    path = path.substring(2);
                }
            }
        }
        return path;
    }

    private boolean isUrl(String config) {
        return config.contains("://");
    }

    private InputStream getURLResource(String config) throws Exception {
        URL url = new URL(config);
        if (exists(url)) {
            URLConnection connection = url.openConnection();
            try {
                return connection.getInputStream();
            } catch (IOException ex) {
                disconnect(connection);
                throw ex;
            }
        }
        return null;
    }

    private boolean exists(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        try {
            connection.setUseCaches(connection.getClass().getSimpleName().startsWith("JNLP"));
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod(WebContentGenerator.METHOD_HEAD);
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == 200) {
                    return true;
                }
                if (responseCode == 404) {
                    disconnect(connection);
                    return false;
                }
            }
            boolean z = connection.getContentLength() >= 0;
            disconnect(connection);
            return z;
        } finally {
            disconnect(connection);
        }
    }

    private void disconnect(URLConnection connection) {
        if (connection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.disconnect();
        }
    }

    private InputStream getFileResource(String config) throws Exception {
        File file = new File(config);
        debug.log("Trying file: %s", config);
        if (file.canRead()) {
            return new FileInputStream(file);
        }
        return null;
    }

    private void loadResource(InputStream resource) throws Exception {
        this.properties.load(resource);
        resolvePropertyPlaceholders();
        if ("true".equalsIgnoreCase(getProperty(SET_SYSTEM_PROPERTIES))) {
            addToSystemProperties();
        }
    }

    private void resolvePropertyPlaceholders() {
        for (String name : this.properties.stringPropertyNames()) {
            String value = this.properties.getProperty(name);
            String resolved = SystemPropertyUtils.resolvePlaceholders(this.properties, value);
            if (resolved != null) {
                this.properties.put(name, resolved);
            }
        }
    }

    private void addToSystemProperties() {
        debug.log("Adding resolved properties to System properties");
        for (String name : this.properties.stringPropertyNames()) {
            String value = this.properties.getProperty(name);
            System.setProperty(name, value);
        }
    }

    private List<String> getPaths() throws Exception {
        String path = getProperty(PATH);
        List<String> paths = path != null ? parsePathsProperty(path) : Collections.emptyList();
        debug.log("Nested archive paths: %s", this.paths);
        return paths;
    }

    private List<String> parsePathsProperty(String commaSeparatedPaths) {
        List<String> paths = new ArrayList<>();
        for (String str : commaSeparatedPaths.split(",")) {
            String path = cleanupPath(str);
            paths.add(path.isEmpty() ? "/" : path);
        }
        if (paths.isEmpty()) {
            paths.add("lib");
        }
        return paths;
    }

    private String cleanupPath(String path) {
        String path2 = path.trim();
        if (path2.startsWith("./")) {
            path2 = path2.substring(2);
        }
        if (isArchive(path2)) {
            return path2;
        }
        if (path2.endsWith("/*")) {
            return path2.substring(0, path2.length() - 1);
        }
        return (path2.endsWith("/") || path2.equals(".")) ? path2 : path2 + "/";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.loader.launch.Launcher
    public ClassLoader createClassLoader(Collection<URL> urls) throws Exception {
        String loaderClassName = getProperty("loader.classLoader");
        if (loaderClassName == null) {
            return super.createClassLoader(urls);
        }
        ClassLoader parent = getClass().getClassLoader();
        ClassLoader classLoader = new LaunchedClassLoader(false, (URL[]) urls.toArray(new URL[0]), parent);
        debug.log("Classpath for custom loader: %s", urls);
        ClassLoader classLoader2 = wrapWithCustomClassLoader(classLoader, loaderClassName);
        debug.log("Using custom class loader: %s", loaderClassName);
        return classLoader2;
    }

    private ClassLoader wrapWithCustomClassLoader(ClassLoader parent, String loaderClassName) throws Exception {
        Instantiator<ClassLoader> instantiator = new Instantiator<>(parent, loaderClassName);
        ClassLoader loader = instantiator.declaredConstructor(ClassLoader.class).newInstance(parent);
        ClassLoader loader2 = loader != null ? loader : instantiator.declaredConstructor(URL[].class, ClassLoader.class).newInstance(NO_URLS, parent);
        ClassLoader loader3 = loader2 != null ? loader2 : instantiator.constructWithoutParameters();
        if (loader3 != null) {
            return loader3;
        }
        throw new IllegalStateException("Unable to create class loader for " + loaderClassName);
    }

    @Override // org.springframework.boot.loader.launch.Launcher
    protected Archive getArchive() {
        return null;
    }

    @Override // org.springframework.boot.loader.launch.Launcher
    protected String getMainClass() throws Exception {
        String mainClass = getProperty(MAIN, "Start-Class");
        if (mainClass == null) {
            throw new IllegalStateException("No '%s' or 'Start-Class' specified".formatted(MAIN));
        }
        return mainClass;
    }

    protected String[] getArgs(String... args) throws Exception {
        String loaderArgs = getProperty(ARGS);
        return loaderArgs != null ? merge(loaderArgs.split("\\s+"), args) : args;
    }

    private String[] merge(String[] a1, String[] a2) {
        String[] result = new String[a1.length + a2.length];
        System.arraycopy(a1, 0, result, 0, a1.length);
        System.arraycopy(a2, 0, result, a1.length, a2.length);
        return result;
    }

    private String getProperty(String name) throws Exception {
        return getProperty(name, null, null);
    }

    private String getProperty(String name, String manifestKey) throws Exception {
        return getProperty(name, manifestKey, null);
    }

    private String getPropertyWithDefault(String name, String defaultValue) throws Exception {
        return getProperty(name, null, defaultValue);
    }

    private String getProperty(String name, String manifestKey, String defaultValue) throws Exception {
        String manifestKey2 = manifestKey != null ? manifestKey : toCamelCase(name.replace('.', '-'));
        String value = SystemPropertyUtils.getProperty(name);
        if (value != null) {
            return getResolvedProperty(name, manifestKey2, value, "environment");
        }
        if (this.properties.containsKey(name)) {
            return getResolvedProperty(name, manifestKey2, this.properties.getProperty(name), JsonEncoder.CONTEXT_PROPERTIES_ATTR_NAME);
        }
        if (this.homeDirectory != null) {
            try {
                ExplodedArchive explodedArchive = new ExplodedArchive(this.homeDirectory);
                try {
                    String value2 = getManifestValue(explodedArchive, manifestKey2);
                    if (value2 != null) {
                        String resolvedProperty = getResolvedProperty(name, manifestKey2, value2, "home directory manifest");
                        explodedArchive.close();
                        return resolvedProperty;
                    }
                    explodedArchive.close();
                } finally {
                }
            } catch (IllegalStateException e) {
            }
        }
        String value3 = getManifestValue(this.archive, manifestKey2);
        if (value3 != null) {
            return getResolvedProperty(name, manifestKey2, value3, "manifest");
        }
        return SystemPropertyUtils.resolvePlaceholders(this.properties, defaultValue);
    }

    String getManifestValue(Archive archive, String manifestKey) throws Exception {
        Manifest manifest = archive.getManifest();
        if (manifest != null) {
            return manifest.getMainAttributes().getValue(manifestKey);
        }
        return null;
    }

    private String getResolvedProperty(String name, String manifestKey, String value, String from) {
        String value2 = SystemPropertyUtils.resolvePlaceholders(this.properties, value);
        String altName = (manifestKey == null || manifestKey.equals(name)) ? "" : "[%s] ".formatted(manifestKey);
        debug.log("Property '%s'%s from %s: %s", name, altName, from, value2);
        return value2;
    }

    void close() throws Exception {
        if (this.archive != null) {
            this.archive.close();
        }
    }

    public static String toCamelCase(CharSequence string) {
        if (string == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        Matcher matcher = WORD_SEPARATOR.matcher(string);
        int i = 0;
        while (true) {
            int pos = i;
            if (matcher.find()) {
                result.append(capitalize(string.subSequence(pos, matcher.end()).toString()));
                i = matcher.end();
            } else {
                result.append(capitalize(string.subSequence(pos, string.length()).toString()));
                return result.toString();
            }
        }
    }

    private static String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override // org.springframework.boot.loader.launch.Launcher
    protected Set<URL> getClassPathUrls() throws Exception {
        Set<URL> urls = new LinkedHashSet<>();
        for (String path : getPaths()) {
            urls.addAll(getClassPathUrlsForPath(cleanupPath(handleUrl(path))));
        }
        urls.addAll(getClassPathUrlsForRoot());
        debug.log("Using class path URLs %s", urls);
        return urls;
    }

    private Set<URL> getClassPathUrlsForPath(String path) throws Exception {
        File file = !isAbsolutePath(path) ? new File(this.homeDirectory, path) : new File(path);
        Set<URL> urls = new LinkedHashSet<>();
        if (!"/".equals(path) && file.isDirectory()) {
            ExplodedArchive explodedArchive = new ExplodedArchive(file);
            try {
                debug.log("Adding classpath entries from directory %s", file);
                urls.add(file.toURI().toURL());
                urls.addAll(explodedArchive.getClassPathUrls(this::isArchive));
                explodedArchive.close();
            } catch (Throwable th) {
                try {
                    explodedArchive.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (!file.getPath().contains(NESTED_ARCHIVE_SEPARATOR) && isArchive(file.getName())) {
            debug.log("Adding classpath entries from jar/zip archive %s", path);
            urls.add(file.toURI().toURL());
        }
        Set<URL> nested = getClassPathUrlsForNested(path);
        if (!nested.isEmpty()) {
            debug.log("Adding classpath entries from nested %s", path);
            urls.addAll(nested);
        }
        return urls;
    }

    private Set<URL> getClassPathUrlsForNested(String path) throws Exception {
        boolean isJustArchive = isArchive(path);
        if ((!path.equals("/") && path.startsWith("/")) || (this.archive.isExploded() && this.archive.getRootDirectory().equals(this.homeDirectory))) {
            return Collections.emptySet();
        }
        File file = null;
        if (isJustArchive) {
            File candidate = new File(this.homeDirectory, path);
            if (candidate.exists()) {
                file = candidate;
                path = "";
            }
        }
        int separatorIndex = path.indexOf(33);
        if (separatorIndex != -1) {
            file = !path.startsWith(JAR_FILE_PREFIX) ? new File(this.homeDirectory, path.substring(0, separatorIndex)) : new File(path.substring(JAR_FILE_PREFIX.length(), separatorIndex));
            path = stripLeadingSlashes(path.substring(separatorIndex + 1));
        }
        if (path.equals("/") || path.equals("./") || path.equals(".")) {
            path = "";
        }
        Archive archive = file != null ? new JarFileArchive(file) : this.archive;
        try {
            Set<URL> urls = new LinkedHashSet<>(archive.getClassPathUrls(includeByPrefix(path)));
            if (!isJustArchive && file != null && path.isEmpty()) {
                urls.add(JarUrl.create(file));
            }
            return urls;
        } finally {
            if (archive != this.archive) {
                archive.close();
            }
        }
    }

    private Set<URL> getClassPathUrlsForRoot() throws IOException {
        debug.log("Adding classpath entries from root archive %s", this.archive);
        return this.archive.getClassPathUrls(JarLauncher::isLibraryFileOrClassesDirectory);
    }

    private Predicate<Archive.Entry> includeByPrefix(String prefix) {
        return entry -> {
            return (entry.isDirectory() && entry.name().equals(prefix)) || (isArchive(entry) && entry.name().startsWith(prefix));
        };
    }

    private boolean isArchive(Archive.Entry entry) {
        return isArchive(entry.name());
    }

    private boolean isArchive(String name) {
        String name2 = name.toLowerCase(Locale.ENGLISH);
        return name2.endsWith(".jar") || name2.endsWith(".zip");
    }

    private boolean isAbsolutePath(String root) {
        return root.contains(":") || root.startsWith("/");
    }

    private String stripLeadingSlashes(String string) {
        while (string.startsWith("/")) {
            string = string.substring(1);
        }
        return string;
    }

    public static void main(String[] args) throws Exception {
        PropertiesLauncher launcher = new PropertiesLauncher();
        launcher.launch(launcher.getArgs(args));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:org/springframework/boot/loader/launch/PropertiesLauncher$Instantiator.class */
    public static final class Instantiator<T> extends Record {
        private final ClassLoader parent;
        private final Class<?> type;

        private Instantiator(ClassLoader parent, Class<?> type) {
            this.parent = parent;
            this.type = type;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Instantiator.class), Instantiator.class, "parent;type", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator;->parent:Ljava/lang/ClassLoader;", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator;->type:Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Instantiator.class), Instantiator.class, "parent;type", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator;->parent:Ljava/lang/ClassLoader;", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator;->type:Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Instantiator.class, Object.class), Instantiator.class, "parent;type", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator;->parent:Ljava/lang/ClassLoader;", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator;->type:Ljava/lang/Class;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public ClassLoader parent() {
            return this.parent;
        }

        public Class<?> type() {
            return this.type;
        }

        Instantiator(ClassLoader parent, String className) throws ClassNotFoundException {
            this(parent, Class.forName(className, true, parent));
        }

        T constructWithoutParameters() throws Exception {
            return declaredConstructor(new Class[0]).newInstance(new Object[0]);
        }

        Using<T> declaredConstructor(Class<?>... parameterTypes) {
            return new Using<>(this, parameterTypes);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:org/springframework/boot/loader/launch/PropertiesLauncher$Instantiator$Using.class */
        public static final class Using<T> extends Record {
            private final Instantiator<T> instantiator;
            private final Class<?>[] parameterTypes;

            private Using(Instantiator<T> instantiator, Class<?>... parameterTypes) {
                this.instantiator = instantiator;
                this.parameterTypes = parameterTypes;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Using.class), Using.class, "instantiator;parameterTypes", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator$Using;->instantiator:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator;", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator$Using;->parameterTypes:[Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Using.class), Using.class, "instantiator;parameterTypes", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator$Using;->instantiator:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator;", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator$Using;->parameterTypes:[Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Using.class, Object.class), Using.class, "instantiator;parameterTypes", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator$Using;->instantiator:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator;", "FIELD:Lorg/springframework/boot/loader/launch/PropertiesLauncher$Instantiator$Using;->parameterTypes:[Ljava/lang/Class;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Instantiator<T> instantiator() {
                return this.instantiator;
            }

            public Class<?>[] parameterTypes() {
                return this.parameterTypes;
            }

            T newInstance(Object... objArr) throws Exception {
                try {
                    Constructor<?> declaredConstructor = this.instantiator.type().getDeclaredConstructor(this.parameterTypes);
                    declaredConstructor.setAccessible(true);
                    return (T) declaredConstructor.newInstance(objArr);
                } catch (NoSuchMethodException e) {
                    return null;
                }
            }
        }
    }
}
