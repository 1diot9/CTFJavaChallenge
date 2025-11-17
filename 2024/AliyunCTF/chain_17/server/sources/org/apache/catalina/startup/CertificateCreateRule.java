package org.apache.catalina.startup;

import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/CertificateCreateRule.class */
public class CertificateCreateRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        SSLHostConfigCertificate.Type type;
        SSLHostConfig sslHostConfig = (SSLHostConfig) this.digester.peek();
        String typeValue = attributes.getValue("type");
        if (typeValue == null || typeValue.length() == 0) {
            type = SSLHostConfigCertificate.Type.UNDEFINED;
        } else {
            type = SSLHostConfigCertificate.Type.valueOf(typeValue);
        }
        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, type);
        this.digester.push(certificate);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(SSLHostConfigCertificate.class.getName()).append(' ').append(this.digester.toVariableName(certificate));
            code.append(" = new ").append(SSLHostConfigCertificate.class.getName());
            code.append('(').append(this.digester.toVariableName(sslHostConfig));
            code.append(", ").append(SSLHostConfigCertificate.Type.class.getName().replace('$', '.')).append('.').append(type).append(");");
            code.append(System.lineSeparator());
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        this.digester.pop();
    }
}
