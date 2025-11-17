package org.apache.catalina.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.coyote.ActionCode;
import org.apache.coyote.UpgradeProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.h2.security.auth.impl.JaasCredentialsValidator;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/SSLAuthenticator.class */
public class SSLAuthenticator extends AuthenticatorBase {
    private final Log log = LogFactory.getLog((Class<?>) SSLAuthenticator.class);

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (checkForCachedAuthentication(request, response, false)) {
            return true;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug(" Looking up certificates");
        }
        X509Certificate[] certs = getRequestCertificates(request);
        if (certs == null || certs.length < 1) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("  No certificates included with this request");
            }
            response.sendError(401, sm.getString("authenticator.certificates"));
            return false;
        }
        Principal principal = this.context.getRealm().authenticate(certs);
        if (principal == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("  Realm.authenticate() returned false");
            }
            response.sendError(401, sm.getString("authenticator.unauthorized"));
            return false;
        }
        register(request, response, principal, HttpServletRequest.CLIENT_CERT_AUTH, null, null);
        return true;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected String getAuthMethod() {
        return HttpServletRequest.CLIENT_CERT_AUTH;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean isPreemptiveAuthPossible(Request request) {
        X509Certificate[] certs = getRequestCertificates(request);
        return certs != null && certs.length > 0;
    }

    protected X509Certificate[] getRequestCertificates(Request request) throws IllegalStateException {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate");
        if (certs == null || certs.length < 1) {
            try {
                request.getCoyoteRequest().action(ActionCode.REQ_SSL_CERTIFICATE, null);
                certs = (X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate");
            } catch (IllegalStateException e) {
            }
        }
        return certs;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.authenticator.AuthenticatorBase, org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        Container container = getContainer();
        if (!(container instanceof Context)) {
            return;
        }
        Context context = (Context) container;
        Container container2 = context.getParent();
        if (!(container2 instanceof Host)) {
            return;
        }
        Host host = (Host) container2;
        Container container3 = host.getParent();
        if (!(container3 instanceof Engine)) {
            return;
        }
        Engine engine = (Engine) container3;
        Connector[] connectors = engine.getService().findConnectors();
        for (Connector connector : connectors) {
            UpgradeProtocol[] upgradeProtocols = connector.findUpgradeProtocols();
            int length = upgradeProtocols.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                UpgradeProtocol upgradeProtocol = upgradeProtocols[i];
                if (!JaasCredentialsValidator.DEFAULT_APPNAME.equals(upgradeProtocol.getAlpnName())) {
                    i++;
                } else {
                    this.log.warn(sm.getString("sslAuthenticatorValve.http2", context.getName(), host.getName(), connector));
                    break;
                }
            }
            SSLHostConfig[] sslHostConfigs = connector.findSslHostConfigs();
            for (SSLHostConfig sslHostConfig : sslHostConfigs) {
                if (!sslHostConfig.isTls13RenegotiationAvailable()) {
                    String[] enabledProtocols = sslHostConfig.getEnabledProtocols();
                    if (enabledProtocols == null) {
                        enabledProtocols = (String[]) sslHostConfig.getProtocols().toArray(new String[0]);
                    }
                    for (String enbabledProtocol : enabledProtocols) {
                        if (org.apache.tomcat.util.net.Constants.SSL_PROTO_TLSv1_3.equals(enbabledProtocol)) {
                            this.log.warn(sm.getString("sslAuthenticatorValve.tls13", context.getName(), host.getName(), connector));
                        }
                    }
                }
            }
        }
    }
}
