package org.springframework.core.io.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReader;
import java.lang.module.ResolvedModule;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NativeDetector;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.VfsResource;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/PathMatchingResourcePatternResolver.class */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {
    private static final Log logger = LogFactory.getLog((Class<?>) PathMatchingResourcePatternResolver.class);
    private static final Set<String> systemModuleNames;
    private static final Predicate<ResolvedModule> isNotSystemModule;

    @Nullable
    private static Method equinoxResolveMethod;
    private final ResourceLoader resourceLoader;
    private PathMatcher pathMatcher;

    static {
        systemModuleNames = NativeDetector.inNativeImage() ? Collections.emptySet() : (Set) ModuleFinder.ofSystem().findAll().stream().map(moduleReference -> {
            return moduleReference.descriptor().name();
        }).collect(Collectors.toSet());
        isNotSystemModule = resolvedModule -> {
            return !systemModuleNames.contains(resolvedModule.name());
        };
        try {
            Class<?> fileLocatorClass = ClassUtils.forName("org.eclipse.core.runtime.FileLocator", PathMatchingResourcePatternResolver.class.getClassLoader());
            equinoxResolveMethod = fileLocatorClass.getMethod("resolve", URL.class);
            logger.trace("Found Equinox FileLocator for OSGi bundle URL resolution");
        } catch (Throwable th) {
            equinoxResolveMethod = null;
        }
    }

    public PathMatchingResourcePatternResolver() {
        this.pathMatcher = new AntPathMatcher();
        this.resourceLoader = new DefaultResourceLoader();
    }

    public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
        this.pathMatcher = new AntPathMatcher();
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }

    public PathMatchingResourcePatternResolver(@Nullable ClassLoader classLoader) {
        this.pathMatcher = new AntPathMatcher();
        this.resourceLoader = new DefaultResourceLoader(classLoader);
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    @Override // org.springframework.core.io.ResourceLoader
    @Nullable
    public ClassLoader getClassLoader() {
        return getResourceLoader().getClassLoader();
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    @Override // org.springframework.core.io.ResourceLoader
    public Resource getResource(String location) {
        return getResourceLoader().getResource(location);
    }

    @Override // org.springframework.core.io.support.ResourcePatternResolver
    public Resource[] getResources(String locationPattern) throws IOException {
        Assert.notNull(locationPattern, "Location pattern must not be null");
        if (locationPattern.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
            String locationPatternWithoutPrefix = locationPattern.substring(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX.length());
            Set<Resource> resources = findAllModulePathResources(locationPatternWithoutPrefix);
            if (getPathMatcher().isPattern(locationPatternWithoutPrefix)) {
                Collections.addAll(resources, findPathMatchingResources(locationPattern));
            } else {
                Collections.addAll(resources, findAllClassPathResources(locationPatternWithoutPrefix));
            }
            return (Resource[]) resources.toArray(new Resource[0]);
        }
        int prefixEnd = locationPattern.startsWith(ResourceUtils.WAR_URL_PREFIX) ? locationPattern.indexOf(ResourceUtils.WAR_URL_SEPARATOR) + 1 : locationPattern.indexOf(58) + 1;
        if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
            return findPathMatchingResources(locationPattern);
        }
        return new Resource[]{getResourceLoader().getResource(locationPattern)};
    }

    protected Resource[] findAllClassPathResources(String location) throws IOException {
        String path = stripLeadingSlash(location);
        Set<Resource> result = doFindAllClassPathResources(path);
        if (logger.isTraceEnabled()) {
            logger.trace("Resolved class path location [" + path + "] to resources " + result);
        }
        return (Resource[]) result.toArray(new Resource[0]);
    }

    protected Set<Resource> doFindAllClassPathResources(String path) throws IOException {
        Set<Resource> result = new LinkedHashSet<>(16);
        ClassLoader cl = getClassLoader();
        Enumeration<URL> resourceUrls = cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path);
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(convertClassLoaderURL(url));
        }
        if (!StringUtils.hasLength(path)) {
            addAllClassLoaderJarRoots(cl, result);
        }
        return result;
    }

    protected Resource convertClassLoaderURL(URL url) {
        if ("file".equals(url.getProtocol())) {
            try {
                return new FileSystemResource(ResourceUtils.toURI(url).getSchemeSpecificPart());
            } catch (URISyntaxException e) {
                return new FileSystemResource(url.getFile());
            }
        }
        return new UrlResource(url);
    }

    protected void addAllClassLoaderJarRoots(@Nullable ClassLoader classLoader, Set<Resource> result) {
        UrlResource urlResource;
        if (classLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            try {
                for (URL url : urlClassLoader.getURLs()) {
                    try {
                        if (ResourceUtils.URL_PROTOCOL_JAR.equals(url.getProtocol())) {
                            urlResource = new UrlResource(url);
                        } else {
                            urlResource = new UrlResource("jar:" + url + "!/");
                        }
                        UrlResource jarResource = urlResource;
                        if (jarResource.exists()) {
                            result.add(jarResource);
                        }
                    } catch (MalformedURLException ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Cannot search for matching files underneath [" + url + "] because it cannot be converted to a valid 'jar:' URL: " + ex.getMessage());
                        }
                    }
                }
            } catch (Exception ex2) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot introspect jar files since ClassLoader [" + classLoader + "] does not support 'getURLs()': " + ex2);
                }
            }
        }
        if (classLoader == ClassLoader.getSystemClassLoader()) {
            addClassPathManifestEntries(result);
        }
        if (classLoader != null) {
            try {
                addAllClassLoaderJarRoots(classLoader.getParent(), result);
            } catch (Exception ex3) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot introspect jar files in parent ClassLoader since [" + classLoader + "] does not support 'getParent()': " + ex3);
                }
            }
        }
    }

    protected void addClassPathManifestEntries(Set<Resource> result) {
        try {
            String javaClassPathProperty = System.getProperty("java.class.path");
            for (String path : StringUtils.delimitedListToStringArray(javaClassPathProperty, File.pathSeparator)) {
                try {
                    String filePath = new File(path).getAbsolutePath();
                    int prefixIndex = filePath.indexOf(58);
                    if (prefixIndex == 1) {
                        filePath = "/" + StringUtils.capitalize(filePath);
                    }
                    String filePath2 = StringUtils.replace(filePath, "#", "%23");
                    UrlResource jarResource = new UrlResource("jar:file:" + filePath2 + "!/");
                    if (!result.contains(jarResource) && !hasDuplicate(filePath2, result) && jarResource.exists()) {
                        result.add(jarResource);
                    }
                } catch (MalformedURLException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Cannot search for matching files underneath [" + path + "] because it cannot be converted to a valid 'jar:' URL: " + ex.getMessage());
                    }
                }
            }
        } catch (Exception ex2) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to evaluate 'java.class.path' manifest entries: " + ex2);
            }
        }
    }

    private boolean hasDuplicate(String filePath, Set<Resource> result) {
        if (result.isEmpty()) {
            return false;
        }
        String duplicatePath = filePath.startsWith("/") ? filePath.substring(1) : "/" + filePath;
        try {
            return result.contains(new UrlResource("jar:file:" + duplicatePath + "!/"));
        } catch (MalformedURLException e) {
            return false;
        }
    }

    protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<>(16);
        for (Resource resource : rootDirResources) {
            Resource rootDirResource = resolveRootDirResource(resource);
            URL rootDirUrl = rootDirResource.getURL();
            if (equinoxResolveMethod != null && rootDirUrl.getProtocol().startsWith("bundle")) {
                URL resolvedUrl = (URL) ReflectionUtils.invokeMethod(equinoxResolveMethod, null, rootDirUrl);
                if (resolvedUrl != null) {
                    rootDirUrl = resolvedUrl;
                }
                rootDirResource = new UrlResource(rootDirUrl);
            }
            if (rootDirUrl.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
                result.addAll(VfsResourceMatchingDelegate.findMatchingResources(rootDirUrl, subPattern, getPathMatcher()));
            } else if (ResourceUtils.isJarURL(rootDirUrl) || isJarResource(rootDirResource)) {
                result.addAll(doFindPathMatchingJarResources(rootDirResource, rootDirUrl, subPattern));
            } else {
                result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Resolved location pattern [" + locationPattern + "] to resources " + result);
        }
        return (Resource[]) result.toArray(new Resource[0]);
    }

    protected String determineRootDir(String location) {
        int rootDirEnd;
        int prefixEnd = location.indexOf(58) + 1;
        int length = location.length();
        while (true) {
            rootDirEnd = length;
            if (rootDirEnd <= prefixEnd || !getPathMatcher().isPattern(location.substring(prefixEnd, rootDirEnd))) {
                break;
            }
            length = location.lastIndexOf(47, rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }

    protected Resource resolveRootDirResource(Resource original) throws IOException {
        return original;
    }

    protected boolean isJarResource(Resource resource) throws IOException {
        return false;
    }

    protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, URL rootDirUrl, String subPattern) throws IOException {
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        boolean closeJarFile;
        URLConnection con = rootDirUrl.openConnection();
        if (con instanceof JarURLConnection) {
            JarURLConnection jarCon = (JarURLConnection) con;
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = jarEntry != null ? jarEntry.getName() : "";
            closeJarFile = !jarCon.getUseCaches();
        } else {
            String urlFile = rootDirUrl.getFile();
            try {
                int separatorIndex = urlFile.indexOf(ResourceUtils.WAR_URL_SEPARATOR);
                if (separatorIndex == -1) {
                    separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
                }
                if (separatorIndex != -1) {
                    jarFileUrl = urlFile.substring(0, separatorIndex);
                    rootEntryPath = urlFile.substring(separatorIndex + 2);
                    jarFile = getJarFile(jarFileUrl);
                } else {
                    jarFile = new JarFile(urlFile);
                    jarFileUrl = urlFile;
                    rootEntryPath = "";
                }
                closeJarFile = true;
            } catch (ZipException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipping invalid jar class path entry [" + urlFile + "]");
                }
                return Collections.emptySet();
            }
        }
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Looking for matching resources in jar file [" + jarFileUrl + "]");
            }
            if (StringUtils.hasLength(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                rootEntryPath = rootEntryPath + "/";
            }
            Set<Resource> result = new LinkedHashSet<>(8);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if (getPathMatcher().match(subPattern, relativePath)) {
                        result.add(rootDirResource.createRelative(relativePath));
                    }
                }
            }
            return result;
        } finally {
            if (closeJarFile) {
                jarFile.close();
            }
        }
    }

    protected JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            try {
                return new JarFile(ResourceUtils.toURI(jarFileUrl).getSchemeSpecificPart());
            } catch (URISyntaxException e) {
                return new JarFile(jarFileUrl.substring(ResourceUtils.FILE_URL_PREFIX.length()));
            }
        }
        return new JarFile(jarFileUrl);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException {
        Set<Resource> result = new LinkedHashSet<>();
        try {
            URI rootDirUri = rootDirResource.getURI();
            Path rootPath = null;
            if (rootDirUri.isAbsolute() && !rootDirUri.isOpaque()) {
                try {
                    try {
                        rootPath = Path.of(rootDirUri);
                    } catch (Exception ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to resolve %s in file system: %s".formatted(rootDirUri, ex));
                        }
                    }
                } catch (FileSystemNotFoundException e) {
                    FileSystems.newFileSystem(rootDirUri, Map.of(), ClassUtils.getDefaultClassLoader());
                    rootPath = Path.of(rootDirUri);
                }
            }
            if (rootPath == null) {
                try {
                    rootPath = Path.of(rootDirResource.getFile().getAbsolutePath(), new String[0]);
                } catch (FileNotFoundException ex2) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Cannot search for matching files underneath " + rootDirResource + " in the file system: " + ex2.getMessage());
                    }
                    return result;
                } catch (Exception ex3) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Failed to resolve " + rootDirResource + " in the file system: " + ex3);
                    }
                    return result;
                }
            }
            if (!Files.exists(rootPath, new LinkOption[0])) {
                if (logger.isInfoEnabled()) {
                    logger.info("Skipping search for files matching pattern [%s]: directory [%s] does not exist".formatted(subPattern, rootPath.toAbsolutePath()));
                }
                return result;
            }
            String rootDir = StringUtils.cleanPath(rootPath.toString());
            if (!rootDir.endsWith("/")) {
                rootDir = rootDir + "/";
            }
            Path rootPathForPattern = rootPath;
            String resourcePattern = rootDir + StringUtils.cleanPath(subPattern);
            Predicate<Path> isMatchingFile = path -> {
                return !path.equals(rootPathForPattern) && getPathMatcher().match(resourcePattern, StringUtils.cleanPath(path.toString()));
            };
            if (logger.isTraceEnabled()) {
                logger.trace("Searching directory [%s] for files matching pattern [%s]".formatted(rootPath.toAbsolutePath(), subPattern));
            }
            try {
                Stream<Path> files = Files.walk(rootPath, new FileVisitOption[0]);
                try {
                    Stream<R> map = files.filter(isMatchingFile).sorted().map(FileSystemResource::new);
                    Objects.requireNonNull(result);
                    map.forEach((v1) -> {
                        r1.add(v1);
                    });
                    if (files != null) {
                        files.close();
                    }
                } finally {
                }
            } catch (Exception ex4) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to search in directory [%s] for files matching pattern [%s]: %s".formatted(rootPath.toAbsolutePath(), subPattern, ex4));
                }
            }
            return result;
        } catch (Exception ex5) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to resolve directory [%s] as URI: %s".formatted(rootDirResource, ex5));
            }
            return result;
        }
    }

    protected Set<Resource> findAllModulePathResources(String locationPattern) throws IOException {
        Predicate<String> predicate;
        Set<Resource> result = new LinkedHashSet<>(16);
        if (NativeDetector.inNativeImage()) {
            return result;
        }
        String resourcePattern = stripLeadingSlash(locationPattern);
        if (getPathMatcher().isPattern(resourcePattern)) {
            predicate = path -> {
                return getPathMatcher().match(resourcePattern, path);
            };
        } else {
            Objects.requireNonNull(resourcePattern);
            predicate = (v1) -> {
                return r0.equals(v1);
            };
        }
        Predicate<String> resourcePatternMatches = predicate;
        try {
            ModuleLayer.boot().configuration().modules().stream().filter(isNotSystemModule).forEach(resolvedModule -> {
                try {
                    ModuleReader moduleReader = resolvedModule.reference().open();
                    try {
                        Stream<String> names = moduleReader.list();
                        try {
                            Stream filter = names.filter(resourcePatternMatches).map(name -> {
                                return findResource(moduleReader, name);
                            }).filter((v0) -> {
                                return Objects.nonNull(v0);
                            });
                            Objects.requireNonNull(result);
                            filter.forEach((v1) -> {
                                r1.add(v1);
                            });
                            if (names != null) {
                                names.close();
                            }
                            if (moduleReader != null) {
                                moduleReader.close();
                            }
                        } catch (Throwable th) {
                            if (names != null) {
                                try {
                                    names.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } finally {
                    }
                } catch (IOException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to read contents of module [%s]".formatted(resolvedModule), ex);
                    }
                    throw new UncheckedIOException(ex);
                }
            });
            if (logger.isTraceEnabled()) {
                logger.trace("Resolved module-path location pattern [%s] to resources %s".formatted(resourcePattern, result));
            }
            return result;
        } catch (UncheckedIOException ex) {
            throw ex.getCause();
        }
    }

    @Nullable
    private Resource findResource(ModuleReader moduleReader, String name) {
        try {
            return (Resource) moduleReader.find(name).map(this::convertModuleSystemURI).orElse(null);
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to find resource [%s] in module path".formatted(name), ex);
                return null;
            }
            return null;
        }
    }

    private Resource convertModuleSystemURI(URI uri) {
        return "file".equals(uri.getScheme()) ? new FileSystemResource(uri.getPath()) : UrlResource.from(uri);
    }

    private static String stripLeadingSlash(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/PathMatchingResourcePatternResolver$VfsResourceMatchingDelegate.class */
    public static class VfsResourceMatchingDelegate {
        private VfsResourceMatchingDelegate() {
        }

        public static Set<Resource> findMatchingResources(URL rootDirUrl, String locationPattern, PathMatcher pathMatcher) throws IOException {
            Object root = VfsPatternUtils.findRoot(rootDirUrl);
            PatternVirtualFileVisitor visitor = new PatternVirtualFileVisitor(VfsPatternUtils.getPath(root), locationPattern, pathMatcher);
            VfsPatternUtils.visit(root, visitor);
            return visitor.getResources();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/PathMatchingResourcePatternResolver$PatternVirtualFileVisitor.class */
    public static class PatternVirtualFileVisitor implements InvocationHandler {
        private final String subPattern;
        private final PathMatcher pathMatcher;
        private final String rootPath;
        private final Set<Resource> resources = new LinkedHashSet();

        public PatternVirtualFileVisitor(String rootPath, String subPattern, PathMatcher pathMatcher) {
            this.subPattern = subPattern;
            this.pathMatcher = pathMatcher;
            this.rootPath = (rootPath.isEmpty() || rootPath.endsWith("/")) ? rootPath : rootPath + "/";
        }

        @Override // java.lang.reflect.InvocationHandler
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (Object.class == method.getDeclaringClass()) {
                boolean z = -1;
                switch (methodName.hashCode()) {
                    case -1295482945:
                        if (methodName.equals("equals")) {
                            z = false;
                            break;
                        }
                        break;
                    case 147696667:
                        if (methodName.equals(IdentityNamingStrategy.HASH_CODE_KEY)) {
                            z = true;
                            break;
                        }
                        break;
                }
                switch (z) {
                    case false:
                        return Boolean.valueOf(proxy == args[0]);
                    case true:
                        return Integer.valueOf(System.identityHashCode(proxy));
                }
            }
            boolean z2 = -1;
            switch (methodName.hashCode()) {
                case -1776922004:
                    if (methodName.equals("toString")) {
                        z2 = 2;
                        break;
                    }
                    break;
                case 107897165:
                    if (methodName.equals("getAttributes")) {
                        z2 = false;
                        break;
                    }
                    break;
                case 112217419:
                    if (methodName.equals("visit")) {
                        z2 = true;
                        break;
                    }
                    break;
            }
            switch (z2) {
                case false:
                    return getAttributes();
                case true:
                    visit(args[0]);
                    return null;
                case true:
                    return toString();
                default:
                    throw new IllegalStateException("Unexpected method invocation: " + method);
            }
        }

        public void visit(Object vfsResource) {
            if (this.pathMatcher.match(this.subPattern, VfsPatternUtils.getPath(vfsResource).substring(this.rootPath.length()))) {
                this.resources.add(new VfsResource(vfsResource));
            }
        }

        @Nullable
        public Object getAttributes() {
            return VfsPatternUtils.getVisitorAttributes();
        }

        public Set<Resource> getResources() {
            return this.resources;
        }

        public int size() {
            return this.resources.size();
        }

        public String toString() {
            return "sub-pattern: " + this.subPattern + ", resources: " + this.resources;
        }
    }
}
