package org.springframework.boot.autoconfigure.info;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "spring.info")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/info/ProjectInfoProperties.class */
public class ProjectInfoProperties {
    private final Build build = new Build();
    private final Git git = new Git();

    public Build getBuild() {
        return this.build;
    }

    public Git getGit() {
        return this.git;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/info/ProjectInfoProperties$Build.class */
    public static class Build {
        private Resource location = new ClassPathResource("META-INF/build-info.properties");
        private Charset encoding = StandardCharsets.UTF_8;

        public Resource getLocation() {
            return this.location;
        }

        public void setLocation(Resource location) {
            this.location = location;
        }

        public Charset getEncoding() {
            return this.encoding;
        }

        public void setEncoding(Charset encoding) {
            this.encoding = encoding;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/info/ProjectInfoProperties$Git.class */
    public static class Git {
        private Resource location = new ClassPathResource("git.properties");
        private Charset encoding = StandardCharsets.UTF_8;

        public Resource getLocation() {
            return this.location;
        }

        public void setLocation(Resource location) {
            this.location = location;
        }

        public Charset getEncoding() {
            return this.encoding;
        }

        public void setEncoding(Charset encoding) {
            this.encoding = encoding;
        }
    }
}
