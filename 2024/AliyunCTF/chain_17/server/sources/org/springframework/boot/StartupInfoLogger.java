package org.springframework.boot;

import java.util.concurrent.Callable;
import org.apache.commons.logging.Log;
import org.springframework.aot.AotDetector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.ApplicationContext;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/StartupInfoLogger.class */
public class StartupInfoLogger {
    private final Class<?> sourceClass;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StartupInfoLogger(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void logStarting(Log applicationLog) {
        Assert.notNull(applicationLog, "Log must not be null");
        applicationLog.info(LogMessage.of(this::getStartingMessage));
        applicationLog.debug(LogMessage.of(this::getRunningMessage));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void logStarted(Log applicationLog, SpringApplication.Startup startup) {
        if (applicationLog.isInfoEnabled()) {
            applicationLog.info(getStartedMessage(startup));
        }
    }

    private CharSequence getStartingMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Starting");
        appendAotMode(message);
        appendApplicationName(message);
        appendVersion(message, this.sourceClass);
        appendJavaVersion(message);
        appendPid(message);
        appendContext(message);
        return message;
    }

    private CharSequence getRunningMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Running with Spring Boot");
        appendVersion(message, getClass());
        message.append(", Spring");
        appendVersion(message, ApplicationContext.class);
        return message;
    }

    private CharSequence getStartedMessage(SpringApplication.Startup startup) {
        StringBuilder message = new StringBuilder();
        message.append(startup.action());
        appendApplicationName(message);
        message.append(" in ");
        message.append(startup.timeTakenToStarted().toMillis() / 1000.0d);
        message.append(" seconds");
        Long uptimeMs = startup.processUptime();
        if (uptimeMs != null) {
            double uptime = uptimeMs.longValue() / 1000.0d;
            message.append(" (process running for ").append(uptime).append(")");
        }
        return message;
    }

    private void appendAotMode(StringBuilder message) {
        append(message, "", () -> {
            if (AotDetector.useGeneratedArtifacts()) {
                return "AOT-processed";
            }
            return null;
        });
    }

    private void appendApplicationName(StringBuilder message) {
        append(message, "", () -> {
            return this.sourceClass != null ? ClassUtils.getShortName(this.sourceClass) : "application";
        });
    }

    private void appendVersion(StringBuilder message, Class<?> source) {
        append(message, "v", () -> {
            return source.getPackage().getImplementationVersion();
        });
    }

    private void appendPid(StringBuilder message) {
        append(message, "with PID ", ApplicationPid::new);
    }

    private void appendContext(StringBuilder message) {
        StringBuilder context = new StringBuilder();
        ApplicationHome home = new ApplicationHome(this.sourceClass);
        if (home.getSource() != null) {
            context.append(home.getSource().getAbsolutePath());
        }
        append(context, "started by ", () -> {
            return System.getProperty("user.name");
        });
        append(context, "in ", () -> {
            return System.getProperty("user.dir");
        });
        if (!context.isEmpty()) {
            message.append(" (");
            message.append((CharSequence) context);
            message.append(")");
        }
    }

    private void appendJavaVersion(StringBuilder message) {
        append(message, "using Java ", () -> {
            return System.getProperty("java.version");
        });
    }

    private void append(StringBuilder message, String prefix, Callable<Object> call) {
        append(message, prefix, call, "");
    }

    private void append(StringBuilder message, String prefix, Callable<Object> call, String defaultValue) {
        Object result = callIfPossible(call);
        String value = result != null ? result.toString() : null;
        if (!StringUtils.hasLength(value)) {
            value = defaultValue;
        }
        if (StringUtils.hasLength(value)) {
            message.append(!message.isEmpty() ? " " : "");
            message.append(prefix);
            message.append(value);
        }
    }

    private Object callIfPossible(Callable<Object> call) {
        try {
            return call.call();
        } catch (Exception e) {
            return null;
        }
    }
}
