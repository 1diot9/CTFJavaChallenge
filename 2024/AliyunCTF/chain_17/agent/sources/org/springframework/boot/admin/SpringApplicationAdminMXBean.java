package org.springframework.boot.admin;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/admin/SpringApplicationAdminMXBean.class */
public interface SpringApplicationAdminMXBean {
    boolean isReady();

    boolean isEmbeddedWebApplication();

    String getProperty(String key);

    void shutdown();
}
