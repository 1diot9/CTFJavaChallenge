package org.apache.coyote.http11;

import jakarta.servlet.http.HttpUpgradeHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.CompressionConfig;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeGroupInfo;
import org.apache.coyote.http11.upgrade.UpgradeProcessorExternal;
import org.apache.coyote.http11.upgrade.UpgradeProcessorInternal;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.Util;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/AbstractHttp11Protocol.class */
public abstract class AbstractHttp11Protocol<S> extends AbstractProtocol<S> {
    protected static final StringManager sm = StringManager.getManager((Class<?>) AbstractHttp11Protocol.class);
    private final CompressionConfig compressionConfig;
    private ContinueResponseTiming continueResponseTiming;
    private boolean useKeepAliveResponseHeader;
    private String relaxedPathChars;
    private String relaxedQueryChars;
    private boolean allowHostHeaderMismatch;
    private boolean rejectIllegalHeader;
    private int maxSavePostSize;
    private int maxHttpHeaderSize;
    private int maxHttpRequestHeaderSize;
    private int maxHttpResponseHeaderSize;
    private int connectionUploadTimeout;
    private boolean disableUploadTimeout;
    private Pattern restrictedUserAgents;
    private String server;
    private boolean serverRemoveAppProvidedValues;
    private int maxTrailerSize;
    private int maxExtensionSize;
    private int maxSwallowSize;
    private boolean secure;
    private Set<String> allowedTrailerHeaders;
    private final List<UpgradeProtocol> upgradeProtocols;
    private final Map<String, UpgradeProtocol> httpUpgradeProtocols;
    private final Map<String, UpgradeProtocol> negotiatedProtocols;
    private final Map<String, UpgradeGroupInfo> upgradeProtocolGroupInfos;

    public AbstractHttp11Protocol(AbstractEndpoint<S, ?> endpoint) {
        super(endpoint);
        this.compressionConfig = new CompressionConfig();
        this.continueResponseTiming = ContinueResponseTiming.IMMEDIATELY;
        this.useKeepAliveResponseHeader = true;
        this.relaxedPathChars = null;
        this.relaxedQueryChars = null;
        this.allowHostHeaderMismatch = false;
        this.rejectIllegalHeader = true;
        this.maxSavePostSize = 4096;
        this.maxHttpHeaderSize = 8192;
        this.maxHttpRequestHeaderSize = -1;
        this.maxHttpResponseHeaderSize = -1;
        this.connectionUploadTimeout = 300000;
        this.disableUploadTimeout = true;
        this.restrictedUserAgents = null;
        this.serverRemoveAppProvidedValues = false;
        this.maxTrailerSize = 8192;
        this.maxExtensionSize = 8192;
        this.maxSwallowSize = 2097152;
        this.allowedTrailerHeaders = ConcurrentHashMap.newKeySet();
        this.upgradeProtocols = new ArrayList();
        this.httpUpgradeProtocols = new HashMap();
        this.negotiatedProtocols = new HashMap();
        this.upgradeProtocolGroupInfos = new ConcurrentHashMap();
        setConnectionTimeout(Constants.DEFAULT_CONNECTION_TIMEOUT);
    }

    @Override // org.apache.coyote.AbstractProtocol, org.apache.coyote.ProtocolHandler
    public void init() throws Exception {
        for (UpgradeProtocol upgradeProtocol : this.upgradeProtocols) {
            configureUpgradeProtocol(upgradeProtocol);
        }
        super.init();
        for (UpgradeProtocol upgradeProtocol2 : this.upgradeProtocols) {
            upgradeProtocol2.setHttp11Protocol(this);
        }
    }

    @Override // org.apache.coyote.AbstractProtocol, org.apache.coyote.ProtocolHandler
    public void destroy() throws Exception {
        ObjectName rgOname = getGlobalRequestProcessorMBeanName();
        if (rgOname != null) {
            Registry registry = Registry.getRegistry(null, null);
            ObjectName query = new ObjectName(rgOname.getCanonicalName() + ",Upgrade=*");
            Set<ObjectInstance> upgrades = registry.getMBeanServer().queryMBeans(query, (QueryExp) null);
            for (ObjectInstance upgrade : upgrades) {
                registry.unregisterComponent(upgrade.getObjectName());
            }
        }
        super.destroy();
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getProtocolName() {
        return "Http";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProtocol
    public AbstractEndpoint<S, ?> getEndpoint() {
        return super.getEndpoint();
    }

    public String getContinueResponseTiming() {
        return this.continueResponseTiming.toString();
    }

    public void setContinueResponseTiming(String continueResponseTiming) {
        this.continueResponseTiming = ContinueResponseTiming.fromString(continueResponseTiming);
    }

    public ContinueResponseTiming getContinueResponseTimingInternal() {
        return this.continueResponseTiming;
    }

    public boolean getUseKeepAliveResponseHeader() {
        return this.useKeepAliveResponseHeader;
    }

    public void setUseKeepAliveResponseHeader(boolean useKeepAliveResponseHeader) {
        this.useKeepAliveResponseHeader = useKeepAliveResponseHeader;
    }

    public String getRelaxedPathChars() {
        return this.relaxedPathChars;
    }

    public void setRelaxedPathChars(String relaxedPathChars) {
        this.relaxedPathChars = relaxedPathChars;
    }

    public String getRelaxedQueryChars() {
        return this.relaxedQueryChars;
    }

    public void setRelaxedQueryChars(String relaxedQueryChars) {
        this.relaxedQueryChars = relaxedQueryChars;
    }

    @Deprecated
    public boolean getAllowHostHeaderMismatch() {
        return this.allowHostHeaderMismatch;
    }

    @Deprecated
    public void setAllowHostHeaderMismatch(boolean allowHostHeaderMismatch) {
        this.allowHostHeaderMismatch = allowHostHeaderMismatch;
    }

    @Deprecated
    public boolean getRejectIllegalHeader() {
        return this.rejectIllegalHeader;
    }

    @Deprecated
    public void setRejectIllegalHeader(boolean rejectIllegalHeader) {
        this.rejectIllegalHeader = rejectIllegalHeader;
    }

    public int getMaxSavePostSize() {
        return this.maxSavePostSize;
    }

    public void setMaxSavePostSize(int maxSavePostSize) {
        this.maxSavePostSize = maxSavePostSize;
    }

    public int getMaxHttpHeaderSize() {
        return this.maxHttpHeaderSize;
    }

    public void setMaxHttpHeaderSize(int valueI) {
        this.maxHttpHeaderSize = valueI;
    }

    public int getMaxHttpRequestHeaderSize() {
        return this.maxHttpRequestHeaderSize == -1 ? getMaxHttpHeaderSize() : this.maxHttpRequestHeaderSize;
    }

    public void setMaxHttpRequestHeaderSize(int valueI) {
        this.maxHttpRequestHeaderSize = valueI;
    }

    public int getMaxHttpResponseHeaderSize() {
        return this.maxHttpResponseHeaderSize == -1 ? getMaxHttpHeaderSize() : this.maxHttpResponseHeaderSize;
    }

    public void setMaxHttpResponseHeaderSize(int valueI) {
        this.maxHttpResponseHeaderSize = valueI;
    }

    public int getConnectionUploadTimeout() {
        return this.connectionUploadTimeout;
    }

    public void setConnectionUploadTimeout(int timeout) {
        this.connectionUploadTimeout = timeout;
    }

    public boolean getDisableUploadTimeout() {
        return this.disableUploadTimeout;
    }

    public void setDisableUploadTimeout(boolean isDisabled) {
        this.disableUploadTimeout = isDisabled;
    }

    public void setCompression(String compression) {
        this.compressionConfig.setCompression(compression);
    }

    public String getCompression() {
        return this.compressionConfig.getCompression();
    }

    protected int getCompressionLevel() {
        return this.compressionConfig.getCompressionLevel();
    }

    public String getNoCompressionUserAgents() {
        return this.compressionConfig.getNoCompressionUserAgents();
    }

    protected Pattern getNoCompressionUserAgentsPattern() {
        return this.compressionConfig.getNoCompressionUserAgentsPattern();
    }

    public void setNoCompressionUserAgents(String noCompressionUserAgents) {
        this.compressionConfig.setNoCompressionUserAgents(noCompressionUserAgents);
    }

    public String getCompressibleMimeType() {
        return this.compressionConfig.getCompressibleMimeType();
    }

    public void setCompressibleMimeType(String valueS) {
        this.compressionConfig.setCompressibleMimeType(valueS);
    }

    public String[] getCompressibleMimeTypes() {
        return this.compressionConfig.getCompressibleMimeTypes();
    }

    public int getCompressionMinSize() {
        return this.compressionConfig.getCompressionMinSize();
    }

    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionConfig.setCompressionMinSize(compressionMinSize);
    }

    public boolean useCompression(Request request, Response response) {
        return this.compressionConfig.useCompression(request, response);
    }

    public String getRestrictedUserAgents() {
        if (this.restrictedUserAgents == null) {
            return null;
        }
        return this.restrictedUserAgents.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Pattern getRestrictedUserAgentsPattern() {
        return this.restrictedUserAgents;
    }

    public void setRestrictedUserAgents(String restrictedUserAgents) {
        if (restrictedUserAgents == null || restrictedUserAgents.length() == 0) {
            this.restrictedUserAgents = null;
        } else {
            this.restrictedUserAgents = Pattern.compile(restrictedUserAgents);
        }
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean getServerRemoveAppProvidedValues() {
        return this.serverRemoveAppProvidedValues;
    }

    public void setServerRemoveAppProvidedValues(boolean serverRemoveAppProvidedValues) {
        this.serverRemoveAppProvidedValues = serverRemoveAppProvidedValues;
    }

    public int getMaxTrailerSize() {
        return this.maxTrailerSize;
    }

    public void setMaxTrailerSize(int maxTrailerSize) {
        this.maxTrailerSize = maxTrailerSize;
    }

    public int getMaxExtensionSize() {
        return this.maxExtensionSize;
    }

    public void setMaxExtensionSize(int maxExtensionSize) {
        this.maxExtensionSize = maxExtensionSize;
    }

    public int getMaxSwallowSize() {
        return this.maxSwallowSize;
    }

    public void setMaxSwallowSize(int maxSwallowSize) {
        this.maxSwallowSize = maxSwallowSize;
    }

    public boolean getSecure() {
        return this.secure;
    }

    public void setSecure(boolean b) {
        this.secure = b;
    }

    public void setAllowedTrailerHeaders(String commaSeparatedHeaders) {
        Set<String> toRemove = new HashSet<>(this.allowedTrailerHeaders);
        if (commaSeparatedHeaders != null) {
            String[] headers = commaSeparatedHeaders.split(",");
            for (String header : headers) {
                String trimmedHeader = header.trim().toLowerCase(Locale.ENGLISH);
                if (toRemove.contains(trimmedHeader)) {
                    toRemove.remove(trimmedHeader);
                } else {
                    this.allowedTrailerHeaders.add(trimmedHeader);
                }
            }
            this.allowedTrailerHeaders.removeAll(toRemove);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Set<String> getAllowedTrailerHeadersInternal() {
        return this.allowedTrailerHeaders;
    }

    public boolean isTrailerHeaderAllowed(String headerName) {
        return this.allowedTrailerHeaders.contains(headerName);
    }

    public String getAllowedTrailerHeaders() {
        List<String> copy = new ArrayList<>(this.allowedTrailerHeaders);
        return StringUtils.join(copy);
    }

    public void addAllowedTrailerHeader(String header) {
        if (header != null) {
            this.allowedTrailerHeaders.add(header.trim().toLowerCase(Locale.ENGLISH));
        }
    }

    public void removeAllowedTrailerHeader(String header) {
        if (header != null) {
            this.allowedTrailerHeaders.remove(header.trim().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        this.upgradeProtocols.add(upgradeProtocol);
    }

    @Override // org.apache.coyote.ProtocolHandler
    public UpgradeProtocol[] findUpgradeProtocols() {
        return (UpgradeProtocol[]) this.upgradeProtocols.toArray(new UpgradeProtocol[0]);
    }

    private void configureUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        String httpUpgradeName = upgradeProtocol.getHttpUpgradeName(getEndpoint().isSSLEnabled());
        boolean httpUpgradeConfigured = false;
        if (httpUpgradeName != null && httpUpgradeName.length() > 0) {
            this.httpUpgradeProtocols.put(httpUpgradeName, upgradeProtocol);
            httpUpgradeConfigured = true;
            getLog().info(sm.getString("abstractHttp11Protocol.httpUpgradeConfigured", getName(), httpUpgradeName));
        }
        String alpnName = upgradeProtocol.getAlpnName();
        if (alpnName != null && alpnName.length() > 0) {
            if (getEndpoint().isSSLEnabled()) {
                this.negotiatedProtocols.put(alpnName, upgradeProtocol);
                getEndpoint().addNegotiatedProtocol(alpnName);
                getLog().info(sm.getString("abstractHttp11Protocol.alpnConfigured", getName(), alpnName));
            } else if (!httpUpgradeConfigured) {
                getLog().error(sm.getString("abstractHttp11Protocol.alpnWithNoAlpn", upgradeProtocol.getClass().getName(), alpnName, getName()));
            }
        }
    }

    @Override // org.apache.coyote.AbstractProtocol
    public UpgradeProtocol getNegotiatedProtocol(String negotiatedName) {
        return this.negotiatedProtocols.get(negotiatedName);
    }

    @Override // org.apache.coyote.AbstractProtocol
    public UpgradeProtocol getUpgradeProtocol(String upgradedName) {
        return this.httpUpgradeProtocols.get(upgradedName);
    }

    public UpgradeGroupInfo getUpgradeGroupInfo(String upgradeProtocol) {
        if (upgradeProtocol == null) {
            return null;
        }
        UpgradeGroupInfo result = this.upgradeProtocolGroupInfos.get(upgradeProtocol);
        if (result == null) {
            synchronized (this.upgradeProtocolGroupInfos) {
                result = this.upgradeProtocolGroupInfos.get(upgradeProtocol);
                if (result == null) {
                    result = new UpgradeGroupInfo();
                    this.upgradeProtocolGroupInfos.put(upgradeProtocol, result);
                    ObjectName oname = getONameForUpgrade(upgradeProtocol);
                    if (oname != null) {
                        try {
                            Registry.getRegistry(null, null).registerComponent(result, oname, (String) null);
                        } catch (Exception e) {
                            getLog().warn(sm.getString("abstractHttp11Protocol.upgradeJmxRegistrationFail"), e);
                            result = null;
                        }
                    }
                }
            }
        }
        return result;
    }

    public ObjectName getONameForUpgrade(String upgradeProtocol) {
        ObjectName oname = null;
        ObjectName parentRgOname = getGlobalRequestProcessorMBeanName();
        if (parentRgOname != null) {
            StringBuilder name = new StringBuilder(parentRgOname.getCanonicalName());
            name.append(",Upgrade=");
            if (Util.objectNameValueNeedsQuote(upgradeProtocol)) {
                name.append(ObjectName.quote(upgradeProtocol));
            } else {
                name.append(upgradeProtocol);
            }
            try {
                oname = new ObjectName(name.toString());
            } catch (Exception e) {
                getLog().warn(sm.getString("abstractHttp11Protocol.upgradeJmxNameFail"), e);
            }
        }
        return oname;
    }

    public boolean isSSLEnabled() {
        return getEndpoint().isSSLEnabled();
    }

    public void setSSLEnabled(boolean SSLEnabled) {
        getEndpoint().setSSLEnabled(SSLEnabled);
    }

    public boolean getUseSendfile() {
        return getEndpoint().getUseSendfile();
    }

    public void setUseSendfile(boolean useSendfile) {
        getEndpoint().setUseSendfile(useSendfile);
    }

    public int getMaxKeepAliveRequests() {
        return getEndpoint().getMaxKeepAliveRequests();
    }

    public void setMaxKeepAliveRequests(int mkar) {
        getEndpoint().setMaxKeepAliveRequests(mkar);
    }

    public String getDefaultSSLHostConfigName() {
        return getEndpoint().getDefaultSSLHostConfigName();
    }

    public void setDefaultSSLHostConfigName(String defaultSSLHostConfigName) {
        getEndpoint().setDefaultSSLHostConfigName(defaultSSLHostConfigName);
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        getEndpoint().addSslHostConfig(sslHostConfig);
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addSslHostConfig(SSLHostConfig sslHostConfig, boolean replace) {
        getEndpoint().addSslHostConfig(sslHostConfig, replace);
    }

    @Override // org.apache.coyote.ProtocolHandler
    public SSLHostConfig[] findSslHostConfigs() {
        return getEndpoint().findSslHostConfigs();
    }

    public void reloadSslHostConfigs() {
        getEndpoint().reloadSslHostConfigs();
    }

    public void reloadSslHostConfig(String hostName) {
        getEndpoint().reloadSslHostConfig(hostName);
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Processor createProcessor() {
        Http11Processor processor = new Http11Processor(this, this.adapter);
        return processor;
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Processor createUpgradeProcessor(SocketWrapperBase<?> socket, UpgradeToken upgradeToken) {
        HttpUpgradeHandler httpUpgradeHandler = upgradeToken.getHttpUpgradeHandler();
        if (httpUpgradeHandler instanceof InternalHttpUpgradeHandler) {
            return new UpgradeProcessorInternal(socket, upgradeToken, getUpgradeGroupInfo(upgradeToken.getProtocol()));
        }
        return new UpgradeProcessorExternal(socket, upgradeToken, getUpgradeGroupInfo(upgradeToken.getProtocol()));
    }
}
