package org.springframework.web.servlet.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/AbstractVersionStrategy.class */
public abstract class AbstractVersionStrategy implements VersionStrategy {
    protected final Log logger = LogFactory.getLog(getClass());
    private final VersionPathStrategy pathStrategy;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractVersionStrategy(VersionPathStrategy pathStrategy) {
        Assert.notNull(pathStrategy, "VersionPathStrategy is required");
        this.pathStrategy = pathStrategy;
    }

    public VersionPathStrategy getVersionPathStrategy() {
        return this.pathStrategy;
    }

    @Override // org.springframework.web.servlet.resource.VersionPathStrategy
    @Nullable
    public String extractVersion(String requestPath) {
        return this.pathStrategy.extractVersion(requestPath);
    }

    @Override // org.springframework.web.servlet.resource.VersionPathStrategy
    public String removeVersion(String requestPath, String version) {
        return this.pathStrategy.removeVersion(requestPath, version);
    }

    @Override // org.springframework.web.servlet.resource.VersionPathStrategy
    public String addVersion(String requestPath, String version) {
        return this.pathStrategy.addVersion(requestPath, version);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/AbstractVersionStrategy$PrefixVersionPathStrategy.class */
    protected static class PrefixVersionPathStrategy implements VersionPathStrategy {
        private final String prefix;

        public PrefixVersionPathStrategy(String version) {
            Assert.hasText(version, "Version must not be empty");
            this.prefix = version;
        }

        @Override // org.springframework.web.servlet.resource.VersionPathStrategy
        @Nullable
        public String extractVersion(String requestPath) {
            if (requestPath.startsWith(this.prefix)) {
                return this.prefix;
            }
            return null;
        }

        @Override // org.springframework.web.servlet.resource.VersionPathStrategy
        public String removeVersion(String requestPath, String version) {
            return requestPath.substring(this.prefix.length());
        }

        @Override // org.springframework.web.servlet.resource.VersionPathStrategy
        public String addVersion(String path, String version) {
            if (path.startsWith(".")) {
                return path;
            }
            return (this.prefix.endsWith("/") || path.startsWith("/")) ? this.prefix + path : this.prefix + "/" + path;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/AbstractVersionStrategy$FileNameVersionPathStrategy.class */
    protected static class FileNameVersionPathStrategy implements VersionPathStrategy {
        private static final Pattern pattern = Pattern.compile("-(\\S*)\\.");

        @Override // org.springframework.web.servlet.resource.VersionPathStrategy
        @Nullable
        public String extractVersion(String requestPath) {
            Matcher matcher = pattern.matcher(requestPath);
            if (matcher.find()) {
                String match = matcher.group(1);
                return match.contains("-") ? match.substring(match.lastIndexOf(45) + 1) : match;
            }
            return null;
        }

        @Override // org.springframework.web.servlet.resource.VersionPathStrategy
        public String removeVersion(String requestPath, String version) {
            return StringUtils.delete(requestPath, "-" + version);
        }

        @Override // org.springframework.web.servlet.resource.VersionPathStrategy
        public String addVersion(String requestPath, String version) {
            String baseFilename = StringUtils.stripFilenameExtension(requestPath);
            String extension = StringUtils.getFilenameExtension(requestPath);
            return baseFilename + "-" + version + "." + extension;
        }
    }
}
