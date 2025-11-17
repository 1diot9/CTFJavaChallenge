package org.springframework.boot;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ApplicationRunner.class */
public interface ApplicationRunner extends Runner {
    void run(ApplicationArguments args) throws Exception;
}
