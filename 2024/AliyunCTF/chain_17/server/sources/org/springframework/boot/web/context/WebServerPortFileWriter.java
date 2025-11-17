package org.springframework.boot.web.context;

import java.io.File;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.system.SystemProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/context/WebServerPortFileWriter.class */
public class WebServerPortFileWriter implements ApplicationListener<WebServerInitializedEvent> {
    private static final String DEFAULT_FILE_NAME = "application.port";
    private static final String[] PROPERTY_VARIABLES = {"PORTFILE", "portfile"};
    private static final Log logger = LogFactory.getLog((Class<?>) WebServerPortFileWriter.class);
    private final File file;

    public WebServerPortFileWriter() {
        this(new File(DEFAULT_FILE_NAME));
    }

    public WebServerPortFileWriter(String filename) {
        this(new File(filename));
    }

    public WebServerPortFileWriter(File file) {
        Assert.notNull(file, "File must not be null");
        String override = SystemProperties.get(PROPERTY_VARIABLES);
        if (override != null) {
            this.file = new File(override);
        } else {
            this.file = file;
        }
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(WebServerInitializedEvent event) {
        File portFile = getPortFile(event.getApplicationContext());
        try {
            String port = String.valueOf(event.getWebServer().getPort());
            createParentDirectory(portFile);
            FileCopyUtils.copy(port.getBytes(), portFile);
            portFile.deleteOnExit();
        } catch (Exception e) {
            logger.warn(LogMessage.format("Cannot create port file %s", this.file));
        }
    }

    protected File getPortFile(ApplicationContext applicationContext) {
        String name;
        String namespace = getServerNamespace(applicationContext);
        if (!StringUtils.hasLength(namespace)) {
            return this.file;
        }
        String name2 = this.file.getName();
        String extension = StringUtils.getFilenameExtension(this.file.getName());
        String name3 = name2.substring(0, (name2.length() - extension.length()) - 1);
        if (isUpperCase(name3)) {
            name = name3 + "-" + namespace.toUpperCase(Locale.ENGLISH);
        } else {
            name = name3 + "-" + namespace.toLowerCase(Locale.ENGLISH);
        }
        if (StringUtils.hasLength(extension)) {
            name = name + "." + extension;
        }
        return new File(this.file.getParentFile(), name);
    }

    private String getServerNamespace(ApplicationContext applicationContext) {
        if (applicationContext instanceof WebServerApplicationContext) {
            WebServerApplicationContext webServerApplicationContext = (WebServerApplicationContext) applicationContext;
            return webServerApplicationContext.getServerNamespace();
        }
        return null;
    }

    private boolean isUpperCase(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (Character.isLetter(name.charAt(i)) && !Character.isUpperCase(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void createParentDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }
}
