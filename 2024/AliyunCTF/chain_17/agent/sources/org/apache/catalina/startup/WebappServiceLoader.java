package org.apache.catalina.startup;

import jakarta.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.catalina.Context;
import org.apache.tomcat.util.scan.JarFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/WebappServiceLoader.class */
public class WebappServiceLoader<T> {
    private static final String CLASSES = "/WEB-INF/classes/";
    private static final String LIB = "/WEB-INF/lib/";
    private static final String SERVICES = "META-INF/services/";
    private final Context context;
    private final ServletContext servletContext;
    private final Pattern containerSciFilterPattern;

    public WebappServiceLoader(Context context) {
        this.context = context;
        this.servletContext = context.getServletContext();
        String containerSciFilter = context.getContainerSciFilter();
        if (containerSciFilter != null && containerSciFilter.length() > 0) {
            this.containerSciFilterPattern = Pattern.compile(containerSciFilter);
        } else {
            this.containerSciFilterPattern = null;
        }
    }

    public List<T> load(Class<T> serviceType) throws IOException {
        Enumeration<URL> containerResources;
        URL url;
        String configFile = "META-INF/services/" + serviceType.getName();
        ClassLoader loader = this.context.getParentClassLoader();
        if (loader == null) {
            containerResources = ClassLoader.getSystemResources(configFile);
        } else {
            containerResources = loader.getResources(configFile);
        }
        LinkedHashSet<String> containerServiceClassNames = new LinkedHashSet<>();
        Set<URL> containerServiceConfigFiles = new HashSet<>();
        while (containerResources.hasMoreElements()) {
            URL containerServiceConfigFile = containerResources.nextElement();
            containerServiceConfigFiles.add(containerServiceConfigFile);
            parseConfigFile(containerServiceClassNames, containerServiceConfigFile);
        }
        if (this.containerSciFilterPattern != null) {
            containerServiceClassNames.removeIf(s -> {
                return this.containerSciFilterPattern.matcher(s).find();
            });
        }
        LinkedHashSet<String> applicationServiceClassNames = new LinkedHashSet<>();
        List<String> orderedLibs = (List) this.servletContext.getAttribute(ServletContext.ORDERED_LIBS);
        if (orderedLibs == null) {
            Enumeration<URL> allResources = this.servletContext.getClassLoader().getResources(configFile);
            while (allResources.hasMoreElements()) {
                URL serviceConfigFile = allResources.nextElement();
                if (!containerServiceConfigFiles.contains(serviceConfigFile)) {
                    parseConfigFile(applicationServiceClassNames, serviceConfigFile);
                }
            }
        } else {
            URL unpacked = this.servletContext.getResource("/WEB-INF/classes/" + configFile);
            if (unpacked != null) {
                parseConfigFile(applicationServiceClassNames, unpacked);
            }
            for (String lib : orderedLibs) {
                URL jarUrl = this.servletContext.getResource("/WEB-INF/lib/" + lib);
                if (jarUrl != null) {
                    String base = jarUrl.toExternalForm();
                    if (base.endsWith("/")) {
                        try {
                            URI uri = new URI(base + configFile);
                            url = uri.toURL();
                        } catch (URISyntaxException e) {
                            throw new IOException(e);
                        }
                    } else {
                        url = JarFactory.getJarEntryURL(jarUrl, configFile);
                    }
                    try {
                        URL url2 = url;
                        parseConfigFile(applicationServiceClassNames, url2);
                    } catch (FileNotFoundException e2) {
                    }
                }
            }
        }
        containerServiceClassNames.addAll(applicationServiceClassNames);
        if (containerServiceClassNames.isEmpty()) {
            return Collections.emptyList();
        }
        return loadServices(serviceType, containerServiceClassNames);
    }

    void parseConfigFile(LinkedHashSet<String> servicesFound, URL url) throws IOException {
        InputStream is = url.openStream();
        try {
            InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8);
            try {
                BufferedReader reader = new BufferedReader(in);
                while (true) {
                    try {
                        String readLine = reader.readLine();
                        String line = readLine;
                        if (readLine == null) {
                            break;
                        }
                        int i = line.indexOf(35);
                        if (i >= 0) {
                            line = line.substring(0, i);
                        }
                        String line2 = line.trim();
                        if (line2.length() != 0) {
                            servicesFound.add(line2);
                        }
                    } catch (Throwable th) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                }
                reader.close();
                in.close();
                if (is != null) {
                    is.close();
                }
            } finally {
            }
        } catch (Throwable th3) {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
            }
            throw th3;
        }
    }

    List<T> loadServices(Class<T> serviceType, LinkedHashSet<String> servicesFound) throws IOException {
        ClassLoader loader = this.servletContext.getClassLoader();
        List<T> services = new ArrayList<>(servicesFound.size());
        Iterator<String> it = servicesFound.iterator();
        while (it.hasNext()) {
            String serviceClass = it.next();
            try {
                Class<?> clazz = Class.forName(serviceClass, true, loader);
                services.add(serviceType.cast(clazz.getConstructor(new Class[0]).newInstance(new Object[0])));
            } catch (ClassCastException | ReflectiveOperationException e) {
                throw new IOException(e);
            }
        }
        return Collections.unmodifiableList(services);
    }
}
