package org.springframework.boot.context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.boot.system.SystemProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/ApplicationPidFileWriter.class */
public class ApplicationPidFileWriter implements ApplicationListener<SpringApplicationEvent>, Ordered {
    private static final Log logger = LogFactory.getLog((Class<?>) ApplicationPidFileWriter.class);
    private static final String DEFAULT_FILE_NAME = "application.pid";
    private static final List<Property> FILE_PROPERTIES;
    private static final List<Property> FAIL_ON_WRITE_ERROR_PROPERTIES;
    private static final AtomicBoolean created;
    private int order;
    private final File file;
    private Class<? extends SpringApplicationEvent> triggerEventType;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/ApplicationPidFileWriter$Property.class */
    public interface Property {
        String getValue(SpringApplicationEvent event);
    }

    static {
        List<Property> properties = new ArrayList<>();
        properties.add(new SpringProperty("spring.pid.", "file"));
        properties.add(new SpringProperty("spring.", "pidfile"));
        properties.add(new SystemProperty("PIDFILE"));
        FILE_PROPERTIES = Collections.unmodifiableList(properties);
        List<Property> properties2 = new ArrayList<>();
        properties2.add(new SpringProperty("spring.pid.", "fail-on-write-error"));
        properties2.add(new SystemProperty("PID_FAIL_ON_WRITE_ERROR"));
        FAIL_ON_WRITE_ERROR_PROPERTIES = Collections.unmodifiableList(properties2);
        created = new AtomicBoolean();
    }

    public ApplicationPidFileWriter() {
        this(new File(DEFAULT_FILE_NAME));
    }

    public ApplicationPidFileWriter(String filename) {
        this(new File(filename));
    }

    public ApplicationPidFileWriter(File file) {
        this.order = -2147483635;
        this.triggerEventType = ApplicationPreparedEvent.class;
        Assert.notNull(file, "File must not be null");
        this.file = file;
    }

    public void setTriggerEventType(Class<? extends SpringApplicationEvent> triggerEventType) {
        Assert.notNull(triggerEventType, "Trigger event type must not be null");
        this.triggerEventType = triggerEventType;
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(SpringApplicationEvent event) {
        if (this.triggerEventType.isInstance(event) && created.compareAndSet(false, true)) {
            try {
                writePidFile(event);
            } catch (Exception ex) {
                String message = String.format("Cannot create pid file %s", this.file);
                if (failOnWriteError(event)) {
                    throw new IllegalStateException(message, ex);
                }
                logger.warn(message, ex);
            }
        }
    }

    private void writePidFile(SpringApplicationEvent event) throws IOException {
        File pidFile = this.file;
        String override = getProperty(event, FILE_PROPERTIES);
        if (override != null) {
            pidFile = new File(override);
        }
        new ApplicationPid().write(pidFile);
        pidFile.deleteOnExit();
    }

    private boolean failOnWriteError(SpringApplicationEvent event) {
        String value = getProperty(event, FAIL_ON_WRITE_ERROR_PROPERTIES);
        return Boolean.parseBoolean(value);
    }

    private String getProperty(SpringApplicationEvent event, List<Property> candidates) {
        for (Property candidate : candidates) {
            String value = candidate.getValue(event);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    protected static void reset() {
        created.set(false);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/ApplicationPidFileWriter$SpringProperty.class */
    private static class SpringProperty implements Property {
        private final String prefix;
        private final String key;

        SpringProperty(String prefix, String key) {
            this.prefix = prefix;
            this.key = key;
        }

        @Override // org.springframework.boot.context.ApplicationPidFileWriter.Property
        public String getValue(SpringApplicationEvent event) {
            Environment environment = getEnvironment(event);
            if (environment == null) {
                return null;
            }
            return environment.getProperty(this.prefix + this.key);
        }

        private Environment getEnvironment(SpringApplicationEvent event) {
            if (event instanceof ApplicationEnvironmentPreparedEvent) {
                ApplicationEnvironmentPreparedEvent environmentPreparedEvent = (ApplicationEnvironmentPreparedEvent) event;
                return environmentPreparedEvent.getEnvironment();
            }
            if (event instanceof ApplicationPreparedEvent) {
                ApplicationPreparedEvent preparedEvent = (ApplicationPreparedEvent) event;
                return preparedEvent.getApplicationContext().getEnvironment();
            }
            if (event instanceof ApplicationReadyEvent) {
                ApplicationReadyEvent readyEvent = (ApplicationReadyEvent) event;
                return readyEvent.getApplicationContext().getEnvironment();
            }
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/ApplicationPidFileWriter$SystemProperty.class */
    private static class SystemProperty implements Property {
        private final String[] properties;

        SystemProperty(String name) {
            this.properties = new String[]{name.toUpperCase(Locale.ENGLISH), name.toLowerCase(Locale.ENGLISH)};
        }

        @Override // org.springframework.boot.context.ApplicationPidFileWriter.Property
        public String getValue(SpringApplicationEvent event) {
            return SystemProperties.get(this.properties);
        }
    }
}
