package org.springframework.boot;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/CommandLineRunner.class */
public interface CommandLineRunner extends Runner {
    void run(String... args) throws Exception;
}
