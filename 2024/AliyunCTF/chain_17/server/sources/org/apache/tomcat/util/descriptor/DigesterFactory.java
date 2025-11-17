package org.apache.tomcat.util.descriptor;

import jakarta.servlet.ServletContext;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.ext.EntityResolver2;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/DigesterFactory.class */
public class DigesterFactory {
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE_NAME);
    private static final Class<ServletContext> CLASS_SERVLET_CONTEXT = ServletContext.class;
    private static final Class<?> CLASS_JSP_CONTEXT;
    public static final Map<String, String> SERVLET_API_PUBLIC_IDS;
    public static final Map<String, String> SERVLET_API_SYSTEM_IDS;

    static {
        Class<?> jspContext = null;
        try {
            jspContext = Class.forName("jakarta.servlet.jsp.JspContext");
        } catch (ClassNotFoundException e) {
        }
        CLASS_JSP_CONTEXT = jspContext;
        Map<String, String> publicIds = new HashMap<>();
        Map<String, String> systemIds = new HashMap<>();
        add(publicIds, XmlIdentifiers.XSD_10_PUBLIC, locationFor("XMLSchema.dtd"));
        add(publicIds, XmlIdentifiers.DATATYPES_PUBLIC, locationFor("datatypes.dtd"));
        add(systemIds, XmlIdentifiers.XML_2001_XSD, locationFor("xml.xsd"));
        add(publicIds, XmlIdentifiers.WEB_22_PUBLIC, locationFor("web-app_2_2.dtd"));
        add(publicIds, XmlIdentifiers.TLD_11_PUBLIC, locationFor("web-jsptaglibrary_1_1.dtd"));
        add(publicIds, XmlIdentifiers.WEB_23_PUBLIC, locationFor("web-app_2_3.dtd"));
        add(publicIds, XmlIdentifiers.TLD_12_PUBLIC, locationFor("web-jsptaglibrary_1_2.dtd"));
        add(systemIds, XmlIdentifiers.WEBSERVICES_11_XSD, locationFor("j2ee_web_services_1_1.xsd"));
        add(systemIds, "http://www.ibm.com/webservices/xsd/j2ee_web_services_client_1_1.xsd", locationFor("j2ee_web_services_client_1_1.xsd"));
        add(systemIds, XmlIdentifiers.WEB_24_XSD, locationFor("web-app_2_4.xsd"));
        add(systemIds, XmlIdentifiers.TLD_20_XSD, locationFor("web-jsptaglibrary_2_0.xsd"));
        addSelf(systemIds, "j2ee_1_4.xsd");
        addSelf(systemIds, "jsp_2_0.xsd");
        add(systemIds, XmlIdentifiers.WEB_25_XSD, locationFor("web-app_2_5.xsd"));
        add(systemIds, XmlIdentifiers.TLD_21_XSD, locationFor("web-jsptaglibrary_2_1.xsd"));
        addSelf(systemIds, "javaee_5.xsd");
        addSelf(systemIds, "jsp_2_1.xsd");
        addSelf(systemIds, "javaee_web_services_1_2.xsd");
        addSelf(systemIds, "javaee_web_services_client_1_2.xsd");
        add(systemIds, XmlIdentifiers.WEB_30_XSD, locationFor("web-app_3_0.xsd"));
        add(systemIds, XmlIdentifiers.WEB_FRAGMENT_30_XSD, locationFor("web-fragment_3_0.xsd"));
        addSelf(systemIds, "web-common_3_0.xsd");
        addSelf(systemIds, "javaee_6.xsd");
        addSelf(systemIds, "jsp_2_2.xsd");
        addSelf(systemIds, "javaee_web_services_1_3.xsd");
        addSelf(systemIds, "javaee_web_services_client_1_3.xsd");
        add(systemIds, XmlIdentifiers.WEB_31_XSD, locationFor("web-app_3_1.xsd"));
        add(systemIds, XmlIdentifiers.WEB_FRAGMENT_31_XSD, locationFor("web-fragment_3_1.xsd"));
        addSelf(systemIds, "web-common_3_1.xsd");
        addSelf(systemIds, "javaee_7.xsd");
        addSelf(systemIds, "jsp_2_3.xsd");
        addSelf(systemIds, "javaee_web_services_1_4.xsd");
        addSelf(systemIds, "javaee_web_services_client_1_4.xsd");
        add(systemIds, XmlIdentifiers.WEB_40_XSD, locationFor("web-app_4_0.xsd"));
        add(systemIds, XmlIdentifiers.WEB_FRAGMENT_40_XSD, locationFor("web-fragment_4_0.xsd"));
        addSelf(systemIds, "web-common_4_0.xsd");
        addSelf(systemIds, "javaee_8.xsd");
        add(systemIds, XmlIdentifiers.WEB_50_XSD, locationFor("web-app_5_0.xsd"));
        add(systemIds, XmlIdentifiers.WEB_FRAGMENT_50_XSD, locationFor("web-fragment_5_0.xsd"));
        add(systemIds, XmlIdentifiers.TLD_30_XSD, locationFor("web-jsptaglibrary_3_0.xsd"));
        addSelf(systemIds, "web-common_5_0.xsd");
        addSelf(systemIds, "jakartaee_9.xsd");
        addSelf(systemIds, "jsp_3_0.xsd");
        addSelf(systemIds, "jakartaee_web_services_2_0.xsd");
        addSelf(systemIds, "jakartaee_web_services_client_2_0.xsd");
        add(systemIds, XmlIdentifiers.WEB_60_XSD, locationFor("web-app_6_0.xsd"));
        add(systemIds, XmlIdentifiers.WEB_FRAGMENT_60_XSD, locationFor("web-fragment_6_0.xsd"));
        add(systemIds, XmlIdentifiers.TLD_31_XSD, locationFor("web-jsptaglibrary_3_1.xsd"));
        addSelf(systemIds, "web-common_6_0.xsd");
        addSelf(systemIds, "jakartaee_10.xsd");
        addSelf(systemIds, "jsp_3_1.xsd");
        addSelf(systemIds, "jakartaee_web_services_2_0.xsd");
        addSelf(systemIds, "jakartaee_web_services_client_2_0.xsd");
        SERVLET_API_PUBLIC_IDS = Collections.unmodifiableMap(publicIds);
        SERVLET_API_SYSTEM_IDS = Collections.unmodifiableMap(systemIds);
    }

    private static void addSelf(Map<String, String> ids, String id) {
        String location = locationFor(id);
        if (location != null) {
            ids.put(id, location);
            ids.put(location, location);
        }
    }

    private static void add(Map<String, String> ids, String id, String location) {
        if (location != null) {
            ids.put(id, location);
            if (id.startsWith("http://")) {
                String httpsId = "https://" + id.substring(7);
                ids.put(httpsId, location);
            }
        }
    }

    private static String locationFor(String name) {
        URL location = CLASS_SERVLET_CONTEXT.getResource("resources/" + name);
        if (location == null && CLASS_JSP_CONTEXT != null) {
            location = CLASS_JSP_CONTEXT.getResource("resources/" + name);
        }
        if (location == null) {
            Log log = LogFactory.getLog((Class<?>) DigesterFactory.class);
            log.warn(sm.getString("digesterFactory.missingSchema", name));
            return null;
        }
        return location.toExternalForm();
    }

    public static Digester newDigester(boolean xmlValidation, boolean xmlNamespaceAware, RuleSet rule, boolean blockExternal) {
        Digester digester = new Digester();
        digester.setNamespaceAware(xmlNamespaceAware);
        digester.setValidating(xmlValidation);
        digester.setUseContextClassLoader(true);
        EntityResolver2 resolver = new LocalResolver(SERVLET_API_PUBLIC_IDS, SERVLET_API_SYSTEM_IDS, blockExternal);
        digester.setEntityResolver(resolver);
        if (rule != null) {
            digester.addRuleSet(rule);
        }
        return digester;
    }
}
