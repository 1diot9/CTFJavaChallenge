package org.springframework.boot.autoconfigure.ssl;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ssl")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslProperties.class */
public class SslProperties {
    private final Bundles bundle = new Bundles();

    public Bundles getBundle() {
        return this.bundle;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslProperties$Bundles.class */
    public static class Bundles {
        private final Map<String, PemSslBundleProperties> pem = new LinkedHashMap();
        private final Map<String, JksSslBundleProperties> jks = new LinkedHashMap();
        private final Watch watch = new Watch();

        public Map<String, PemSslBundleProperties> getPem() {
            return this.pem;
        }

        public Map<String, JksSslBundleProperties> getJks() {
            return this.jks;
        }

        public Watch getWatch() {
            return this.watch;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslProperties$Bundles$Watch.class */
        public static class Watch {
            private final File file = new File();

            public File getFile() {
                return this.file;
            }

            /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslProperties$Bundles$Watch$File.class */
            public static class File {
                private Duration quietPeriod = Duration.ofSeconds(10);

                public Duration getQuietPeriod() {
                    return this.quietPeriod;
                }

                public void setQuietPeriod(Duration quietPeriod) {
                    this.quietPeriod = quietPeriod;
                }
            }
        }
    }
}
