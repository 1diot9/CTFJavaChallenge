package org.apache.naming.factory;

import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/BeanFactory.class */
public class BeanFactory implements ObjectFactory {
    private static final StringManager sm = StringManager.getManager((Class<?>) BeanFactory.class);
    private final Log log = LogFactory.getLog((Class<?>) BeanFactory.class);

    /* JADX WARN: Code restructure failed: missing block: B:101:0x02cc, code lost:            throw new javax.naming.NamingException(org.apache.naming.factory.BeanFactory.sm.getString("beanFactory.noStringConversion", r0, r0.getName()));     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x0250, code lost:            r0[0] = java.lang.Boolean.valueOf(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x022f, code lost:            r0[0] = java.lang.Double.valueOf(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x020e, code lost:            r0[0] = java.lang.Float.valueOf(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x01ed, code lost:            r0[0] = java.lang.Long.valueOf(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x01cc, code lost:            r0[0] = java.lang.Integer.valueOf(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x01ab, code lost:            r0[0] = java.lang.Short.valueOf(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x018a, code lost:            r0[0] = java.lang.Byte.valueOf(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x0165, code lost:            r0[0] = java.lang.Character.valueOf(r0.charAt(0));     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0129, code lost:            r0 = r0[r26].getPropertyType();        r28 = r0[r26].getWriteMethod();     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0144, code lost:            if (r0.equals(java.lang.String.class) == false) goto L38;     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0147, code lost:            r0[0] = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x02cf, code lost:            if (r28 == null) goto L129;     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x02d2, code lost:            r28.invoke(r0, r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x02f7, code lost:            throw new javax.naming.NamingException(org.apache.naming.factory.BeanFactory.sm.getString("beanFactory.readOnlyProperty", r0));     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0157, code lost:            if (r0.equals(java.lang.Character.class) != false) goto L42;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0162, code lost:            if (r0.equals(java.lang.Character.TYPE) == false) goto L43;     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x017c, code lost:            if (r0.equals(java.lang.Byte.class) != false) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0187, code lost:            if (r0.equals(java.lang.Byte.TYPE) == false) goto L48;     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x019d, code lost:            if (r0.equals(java.lang.Short.class) != false) goto L52;     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x01a8, code lost:            if (r0.equals(java.lang.Short.TYPE) == false) goto L53;     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x01be, code lost:            if (r0.equals(java.lang.Integer.class) != false) goto L57;     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x01c9, code lost:            if (r0.equals(java.lang.Integer.TYPE) == false) goto L58;     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x01df, code lost:            if (r0.equals(java.lang.Long.class) != false) goto L62;     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x01ea, code lost:            if (r0.equals(java.lang.Long.TYPE) == false) goto L63;     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0200, code lost:            if (r0.equals(java.lang.Float.class) != false) goto L67;     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x020b, code lost:            if (r0.equals(java.lang.Float.TYPE) == false) goto L68;     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0221, code lost:            if (r0.equals(java.lang.Double.class) != false) goto L72;     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x022c, code lost:            if (r0.equals(java.lang.Double.TYPE) == false) goto L73;     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0242, code lost:            if (r0.equals(java.lang.Boolean.class) != false) goto L77;     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x024d, code lost:            if (r0.equals(java.lang.Boolean.TYPE) == false) goto L78;     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x025e, code lost:            if (r28 == null) goto L130;     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x0261, code lost:            r0 = r28.getName();     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0268, code lost:            r28 = r0.getClass().getMethod(r0, java.lang.String.class);        r0[0] = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x02a8, code lost:            throw new javax.naming.NamingException(org.apache.naming.factory.BeanFactory.sm.getString("beanFactory.noStringConversion", r0, r0.getName()));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Object getObjectInstance(java.lang.Object r10, javax.naming.Name r11, javax.naming.Context r12, java.util.Hashtable<?, ?> r13) throws javax.naming.NamingException {
        /*
            Method dump skipped, instructions count: 894
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.naming.factory.BeanFactory.getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable):java.lang.Object");
    }
}
