package org.apache.catalina.authenticator.jaspic;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.SAXException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/PersistentProviderRegistrations.class */
public final class PersistentProviderRegistrations {
    private static final Log log = LogFactory.getLog((Class<?>) PersistentProviderRegistrations.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) PersistentProviderRegistrations.class);

    private PersistentProviderRegistrations() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Providers loadProviders(File configFile) {
        try {
            InputStream is = new FileInputStream(configFile);
            try {
                Digester digester = new Digester();
                try {
                    digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
                } catch (SAXException se) {
                    log.warn(sm.getString("persistentProviderRegistrations.xmlFeatureEncoding"), se);
                }
                digester.setValidating(true);
                digester.setNamespaceAware(true);
                Providers result = new Providers();
                digester.push(result);
                digester.addObjectCreate("jaspic-providers/provider", Provider.class.getName());
                digester.addSetProperties("jaspic-providers/provider");
                digester.addSetNext("jaspic-providers/provider", "addProvider", Provider.class.getName());
                digester.addObjectCreate("jaspic-providers/provider/property", Property.class.getName());
                digester.addSetProperties("jaspic-providers/provider/property");
                digester.addSetNext("jaspic-providers/provider/property", "addProperty", Property.class.getName());
                digester.parse(is);
                is.close();
                return result;
            } finally {
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new SecurityException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeProviders(Providers providers, File configFile) {
        File configFileOld = new File(configFile.getAbsolutePath() + ".old");
        File configFileNew = new File(configFile.getAbsolutePath() + ".new");
        if (configFileOld.exists() && configFileOld.delete()) {
            throw new SecurityException(sm.getString("persistentProviderRegistrations.existsDeleteFail", configFileOld.getAbsolutePath()));
        }
        if (configFileNew.exists() && configFileNew.delete()) {
            throw new SecurityException(sm.getString("persistentProviderRegistrations.existsDeleteFail", configFileNew.getAbsolutePath()));
        }
        try {
            OutputStream fos = new FileOutputStream(configFileNew);
            try {
                Writer writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                try {
                    writer.write("<?xml version='1.0' encoding='utf-8'?>\n<jaspic-providers\n    xmlns=\"http://tomcat.apache.org/xml\"\n    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n    xsi:schemaLocation=\"http://tomcat.apache.org/xml jaspic-providers.xsd\"\n    version=\"1.0\">\n");
                    for (Provider provider : providers.providers) {
                        writer.write("  <provider");
                        writeOptional(JsonEncoder.CLASS_NAME_ATTR_NAME, provider.getClassName(), writer);
                        writeOptional("layer", provider.getLayer(), writer);
                        writeOptional("appContext", provider.getAppContext(), writer);
                        writeOptional("description", provider.getDescription(), writer);
                        writer.write(">\n");
                        for (Map.Entry<String, String> entry : provider.getProperties().entrySet()) {
                            writer.write("    <property name=\"");
                            writer.write(entry.getKey());
                            writer.write("\" value=\"");
                            writer.write(entry.getValue());
                            writer.write("\"/>\n");
                        }
                        writer.write("  </provider>\n");
                    }
                    writer.write("</jaspic-providers>\n");
                    writer.close();
                    fos.close();
                    if (configFile.isFile() && !configFile.renameTo(configFileOld)) {
                        throw new SecurityException(sm.getString("persistentProviderRegistrations.moveFail", configFile.getAbsolutePath(), configFileOld.getAbsolutePath()));
                    }
                    if (!configFileNew.renameTo(configFile)) {
                        throw new SecurityException(sm.getString("persistentProviderRegistrations.moveFail", configFileNew.getAbsolutePath(), configFile.getAbsolutePath()));
                    }
                    if (configFileOld.exists() && !configFileOld.delete()) {
                        Log log2 = LogFactory.getLog((Class<?>) PersistentProviderRegistrations.class);
                        log2.warn(sm.getString("persistentProviderRegistrations.deleteFail", configFileOld.getAbsolutePath()));
                    }
                } catch (Throwable th) {
                    try {
                        writer.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } finally {
            }
        } catch (IOException e) {
            if (!configFileNew.delete()) {
                Log log3 = LogFactory.getLog((Class<?>) PersistentProviderRegistrations.class);
                log3.warn(sm.getString("persistentProviderRegistrations.deleteFail", configFileNew.getAbsolutePath()));
            }
            throw new SecurityException(e);
        }
    }

    private static void writeOptional(String name, String value, Writer writer) throws IOException {
        if (value != null) {
            writer.write(" " + name + "=\"");
            writer.write(value);
            writer.write("\"");
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/PersistentProviderRegistrations$Providers.class */
    public static class Providers {
        private final List<Provider> providers = new ArrayList();

        public void addProvider(Provider provider) {
            this.providers.add(provider);
        }

        public List<Provider> getProviders() {
            return this.providers;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/PersistentProviderRegistrations$Provider.class */
    public static class Provider {
        private String className;
        private String layer;
        private String appContext;
        private String description;
        private final Map<String, String> properties = new HashMap();

        public String getClassName() {
            return this.className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getLayer() {
            return this.layer;
        }

        public void setLayer(String layer) {
            this.layer = layer;
        }

        public String getAppContext() {
            return this.appContext;
        }

        public void setAppContext(String appContext) {
            this.appContext = appContext;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void addProperty(Property property) {
            this.properties.put(property.getName(), property.getValue());
        }

        public void setProperty(String name, String value) {
            addProperty(name, value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addProperty(String name, String value) {
            this.properties.put(name, value);
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/PersistentProviderRegistrations$Property.class */
    public static class Property {
        private String name;
        private String value;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
