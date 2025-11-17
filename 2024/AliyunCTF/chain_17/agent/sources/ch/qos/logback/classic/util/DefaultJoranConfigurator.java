package ch.qos.logback.classic.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ConfiguratorRank;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.Loader;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

@ConfiguratorRank(0)
/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/util/DefaultJoranConfigurator.class */
public class DefaultJoranConfigurator extends ContextAwareBase implements Configurator {
    @Override // ch.qos.logback.classic.spi.Configurator
    public Configurator.ExecutionStatus configure(LoggerContext context) {
        URL url = performMultiStepConfigurationFileSearch(true);
        if (url != null) {
            try {
                configureByResource(url);
            } catch (JoranException e) {
                e.printStackTrace();
            }
            return Configurator.ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
        }
        return Configurator.ExecutionStatus.INVOKE_NEXT_IF_ANY;
    }

    private URL performMultiStepConfigurationFileSearch(boolean updateStatus) {
        ClassLoader myClassLoader = Loader.getClassLoaderOfObject(this);
        URL url = findConfigFileURLFromSystemProperties(myClassLoader, updateStatus);
        if (url != null) {
            return url;
        }
        URL url2 = getResource("logback-test.xml", myClassLoader, updateStatus);
        if (url2 != null) {
            return url2;
        }
        return getResource("logback.xml", myClassLoader, updateStatus);
    }

    public void configureByResource(URL url) throws JoranException {
        if (url == null) {
            throw new IllegalArgumentException("URL argument cannot be null");
        }
        String urlString = url.toString();
        if (urlString.endsWith("xml")) {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(this.context);
            configurator.doConfigure(url);
            return;
        }
        throw new LogbackException("Unexpected filename extension of file [" + url.toString() + "]. Should be .xml");
    }

    public URL findURLOfDefaultConfigurationFile(boolean updateStatus) {
        return performMultiStepConfigurationFileSearch(updateStatus);
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0088  */
    /* JADX WARN: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.net.URL findConfigFileURLFromSystemProperties(java.lang.ClassLoader r6, boolean r7) {
        /*
            r5 = this;
            java.lang.String r0 = "logback.configurationFile"
            java.lang.String r0 = ch.qos.logback.core.util.OptionHelper.getSystemProperty(r0)
            r8 = r0
            r0 = r8
            if (r0 == 0) goto La4
            r0 = 0
            r9 = r0
            java.net.URL r0 = new java.net.URL     // Catch: java.net.MalformedURLException -> L2a java.lang.Throwable -> L93
            r1 = r0
            r2 = r8
            r1.<init>(r2)     // Catch: java.net.MalformedURLException -> L2a java.lang.Throwable -> L93
            r9 = r0
            r0 = r9
            r10 = r0
            r0 = r7
            if (r0 == 0) goto L27
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
        L27:
            r0 = r10
            return r0
        L2a:
            r10 = move-exception
            r0 = r8
            r1 = r6
            java.net.URL r0 = ch.qos.logback.core.util.Loader.getResource(r0, r1)     // Catch: java.lang.Throwable -> L93
            r9 = r0
            r0 = r9
            if (r0 == 0) goto L4b
            r0 = r9
            r11 = r0
            r0 = r7
            if (r0 == 0) goto L48
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
        L48:
            r0 = r11
            return r0
        L4b:
            java.io.File r0 = new java.io.File     // Catch: java.lang.Throwable -> L93
            r1 = r0
            r2 = r8
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L93
            r11 = r0
            r0 = r11
            boolean r0 = r0.exists()     // Catch: java.lang.Throwable -> L93
            if (r0 == 0) goto L84
            r0 = r11
            boolean r0 = r0.isFile()     // Catch: java.lang.Throwable -> L93
            if (r0 == 0) goto L84
            r0 = r11
            java.net.URI r0 = r0.toURI()     // Catch: java.net.MalformedURLException -> L82 java.lang.Throwable -> L93
            java.net.URL r0 = r0.toURL()     // Catch: java.net.MalformedURLException -> L82 java.lang.Throwable -> L93
            r9 = r0
            r0 = r9
            r12 = r0
            r0 = r7
            if (r0 == 0) goto L7f
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
        L7f:
            r0 = r12
            return r0
        L82:
            r12 = move-exception
        L84:
            r0 = r7
            if (r0 == 0) goto La4
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
            goto La4
        L93:
            r13 = move-exception
            r0 = r7
            if (r0 == 0) goto La1
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
        La1:
            r0 = r13
            throw r0
        La4:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.util.DefaultJoranConfigurator.findConfigFileURLFromSystemProperties(java.lang.ClassLoader, boolean):java.net.URL");
    }

    private URL getResource(String filename, ClassLoader myClassLoader, boolean updateStatus) {
        URL url = Loader.getResource(filename, myClassLoader);
        if (updateStatus) {
            statusOnResourceSearch(filename, myClassLoader, url);
        }
        return url;
    }

    private void statusOnResourceSearch(String resourceName, ClassLoader classLoader, URL url) {
        StatusManager sm = this.context.getStatusManager();
        if (url == null) {
            sm.add(new InfoStatus("Could NOT find resource [" + resourceName + "]", this.context));
        } else {
            sm.add(new InfoStatus("Found resource [" + resourceName + "] at [" + url.toString() + "]", this.context));
            multiplicityWarning(resourceName, classLoader);
        }
    }

    private void multiplicityWarning(String resourceName, ClassLoader classLoader) {
        Set<URL> urlSet = null;
        try {
            urlSet = Loader.getResources(resourceName, classLoader);
        } catch (IOException e) {
            addError("Failed to get url list for resource [" + resourceName + "]", e);
        }
        if (urlSet != null && urlSet.size() > 1) {
            addWarn("Resource [" + resourceName + "] occurs multiple times on the classpath.");
            for (URL url : urlSet) {
                addWarn("Resource [" + resourceName + "] occurs at [" + url.toString() + "]");
            }
        }
    }
}
