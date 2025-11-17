package ch.qos.logback.core.net.ssl;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/ssl/SSLConfiguration.class */
public class SSLConfiguration extends SSLContextFactoryBean {
    private SSLParametersConfiguration parameters;

    public SSLParametersConfiguration getParameters() {
        if (this.parameters == null) {
            this.parameters = new SSLParametersConfiguration();
        }
        return this.parameters;
    }

    public void setParameters(SSLParametersConfiguration parameters) {
        this.parameters = parameters;
    }
}
