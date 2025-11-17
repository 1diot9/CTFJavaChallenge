package org.apache.catalina.startup;

import java.lang.reflect.Method;
import org.apache.catalina.Executor;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/ConnectorCreateRule.class */
public class ConnectorCreateRule extends Rule {
    private static final Log log = LogFactory.getLog((Class<?>) ConnectorCreateRule.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) ConnectorCreateRule.class);

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Service svc = (Service) this.digester.peek();
        Executor ex = null;
        String executorName = attributes.getValue("executor");
        if (executorName != null) {
            ex = svc.getExecutor(executorName);
        }
        String protocolName = attributes.getValue("protocol");
        Connector con = new Connector(protocolName);
        if (ex != null) {
            setExecutor(con, ex);
        }
        String sslImplementationName = attributes.getValue("sslImplementationName");
        if (sslImplementationName != null) {
            setSSLImplementationName(con, sslImplementationName);
        }
        this.digester.push(con);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(Connector.class.getName()).append(' ').append(this.digester.toVariableName(con));
            code.append(" = new ").append(Connector.class.getName());
            code.append("(new ").append(con.getProtocolHandlerClassName()).append("());");
            code.append(System.lineSeparator());
            if (ex != null) {
                code.append(this.digester.toVariableName(con)).append(".getProtocolHandler().setExecutor(");
                code.append(this.digester.toVariableName(svc)).append(".getExecutor(").append(executorName);
                code.append("));");
                code.append(System.lineSeparator());
            }
            if (sslImplementationName != null) {
                code.append("((").append(AbstractHttp11JsseProtocol.class.getName()).append("<?>) ");
                code.append(this.digester.toVariableName(con)).append(".getProtocolHandler()).setSslImplementationName(\"");
                code.append(sslImplementationName).append("\");");
                code.append(System.lineSeparator());
            }
        }
    }

    private static void setExecutor(Connector con, Executor ex) throws Exception {
        Method m = IntrospectionUtils.findMethod(con.getProtocolHandler().getClass(), "setExecutor", new Class[]{java.util.concurrent.Executor.class});
        if (m != null) {
            m.invoke(con.getProtocolHandler(), ex);
        } else {
            log.warn(sm.getString("connector.noSetExecutor", con));
        }
    }

    private static void setSSLImplementationName(Connector con, String sslImplementationName) throws Exception {
        Method m = IntrospectionUtils.findMethod(con.getProtocolHandler().getClass(), "setSslImplementationName", new Class[]{String.class});
        if (m != null) {
            m.invoke(con.getProtocolHandler(), sslImplementationName);
        } else {
            log.warn(sm.getString("connector.noSetSSLImplementationName", con));
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        this.digester.pop();
    }
}
