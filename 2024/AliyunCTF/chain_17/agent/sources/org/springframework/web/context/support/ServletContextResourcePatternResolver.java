package org.springframework.web.context.support;

import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletContextResourcePatternResolver.class */
public class ServletContextResourcePatternResolver extends PathMatchingResourcePatternResolver {
    private static final Log logger = LogFactory.getLog((Class<?>) ServletContextResourcePatternResolver.class);

    public ServletContextResourcePatternResolver(ServletContext servletContext) {
        super(new ServletContextResourceLoader(servletContext));
    }

    public ServletContextResourcePatternResolver(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.io.support.PathMatchingResourcePatternResolver
    public Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException {
        if (rootDirResource instanceof ServletContextResource) {
            ServletContextResource scResource = (ServletContextResource) rootDirResource;
            ServletContext sc = scResource.getServletContext();
            String fullPattern = scResource.getPath() + subPattern;
            Set<Resource> result = new LinkedHashSet<>(8);
            doRetrieveMatchingServletContextResources(sc, fullPattern, scResource.getPath(), result);
            return result;
        }
        return super.doFindPathMatchingFileResources(rootDirResource, subPattern);
    }

    protected void doRetrieveMatchingServletContextResources(ServletContext servletContext, String fullPattern, String dir, Set<Resource> result) throws IOException {
        String absoluteJarPath;
        int dirIndex;
        Set<String> candidates = servletContext.getResourcePaths(dir);
        if (candidates != null) {
            boolean dirDepthNotFixed = fullPattern.contains(SecurityConstraint.ROLE_ALL_AUTHENTICATED_USERS);
            int jarFileSep = fullPattern.indexOf("!/");
            String jarFilePath = null;
            String pathInJarFile = null;
            if (jarFileSep > 0 && jarFileSep + "!/".length() < fullPattern.length()) {
                jarFilePath = fullPattern.substring(0, jarFileSep);
                pathInJarFile = fullPattern.substring(jarFileSep + "!/".length());
            }
            Iterator<String> it = candidates.iterator();
            while (it.hasNext()) {
                String currPath = it.next();
                if (!currPath.startsWith(dir) && (dirIndex = currPath.indexOf(dir)) != -1) {
                    currPath = currPath.substring(dirIndex);
                }
                if (currPath.endsWith("/") && (dirDepthNotFixed || StringUtils.countOccurrencesOf(currPath, "/") <= StringUtils.countOccurrencesOf(fullPattern, "/"))) {
                    doRetrieveMatchingServletContextResources(servletContext, fullPattern, currPath, result);
                }
                if (jarFilePath != null && getPathMatcher().match(jarFilePath, currPath) && (absoluteJarPath = servletContext.getRealPath(currPath)) != null) {
                    doRetrieveMatchingJarEntries(absoluteJarPath, pathInJarFile, result);
                }
                if (getPathMatcher().match(fullPattern, currPath)) {
                    result.add(new ServletContextResource(servletContext, currPath));
                }
            }
        }
    }

    private void doRetrieveMatchingJarEntries(String jarFilePath, String entryPattern, Set<Resource> result) {
        if (logger.isDebugEnabled()) {
            logger.debug("Searching jar file [" + jarFilePath + "] for entries matching [" + entryPattern + "]");
        }
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            try {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryPath = entry.getName();
                    if (getPathMatcher().match(entryPattern, entryPath)) {
                        result.add(new UrlResource("jar", "file:" + jarFilePath + "!/" + entryPath));
                    }
                }
                jarFile.close();
            } finally {
            }
        } catch (IOException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Cannot search for matching resources in jar file [" + jarFilePath + "] because the jar cannot be opened through the file system", ex);
            }
        }
    }
}
