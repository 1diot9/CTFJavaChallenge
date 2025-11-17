package org.springframework.http.client.reactive;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JdkHttpClientResourceFactory.class */
public class JdkHttpClientResourceFactory implements InitializingBean, DisposableBean {

    @Nullable
    private Executor executor;
    private String threadPrefix = "jdk-http";

    public void setExecutor(@Nullable Executor executor) {
        this.executor = executor;
    }

    @Nullable
    public Executor getExecutor() {
        return this.executor;
    }

    public void setThreadPrefix(String threadPrefix) {
        Assert.notNull(threadPrefix, "Thread prefix is required");
        this.threadPrefix = threadPrefix;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (this.executor == null) {
            String name = this.threadPrefix + "@" + Integer.toHexString(hashCode());
            this.executor = Executors.newCachedThreadPool(new CustomizableThreadFactory(name));
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        Executor executor = this.executor;
        if (executor instanceof ExecutorService) {
            ExecutorService executorService = (ExecutorService) executor;
            executorService.shutdown();
        }
    }
}
