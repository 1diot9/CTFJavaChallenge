package org.springframework.boot;

import cn.hutool.core.util.CharsetUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationBannerPrinter.class */
public class SpringApplicationBannerPrinter {
    static final String BANNER_LOCATION_PROPERTY = "spring.banner.location";
    static final String DEFAULT_BANNER_LOCATION = "banner.txt";
    private static final Banner DEFAULT_BANNER = new SpringBootBanner();
    private final ResourceLoader resourceLoader;
    private final Banner fallbackBanner;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringApplicationBannerPrinter(ResourceLoader resourceLoader, Banner fallbackBanner) {
        this.resourceLoader = resourceLoader;
        this.fallbackBanner = fallbackBanner;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Banner print(Environment environment, Class<?> sourceClass, Log logger) {
        Banner banner = getBanner(environment);
        try {
            logger.info(createStringFromBanner(banner, environment, sourceClass));
        } catch (UnsupportedEncodingException ex) {
            logger.warn("Failed to create String for banner", ex);
        }
        return new PrintedBanner(banner, sourceClass);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Banner print(Environment environment, Class<?> sourceClass, PrintStream out) {
        Banner banner = getBanner(environment);
        banner.printBanner(environment, sourceClass, out);
        return new PrintedBanner(banner, sourceClass);
    }

    private Banner getBanner(Environment environment) {
        Banner textBanner = getTextBanner(environment);
        if (textBanner != null) {
            return textBanner;
        }
        if (this.fallbackBanner != null) {
            return this.fallbackBanner;
        }
        return DEFAULT_BANNER;
    }

    private Banner getTextBanner(Environment environment) {
        String location = environment.getProperty("spring.banner.location", "banner.txt");
        Resource resource = this.resourceLoader.getResource(location);
        try {
            if (resource.exists() && !resource.getURL().toExternalForm().contains("liquibase-core")) {
                return new ResourceBanner(resource);
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private String createStringFromBanner(Banner banner, Environment environment, Class<?> mainApplicationClass) throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        banner.printBanner(environment, mainApplicationClass, new PrintStream(baos));
        String charset = environment.getProperty("spring.banner.charset", CharsetUtil.UTF_8);
        return baos.toString(charset);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationBannerPrinter$PrintedBanner.class */
    public static class PrintedBanner implements Banner {
        private final Banner banner;
        private final Class<?> sourceClass;

        PrintedBanner(Banner banner, Class<?> sourceClass) {
            this.banner = banner;
            this.sourceClass = sourceClass;
        }

        @Override // org.springframework.boot.Banner
        public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
            this.banner.printBanner(environment, sourceClass != null ? sourceClass : this.sourceClass, out);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationBannerPrinter$SpringApplicationBannerPrinterRuntimeHints.class */
    static class SpringApplicationBannerPrinterRuntimeHints implements RuntimeHintsRegistrar {
        SpringApplicationBannerPrinterRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("banner.txt");
        }
    }
}
