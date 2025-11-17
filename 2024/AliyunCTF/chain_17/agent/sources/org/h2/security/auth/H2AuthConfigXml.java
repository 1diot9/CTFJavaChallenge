package org.h2.security.auth;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/H2AuthConfigXml.class */
public class H2AuthConfigXml extends DefaultHandler {
    private H2AuthConfig result;
    private HasConfigProperties lastConfigProperties;

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
        boolean z = -1;
        switch (str3.hashCode()) {
            case -1269306990:
                if (str3.equals("h2Auth")) {
                    z = false;
                    break;
                }
                break;
            case -993141291:
                if (str3.equals(BeanDefinitionParserDelegate.PROPERTY_ELEMENT)) {
                    z = 3;
                    break;
                }
                break;
            case 108386959:
                if (str3.equals("realm")) {
                    z = true;
                    break;
                }
                break;
            case 1169083352:
                if (str3.equals("userToRolesMapper")) {
                    z = 2;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                this.result = new H2AuthConfig();
                this.result.setAllowUserRegistration("true".equals(getAttributeValueOr("allowUserRegistration", attributes, "false")));
                this.result.setCreateMissingRoles("true".equals(getAttributeValueOr("createMissingRoles", attributes, "true")));
                return;
            case true:
                RealmConfig realmConfig = new RealmConfig();
                realmConfig.setName(getMandatoryAttributeValue("name", attributes));
                realmConfig.setValidatorClass(getMandatoryAttributeValue("validatorClass", attributes));
                this.result.getRealms().add(realmConfig);
                this.lastConfigProperties = realmConfig;
                return;
            case true:
                UserToRolesMapperConfig userToRolesMapperConfig = new UserToRolesMapperConfig();
                userToRolesMapperConfig.setClassName(getMandatoryAttributeValue(JsonEncoder.CLASS_NAME_ATTR_NAME, attributes));
                this.result.getUserToRolesMappers().add(userToRolesMapperConfig);
                this.lastConfigProperties = userToRolesMapperConfig;
                return;
            case true:
                if (this.lastConfigProperties == null) {
                    throw new SAXException("property element in the wrong place");
                }
                this.lastConfigProperties.getProperties().add(new PropertyConfig(getMandatoryAttributeValue("name", attributes), getMandatoryAttributeValue("value", attributes)));
                return;
            default:
                throw new SAXException("unexpected element " + str3);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String str, String str2, String str3) throws SAXException {
        if (this.lastConfigProperties != null && !str3.equals(BeanDefinitionParserDelegate.PROPERTY_ELEMENT)) {
            this.lastConfigProperties = null;
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.EntityResolver
    public InputSource resolveEntity(String str, String str2) throws IOException, SAXException {
        return new InputSource(new StringReader(""));
    }

    private static String getMandatoryAttributeValue(String str, Attributes attributes) throws SAXException {
        String value = attributes.getValue(str);
        if (value == null || value.trim().equals("")) {
            throw new SAXException("missing attribute " + str);
        }
        return value;
    }

    private static String getAttributeValueOr(String str, Attributes attributes, String str2) {
        String value = attributes.getValue(str);
        if (value == null || value.trim().equals("")) {
            return str2;
        }
        return value;
    }

    public H2AuthConfig getResult() {
        return this.result;
    }

    public static H2AuthConfig parseFrom(URL url) throws SAXException, IOException, ParserConfigurationException {
        InputStream openStream = url.openStream();
        Throwable th = null;
        try {
            H2AuthConfig parseFrom = parseFrom(openStream);
            if (openStream != null) {
                if (0 != 0) {
                    try {
                        openStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    openStream.close();
                }
            }
            return parseFrom;
        } catch (Throwable th3) {
            if (openStream != null) {
                if (0 != 0) {
                    try {
                        openStream.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    openStream.close();
                }
            }
            throw th3;
        }
    }

    public static H2AuthConfig parseFrom(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        SAXParserFactory newInstance = SAXParserFactory.newInstance();
        newInstance.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        newInstance.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        newInstance.setFeature("http://xml.org/sax/features/external-general-entities", false);
        newInstance.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        newInstance.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        SAXParser newSAXParser = newInstance.newSAXParser();
        H2AuthConfigXml h2AuthConfigXml = new H2AuthConfigXml();
        newSAXParser.parse(inputStream, h2AuthConfigXml);
        return h2AuthConfigXml.getResult();
    }
}
