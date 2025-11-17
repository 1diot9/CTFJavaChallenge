package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StringCollectionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/ssl/SSLParametersConfiguration.class */
public class SSLParametersConfiguration extends ContextAwareBase {
    private String includedProtocols;
    private String excludedProtocols;
    private String includedCipherSuites;
    private String excludedCipherSuites;
    private Boolean needClientAuth;
    private Boolean wantClientAuth;
    private String[] enabledProtocols;
    private String[] enabledCipherSuites;
    private Boolean hostnameVerification;

    public void configure(SSLConfigurable socket) {
        socket.setEnabledProtocols(enabledProtocols(socket.getSupportedProtocols(), socket.getDefaultProtocols()));
        socket.setEnabledCipherSuites(enabledCipherSuites(socket.getSupportedCipherSuites(), socket.getDefaultCipherSuites()));
        if (isNeedClientAuth() != null) {
            socket.setNeedClientAuth(isNeedClientAuth().booleanValue());
        }
        if (isWantClientAuth() != null) {
            socket.setWantClientAuth(isWantClientAuth().booleanValue());
        }
        if (this.hostnameVerification != null) {
            addInfo("hostnameVerification=" + this.hostnameVerification);
            socket.setHostnameVerification(this.hostnameVerification.booleanValue());
        }
    }

    public boolean getHostnameVerification() {
        if (this.hostnameVerification == null) {
            return false;
        }
        return this.hostnameVerification.booleanValue();
    }

    public void setHostnameVerification(boolean hostnameVerification) {
        this.hostnameVerification = Boolean.valueOf(hostnameVerification);
    }

    private String[] enabledProtocols(String[] supportedProtocols, String[] defaultProtocols) {
        if (this.enabledProtocols == null) {
            if (OptionHelper.isNullOrEmpty(getIncludedProtocols()) && OptionHelper.isNullOrEmpty(getExcludedProtocols())) {
                this.enabledProtocols = (String[]) Arrays.copyOf(defaultProtocols, defaultProtocols.length);
            } else {
                this.enabledProtocols = includedStrings(supportedProtocols, getIncludedProtocols(), getExcludedProtocols());
            }
            for (String protocol : this.enabledProtocols) {
                addInfo("enabled protocol: " + protocol);
            }
        }
        return this.enabledProtocols;
    }

    private String[] enabledCipherSuites(String[] supportedCipherSuites, String[] defaultCipherSuites) {
        if (this.enabledCipherSuites == null) {
            if (OptionHelper.isNullOrEmpty(getIncludedCipherSuites()) && OptionHelper.isNullOrEmpty(getExcludedCipherSuites())) {
                this.enabledCipherSuites = (String[]) Arrays.copyOf(defaultCipherSuites, defaultCipherSuites.length);
            } else {
                this.enabledCipherSuites = includedStrings(supportedCipherSuites, getIncludedCipherSuites(), getExcludedCipherSuites());
            }
            for (String cipherSuite : this.enabledCipherSuites) {
                addInfo("enabled cipher suite: " + cipherSuite);
            }
        }
        return this.enabledCipherSuites;
    }

    private String[] includedStrings(String[] defaults, String included, String excluded) {
        List<String> values = new ArrayList<>(defaults.length);
        values.addAll(Arrays.asList(defaults));
        if (included != null) {
            StringCollectionUtil.retainMatching(values, stringToArray(included));
        }
        if (excluded != null) {
            StringCollectionUtil.removeMatching(values, stringToArray(excluded));
        }
        return (String[]) values.toArray(new String[values.size()]);
    }

    private String[] stringToArray(String s) {
        return s.split("\\s*,\\s*");
    }

    public String getIncludedProtocols() {
        return this.includedProtocols;
    }

    public void setIncludedProtocols(String protocols) {
        this.includedProtocols = protocols;
    }

    public String getExcludedProtocols() {
        return this.excludedProtocols;
    }

    public void setExcludedProtocols(String protocols) {
        this.excludedProtocols = protocols;
    }

    public String getIncludedCipherSuites() {
        return this.includedCipherSuites;
    }

    public void setIncludedCipherSuites(String cipherSuites) {
        this.includedCipherSuites = cipherSuites;
    }

    public String getExcludedCipherSuites() {
        return this.excludedCipherSuites;
    }

    public void setExcludedCipherSuites(String cipherSuites) {
        this.excludedCipherSuites = cipherSuites;
    }

    public Boolean isNeedClientAuth() {
        return this.needClientAuth;
    }

    public void setNeedClientAuth(Boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    public Boolean isWantClientAuth() {
        return this.wantClientAuth;
    }

    public void setWantClientAuth(Boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }
}
