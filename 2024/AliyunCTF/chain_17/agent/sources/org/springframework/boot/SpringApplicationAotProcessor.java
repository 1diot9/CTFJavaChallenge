package org.springframework.boot;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Supplier;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.aot.AbstractAotProcessor;
import org.springframework.context.aot.ContextAotProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.ThrowingSupplier;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationAotProcessor.class */
public class SpringApplicationAotProcessor extends ContextAotProcessor {
    private final String[] applicationArgs;

    public SpringApplicationAotProcessor(Class<?> application, AbstractAotProcessor.Settings settings, String[] applicationArgs) {
        super(application, settings);
        this.applicationArgs = applicationArgs;
    }

    @Override // org.springframework.context.aot.ContextAotProcessor
    protected GenericApplicationContext prepareApplicationContext(Class<?> application) {
        return new AotProcessorHook(application).run(() -> {
            Method mainMethod = application.getMethod("main", String[].class);
            return ReflectionUtils.invokeMethod(mainMethod, null, this.applicationArgs);
        });
    }

    public static void main(String[] args) throws Exception {
        String[] strArr;
        Assert.isTrue(args.length >= 6, (Supplier<String>) () -> {
            return "Usage: " + SpringApplicationAotProcessor.class.getName() + " <applicationName> <sourceOutput> <resourceOutput> <classOutput> <groupId> <artifactId> <originalArgs...>";
        });
        Class<?> application = Class.forName(args[0]);
        AbstractAotProcessor.Settings settings = AbstractAotProcessor.Settings.builder().sourceOutput(Paths.get(args[1], new String[0])).resourceOutput(Paths.get(args[2], new String[0])).classOutput(Paths.get(args[3], new String[0])).groupId(StringUtils.hasText(args[4]) ? args[4] : "unspecified").artifactId(args[5]).build();
        if (args.length > 6) {
            strArr = (String[]) Arrays.copyOfRange(args, 6, args.length);
        } else {
            strArr = new String[0];
        }
        String[] applicationArgs = strArr;
        new SpringApplicationAotProcessor(application, settings, applicationArgs).process();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationAotProcessor$AotProcessorHook.class */
    private static final class AotProcessorHook implements SpringApplicationHook {
        private final Class<?> application;

        private AotProcessorHook(Class<?> application) {
            this.application = application;
        }

        @Override // org.springframework.boot.SpringApplicationHook
        public SpringApplicationRunListener getRunListener(SpringApplication application) {
            return new SpringApplicationRunListener() { // from class: org.springframework.boot.SpringApplicationAotProcessor.AotProcessorHook.1
                @Override // org.springframework.boot.SpringApplicationRunListener
                public void contextLoaded(ConfigurableApplicationContext context) {
                    throw new SpringApplication.AbandonedRunException(context);
                }
            };
        }

        private <T> GenericApplicationContext run(ThrowingSupplier<T> action) {
            try {
                SpringApplication.withHook(this, action);
                throw new IllegalStateException("No application context available after calling main method of '%s'. Does it run a SpringApplication?".formatted(this.application.getName()));
            } catch (SpringApplication.AbandonedRunException ex) {
                ApplicationContext context = ex.getApplicationContext();
                Assert.isInstanceOf((Class<?>) GenericApplicationContext.class, context, (Supplier<String>) () -> {
                    return "AOT processing requires a GenericApplicationContext but got a " + context.getClass().getName();
                });
                return (GenericApplicationContext) context;
            }
        }
    }
}
