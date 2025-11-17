package org.apache.catalina.manager;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import org.apache.catalina.mbeans.MBeanDumper;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/manager/JMXProxyServlet.class */
public class JMXProxyServlet extends HttpServlet {
    private static final long serialVersionUID = 1;
    private static final String[] NO_PARAMETERS = new String[0];
    private static final StringManager sm = StringManager.getManager((Class<?>) JMXProxyServlet.class);
    protected transient MBeanServer mBeanServer = null;
    protected transient Registry registry;

    @Override // jakarta.servlet.GenericServlet
    public void init() throws ServletException {
        this.registry = Registry.getRegistry(null, null);
        this.mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
    }

    @Override // jakarta.servlet.http.HttpServlet
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain;charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        PrintWriter writer = response.getWriter();
        if (this.mBeanServer == null) {
            writer.println("Error - No mbean server");
            return;
        }
        String qry = request.getParameter("set");
        if (qry != null) {
            String name = request.getParameter("att");
            String val = request.getParameter("val");
            setAttribute(writer, qry, name, val);
            return;
        }
        String qry2 = request.getParameter(BeanUtil.PREFIX_GETTER_GET);
        if (qry2 != null) {
            String name2 = request.getParameter("att");
            getAttribute(writer, qry2, name2, request.getParameter("key"));
            return;
        }
        String qry3 = request.getParameter("invoke");
        if (qry3 != null) {
            String opName = request.getParameter("op");
            String[] params = getInvokeParameters(request.getParameter("ps"));
            invokeOperation(writer, qry3, opName, params);
        } else {
            String qry4 = request.getParameter("qry");
            if (qry4 == null) {
                qry4 = "*:*";
            }
            listBeans(writer, qry4);
        }
    }

    public void getAttribute(PrintWriter writer, String onameStr, String att, String key) {
        String valueStr;
        try {
            ObjectName oname = new ObjectName(onameStr);
            Object value = this.mBeanServer.getAttribute(oname, att);
            if (null != key && (value instanceof CompositeData)) {
                value = ((CompositeData) value).get(key);
            }
            if (value != null) {
                valueStr = value.toString();
            } else {
                valueStr = "<null>";
            }
            writer.print("OK - Attribute get '");
            writer.print(onameStr);
            writer.print("' - ");
            writer.print(att);
            if (null != key) {
                writer.print(" - key '");
                writer.print(key);
                writer.print("'");
            }
            writer.print(" = ");
            writer.println(MBeanDumper.escape(valueStr));
        } catch (Exception ex) {
            writer.println("Error - " + ex.toString());
            ex.printStackTrace(writer);
        }
    }

    public void setAttribute(PrintWriter writer, String onameStr, String att, String val) {
        try {
            setAttributeInternal(onameStr, att, val);
            writer.println("OK - Attribute set");
        } catch (Exception ex) {
            writer.println("Error - " + ex.toString());
            ex.printStackTrace(writer);
        }
    }

    public void listBeans(PrintWriter writer, String qry) {
        try {
            Set<ObjectName> names = this.mBeanServer.queryNames(new ObjectName(qry), (QueryExp) null);
            writer.println("OK - Number of results: " + names.size());
            writer.println();
            String dump = MBeanDumper.dumpBeans(this.mBeanServer, names);
            writer.print(dump);
        } catch (Exception ex) {
            writer.println("Error - " + ex.toString());
            ex.printStackTrace(writer);
        }
    }

    public boolean isSupported(String type) {
        return true;
    }

    private void invokeOperation(PrintWriter writer, String onameStr, String op, String[] valuesStr) {
        try {
            Object retVal = invokeOperationInternal(onameStr, op, valuesStr);
            if (retVal != null) {
                writer.println("OK - Operation " + op + " returned:");
                output("", writer, retVal);
            } else {
                writer.println("OK - Operation " + op + " without return value");
            }
        } catch (Exception ex) {
            writer.println("Error - " + ex.toString());
            ex.printStackTrace(writer);
        }
    }

    private String[] getInvokeParameters(String paramString) {
        if (paramString == null) {
            return NO_PARAMETERS;
        }
        return paramString.split(",");
    }

    private void setAttributeInternal(String onameStr, String attributeName, String value) throws OperationsException, MBeanException, ReflectionException {
        ObjectName oname = new ObjectName(onameStr);
        String type = this.registry.getType(oname, attributeName);
        Object valueObj = this.registry.convertValue(type, value);
        this.mBeanServer.setAttribute(oname, new Attribute(attributeName, valueObj));
    }

    private Object invokeOperationInternal(String onameStr, String operation, String[] parameters) throws OperationsException, MBeanException, ReflectionException {
        ObjectName oname = new ObjectName(onameStr);
        int paramCount = null == parameters ? 0 : parameters.length;
        MBeanOperationInfo methodInfo = this.registry.getMethodInfo(oname, operation, paramCount);
        if (null == methodInfo) {
            try {
                MBeanInfo info = this.registry.getMBeanServer().getMBeanInfo(oname);
                throw new IllegalArgumentException(sm.getString("jmxProxyServlet.noOperationOnBean", operation, Integer.valueOf(paramCount), onameStr, info.getClassName()));
            } catch (InstanceNotFoundException infe) {
                throw infe;
            } catch (Exception e) {
                throw new IllegalArgumentException(sm.getString("jmxProxyServlet.noBeanFound", onameStr), e);
            }
        }
        MBeanParameterInfo[] signature = methodInfo.getSignature();
        String[] signatureTypes = new String[signature.length];
        Object[] values = new Object[signature.length];
        for (int i = 0; i < signature.length; i++) {
            MBeanParameterInfo pi = signature[i];
            signatureTypes[i] = pi.getType();
            values[i] = this.registry.convertValue(pi.getType(), parameters[i]);
        }
        return this.mBeanServer.invoke(oname, operation, values, signatureTypes);
    }

    private void output(String indent, PrintWriter writer, Object result) {
        String strValue;
        if (result instanceof Object[]) {
            for (Object obj : (Object[]) result) {
                output("  " + indent, writer, obj);
            }
            return;
        }
        if (result != null) {
            strValue = result.toString();
        } else {
            strValue = "<null>";
        }
        writer.println(indent + strValue);
    }
}
