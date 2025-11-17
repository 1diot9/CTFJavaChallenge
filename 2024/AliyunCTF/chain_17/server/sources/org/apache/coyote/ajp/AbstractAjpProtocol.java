package org.apache.coyote.ajp;

import java.net.InetAddress;
import java.util.regex.Pattern;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Processor;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/ajp/AbstractAjpProtocol.class */
public abstract class AbstractAjpProtocol<S> extends AbstractProtocol<S> {
    protected static final StringManager sm = StringManager.getManager((Class<?>) AbstractAjpProtocol.class);
    private boolean ajpFlush;
    private boolean tomcatAuthentication;
    private boolean tomcatAuthorization;
    private String secret;
    private boolean secretRequired;
    private Pattern allowedRequestAttributesPattern;
    private int packetSize;

    public AbstractAjpProtocol(AbstractEndpoint<S, ?> endpoint) {
        super(endpoint);
        this.ajpFlush = true;
        this.tomcatAuthentication = true;
        this.tomcatAuthorization = false;
        this.secret = null;
        this.secretRequired = true;
        this.packetSize = 8192;
        setConnectionTimeout(-1);
        getEndpoint().setUseSendfile(false);
        getEndpoint().setAddress(InetAddress.getLoopbackAddress());
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getProtocolName() {
        return "Ajp";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProtocol
    public AbstractEndpoint<S, ?> getEndpoint() {
        return super.getEndpoint();
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected UpgradeProtocol getNegotiatedProtocol(String name) {
        return null;
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected UpgradeProtocol getUpgradeProtocol(String name) {
        return null;
    }

    public boolean getAjpFlush() {
        return this.ajpFlush;
    }

    public void setAjpFlush(boolean ajpFlush) {
        this.ajpFlush = ajpFlush;
    }

    public boolean getTomcatAuthentication() {
        return this.tomcatAuthentication;
    }

    public void setTomcatAuthentication(boolean tomcatAuthentication) {
        this.tomcatAuthentication = tomcatAuthentication;
    }

    public boolean getTomcatAuthorization() {
        return this.tomcatAuthorization;
    }

    public void setTomcatAuthorization(boolean tomcatAuthorization) {
        this.tomcatAuthorization = tomcatAuthorization;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getSecret() {
        return this.secret;
    }

    @Deprecated
    public void setRequiredSecret(String requiredSecret) {
        setSecret(requiredSecret);
    }

    @Deprecated
    protected String getRequiredSecret() {
        return getSecret();
    }

    public void setSecretRequired(boolean secretRequired) {
        this.secretRequired = secretRequired;
    }

    public boolean getSecretRequired() {
        return this.secretRequired;
    }

    public void setAllowedRequestAttributesPattern(String allowedRequestAttributesPattern) {
        this.allowedRequestAttributesPattern = Pattern.compile(allowedRequestAttributesPattern);
    }

    public String getAllowedRequestAttributesPattern() {
        return this.allowedRequestAttributesPattern.pattern();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Pattern getAllowedRequestAttributesPatternInternal() {
        return this.allowedRequestAttributesPattern;
    }

    public int getPacketSize() {
        return this.packetSize;
    }

    public void setPacketSize(int packetSize) {
        this.packetSize = Math.max(packetSize, 8192);
    }

    @Override // org.apache.coyote.ProtocolHandler
    public int getDesiredBufferSize() {
        return getPacketSize() - 8;
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        getLog().warn(sm.getString("ajpprotocol.noSSL", sslHostConfig.getHostName()));
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addSslHostConfig(SSLHostConfig sslHostConfig, boolean replace) {
        getLog().warn(sm.getString("ajpprotocol.noSSL", sslHostConfig.getHostName()));
    }

    @Override // org.apache.coyote.ProtocolHandler
    public SSLHostConfig[] findSslHostConfigs() {
        return new SSLHostConfig[0];
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        getLog().warn(sm.getString("ajpprotocol.noUpgrade", upgradeProtocol.getClass().getName()));
    }

    @Override // org.apache.coyote.ProtocolHandler
    public UpgradeProtocol[] findUpgradeProtocols() {
        return new UpgradeProtocol[0];
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Processor createProcessor() {
        AjpProcessor processor = new AjpProcessor(this, getAdapter());
        return processor;
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Processor createUpgradeProcessor(SocketWrapperBase<?> socket, UpgradeToken upgradeToken) {
        throw new IllegalStateException(sm.getString("ajpprotocol.noUpgradeHandler", upgradeToken.getHttpUpgradeHandler().getClass().getName()));
    }

    @Override // org.apache.coyote.AbstractProtocol, org.apache.coyote.ProtocolHandler
    public void start() throws Exception {
        String secret;
        if (getSecretRequired() && ((secret = getSecret()) == null || secret.length() == 0)) {
            throw new IllegalArgumentException(sm.getString("ajpprotocol.noSecret"));
        }
        super.start();
    }
}
