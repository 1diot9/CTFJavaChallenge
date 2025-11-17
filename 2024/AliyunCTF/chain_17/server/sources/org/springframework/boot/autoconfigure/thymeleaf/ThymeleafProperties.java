package org.springframework.boot.autoconfigure.thymeleaf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "spring.thymeleaf")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafProperties.class */
public class ThymeleafProperties {
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    public static final String DEFAULT_PREFIX = "classpath:/templates/";
    public static final String DEFAULT_SUFFIX = ".html";
    private Integer templateResolverOrder;
    private String[] viewNames;
    private String[] excludedViewNames;
    private boolean enableSpringElCompiler;
    private boolean checkTemplate = true;
    private boolean checkTemplateLocation = true;
    private String prefix = "classpath:/templates/";
    private String suffix = DEFAULT_SUFFIX;
    private String mode = "HTML";
    private Charset encoding = DEFAULT_ENCODING;
    private boolean cache = true;
    private boolean renderHiddenMarkersBeforeCheckboxes = false;
    private boolean enabled = true;
    private final Servlet servlet = new Servlet();
    private final Reactive reactive = new Reactive();

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCheckTemplate() {
        return this.checkTemplate;
    }

    public void setCheckTemplate(boolean checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public boolean isCheckTemplateLocation() {
        return this.checkTemplateLocation;
    }

    public void setCheckTemplateLocation(boolean checkTemplateLocation) {
        this.checkTemplateLocation = checkTemplateLocation;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Charset getEncoding() {
        return this.encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public boolean isCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public Integer getTemplateResolverOrder() {
        return this.templateResolverOrder;
    }

    public void setTemplateResolverOrder(Integer templateResolverOrder) {
        this.templateResolverOrder = templateResolverOrder;
    }

    public String[] getExcludedViewNames() {
        return this.excludedViewNames;
    }

    public void setExcludedViewNames(String[] excludedViewNames) {
        this.excludedViewNames = excludedViewNames;
    }

    public String[] getViewNames() {
        return this.viewNames;
    }

    public void setViewNames(String[] viewNames) {
        this.viewNames = viewNames;
    }

    public boolean isEnableSpringElCompiler() {
        return this.enableSpringElCompiler;
    }

    public void setEnableSpringElCompiler(boolean enableSpringElCompiler) {
        this.enableSpringElCompiler = enableSpringElCompiler;
    }

    public boolean isRenderHiddenMarkersBeforeCheckboxes() {
        return this.renderHiddenMarkersBeforeCheckboxes;
    }

    public void setRenderHiddenMarkersBeforeCheckboxes(boolean renderHiddenMarkersBeforeCheckboxes) {
        this.renderHiddenMarkersBeforeCheckboxes = renderHiddenMarkersBeforeCheckboxes;
    }

    public Reactive getReactive() {
        return this.reactive;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafProperties$Servlet.class */
    public static class Servlet {
        private MimeType contentType = MimeType.valueOf("text/html");
        private boolean producePartialOutputWhileProcessing = true;

        public MimeType getContentType() {
            return this.contentType;
        }

        public void setContentType(MimeType contentType) {
            this.contentType = contentType;
        }

        public boolean isProducePartialOutputWhileProcessing() {
            return this.producePartialOutputWhileProcessing;
        }

        public void setProducePartialOutputWhileProcessing(boolean producePartialOutputWhileProcessing) {
            this.producePartialOutputWhileProcessing = producePartialOutputWhileProcessing;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafProperties$Reactive.class */
    public static class Reactive {
        private DataSize maxChunkSize = DataSize.ofBytes(0);
        private List<MediaType> mediaTypes;
        private String[] fullModeViewNames;
        private String[] chunkedModeViewNames;

        public List<MediaType> getMediaTypes() {
            return this.mediaTypes;
        }

        public void setMediaTypes(List<MediaType> mediaTypes) {
            this.mediaTypes = mediaTypes;
        }

        public DataSize getMaxChunkSize() {
            return this.maxChunkSize;
        }

        public void setMaxChunkSize(DataSize maxChunkSize) {
            this.maxChunkSize = maxChunkSize;
        }

        public String[] getFullModeViewNames() {
            return this.fullModeViewNames;
        }

        public void setFullModeViewNames(String[] fullModeViewNames) {
            this.fullModeViewNames = fullModeViewNames;
        }

        public String[] getChunkedModeViewNames() {
            return this.chunkedModeViewNames;
        }

        public void setChunkedModeViewNames(String[] chunkedModeViewNames) {
            this.chunkedModeViewNames = chunkedModeViewNames;
        }
    }
}
