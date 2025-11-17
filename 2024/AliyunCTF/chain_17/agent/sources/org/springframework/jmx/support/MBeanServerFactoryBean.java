package org.springframework.jmx.support;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/support/MBeanServerFactoryBean.class */
public class MBeanServerFactoryBean implements FactoryBean<MBeanServer>, InitializingBean, DisposableBean {

    @Nullable
    private String agentId;

    @Nullable
    private String defaultDomain;

    @Nullable
    private MBeanServer server;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean locateExistingServerIfPossible = false;
    private boolean registerWithFactory = true;
    private boolean newlyRegistered = false;

    public void setLocateExistingServerIfPossible(boolean locateExistingServerIfPossible) {
        this.locateExistingServerIfPossible = locateExistingServerIfPossible;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }

    public void setRegisterWithFactory(boolean registerWithFactory) {
        this.registerWithFactory = registerWithFactory;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws MBeanServerNotFoundException {
        if (this.locateExistingServerIfPossible || this.agentId != null) {
            try {
                this.server = locateMBeanServer(this.agentId);
            } catch (MBeanServerNotFoundException ex) {
                if (this.agentId != null) {
                    throw ex;
                }
                this.logger.debug("No existing MBeanServer found - creating new one");
            }
        }
        if (this.server == null) {
            this.server = createMBeanServer(this.defaultDomain, this.registerWithFactory);
            this.newlyRegistered = this.registerWithFactory;
        }
    }

    protected MBeanServer locateMBeanServer(@Nullable String agentId) throws MBeanServerNotFoundException {
        return JmxUtils.locateMBeanServer(agentId);
    }

    protected MBeanServer createMBeanServer(@Nullable String defaultDomain, boolean registerWithFactory) {
        if (registerWithFactory) {
            return MBeanServerFactory.createMBeanServer(defaultDomain);
        }
        return MBeanServerFactory.newMBeanServer(defaultDomain);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public MBeanServer getObject() {
        return this.server;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends MBeanServer> getObjectType() {
        return this.server != null ? this.server.getClass() : MBeanServer.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.newlyRegistered) {
            MBeanServerFactory.releaseMBeanServer(this.server);
        }
    }
}
