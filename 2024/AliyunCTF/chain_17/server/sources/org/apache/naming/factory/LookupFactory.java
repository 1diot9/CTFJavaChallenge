package org.apache.naming.factory;

import java.util.HashSet;
import java.util.Set;
import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/LookupFactory.class */
public class LookupFactory implements ObjectFactory {
    private static final Log log = LogFactory.getLog((Class<?>) LookupFactory.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) LookupFactory.class);
    private static final ThreadLocal<Set<String>> names = ThreadLocal.withInitial(HashSet::new);

    /* JADX WARN: Removed duplicated region for block: B:16:0x00f2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Object getObjectInstance(java.lang.Object r8, javax.naming.Name r9, javax.naming.Context r10, java.util.Hashtable<?, ?> r11) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 531
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.naming.factory.LookupFactory.getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable):java.lang.Object");
    }
}
