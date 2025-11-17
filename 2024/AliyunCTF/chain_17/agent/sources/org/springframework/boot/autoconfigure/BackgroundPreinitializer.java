package org.springframework.boot.autoconfigure;

import jakarta.validation.Configuration;
import jakarta.validation.Validation;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.authenticator.NonLoginAuthenticator;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.NativeDetector;
import org.springframework.core.Ordered;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/BackgroundPreinitializer.class */
public class BackgroundPreinitializer implements ApplicationListener<SpringApplicationEvent>, Ordered {
    public static final String IGNORE_BACKGROUNDPREINITIALIZER_PROPERTY_NAME = "spring.backgroundpreinitializer.ignore";
    private static final AtomicBoolean preinitializationStarted = new AtomicBoolean();
    private static final CountDownLatch preinitializationComplete = new CountDownLatch(1);
    private static final boolean ENABLED;

    static {
        ENABLED = !Boolean.getBoolean(IGNORE_BACKGROUNDPREINITIALIZER_PROPERTY_NAME) && Runtime.getRuntime().availableProcessors() > 1;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return -2147483627;
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(SpringApplicationEvent event) {
        if (!ENABLED || NativeDetector.inNativeImage()) {
            return;
        }
        if ((event instanceof ApplicationEnvironmentPreparedEvent) && preinitializationStarted.compareAndSet(false, true)) {
            performPreinitialization();
        }
        if (((event instanceof ApplicationReadyEvent) || (event instanceof ApplicationFailedEvent)) && preinitializationStarted.get()) {
            try {
                preinitializationComplete.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void performPreinitialization() {
        try {
            Thread thread = new Thread(new Runnable() { // from class: org.springframework.boot.autoconfigure.BackgroundPreinitializer.1
                @Override // java.lang.Runnable
                public void run() {
                    runSafely(new ConversionServiceInitializer());
                    runSafely(new ValidationInitializer());
                    if (!runSafely(new MessageConverterInitializer())) {
                        runSafely(new JacksonInitializer());
                    }
                    runSafely(new CharsetInitializer());
                    runSafely(new TomcatInitializer());
                    runSafely(new JdkInitializer());
                    BackgroundPreinitializer.preinitializationComplete.countDown();
                }

                boolean runSafely(Runnable runnable) {
                    try {
                        runnable.run();
                        return true;
                    } catch (Throwable th) {
                        return false;
                    }
                }
            }, "background-preinit");
            thread.start();
        } catch (Exception e) {
            preinitializationComplete.countDown();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/BackgroundPreinitializer$MessageConverterInitializer.class */
    private static final class MessageConverterInitializer implements Runnable {
        private MessageConverterInitializer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            new AllEncompassingFormHttpMessageConverter();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/BackgroundPreinitializer$ValidationInitializer.class */
    private static final class ValidationInitializer implements Runnable {
        private ValidationInitializer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Configuration<?> configuration = Validation.byDefaultProvider().configure();
            configuration.buildValidatorFactory().getValidator();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/BackgroundPreinitializer$JacksonInitializer.class */
    private static final class JacksonInitializer implements Runnable {
        private JacksonInitializer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Jackson2ObjectMapperBuilder.json().build();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/BackgroundPreinitializer$ConversionServiceInitializer.class */
    private static final class ConversionServiceInitializer implements Runnable {
        private ConversionServiceInitializer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            new DefaultFormattingConversionService();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/BackgroundPreinitializer$CharsetInitializer.class */
    private static final class CharsetInitializer implements Runnable {
        private CharsetInitializer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            StandardCharsets.UTF_8.name();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/BackgroundPreinitializer$TomcatInitializer.class */
    private static final class TomcatInitializer implements Runnable {
        private TomcatInitializer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            new Rfc6265CookieProcessor();
            new NonLoginAuthenticator();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/BackgroundPreinitializer$JdkInitializer.class */
    private static final class JdkInitializer implements Runnable {
        private JdkInitializer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ZoneId.systemDefault();
        }
    }
}
