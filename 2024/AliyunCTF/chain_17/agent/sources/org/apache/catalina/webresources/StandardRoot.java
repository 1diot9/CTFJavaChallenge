package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.ObjectName;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.TrackedWebResource;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.scan.Constants;
import org.springframework.util.ResourceUtils;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/StandardRoot.class */
public class StandardRoot extends LifecycleMBeanBase implements WebResourceRoot {
    private static final Log log = LogFactory.getLog((Class<?>) StandardRoot.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) StandardRoot.class);
    private Context context;
    private WebResourceSet main;
    private boolean allowLinking = false;
    private final List<WebResourceSet> preResources = new ArrayList();
    private final List<WebResourceSet> classResources = new ArrayList();
    private final List<WebResourceSet> jarResources = new ArrayList();
    private final List<WebResourceSet> postResources = new ArrayList();
    private final Cache cache = new Cache(this);
    private boolean cachingAllowed = true;
    private ObjectName cacheJmxName = null;
    private boolean trackLockedFiles = false;
    private final Set<TrackedWebResource> trackedResources = ConcurrentHashMap.newKeySet();
    private WebResourceRoot.ArchiveIndexStrategy archiveIndexStrategy = WebResourceRoot.ArchiveIndexStrategy.SIMPLE;
    private final List<WebResourceSet> mainResources = new ArrayList();
    private final List<List<WebResourceSet>> allResources = new ArrayList();

    public StandardRoot() {
        this.allResources.add(this.preResources);
        this.allResources.add(this.mainResources);
        this.allResources.add(this.classResources);
        this.allResources.add(this.jarResources);
        this.allResources.add(this.postResources);
    }

    public StandardRoot(Context context) {
        this.allResources.add(this.preResources);
        this.allResources.add(this.mainResources);
        this.allResources.add(this.classResources);
        this.allResources.add(this.jarResources);
        this.allResources.add(this.postResources);
        this.context = context;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public String[] list(String path) {
        return list(path, true);
    }

    private String[] list(String path, boolean validate) {
        if (validate) {
            path = validate(path);
        }
        HashSet<String> result = new LinkedHashSet<>();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                if (!webResourceSet.getClassLoaderOnly()) {
                    String[] entries = webResourceSet.list(path);
                    result.addAll(Arrays.asList(entries));
                }
            }
        }
        return (String[]) result.toArray(new String[0]);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public Set<String> listWebAppPaths(String path) {
        String path2 = validate(path);
        Set<String> result = new HashSet<>();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                if (!webResourceSet.getClassLoaderOnly()) {
                    result.addAll(webResourceSet.listWebAppPaths(path2));
                }
            }
        }
        if (result.size() == 0) {
            return null;
        }
        return result;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public boolean mkdir(String path) {
        String path2 = validate(path);
        if (preResourceExists(path2)) {
            return false;
        }
        boolean mkdirResult = this.main.mkdir(path2);
        if (mkdirResult && isCachingAllowed()) {
            this.cache.removeCacheEntry(path2);
        }
        return mkdirResult;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public boolean write(String path, InputStream is, boolean overwrite) {
        String path2 = validate(path);
        if (!overwrite && preResourceExists(path2)) {
            return false;
        }
        boolean writeResult = this.main.write(path2, is, overwrite);
        if (writeResult && isCachingAllowed()) {
            this.cache.removeCacheEntry(path2);
        }
        return writeResult;
    }

    private boolean preResourceExists(String path) {
        for (WebResourceSet webResourceSet : this.preResources) {
            WebResource webResource = webResourceSet.getResource(path);
            if (webResource.exists()) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResource getResource(String path) {
        return getResource(path, true, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WebResource getResource(String path, boolean validate, boolean useClassLoaderResources) {
        if (validate) {
            path = validate(path);
        }
        if (isCachingAllowed()) {
            return this.cache.getResource(path, useClassLoaderResources);
        }
        return getResourceInternal(path, useClassLoaderResources);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResource getClassLoaderResource(String path) {
        return getResource("/WEB-INF/classes" + path, true, true);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResource[] getClassLoaderResources(String path) {
        return getResources("/WEB-INF/classes" + path, true);
    }

    private String validate(String path) {
        String result;
        if (!getState().isAvailable()) {
            throw new IllegalStateException(sm.getString("standardRoot.checkStateNotStarted"));
        }
        if (path == null || path.length() == 0 || !path.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("standardRoot.invalidPath", path));
        }
        if (File.separatorChar == '\\') {
            result = RequestUtil.normalize(path, true);
        } else {
            result = RequestUtil.normalize(path, false);
        }
        if (result == null || result.length() == 0 || !result.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("standardRoot.invalidPathNormal", path, result));
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final WebResource getResourceInternal(String path, boolean useClassLoaderResources) {
        WebResource virtual = null;
        WebResource mainEmpty = null;
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                if ((!useClassLoaderResources && !webResourceSet.getClassLoaderOnly()) || (useClassLoaderResources && !webResourceSet.getStaticOnly())) {
                    WebResource result = webResourceSet.getResource(path);
                    if (result.exists()) {
                        return result;
                    }
                    if (virtual == null) {
                        if (result.isVirtual()) {
                            virtual = result;
                        } else if (this.main.equals(webResourceSet)) {
                            mainEmpty = result;
                        }
                    }
                }
            }
        }
        if (virtual != null) {
            return virtual;
        }
        return mainEmpty;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResource[] getResources(String path) {
        return getResources(path, false);
    }

    private WebResource[] getResources(String path, boolean useClassLoaderResources) {
        String path2 = validate(path);
        if (isCachingAllowed()) {
            return this.cache.getResources(path2, useClassLoaderResources);
        }
        return getResourcesInternal(path2, useClassLoaderResources);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WebResource[] getResourcesInternal(String path, boolean useClassLoaderResources) {
        List<WebResource> result = new ArrayList<>();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                if (useClassLoaderResources || !webResourceSet.getClassLoaderOnly()) {
                    WebResource webResource = webResourceSet.getResource(path);
                    if (webResource.exists()) {
                        result.add(webResource);
                    }
                }
            }
        }
        if (result.size() == 0) {
            result.add(this.main.getResource(path));
        }
        return (WebResource[]) result.toArray(new WebResource[0]);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResource[] listResources(String path) {
        return listResources(path, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WebResource[] listResources(String path, boolean validate) {
        if (validate) {
            path = validate(path);
        }
        String[] resources = list(path, false);
        WebResource[] result = new WebResource[resources.length];
        for (int i = 0; i < resources.length; i++) {
            if (path.charAt(path.length() - 1) == '/') {
                result[i] = getResource(path + resources[i], false, false);
            } else {
                result[i] = getResource(path + "/" + resources[i], false, false);
            }
        }
        return result;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void createWebResourceSet(WebResourceRoot.ResourceSetType type, String webAppMount, URL url, String internalPath) {
        BaseLocation baseLocation = new BaseLocation(url);
        createWebResourceSet(type, webAppMount, baseLocation.getBasePath(), baseLocation.getArchivePath(), internalPath);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void createWebResourceSet(WebResourceRoot.ResourceSetType type, String webAppMount, String base, String archivePath, String internalPath) {
        List<WebResourceSet> resourceList;
        WebResourceSet resourceSet;
        switch (type) {
            case PRE:
                resourceList = this.preResources;
                break;
            case CLASSES_JAR:
                resourceList = this.classResources;
                break;
            case RESOURCE_JAR:
                resourceList = this.jarResources;
                break;
            case POST:
                resourceList = this.postResources;
                break;
            default:
                throw new IllegalArgumentException(sm.getString("standardRoot.createUnknownType", type));
        }
        File file = new File(base);
        if (file.isFile()) {
            if (archivePath != null) {
                resourceSet = new JarWarResourceSet(this, webAppMount, base, archivePath, internalPath);
            } else if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar")) {
                resourceSet = new JarResourceSet(this, webAppMount, base, internalPath);
            } else {
                resourceSet = new FileResourceSet(this, webAppMount, base, internalPath);
            }
        } else if (file.isDirectory()) {
            resourceSet = new DirResourceSet(this, webAppMount, base, internalPath);
        } else {
            throw new IllegalArgumentException(sm.getString("standardRoot.createInvalidFile", file));
        }
        if (type.equals(WebResourceRoot.ResourceSetType.CLASSES_JAR)) {
            resourceSet.setClassLoaderOnly(true);
        } else if (type.equals(WebResourceRoot.ResourceSetType.RESOURCE_JAR)) {
            resourceSet.setStaticOnly(true);
        }
        resourceList.add(resourceSet);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void addPreResources(WebResourceSet webResourceSet) {
        webResourceSet.setRoot(this);
        this.preResources.add(webResourceSet);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResourceSet[] getPreResources() {
        return (WebResourceSet[]) this.preResources.toArray(new WebResourceSet[0]);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void addJarResources(WebResourceSet webResourceSet) {
        webResourceSet.setRoot(this);
        this.jarResources.add(webResourceSet);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResourceSet[] getJarResources() {
        return (WebResourceSet[]) this.jarResources.toArray(new WebResourceSet[0]);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void addPostResources(WebResourceSet webResourceSet) {
        webResourceSet.setRoot(this);
        this.postResources.add(webResourceSet);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResourceSet[] getPostResources() {
        return (WebResourceSet[]) this.postResources.toArray(new WebResourceSet[0]);
    }

    protected WebResourceSet[] getClassResources() {
        return (WebResourceSet[]) this.classResources.toArray(new WebResourceSet[0]);
    }

    protected void addClassResources(WebResourceSet webResourceSet) {
        webResourceSet.setRoot(this);
        this.classResources.add(webResourceSet);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void setAllowLinking(boolean allowLinking) {
        if (this.allowLinking != allowLinking && this.cachingAllowed) {
            this.cache.clear();
        }
        this.allowLinking = allowLinking;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public boolean getAllowLinking() {
        return this.allowLinking;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void setCachingAllowed(boolean cachingAllowed) {
        this.cachingAllowed = cachingAllowed;
        if (!cachingAllowed) {
            this.cache.clear();
        }
    }

    @Override // org.apache.catalina.WebResourceRoot
    public boolean isCachingAllowed() {
        return this.cachingAllowed;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResourceRoot.CacheStrategy getCacheStrategy() {
        return this.cache.getCacheStrategy();
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void setCacheStrategy(WebResourceRoot.CacheStrategy strategy) {
        this.cache.setCacheStrategy(strategy);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public long getCacheTtl() {
        return this.cache.getTtl();
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void setCacheTtl(long cacheTtl) {
        this.cache.setTtl(cacheTtl);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public long getCacheMaxSize() {
        return this.cache.getMaxSize();
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void setCacheMaxSize(long cacheMaxSize) {
        this.cache.setMaxSize(cacheMaxSize);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void setCacheObjectMaxSize(int cacheObjectMaxSize) {
        this.cache.setObjectMaxSize(cacheObjectMaxSize);
        if (getState().isAvailable()) {
            this.cache.enforceObjectMaxSizeLimit();
        }
    }

    @Override // org.apache.catalina.WebResourceRoot
    public int getCacheObjectMaxSize() {
        return this.cache.getObjectMaxSize();
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void setTrackLockedFiles(boolean trackLockedFiles) {
        this.trackLockedFiles = trackLockedFiles;
        if (!trackLockedFiles) {
            this.trackedResources.clear();
        }
    }

    @Override // org.apache.catalina.WebResourceRoot
    public boolean getTrackLockedFiles() {
        return this.trackLockedFiles;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void setArchiveIndexStrategy(String archiveIndexStrategy) {
        this.archiveIndexStrategy = WebResourceRoot.ArchiveIndexStrategy.valueOf(archiveIndexStrategy.toUpperCase(Locale.ENGLISH));
    }

    @Override // org.apache.catalina.WebResourceRoot
    public String getArchiveIndexStrategy() {
        return this.archiveIndexStrategy.name();
    }

    @Override // org.apache.catalina.WebResourceRoot
    public WebResourceRoot.ArchiveIndexStrategy getArchiveIndexStrategyEnum() {
        return this.archiveIndexStrategy;
    }

    public List<String> getTrackedResources() {
        List<String> result = new ArrayList<>(this.trackedResources.size());
        for (TrackedWebResource resource : this.trackedResources) {
            result.add(resource.toString());
        }
        return result;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public Context getContext() {
        return this.context;
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void setContext(Context context) {
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void processWebInfLib() throws LifecycleException {
        WebResource[] possibleJars = listResources("/WEB-INF/lib", false);
        for (WebResource possibleJar : possibleJars) {
            if (possibleJar.isFile() && possibleJar.getName().endsWith(".jar")) {
                createWebResourceSet(WebResourceRoot.ResourceSetType.CLASSES_JAR, Constants.WEB_INF_CLASSES, possibleJar.getURL(), "/");
            }
        }
    }

    protected final void setMainResources(WebResourceSet main) {
        this.main = main;
        this.mainResources.clear();
        this.mainResources.add(main);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void backgroundProcess() {
        this.cache.backgroundProcess();
        gc();
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void gc() {
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                webResourceSet.gc();
            }
        }
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void registerTrackedResource(TrackedWebResource trackedResource) {
        this.trackedResources.add(trackedResource);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public void deregisterTrackedResource(TrackedWebResource trackedResource) {
        this.trackedResources.remove(trackedResource);
    }

    @Override // org.apache.catalina.WebResourceRoot
    public List<URL> getBaseUrls() {
        URL url;
        List<URL> result = new ArrayList<>();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                if (!webResourceSet.getClassLoaderOnly() && (url = webResourceSet.getBaseUrl()) != null) {
                    result.add(url);
                }
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPackedWarFile() {
        return (this.main instanceof WarResourceSet) && this.preResources.isEmpty() && this.postResources.isEmpty();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        return this.context.getDomain();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        return "type=WebResourceRoot" + this.context.getMBeanKeyProperties();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.context == null) {
            throw new IllegalStateException(sm.getString("standardRoot.noContext"));
        }
        this.cacheJmxName = register(this.cache, getObjectNameKeyProperties() + ",name=Cache");
        registerURLStreamHandlerFactory();
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                webResourceSet.init();
            }
        }
    }

    protected void registerURLStreamHandlerFactory() {
        if (!JreCompat.isGraalAvailable()) {
            TomcatURLStreamHandlerFactory.register();
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        this.mainResources.clear();
        this.main = createMainResourceSet();
        this.mainResources.add(this.main);
        for (List<WebResourceSet> list : this.allResources) {
            if (list != this.classResources) {
                for (WebResourceSet webResourceSet : list) {
                    webResourceSet.start();
                }
            }
        }
        processWebInfLib();
        for (WebResourceSet classResource : this.classResources) {
            classResource.start();
        }
        this.cache.enforceObjectMaxSizeLimit();
        setState(LifecycleState.STARTING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WebResourceSet createMainResourceSet() {
        WebResourceSet mainResourceSet;
        String docBase = this.context.getDocBase();
        if (docBase == null) {
            mainResourceSet = new EmptyResourceSet(this);
        } else {
            File f = new File(docBase);
            if (!f.isAbsolute()) {
                f = new File(((Host) this.context.getParent()).getAppBaseFile(), f.getPath());
            }
            if (f.isDirectory()) {
                mainResourceSet = new DirResourceSet(this, "/", f.getAbsolutePath(), "/");
            } else if (f.isFile() && docBase.endsWith(".war")) {
                mainResourceSet = new WarResourceSet(this, "/", f.getAbsolutePath());
            } else {
                throw new IllegalArgumentException(sm.getString("standardRoot.startInvalidMain", f.getAbsolutePath()));
            }
        }
        return mainResourceSet;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                webResourceSet.stop();
            }
        }
        if (this.main != null) {
            this.main.destroy();
        }
        this.mainResources.clear();
        for (WebResourceSet webResourceSet2 : this.jarResources) {
            webResourceSet2.destroy();
        }
        this.jarResources.clear();
        for (WebResourceSet webResourceSet3 : this.classResources) {
            webResourceSet3.destroy();
        }
        this.classResources.clear();
        for (TrackedWebResource trackedResource : this.trackedResources) {
            log.error(sm.getString("standardRoot.lockedFile", this.context.getName(), trackedResource.getName()), trackedResource.getCreatedBy());
            try {
                trackedResource.close();
            } catch (IOException e) {
            }
        }
        this.cache.clear();
        setState(LifecycleState.STOPPING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        for (List<WebResourceSet> list : this.allResources) {
            for (WebResourceSet webResourceSet : list) {
                webResourceSet.destroy();
            }
        }
        unregister(this.cacheJmxName);
        super.destroyInternal();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/StandardRoot$BaseLocation.class */
    public static class BaseLocation {
        private final String basePath;
        private final String archivePath;

        BaseLocation(URL url) {
            int endOfFileUrl;
            File f;
            if ("jar".equals(url.getProtocol()) || ResourceUtils.URL_PROTOCOL_WAR.equals(url.getProtocol())) {
                String jarUrl = url.toString();
                if ("jar".equals(url.getProtocol())) {
                    endOfFileUrl = jarUrl.indexOf("!/");
                } else {
                    endOfFileUrl = jarUrl.indexOf(UriUtil.getWarSeparator());
                }
                String fileUrl = jarUrl.substring(4, endOfFileUrl);
                try {
                    f = new File(new URI(fileUrl));
                    int startOfArchivePath = endOfFileUrl + 2;
                    if (jarUrl.length() > startOfArchivePath) {
                        this.archivePath = jarUrl.substring(startOfArchivePath);
                    } else {
                        this.archivePath = null;
                    }
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException(e);
                }
            } else if ("file".equals(url.getProtocol())) {
                try {
                    f = new File(url.toURI());
                    this.archivePath = null;
                } catch (URISyntaxException e2) {
                    throw new IllegalArgumentException(e2);
                }
            } else {
                throw new IllegalArgumentException(StandardRoot.sm.getString("standardRoot.unsupportedProtocol", url.getProtocol()));
            }
            this.basePath = f.getAbsolutePath();
        }

        String getBasePath() {
            return this.basePath;
        }

        String getArchivePath() {
            return this.archivePath;
        }
    }
}
