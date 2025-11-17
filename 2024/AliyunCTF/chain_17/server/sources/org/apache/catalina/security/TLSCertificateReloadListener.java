package org.apache.catalina.security;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/security/TLSCertificateReloadListener.class */
public class TLSCertificateReloadListener implements LifecycleListener {
    private static final Log log = LogFactory.getLog((Class<?>) TLSCertificateReloadListener.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) TLSCertificateReloadListener.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private int checkPeriod = 86400;
    private int daysBefore = 14;
    private Calendar nextCheck = Calendar.getInstance();

    public int getCheckPeriod() {
        return this.checkPeriod;
    }

    public void setCheckPeriod(int checkPeriod) {
        this.checkPeriod = checkPeriod;
    }

    public int getDaysBefore() {
        return this.daysBefore;
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals(Lifecycle.PERIODIC_EVENT)) {
            if (event.getSource() instanceof Server) {
                Server server = (Server) event.getSource();
                checkCertificatesForRenewal(server);
                return;
            }
            return;
        }
        if (event.getType().equals(Lifecycle.BEFORE_INIT_EVENT) && !(event.getLifecycle() instanceof Server)) {
            log.warn(sm.getString("listener.notServer", event.getLifecycle().getClass().getSimpleName()));
        }
    }

    private void checkCertificatesForRenewal(Server server) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.compareTo(this.nextCheck) > 0) {
            this.nextCheck.add(13, getCheckPeriod());
            calendar.add(5, getDaysBefore());
            Service[] services = server.findServices();
            for (Service service : services) {
                Connector[] connectors = service.findConnectors();
                for (Connector connector : connectors) {
                    SSLHostConfig[] sslHostConfigs = connector.findSslHostConfigs();
                    for (SSLHostConfig sslHostConfig : sslHostConfigs) {
                        if (!sslHostConfig.certificatesExpiringBefore(calendar.getTime()).isEmpty()) {
                            try {
                                connector.getProtocolHandler().addSslHostConfig(sslHostConfig, true);
                                Set<X509Certificate> expiringCertificates = sslHostConfig.certificatesExpiringBefore(calendar.getTime());
                                log.info(sm.getString("tlsCertRenewalListener.reloadSuccess", connector, sslHostConfig.getHostName()));
                                if (!expiringCertificates.isEmpty()) {
                                    for (X509Certificate expiringCertificate : expiringCertificates) {
                                        log.warn(sm.getString("tlsCertRenewalListener.notRenewed", connector, sslHostConfig.getHostName(), expiringCertificate.getSubjectX500Principal().getName(), this.dateFormat.format(expiringCertificate.getNotAfter())));
                                    }
                                }
                            } catch (IllegalArgumentException iae) {
                                log.error(sm.getString("tlsCertRenewalListener.reloadFailed", connector, sslHostConfig.getHostName()), iae);
                            }
                        }
                    }
                }
            }
        }
    }
}
