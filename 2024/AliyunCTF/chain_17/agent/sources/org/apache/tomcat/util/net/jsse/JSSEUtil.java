package org.apache.tomcat.util.net.jsse;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JreVendor;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/jsse/JSSEUtil.class */
public class JSSEUtil extends SSLUtilBase {
    private static final Log log = LogFactory.getLog((Class<?>) JSSEUtil.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) JSSEUtil.class);
    private volatile boolean initialized;
    private volatile Set<String> implementedProtocols;
    private volatile Set<String> implementedCiphers;

    public JSSEUtil(SSLHostConfigCertificate certificate) {
        this(certificate, true);
    }

    public JSSEUtil(SSLHostConfigCertificate certificate, boolean warnOnSkip) {
        super(certificate, warnOnSkip);
        this.initialized = false;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Log getLog() {
        return log;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Set<String> getImplementedProtocols() {
        initialise();
        return this.implementedProtocols;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Set<String> getImplementedCiphers() {
        initialise();
        return this.implementedCiphers;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected boolean isTls13RenegAuthAvailable() {
        return false;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    public SSLContext createSSLContextInternal(List<String> negotiableProtocols) throws NoSuchAlgorithmException {
        return new JSSESSLContext(this.sslHostConfig.getSslProtocol());
    }

    private void initialise() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    try {
                        SSLContext context = new JSSESSLContext(this.sslHostConfig.getSslProtocol());
                        context.init(null, null, null);
                        String[] implementedProtocolsArray = context.getSupportedSSLParameters().getProtocols();
                        this.implementedProtocols = new HashSet(implementedProtocolsArray.length);
                        for (String protocol : implementedProtocolsArray) {
                            String protocolUpper = protocol.toUpperCase(Locale.ENGLISH);
                            if (!"SSLV2HELLO".equals(protocolUpper) && !"SSLV3".equals(protocolUpper) && protocolUpper.contains("SSL")) {
                                log.debug(sm.getString("jsseUtil.excludeProtocol", protocol));
                            } else {
                                this.implementedProtocols.add(protocol);
                            }
                        }
                        if (this.implementedProtocols.size() == 0) {
                            log.warn(sm.getString("jsseUtil.noDefaultProtocols"));
                        }
                        String[] implementedCipherSuiteArray = context.getSupportedSSLParameters().getCipherSuites();
                        if (JreVendor.IS_IBM_JVM) {
                            this.implementedCiphers = new HashSet(implementedCipherSuiteArray.length * 2);
                            for (String name : implementedCipherSuiteArray) {
                                this.implementedCiphers.add(name);
                                if (name.startsWith("SSL")) {
                                    this.implementedCiphers.add("TLS" + name.substring(3));
                                }
                            }
                        } else {
                            this.implementedCiphers = new HashSet(Arrays.asList(implementedCipherSuiteArray));
                        }
                        this.initialized = true;
                    } catch (KeyManagementException | NoSuchAlgorithmException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            }
        }
    }
}
