package org.apache.catalina;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/WebResourceRoot.class */
public interface WebResourceRoot extends Lifecycle {

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/WebResourceRoot$CacheStrategy.class */
    public interface CacheStrategy {
        boolean noCache(String str);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/WebResourceRoot$ResourceSetType.class */
    public enum ResourceSetType {
        PRE,
        RESOURCE_JAR,
        POST,
        CLASSES_JAR
    }

    WebResource getResource(String str);

    WebResource[] getResources(String str);

    WebResource getClassLoaderResource(String str);

    WebResource[] getClassLoaderResources(String str);

    String[] list(String str);

    Set<String> listWebAppPaths(String str);

    WebResource[] listResources(String str);

    boolean mkdir(String str);

    boolean write(String str, InputStream inputStream, boolean z);

    void createWebResourceSet(ResourceSetType resourceSetType, String str, URL url, String str2);

    void createWebResourceSet(ResourceSetType resourceSetType, String str, String str2, String str3, String str4);

    void addPreResources(WebResourceSet webResourceSet);

    WebResourceSet[] getPreResources();

    void addJarResources(WebResourceSet webResourceSet);

    WebResourceSet[] getJarResources();

    void addPostResources(WebResourceSet webResourceSet);

    WebResourceSet[] getPostResources();

    Context getContext();

    void setContext(Context context);

    void setAllowLinking(boolean z);

    boolean getAllowLinking();

    void setCachingAllowed(boolean z);

    boolean isCachingAllowed();

    void setCacheTtl(long j);

    long getCacheTtl();

    void setCacheMaxSize(long j);

    long getCacheMaxSize();

    void setCacheObjectMaxSize(int i);

    int getCacheObjectMaxSize();

    void setTrackLockedFiles(boolean z);

    boolean getTrackLockedFiles();

    void setArchiveIndexStrategy(String str);

    String getArchiveIndexStrategy();

    ArchiveIndexStrategy getArchiveIndexStrategyEnum();

    void backgroundProcess();

    void registerTrackedResource(TrackedWebResource trackedWebResource);

    void deregisterTrackedResource(TrackedWebResource trackedWebResource);

    List<URL> getBaseUrls();

    void gc();

    default CacheStrategy getCacheStrategy() {
        return null;
    }

    default void setCacheStrategy(CacheStrategy strategy) {
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/WebResourceRoot$ArchiveIndexStrategy.class */
    public enum ArchiveIndexStrategy {
        SIMPLE(false, false),
        BLOOM(true, true),
        PURGED(true, false);

        private final boolean usesBloom;
        private final boolean retain;

        ArchiveIndexStrategy(boolean usesBloom, boolean retain) {
            this.usesBloom = usesBloom;
            this.retain = retain;
        }

        public boolean getUsesBloom() {
            return this.usesBloom;
        }

        public boolean getRetain() {
            return this.retain;
        }
    }
}
