package org.apache.tomcat.util.scan;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.lang.module.ResolvedModule;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/scan/StandardJarScanner.class */
public class StandardJarScanner implements JarScanner {
    private static final StringManager sm = StringManager.getManager(Constants.Package);
    private static final Set<ClassLoader> CLASSLOADER_HIERARCHY;
    private final Log log = LogFactory.getLog((Class<?>) StandardJarScanner.class);
    private boolean scanClassPath = true;
    private boolean scanManifest = true;
    private boolean scanAllFiles = false;
    private boolean scanAllDirectories = true;
    private boolean scanBootstrapClassPath = false;
    private JarScanFilter jarScanFilter = new StandardJarScanFilter();

    static {
        Set<ClassLoader> cls = new HashSet<>();
        ClassLoader classLoader = StandardJarScanner.class.getClassLoader();
        while (true) {
            ClassLoader cl = classLoader;
            if (cl != null) {
                cls.add(cl);
                classLoader = cl.getParent();
            } else {
                CLASSLOADER_HIERARCHY = Collections.unmodifiableSet(cls);
                return;
            }
        }
    }

    public boolean isScanClassPath() {
        return this.scanClassPath;
    }

    public void setScanClassPath(boolean scanClassPath) {
        this.scanClassPath = scanClassPath;
    }

    public boolean isScanManifest() {
        return this.scanManifest;
    }

    public void setScanManifest(boolean scanManifest) {
        this.scanManifest = scanManifest;
    }

    public boolean isScanAllFiles() {
        return this.scanAllFiles;
    }

    public void setScanAllFiles(boolean scanAllFiles) {
        this.scanAllFiles = scanAllFiles;
    }

    public boolean isScanAllDirectories() {
        return this.scanAllDirectories;
    }

    public void setScanAllDirectories(boolean scanAllDirectories) {
        this.scanAllDirectories = scanAllDirectories;
    }

    public boolean isScanBootstrapClassPath() {
        return this.scanBootstrapClassPath;
    }

    public void setScanBootstrapClassPath(boolean scanBootstrapClassPath) {
        this.scanBootstrapClassPath = scanBootstrapClassPath;
    }

    @Override // org.apache.tomcat.JarScanner
    public JarScanFilter getJarScanFilter() {
        return this.jarScanFilter;
    }

    @Override // org.apache.tomcat.JarScanner
    public void setJarScanFilter(JarScanFilter jarScanFilter) {
        this.jarScanFilter = jarScanFilter;
    }

    @Override // org.apache.tomcat.JarScanner
    public void scan(JarScanType scanType, ServletContext context, JarScannerCallback callback) {
        if (this.log.isTraceEnabled()) {
            this.log.trace(sm.getString("jarScan.webinflibStart"));
        }
        if (this.jarScanFilter.isSkipAll()) {
            return;
        }
        Set<URL> processedURLs = new HashSet<>();
        Set<String> dirList = context.getResourcePaths(Constants.WEB_INF_LIB);
        if (dirList != null) {
            for (String path : dirList) {
                if (path.endsWith(".jar") && getJarScanFilter().check(scanType, path.substring(path.lastIndexOf(47) + 1))) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(sm.getString("jarScan.webinflibJarScan", path));
                    }
                    URL url = null;
                    try {
                        url = context.getResource(path);
                        if (url != null) {
                            processedURLs.add(url);
                            process(scanType, callback, url, path, true, null);
                        } else {
                            this.log.warn(sm.getString("jarScan.webinflibFail", path));
                        }
                    } catch (IOException e) {
                        this.log.warn(sm.getString("jarScan.webinflibFail", url), e);
                    }
                } else if (this.log.isTraceEnabled()) {
                    this.log.trace(sm.getString("jarScan.webinflibJarNoScan", path));
                }
            }
        }
        try {
            URL webInfURL = context.getResource(Constants.WEB_INF_CLASSES);
            if (webInfURL != null) {
                processedURLs.add(webInfURL);
                if (isScanAllDirectories()) {
                    URL url2 = context.getResource("/WEB-INF/classes/META-INF");
                    if (url2 != null) {
                        try {
                            callback.scanWebInfClasses();
                        } catch (IOException e2) {
                            this.log.warn(sm.getString("jarScan.webinfclassesFail"), e2);
                        }
                    }
                }
            }
        } catch (MalformedURLException e3) {
        }
        if (isScanClassPath()) {
            doScanClassPath(scanType, context, callback, processedURLs);
        }
    }

    protected void doScanClassPath(JarScanType scanType, ServletContext context, JarScannerCallback callback, Set<URL> processedURLs) {
        if (this.log.isTraceEnabled()) {
            this.log.trace(sm.getString("jarScan.classloaderStart"));
        }
        ClassLoader stopLoader = null;
        if (!isScanBootstrapClassPath()) {
            stopLoader = ClassLoader.getSystemClassLoader().getParent();
        }
        boolean isWebapp = true;
        Deque<URL> classPathUrlsToProcess = new ArrayDeque<>();
        for (ClassLoader classLoader = context.getClassLoader(); classLoader != null && classLoader != stopLoader; classLoader = classLoader.getParent()) {
            if (classLoader instanceof URLClassLoader) {
                if (isWebapp) {
                    isWebapp = isWebappClassLoader(classLoader);
                }
                classPathUrlsToProcess.addAll(Arrays.asList(((URLClassLoader) classLoader).getURLs()));
                processURLs(scanType, callback, processedURLs, isWebapp, classPathUrlsToProcess);
            }
        }
        addClassPath(classPathUrlsToProcess);
        for (ResolvedModule module : ModuleLayer.boot().configuration().modules()) {
            Optional<URI> uri = module.reference().location();
            if (uri.isPresent()) {
                try {
                    classPathUrlsToProcess.add(uri.get().toURL());
                } catch (MalformedURLException e) {
                    this.log.warn(sm.getString("jarScan.invalidModuleUri", uri), e);
                }
            }
        }
        processURLs(scanType, callback, processedURLs, false, classPathUrlsToProcess);
    }

    protected void processURLs(JarScanType scanType, JarScannerCallback callback, Set<URL> processedURLs, boolean isWebapp, Deque<URL> classPathUrlsToProcess) {
        if (this.jarScanFilter.isSkipAll()) {
            return;
        }
        while (!classPathUrlsToProcess.isEmpty()) {
            URL url = classPathUrlsToProcess.pop();
            if (!processedURLs.contains(url)) {
                ClassPathEntry cpe = new ClassPathEntry(url);
                if ((cpe.isJar() || scanType == JarScanType.PLUGGABILITY || isScanAllDirectories()) && getJarScanFilter().check(scanType, cpe.getName())) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(sm.getString("jarScan.classloaderJarScan", url));
                    }
                    try {
                        processedURLs.add(url);
                        process(scanType, callback, url, null, isWebapp, classPathUrlsToProcess);
                    } catch (IOException ioe) {
                        this.log.warn(sm.getString("jarScan.classloaderFail", url), ioe);
                    }
                } else if (this.log.isTraceEnabled()) {
                    this.log.trace(sm.getString("jarScan.classloaderJarNoScan", url));
                }
            }
        }
    }

    protected void addClassPath(Deque<URL> classPathUrlsToProcess) {
        String classPath = System.getProperty("java.class.path");
        if (classPath == null || classPath.length() == 0) {
            return;
        }
        String[] classPathEntries = classPath.split(File.pathSeparator);
        for (String classPathEntry : classPathEntries) {
            File f = new File(classPathEntry);
            try {
                classPathUrlsToProcess.add(f.toURI().toURL());
            } catch (MalformedURLException e) {
                this.log.warn(sm.getString("jarScan.classPath.badEntry", classPathEntry), e);
            }
        }
    }

    private static boolean isWebappClassLoader(ClassLoader classLoader) {
        return !CLASSLOADER_HIERARCHY.contains(classLoader);
    }

    protected void process(JarScanType scanType, JarScannerCallback callback, URL url, String webappPath, boolean isWebapp, Deque<URL> classPathUrlsToProcess) throws IOException {
        if (this.log.isTraceEnabled()) {
            this.log.trace(sm.getString("jarScan.jarUrlStart", url));
        }
        if (ResourceUtils.URL_PROTOCOL_JAR.equals(url.getProtocol()) || url.getPath().endsWith(".jar")) {
            Jar jar = JarFactory.newInstance(url);
            try {
                if (isScanManifest()) {
                    processManifest(jar, isWebapp, classPathUrlsToProcess);
                }
                callback.scan(jar, webappPath, isWebapp);
                if (jar != null) {
                    jar.close();
                    return;
                }
                return;
            } catch (Throwable th) {
                if (jar != null) {
                    try {
                        jar.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if ("file".equals(url.getProtocol())) {
            try {
                File f = new File(url.toURI());
                if (f.isFile() && isScanAllFiles()) {
                    URL jarURL = UriUtil.buildJarUrl(f);
                    Jar jar2 = JarFactory.newInstance(jarURL);
                    try {
                        if (isScanManifest()) {
                            processManifest(jar2, isWebapp, classPathUrlsToProcess);
                        }
                        callback.scan(jar2, webappPath, isWebapp);
                        if (jar2 != null) {
                            jar2.close();
                        }
                    } finally {
                    }
                } else if (f.isDirectory()) {
                    if (scanType == JarScanType.PLUGGABILITY) {
                        callback.scan(f, webappPath, isWebapp);
                    } else {
                        File metainf = new File(f.getAbsoluteFile() + File.separator + "META-INF");
                        if (metainf.isDirectory()) {
                            callback.scan(f, webappPath, isWebapp);
                        }
                    }
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                throw new IOException(t);
            }
        }
    }

    private void processManifest(Jar jar, boolean isWebapp, Deque<URL> classPathUrlsToProcess) throws IOException {
        Manifest manifest;
        if (!isWebapp && classPathUrlsToProcess != null && (manifest = jar.getManifest()) != null) {
            Attributes attributes = manifest.getMainAttributes();
            String classPathAttribute = attributes.getValue("Class-Path");
            if (classPathAttribute == null) {
                return;
            }
            String[] classPathEntries = classPathAttribute.split(" ");
            for (String str : classPathEntries) {
                String classPathEntry = str.trim();
                if (classPathEntry.length() != 0) {
                    URL jarURL = jar.getJarFileURL();
                    try {
                        URI jarURI = jarURL.toURI();
                        URI classPathEntryURI = jarURI.resolve(classPathEntry);
                        URL classPathEntryURL = classPathEntryURI.toURL();
                        classPathUrlsToProcess.add(classPathEntryURL);
                    } catch (Exception e) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(sm.getString("jarScan.invalidUri", jarURL), e);
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/scan/StandardJarScanner$ClassPathEntry.class */
    public static class ClassPathEntry {
        private final boolean jar;
        private final String name;

        ClassPathEntry(URL url) {
            String path = url.getPath();
            int end = path.lastIndexOf(".jar");
            if (end != -1) {
                this.jar = true;
                int start = path.lastIndexOf(47, end);
                this.name = path.substring(start + 1, end + 4);
            } else {
                this.jar = false;
                path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
                int start2 = path.lastIndexOf(47);
                this.name = path.substring(start2 + 1);
            }
        }

        public boolean isJar() {
            return this.jar;
        }

        public String getName() {
            return this.name;
        }
    }
}
