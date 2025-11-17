package org.apache.catalina.mbeans;

import java.lang.reflect.Array;
import java.util.Set;
import java.util.StringJoiner;
import javax.management.JMRuntimeException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/MBeanDumper.class */
public class MBeanDumper {
    private static final Log log = LogFactory.getLog((Class<?>) MBeanDumper.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) MBeanDumper.class);
    private static final String CRLF = "\r\n";

    public static String dumpBeans(MBeanServer mbeanServer, Set<ObjectName> names) {
        String valueString;
        StringBuilder buf = new StringBuilder();
        for (ObjectName oname : names) {
            buf.append("Name: ");
            buf.append(oname.toString());
            buf.append("\r\n");
            try {
                MBeanInfo minfo = mbeanServer.getMBeanInfo(oname);
                String code = minfo.getClassName();
                if ("org.apache.commons.modeler.BaseModelMBean".equals(code)) {
                    code = (String) mbeanServer.getAttribute(oname, "modelerType");
                }
                buf.append("modelerType: ");
                buf.append(code);
                buf.append("\r\n");
                MBeanAttributeInfo[] attrs = minfo.getAttributes();
                for (MBeanAttributeInfo attr : attrs) {
                    if (attr.isReadable()) {
                        String attName = attr.getName();
                        if (!"modelerType".equals(attName) && attName.indexOf(61) < 0 && attName.indexOf(58) < 0 && attName.indexOf(32) < 0) {
                            try {
                                try {
                                    Object value = mbeanServer.getAttribute(oname, attName);
                                    if (value != null) {
                                        try {
                                            Class<?> c = value.getClass();
                                            if (c.isArray()) {
                                                int len = Array.getLength(value);
                                                StringBuilder sb = new StringBuilder("Array[" + c.getComponentType().getName() + "] of length " + len);
                                                if (len > 0) {
                                                    sb.append("\r\n");
                                                }
                                                for (int j = 0; j < len; j++) {
                                                    Object item = Array.get(value, j);
                                                    sb.append(tableItemToString(item));
                                                    if (j < len - 1) {
                                                        sb.append("\r\n");
                                                    }
                                                }
                                                valueString = sb.toString();
                                            } else if (TabularData.class.isInstance(value)) {
                                                TabularData tab = (TabularData) TabularData.class.cast(value);
                                                StringJoiner joiner = new StringJoiner("\r\n");
                                                joiner.add("TabularData[" + tab.getTabularType().getRowType().getTypeName() + "] of length " + tab.size());
                                                for (Object item2 : tab.values()) {
                                                    joiner.add(tableItemToString(item2));
                                                }
                                                valueString = joiner.toString();
                                            } else {
                                                valueString = valueToString(value);
                                            }
                                            buf.append(attName);
                                            buf.append(": ");
                                            buf.append(valueString);
                                            buf.append("\r\n");
                                        } catch (Throwable t) {
                                            ExceptionUtils.handleThrowable(t);
                                        }
                                    }
                                } catch (JMRuntimeException e) {
                                    Throwable cause = e.getCause();
                                    if (cause instanceof UnsupportedOperationException) {
                                        if (log.isDebugEnabled()) {
                                            log.debug(sm.getString("mBeanDumper.getAttributeError", attName, oname), e);
                                        }
                                    } else if (cause instanceof NullPointerException) {
                                        if (log.isDebugEnabled()) {
                                            log.debug(sm.getString("mBeanDumper.getAttributeError", attName, oname), e);
                                        }
                                    } else {
                                        log.error(sm.getString("mBeanDumper.getAttributeError", attName, oname), e);
                                    }
                                }
                            } catch (Throwable t2) {
                                ExceptionUtils.handleThrowable(t2);
                                log.error(sm.getString("mBeanDumper.getAttributeError", attName, oname), t2);
                            }
                        }
                    }
                }
            } catch (Throwable t3) {
                ExceptionUtils.handleThrowable(t3);
            }
            buf.append("\r\n");
        }
        return buf.toString();
    }

    public static String escape(String value) {
        int idx = value.indexOf(10);
        if (idx < 0) {
            return value;
        }
        int prev = 0;
        StringBuilder sb = new StringBuilder();
        while (idx >= 0) {
            appendHead(sb, value, prev, idx);
            sb.append("\\n\n ");
            prev = idx + 1;
            if (idx == value.length() - 1) {
                break;
            }
            idx = value.indexOf(10, idx + 1);
        }
        if (prev < value.length()) {
            appendHead(sb, value, prev, value.length());
        }
        return sb.toString();
    }

    private static void appendHead(StringBuilder sb, String value, int start, int end) {
        if (end < 1) {
            return;
        }
        int i = start;
        while (true) {
            int pos = i;
            if (end - pos > 78) {
                sb.append(value.substring(pos, pos + 78));
                sb.append("\n ");
                i = pos + 78;
            } else {
                sb.append(value.substring(pos, end));
                return;
            }
        }
    }

    private static String tableItemToString(Object item) {
        if (item == null) {
            return "\tNULL VALUE";
        }
        try {
            return "\t" + valueToString(item);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            return "\tNON-STRINGABLE VALUE";
        }
    }

    private static String valueToString(Object value) {
        String valueString;
        if (CompositeData.class.isInstance(value)) {
            StringBuilder sb = new StringBuilder("{");
            String sep = "";
            CompositeData composite = (CompositeData) CompositeData.class.cast(value);
            Set<String> keys = composite.getCompositeType().keySet();
            for (String key : keys) {
                sb.append(sep).append(key).append('=').append(composite.get(key));
                sep = ", ";
            }
            sb.append('}');
            valueString = sb.toString();
        } else {
            valueString = value.toString();
        }
        return escape(valueString);
    }
}
