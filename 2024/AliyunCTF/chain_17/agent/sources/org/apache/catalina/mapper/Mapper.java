package org.apache.catalina.mapper;

import jakarta.servlet.http.MappingMatch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.Ascii;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mapper/Mapper.class */
public final class Mapper {
    private static final Log log = LogFactory.getLog((Class<?>) Mapper.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Mapper.class);
    volatile MappedHost[] hosts = new MappedHost[0];
    private volatile String defaultHostName = null;
    private volatile MappedHost defaultHost = null;
    private final Map<Context, ContextVersion> contextObjectToContextVersionMap = new ConcurrentHashMap();

    public synchronized void setDefaultHostName(String defaultHostName) {
        this.defaultHostName = renameWildcardHost(defaultHostName);
        if (this.defaultHostName == null) {
            this.defaultHost = null;
        } else {
            this.defaultHost = (MappedHost) exactFind(this.hosts, this.defaultHostName);
        }
    }

    public synchronized void addHost(String name, String[] aliases, Host host) {
        String name2 = renameWildcardHost(name);
        MappedHost[] newHosts = new MappedHost[this.hosts.length + 1];
        MappedHost newHost = new MappedHost(name2, host);
        if (insertMap(this.hosts, newHosts, newHost)) {
            this.hosts = newHosts;
            if (newHost.name.equals(this.defaultHostName)) {
                this.defaultHost = newHost;
            }
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("mapper.addHost.success", name2));
            }
        } else {
            MappedHost duplicate = this.hosts[find(this.hosts, name2)];
            if (duplicate.object == host) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("mapper.addHost.sameHost", name2));
                }
                newHost = duplicate;
            } else {
                log.error(sm.getString("mapper.duplicateHost", name2, duplicate.getRealHostName()));
                return;
            }
        }
        List<MappedHost> newAliases = new ArrayList<>(aliases.length);
        for (String alias : aliases) {
            MappedHost newAlias = new MappedHost(renameWildcardHost(alias), newHost);
            if (addHostAliasImpl(newAlias)) {
                newAliases.add(newAlias);
            }
        }
        newHost.addAliases(newAliases);
    }

    public synchronized void removeHost(String name) {
        MappedHost host = (MappedHost) exactFind(this.hosts, renameWildcardHost(name));
        if (host == null || host.isAlias()) {
            return;
        }
        MappedHost[] newHosts = (MappedHost[]) this.hosts.clone();
        int j = 0;
        for (int i = 0; i < newHosts.length; i++) {
            if (newHosts[i].getRealHost() != host) {
                int i2 = j;
                j++;
                newHosts[i2] = newHosts[i];
            }
        }
        this.hosts = (MappedHost[]) Arrays.copyOf(newHosts, j);
    }

    public synchronized void addHostAlias(String name, String alias) {
        MappedHost realHost = (MappedHost) exactFind(this.hosts, name);
        if (realHost == null) {
            return;
        }
        MappedHost newAlias = new MappedHost(renameWildcardHost(alias), realHost);
        if (addHostAliasImpl(newAlias)) {
            realHost.addAlias(newAlias);
        }
    }

    private synchronized boolean addHostAliasImpl(MappedHost newAlias) {
        MappedHost[] newHosts = new MappedHost[this.hosts.length + 1];
        if (insertMap(this.hosts, newHosts, newAlias)) {
            this.hosts = newHosts;
            if (newAlias.name.equals(this.defaultHostName)) {
                this.defaultHost = newAlias;
            }
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("mapper.addHostAlias.success", newAlias.name, newAlias.getRealHostName()));
                return true;
            }
            return true;
        }
        MappedHost duplicate = this.hosts[find(this.hosts, newAlias.name)];
        if (duplicate.getRealHost() == newAlias.getRealHost()) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("mapper.addHostAlias.sameHost", newAlias.name, newAlias.getRealHostName()));
                return false;
            }
            return false;
        }
        log.error(sm.getString("mapper.duplicateHostAlias", newAlias.name, newAlias.getRealHostName(), duplicate.getRealHostName()));
        return false;
    }

    public synchronized void removeHostAlias(String alias) {
        String alias2 = renameWildcardHost(alias);
        MappedHost hostMapping = (MappedHost) exactFind(this.hosts, alias2);
        if (hostMapping == null || !hostMapping.isAlias()) {
            return;
        }
        MappedHost[] newHosts = new MappedHost[this.hosts.length - 1];
        if (removeMap(this.hosts, newHosts, alias2)) {
            this.hosts = newHosts;
            hostMapping.getRealHost().removeAlias(hostMapping);
        }
    }

    private void updateContextList(MappedHost realHost, ContextList newContextList) {
        realHost.contextList = newContextList;
        for (MappedHost alias : realHost.getAliases()) {
            alias.contextList = newContextList;
        }
    }

    public void addContextVersion(String hostName, Host host, String path, String version, Context context, String[] welcomeResources, WebResourceRoot resources, Collection<WrapperMappingInfo> wrappers) {
        String hostName2 = renameWildcardHost(hostName);
        MappedHost mappedHost = (MappedHost) exactFind(this.hosts, hostName2);
        if (mappedHost == null) {
            addHost(hostName2, new String[0], host);
            mappedHost = (MappedHost) exactFind(this.hosts, hostName2);
            if (mappedHost == null) {
                log.error(sm.getString("mapper.addContext.noHost", hostName2));
                return;
            }
        }
        if (mappedHost.isAlias()) {
            log.error(sm.getString("mapper.addContext.hostIsAlias", hostName2));
            return;
        }
        int slashCount = slashCount(path);
        synchronized (mappedHost) {
            ContextVersion newContextVersion = new ContextVersion(version, path, slashCount, context, resources, welcomeResources);
            if (wrappers != null) {
                addWrappers(newContextVersion, wrappers);
            }
            ContextList contextList = mappedHost.contextList;
            MappedContext mappedContext = (MappedContext) exactFind(contextList.contexts, path);
            if (mappedContext == null) {
                ContextList newContextList = contextList.addContext(new MappedContext(path, newContextVersion), slashCount);
                if (newContextList != null) {
                    updateContextList(mappedHost, newContextList);
                    this.contextObjectToContextVersionMap.put(context, newContextVersion);
                }
            } else {
                ContextVersion[] contextVersions = mappedContext.versions;
                ContextVersion[] newContextVersions = new ContextVersion[contextVersions.length + 1];
                if (insertMap(contextVersions, newContextVersions, newContextVersion)) {
                    mappedContext.versions = newContextVersions;
                    this.contextObjectToContextVersionMap.put(context, newContextVersion);
                } else {
                    int pos = find(contextVersions, version);
                    if (pos >= 0 && contextVersions[pos].name.equals(version)) {
                        contextVersions[pos] = newContextVersion;
                        this.contextObjectToContextVersionMap.put(context, newContextVersion);
                    }
                }
            }
        }
    }

    public void removeContextVersion(Context ctxt, String hostName, String path, String version) {
        String hostName2 = renameWildcardHost(hostName);
        this.contextObjectToContextVersionMap.remove(ctxt);
        MappedHost host = (MappedHost) exactFind(this.hosts, hostName2);
        if (host == null || host.isAlias()) {
            return;
        }
        synchronized (host) {
            ContextList contextList = host.contextList;
            MappedContext context = (MappedContext) exactFind(contextList.contexts, path);
            if (context == null) {
                return;
            }
            ContextVersion[] contextVersions = context.versions;
            ContextVersion[] newContextVersions = new ContextVersion[contextVersions.length - 1];
            if (removeMap(contextVersions, newContextVersions, version)) {
                if (newContextVersions.length == 0) {
                    ContextList newContextList = contextList.removeContext(path);
                    if (newContextList != null) {
                        updateContextList(host, newContextList);
                    }
                } else {
                    context.versions = newContextVersions;
                }
            }
        }
    }

    public void pauseContextVersion(Context ctxt, String hostName, String contextPath, String version) {
        ContextVersion contextVersion = findContextVersion(renameWildcardHost(hostName), contextPath, version, true);
        if (contextVersion == null || !ctxt.equals(contextVersion.object)) {
            return;
        }
        contextVersion.markPaused();
    }

    private ContextVersion findContextVersion(String hostName, String contextPath, String version, boolean silent) {
        MappedHost host = (MappedHost) exactFind(this.hosts, hostName);
        if (host == null || host.isAlias()) {
            if (!silent) {
                log.error(sm.getString("mapper.findContext.noHostOrAlias", hostName));
                return null;
            }
            return null;
        }
        MappedContext context = (MappedContext) exactFind(host.contextList.contexts, contextPath);
        if (context == null) {
            if (!silent) {
                log.error(sm.getString("mapper.findContext.noContext", contextPath));
                return null;
            }
            return null;
        }
        ContextVersion contextVersion = (ContextVersion) exactFind(context.versions, version);
        if (contextVersion == null) {
            if (!silent) {
                log.error(sm.getString("mapper.findContext.noContextVersion", contextPath, version));
                return null;
            }
            return null;
        }
        return contextVersion;
    }

    public void addWrapper(String hostName, String contextPath, String version, String path, Wrapper wrapper, boolean jspWildCard, boolean resourceOnly) {
        ContextVersion contextVersion = findContextVersion(renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null) {
            return;
        }
        addWrapper(contextVersion, path, wrapper, jspWildCard, resourceOnly);
    }

    public void addWrappers(String hostName, String contextPath, String version, Collection<WrapperMappingInfo> wrappers) {
        ContextVersion contextVersion = findContextVersion(renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null) {
            return;
        }
        addWrappers(contextVersion, wrappers);
    }

    private void addWrappers(ContextVersion contextVersion, Collection<WrapperMappingInfo> wrappers) {
        for (WrapperMappingInfo wrapper : wrappers) {
            addWrapper(contextVersion, wrapper.getMapping(), wrapper.getWrapper(), wrapper.isJspWildCard(), wrapper.isResourceOnly());
        }
    }

    protected void addWrapper(ContextVersion context, String path, Wrapper wrapper, boolean jspWildCard, boolean resourceOnly) {
        String name;
        synchronized (context) {
            if (path.endsWith("/*")) {
                String name2 = path.substring(0, path.length() - 2);
                MappedWrapper newWrapper = new MappedWrapper(name2, wrapper, jspWildCard, resourceOnly);
                MappedWrapper[] oldWrappers = context.wildcardWrappers;
                MappedWrapper[] newWrappers = new MappedWrapper[oldWrappers.length + 1];
                if (insertMap(oldWrappers, newWrappers, newWrapper)) {
                    context.wildcardWrappers = newWrappers;
                    int slashCount = slashCount(newWrapper.name);
                    if (slashCount > context.nesting) {
                        context.nesting = slashCount;
                    }
                }
            } else if (path.startsWith("*.")) {
                String name3 = path.substring(2);
                MappedWrapper newWrapper2 = new MappedWrapper(name3, wrapper, jspWildCard, resourceOnly);
                MappedWrapper[] oldWrappers2 = context.extensionWrappers;
                MappedWrapper[] newWrappers2 = new MappedWrapper[oldWrappers2.length + 1];
                if (insertMap(oldWrappers2, newWrappers2, newWrapper2)) {
                    context.extensionWrappers = newWrappers2;
                }
            } else if (path.equals("/")) {
                MappedWrapper newWrapper3 = new MappedWrapper("", wrapper, jspWildCard, resourceOnly);
                context.defaultWrapper = newWrapper3;
            } else {
                if (path.length() == 0) {
                    name = "/";
                } else {
                    name = path;
                }
                MappedWrapper newWrapper4 = new MappedWrapper(name, wrapper, jspWildCard, resourceOnly);
                MappedWrapper[] oldWrappers3 = context.exactWrappers;
                MappedWrapper[] newWrappers3 = new MappedWrapper[oldWrappers3.length + 1];
                if (insertMap(oldWrappers3, newWrappers3, newWrapper4)) {
                    context.exactWrappers = newWrappers3;
                }
            }
        }
    }

    public void removeWrapper(String hostName, String contextPath, String version, String path) {
        ContextVersion contextVersion = findContextVersion(renameWildcardHost(hostName), contextPath, version, true);
        if (contextVersion == null || contextVersion.isPaused()) {
            return;
        }
        removeWrapper(contextVersion, path);
    }

    protected void removeWrapper(ContextVersion context, String path) {
        String name;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("mapper.removeWrapper", context.name, path));
        }
        synchronized (context) {
            if (path.endsWith("/*")) {
                String name2 = path.substring(0, path.length() - 2);
                MappedWrapper[] oldWrappers = context.wildcardWrappers;
                if (oldWrappers.length == 0) {
                    return;
                }
                MappedWrapper[] newWrappers = new MappedWrapper[oldWrappers.length - 1];
                if (removeMap(oldWrappers, newWrappers, name2)) {
                    context.nesting = 0;
                    for (MappedWrapper newWrapper : newWrappers) {
                        int slashCount = slashCount(newWrapper.name);
                        if (slashCount > context.nesting) {
                            context.nesting = slashCount;
                        }
                    }
                    context.wildcardWrappers = newWrappers;
                }
            } else if (path.startsWith("*.")) {
                String name3 = path.substring(2);
                MappedWrapper[] oldWrappers2 = context.extensionWrappers;
                if (oldWrappers2.length == 0) {
                    return;
                }
                MappedWrapper[] newWrappers2 = new MappedWrapper[oldWrappers2.length - 1];
                if (removeMap(oldWrappers2, newWrappers2, name3)) {
                    context.extensionWrappers = newWrappers2;
                }
            } else if (path.equals("/")) {
                context.defaultWrapper = null;
            } else {
                if (path.length() == 0) {
                    name = "/";
                } else {
                    name = path;
                }
                MappedWrapper[] oldWrappers3 = context.exactWrappers;
                if (oldWrappers3.length == 0) {
                    return;
                }
                MappedWrapper[] newWrappers3 = new MappedWrapper[oldWrappers3.length - 1];
                if (removeMap(oldWrappers3, newWrappers3, name)) {
                    context.exactWrappers = newWrappers3;
                }
            }
        }
    }

    public void addWelcomeFile(String hostName, String contextPath, String version, String welcomeFile) {
        ContextVersion contextVersion = findContextVersion(renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null) {
            return;
        }
        int len = contextVersion.welcomeResources.length + 1;
        String[] newWelcomeResources = new String[len];
        System.arraycopy(contextVersion.welcomeResources, 0, newWelcomeResources, 0, len - 1);
        newWelcomeResources[len - 1] = welcomeFile;
        contextVersion.welcomeResources = newWelcomeResources;
    }

    public void removeWelcomeFile(String hostName, String contextPath, String version, String welcomeFile) {
        ContextVersion contextVersion = findContextVersion(renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null || contextVersion.isPaused()) {
            return;
        }
        int match = -1;
        int i = 0;
        while (true) {
            if (i >= contextVersion.welcomeResources.length) {
                break;
            }
            if (!welcomeFile.equals(contextVersion.welcomeResources[i])) {
                i++;
            } else {
                match = i;
                break;
            }
        }
        if (match > -1) {
            int len = contextVersion.welcomeResources.length - 1;
            String[] newWelcomeResources = new String[len];
            System.arraycopy(contextVersion.welcomeResources, 0, newWelcomeResources, 0, match);
            if (match < len) {
                System.arraycopy(contextVersion.welcomeResources, match + 1, newWelcomeResources, match, len - match);
            }
            contextVersion.welcomeResources = newWelcomeResources;
        }
    }

    public void clearWelcomeFiles(String hostName, String contextPath, String version) {
        ContextVersion contextVersion = findContextVersion(renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null) {
            return;
        }
        contextVersion.welcomeResources = new String[0];
    }

    public void map(MessageBytes host, MessageBytes uri, String version, MappingData mappingData) throws IOException {
        if (host.isNull()) {
            String defaultHostName = this.defaultHostName;
            if (defaultHostName == null) {
                return;
            }
            host.setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
            host.getCharChunk().append(defaultHostName);
        }
        host.toChars();
        uri.toChars();
        internalMap(host.getCharChunk(), uri.getCharChunk(), version, mappingData);
    }

    public void map(Context context, MessageBytes uri, MappingData mappingData) throws IOException {
        ContextVersion contextVersion = this.contextObjectToContextVersionMap.get(context);
        uri.toChars();
        CharChunk uricc = uri.getCharChunk();
        uricc.setLimit(-1);
        internalMapWrapper(contextVersion, uricc, mappingData);
    }

    private void internalMap(CharChunk host, CharChunk uri, String version, MappingData mappingData) throws IOException {
        int lastSlash;
        if (mappingData.host != null) {
            throw new AssertionError();
        }
        MappedHost[] hosts = this.hosts;
        MappedHost mappedHost = (MappedHost) exactFindIgnoreCase(hosts, host);
        if (mappedHost == null) {
            int firstDot = host.indexOf('.');
            if (firstDot > -1) {
                int offset = host.getOffset();
                try {
                    host.setOffset(firstDot + offset);
                    mappedHost = (MappedHost) exactFindIgnoreCase(hosts, host);
                    host.setOffset(offset);
                } catch (Throwable th) {
                    host.setOffset(offset);
                    throw th;
                }
            }
            if (mappedHost == null) {
                mappedHost = this.defaultHost;
                if (mappedHost == null) {
                    return;
                }
            }
        }
        mappingData.host = (Host) mappedHost.object;
        if (uri.isNull()) {
            return;
        }
        uri.setLimit(-1);
        ContextList contextList = mappedHost.contextList;
        MappedContext[] contexts = contextList.contexts;
        int pos = find(contexts, uri);
        if (pos == -1) {
            return;
        }
        int lastSlash2 = -1;
        int uriEnd = uri.getEnd();
        boolean found = false;
        MappedContext context = null;
        while (true) {
            if (pos < 0) {
                break;
            }
            context = contexts[pos];
            if (uri.startsWith(context.name)) {
                int length = context.name.length();
                if (uri.getLength() == length) {
                    found = true;
                    break;
                } else if (uri.startsWithIgnoreCase("/", length)) {
                    found = true;
                    break;
                }
            }
            if (lastSlash2 == -1) {
                lastSlash = nthSlash(uri, contextList.nesting + 1);
            } else {
                lastSlash = lastSlash(uri);
            }
            lastSlash2 = lastSlash;
            uri.setEnd(lastSlash2);
            pos = find(contexts, uri);
        }
        uri.setEnd(uriEnd);
        if (!found) {
            if (contexts[0].name.equals("")) {
                context = contexts[0];
            } else {
                context = null;
            }
        }
        if (context == null) {
            return;
        }
        ContextVersion contextVersion = null;
        ContextVersion[] contextVersions = context.versions;
        int versionCount = contextVersions.length;
        if (versionCount > 1) {
            Context[] contextObjects = new Context[contextVersions.length];
            for (int i = 0; i < contextObjects.length; i++) {
                contextObjects[i] = (Context) contextVersions[i].object;
            }
            mappingData.contexts = contextObjects;
            if (version != null) {
                contextVersion = (ContextVersion) exactFind(contextVersions, version);
            }
        }
        if (contextVersion == null) {
            contextVersion = contextVersions[versionCount - 1];
        }
        mappingData.context = (Context) contextVersion.object;
        mappingData.contextSlashCount = contextVersion.slashCount;
        if (!contextVersion.isPaused()) {
            internalMapWrapper(contextVersion, uri, mappingData);
        }
    }

    private void internalMapWrapper(ContextVersion contextVersion, CharChunk path, MappingData mappingData) throws IOException {
        WebResource file;
        String pathStr;
        WebResource file2;
        int pathOffset = path.getOffset();
        int pathEnd = path.getEnd();
        boolean noServletPath = false;
        int length = contextVersion.path.length();
        if (length == pathEnd - pathOffset) {
            noServletPath = true;
        }
        int servletPath = pathOffset + length;
        path.setOffset(servletPath);
        MappedWrapper[] exactWrappers = contextVersion.exactWrappers;
        internalMapExactWrapper(exactWrappers, path, mappingData);
        boolean checkJspWelcomeFiles = false;
        MappedWrapper[] wildcardWrappers = contextVersion.wildcardWrappers;
        if (mappingData.wrapper == null) {
            internalMapWildcardWrapper(wildcardWrappers, contextVersion.nesting, path, mappingData);
            if (mappingData.wrapper != null && mappingData.jspWildCard) {
                char[] buf = path.getBuffer();
                if (buf[pathEnd - 1] == '/') {
                    mappingData.wrapper = null;
                    checkJspWelcomeFiles = true;
                } else {
                    mappingData.wrapperPath.setChars(buf, path.getStart(), path.getLength());
                    mappingData.pathInfo.recycle();
                }
            }
        }
        if (mappingData.wrapper == null && noServletPath && ((Context) contextVersion.object).getMapperContextRootRedirectEnabled()) {
            path.append('/');
            int pathEnd2 = path.getEnd();
            mappingData.redirectPath.setChars(path.getBuffer(), pathOffset, pathEnd2 - pathOffset);
            path.setEnd(pathEnd2 - 1);
            return;
        }
        MappedWrapper[] extensionWrappers = contextVersion.extensionWrappers;
        if (mappingData.wrapper == null && !checkJspWelcomeFiles) {
            internalMapExtensionWrapper(extensionWrappers, path, mappingData, true);
        }
        if (mappingData.wrapper == null) {
            boolean checkWelcomeFiles = checkJspWelcomeFiles;
            if (!checkWelcomeFiles) {
                char[] buf2 = path.getBuffer();
                checkWelcomeFiles = buf2[pathEnd - 1] == '/';
            }
            if (checkWelcomeFiles) {
                for (int i = 0; i < contextVersion.welcomeResources.length && mappingData.wrapper == null; i++) {
                    path.setOffset(pathOffset);
                    path.setEnd(pathEnd);
                    path.append(contextVersion.welcomeResources[i], 0, contextVersion.welcomeResources[i].length());
                    path.setOffset(servletPath);
                    internalMapExactWrapper(exactWrappers, path, mappingData);
                    if (mappingData.wrapper == null) {
                        internalMapWildcardWrapper(wildcardWrappers, contextVersion.nesting, path, mappingData);
                    }
                    if (mappingData.wrapper == null && contextVersion.resources != null && (file2 = contextVersion.resources.getResource((pathStr = path.toString()))) != null && file2.isFile()) {
                        internalMapExtensionWrapper(extensionWrappers, path, mappingData, true);
                        if (mappingData.wrapper == null && contextVersion.defaultWrapper != null) {
                            mappingData.wrapper = (Wrapper) contextVersion.defaultWrapper.object;
                            mappingData.requestPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                            mappingData.wrapperPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                            mappingData.requestPath.setString(pathStr);
                            mappingData.wrapperPath.setString(pathStr);
                        }
                    }
                }
                path.setOffset(servletPath);
                path.setEnd(pathEnd);
            }
        }
        if (mappingData.wrapper == null) {
            boolean checkWelcomeFiles2 = checkJspWelcomeFiles;
            if (!checkWelcomeFiles2) {
                char[] buf3 = path.getBuffer();
                checkWelcomeFiles2 = buf3[pathEnd - 1] == '/';
            }
            if (checkWelcomeFiles2) {
                for (int i2 = 0; i2 < contextVersion.welcomeResources.length && mappingData.wrapper == null; i2++) {
                    path.setOffset(pathOffset);
                    path.setEnd(pathEnd);
                    path.append(contextVersion.welcomeResources[i2], 0, contextVersion.welcomeResources[i2].length());
                    path.setOffset(servletPath);
                    internalMapExtensionWrapper(extensionWrappers, path, mappingData, false);
                }
                path.setOffset(servletPath);
                path.setEnd(pathEnd);
            }
        }
        if (mappingData.wrapper == null && !checkJspWelcomeFiles) {
            if (contextVersion.defaultWrapper != null) {
                mappingData.wrapper = (Wrapper) contextVersion.defaultWrapper.object;
                mappingData.requestPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                mappingData.wrapperPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                mappingData.matchType = MappingMatch.DEFAULT;
            }
            char[] buf4 = path.getBuffer();
            if (contextVersion.resources != null && buf4[pathEnd - 1] != '/') {
                String pathStr2 = path.toString();
                if (((Context) contextVersion.object).getMapperDirectoryRedirectEnabled()) {
                    if (pathStr2.length() == 0) {
                        file = contextVersion.resources.getResource("/");
                    } else {
                        file = contextVersion.resources.getResource(pathStr2);
                    }
                    if (file != null && file.isDirectory()) {
                        path.setOffset(pathOffset);
                        path.append('/');
                        mappingData.redirectPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                    } else {
                        mappingData.requestPath.setString(pathStr2);
                        mappingData.wrapperPath.setString(pathStr2);
                    }
                } else {
                    mappingData.requestPath.setString(pathStr2);
                    mappingData.wrapperPath.setString(pathStr2);
                }
            }
        }
        path.setOffset(pathOffset);
        path.setEnd(pathEnd);
    }

    private void internalMapExactWrapper(MappedWrapper[] wrappers, CharChunk path, MappingData mappingData) {
        MappedWrapper wrapper = (MappedWrapper) exactFind(wrappers, path);
        if (wrapper != null) {
            mappingData.requestPath.setString(wrapper.name);
            mappingData.wrapper = (Wrapper) wrapper.object;
            if (path.equals("/")) {
                mappingData.pathInfo.setString("/");
                mappingData.wrapperPath.setString("");
                mappingData.matchType = MappingMatch.CONTEXT_ROOT;
            } else {
                mappingData.wrapperPath.setString(wrapper.name);
                mappingData.matchType = MappingMatch.EXACT;
            }
        }
    }

    private void internalMapWildcardWrapper(MappedWrapper[] wrappers, int nesting, CharChunk path, MappingData mappingData) {
        int lastSlash;
        int pathEnd = path.getEnd();
        int lastSlash2 = -1;
        int length = -1;
        int pos = find(wrappers, path);
        if (pos != -1) {
            boolean found = false;
            while (true) {
                if (pos < 0) {
                    break;
                }
                if (path.startsWith(wrappers[pos].name)) {
                    length = wrappers[pos].name.length();
                    if (path.getLength() == length) {
                        found = true;
                        break;
                    } else if (path.startsWithIgnoreCase("/", length)) {
                        found = true;
                        break;
                    }
                }
                if (lastSlash2 == -1) {
                    lastSlash = nthSlash(path, nesting + 1);
                } else {
                    lastSlash = lastSlash(path);
                }
                lastSlash2 = lastSlash;
                path.setEnd(lastSlash2);
                pos = find(wrappers, path);
            }
            path.setEnd(pathEnd);
            if (found) {
                mappingData.wrapperPath.setString(wrappers[pos].name);
                if (path.getLength() > length) {
                    mappingData.pathInfo.setChars(path.getBuffer(), path.getOffset() + length, path.getLength() - length);
                }
                mappingData.requestPath.setChars(path.getBuffer(), path.getOffset(), path.getLength());
                mappingData.wrapper = (Wrapper) wrappers[pos].object;
                mappingData.jspWildCard = wrappers[pos].jspWildCard;
                mappingData.matchType = MappingMatch.PATH;
            }
        }
    }

    private void internalMapExtensionWrapper(MappedWrapper[] wrappers, CharChunk path, MappingData mappingData, boolean resourceExpected) {
        char[] buf = path.getBuffer();
        int pathEnd = path.getEnd();
        int servletPath = path.getOffset();
        int slash = -1;
        int i = pathEnd - 1;
        while (true) {
            if (i < servletPath) {
                break;
            }
            if (buf[i] != '/') {
                i--;
            } else {
                slash = i;
                break;
            }
        }
        if (slash >= 0) {
            int period = -1;
            int i2 = pathEnd - 1;
            while (true) {
                if (i2 <= slash) {
                    break;
                }
                if (buf[i2] != '.') {
                    i2--;
                } else {
                    period = i2;
                    break;
                }
            }
            if (period >= 0) {
                path.setOffset(period + 1);
                path.setEnd(pathEnd);
                MappedWrapper wrapper = (MappedWrapper) exactFind(wrappers, path);
                if (wrapper != null && (resourceExpected || !wrapper.resourceOnly)) {
                    mappingData.wrapperPath.setChars(buf, servletPath, pathEnd - servletPath);
                    mappingData.requestPath.setChars(buf, servletPath, pathEnd - servletPath);
                    mappingData.wrapper = (Wrapper) wrapper.object;
                    mappingData.matchType = MappingMatch.EXTENSION;
                }
                path.setOffset(servletPath);
                path.setEnd(pathEnd);
            }
        }
    }

    private static <T> int find(MapElement<T>[] map, CharChunk name) {
        return find(map, name, name.getStart(), name.getEnd());
    }

    private static <T> int find(MapElement<T>[] map, CharChunk name, int start, int end) {
        int a = 0;
        int b = map.length - 1;
        if (b == -1 || compare(name, start, end, map[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        do {
            int i = (b + a) >>> 1;
            int result = compare(name, start, end, map[i].name);
            if (result == 1) {
                a = i;
            } else {
                if (result == 0) {
                    return i;
                }
                b = i;
            }
        } while (b - a != 1);
        int result2 = compare(name, start, end, map[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    private static <T> int findIgnoreCase(MapElement<T>[] map, CharChunk name) {
        return findIgnoreCase(map, name, name.getStart(), name.getEnd());
    }

    private static <T> int findIgnoreCase(MapElement<T>[] map, CharChunk name, int start, int end) {
        int a = 0;
        int b = map.length - 1;
        if (b == -1 || compareIgnoreCase(name, start, end, map[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        do {
            int i = (b + a) >>> 1;
            int result = compareIgnoreCase(name, start, end, map[i].name);
            if (result == 1) {
                a = i;
            } else {
                if (result == 0) {
                    return i;
                }
                b = i;
            }
        } while (b - a != 1);
        int result2 = compareIgnoreCase(name, start, end, map[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    private static <T> int find(MapElement<T>[] map, String name) {
        int a = 0;
        int b = map.length - 1;
        if (b == -1 || name.compareTo(map[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        do {
            int i = (b + a) >>> 1;
            int result = name.compareTo(map[i].name);
            if (result > 0) {
                a = i;
            } else {
                if (result == 0) {
                    return i;
                }
                b = i;
            }
        } while (b - a != 1);
        int result2 = name.compareTo(map[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    private static <T, E extends MapElement<T>> E exactFind(E[] map, String name) {
        int pos = find(map, name);
        if (pos >= 0) {
            E result = map[pos];
            if (name.equals(result.name)) {
                return result;
            }
            return null;
        }
        return null;
    }

    private static <T, E extends MapElement<T>> E exactFind(E[] map, CharChunk name) {
        int pos = find(map, name);
        if (pos >= 0) {
            E result = map[pos];
            if (name.equals(result.name)) {
                return result;
            }
            return null;
        }
        return null;
    }

    private static <T, E extends MapElement<T>> E exactFindIgnoreCase(E[] map, CharChunk name) {
        int pos = findIgnoreCase(map, name);
        if (pos >= 0) {
            E result = map[pos];
            if (name.equalsIgnoreCase(result.name)) {
                return result;
            }
            return null;
        }
        return null;
    }

    private static int compare(CharChunk name, int start, int end, String compareTo) {
        int result = 0;
        char[] c = name.getBuffer();
        int compareLen = compareTo.length();
        int len = compareLen;
        if (end - start < len) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; i++) {
            char nameChar = c[i + start];
            char compareToChar = compareTo.charAt(i);
            if (nameChar > compareToChar) {
                result = 1;
            } else if (nameChar < compareToChar) {
                result = -1;
            }
        }
        if (result == 0) {
            if (compareLen > end - start) {
                result = -1;
            } else if (compareLen < end - start) {
                result = 1;
            }
        }
        return result;
    }

    private static int compareIgnoreCase(CharChunk name, int start, int end, String compareTo) {
        int result = 0;
        char[] c = name.getBuffer();
        int compareLen = compareTo.length();
        int len = compareLen;
        if (end - start < len) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; i++) {
            int nameLower = Ascii.toLower(c[i + start]);
            int compareLower = Ascii.toLower(compareTo.charAt(i));
            if (nameLower > compareLower) {
                result = 1;
            } else if (nameLower < compareLower) {
                result = -1;
            }
        }
        if (result == 0) {
            if (compareLen > end - start) {
                result = -1;
            } else if (compareLen < end - start) {
                result = 1;
            }
        }
        return result;
    }

    private static int lastSlash(CharChunk name) {
        char[] c = name.getBuffer();
        int end = name.getEnd();
        int start = name.getStart();
        int pos = end;
        while (pos > start) {
            pos--;
            if (c[pos] == '/') {
                break;
            }
        }
        return pos;
    }

    private static int nthSlash(CharChunk name, int n) {
        char[] c = name.getBuffer();
        int end = name.getEnd();
        int start = name.getStart();
        int pos = start;
        int count = 0;
        while (true) {
            if (pos >= end) {
                break;
            }
            int i = pos;
            pos++;
            if (c[i] == '/') {
                count++;
                if (count == n) {
                    pos--;
                    break;
                }
            }
        }
        return pos;
    }

    private static int slashCount(String name) {
        int pos = -1;
        int count = 0;
        while (true) {
            int indexOf = name.indexOf(47, pos + 1);
            pos = indexOf;
            if (indexOf != -1) {
                count++;
            } else {
                return count;
            }
        }
    }

    private static <T> boolean insertMap(MapElement<T>[] oldMap, MapElement<T>[] newMap, MapElement<T> newElement) {
        int pos = find(oldMap, newElement.name);
        if (pos != -1 && newElement.name.equals(oldMap[pos].name)) {
            return false;
        }
        System.arraycopy(oldMap, 0, newMap, 0, pos + 1);
        newMap[pos + 1] = newElement;
        System.arraycopy(oldMap, pos + 1, newMap, pos + 2, (oldMap.length - pos) - 1);
        return true;
    }

    private static <T> boolean removeMap(MapElement<T>[] oldMap, MapElement<T>[] newMap, String name) {
        int pos = find(oldMap, name);
        if (pos != -1 && name.equals(oldMap[pos].name)) {
            System.arraycopy(oldMap, 0, newMap, 0, pos);
            System.arraycopy(oldMap, pos + 1, newMap, pos, (oldMap.length - pos) - 1);
            return true;
        }
        return false;
    }

    private static String renameWildcardHost(String hostName) {
        if (hostName != null && hostName.startsWith("*.")) {
            return hostName.substring(1);
        }
        return hostName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mapper/Mapper$MapElement.class */
    public static abstract class MapElement<T> {
        public final String name;
        public final T object;

        public MapElement(String name, T object) {
            this.name = name;
            this.object = object;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mapper/Mapper$MappedHost.class */
    public static final class MappedHost extends MapElement<Host> {
        public volatile ContextList contextList;
        private final MappedHost realHost;
        private final List<MappedHost> aliases;

        public MappedHost(String name, Host host) {
            super(name, host);
            this.realHost = this;
            this.contextList = new ContextList();
            this.aliases = new CopyOnWriteArrayList();
        }

        public MappedHost(String alias, MappedHost realHost) {
            super(alias, (Host) realHost.object);
            this.realHost = realHost;
            this.contextList = realHost.contextList;
            this.aliases = null;
        }

        public boolean isAlias() {
            return this.realHost != this;
        }

        public MappedHost getRealHost() {
            return this.realHost;
        }

        public String getRealHostName() {
            return this.realHost.name;
        }

        public Collection<MappedHost> getAliases() {
            return this.aliases;
        }

        public void addAlias(MappedHost alias) {
            this.aliases.add(alias);
        }

        public void addAliases(Collection<? extends MappedHost> c) {
            this.aliases.addAll(c);
        }

        public void removeAlias(MappedHost alias) {
            this.aliases.remove(alias);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mapper/Mapper$ContextList.class */
    public static final class ContextList {
        public final MappedContext[] contexts;
        public final int nesting;

        public ContextList() {
            this(new MappedContext[0], 0);
        }

        private ContextList(MappedContext[] contexts, int nesting) {
            this.contexts = contexts;
            this.nesting = nesting;
        }

        public ContextList addContext(MappedContext mappedContext, int slashCount) {
            MappedContext[] newContexts = new MappedContext[this.contexts.length + 1];
            if (Mapper.insertMap(this.contexts, newContexts, mappedContext)) {
                return new ContextList(newContexts, Math.max(this.nesting, slashCount));
            }
            return null;
        }

        public ContextList removeContext(String path) {
            MappedContext[] newContexts = new MappedContext[this.contexts.length - 1];
            if (Mapper.removeMap(this.contexts, newContexts, path)) {
                int newNesting = 0;
                for (MappedContext context : newContexts) {
                    newNesting = Math.max(newNesting, Mapper.slashCount(context.name));
                }
                return new ContextList(newContexts, newNesting);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mapper/Mapper$MappedContext.class */
    public static final class MappedContext extends MapElement<Void> {
        public volatile ContextVersion[] versions;

        public MappedContext(String name, ContextVersion firstVersion) {
            super(name, null);
            this.versions = new ContextVersion[]{firstVersion};
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mapper/Mapper$ContextVersion.class */
    public static final class ContextVersion extends MapElement<Context> {
        public final String path;
        public final int slashCount;
        public final WebResourceRoot resources;
        public String[] welcomeResources;
        public MappedWrapper defaultWrapper;
        public MappedWrapper[] exactWrappers;
        public MappedWrapper[] wildcardWrappers;
        public MappedWrapper[] extensionWrappers;
        public int nesting;
        private volatile boolean paused;

        public ContextVersion(String version, String path, int slashCount, Context context, WebResourceRoot resources, String[] welcomeResources) {
            super(version, context);
            this.defaultWrapper = null;
            this.exactWrappers = new MappedWrapper[0];
            this.wildcardWrappers = new MappedWrapper[0];
            this.extensionWrappers = new MappedWrapper[0];
            this.nesting = 0;
            this.path = path;
            this.slashCount = slashCount;
            this.resources = resources;
            this.welcomeResources = welcomeResources;
        }

        public boolean isPaused() {
            return this.paused;
        }

        public void markPaused() {
            this.paused = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mapper/Mapper$MappedWrapper.class */
    public static class MappedWrapper extends MapElement<Wrapper> {
        public final boolean jspWildCard;
        public final boolean resourceOnly;

        public MappedWrapper(String name, Wrapper wrapper, boolean jspWildCard, boolean resourceOnly) {
            super(name, wrapper);
            this.jspWildCard = jspWildCard;
            this.resourceOnly = resourceOnly;
        }
    }
}
