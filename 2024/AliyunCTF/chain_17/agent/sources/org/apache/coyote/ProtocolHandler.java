package org.apache.coyote;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.coyote.ajp.AjpNioProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/ProtocolHandler.class */
public interface ProtocolHandler {
    Adapter getAdapter();

    void setAdapter(Adapter adapter);

    Executor getExecutor();

    void setExecutor(Executor executor);

    ScheduledExecutorService getUtilityExecutor();

    void setUtilityExecutor(ScheduledExecutorService scheduledExecutorService);

    void init() throws Exception;

    void start() throws Exception;

    void pause() throws Exception;

    void resume() throws Exception;

    void stop() throws Exception;

    void destroy() throws Exception;

    void closeServerSocketGraceful();

    long awaitConnectionsClose(long j);

    boolean isSendfileSupported();

    void addSslHostConfig(SSLHostConfig sSLHostConfig);

    void addSslHostConfig(SSLHostConfig sSLHostConfig, boolean z);

    SSLHostConfig[] findSslHostConfigs();

    void addUpgradeProtocol(UpgradeProtocol upgradeProtocol);

    UpgradeProtocol[] findUpgradeProtocols();

    default int getDesiredBufferSize() {
        return -1;
    }

    default String getId() {
        return null;
    }

    static ProtocolHandler create(String protocol) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        if (protocol == null || "HTTP/1.1".equals(protocol) || Http11NioProtocol.class.getName().equals(protocol)) {
            return new Http11NioProtocol();
        }
        if ("AJP/1.3".equals(protocol) || AjpNioProtocol.class.getName().equals(protocol)) {
            return new AjpNioProtocol();
        }
        Class<?> clazz = Class.forName(protocol);
        return (ProtocolHandler) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
    }
}
