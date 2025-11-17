package org.h2.jdbcx;

import ch.qos.logback.classic.ClassicConstants;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.h2.engine.Constants;
import org.h2.engine.SysProperties;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbcx/JdbcDataSourceFactory.class */
public final class JdbcDataSourceFactory implements ObjectFactory {
    private static final TraceSystem traceSystem = new TraceSystem(SysProperties.CLIENT_TRACE_DIRECTORY + "h2datasource" + Constants.SUFFIX_TRACE_FILE);
    private final Trace trace = traceSystem.getTrace(14);

    static {
        traceSystem.setLevelFile(SysProperties.DATASOURCE_TRACE_LEVEL);
    }

    public synchronized Object getObjectInstance(Object obj, Name name, Context context, Hashtable<?, ?> hashtable) {
        if (this.trace.isDebugEnabled()) {
            this.trace.debug("getObjectInstance obj={0} name={1} nameCtx={2} environment={3}", obj, name, context, hashtable);
        }
        if (obj instanceof Reference) {
            Reference reference = (Reference) obj;
            if (reference.getClassName().equals(JdbcDataSource.class.getName())) {
                JdbcDataSource jdbcDataSource = new JdbcDataSource();
                jdbcDataSource.setURL((String) reference.get("url").getContent());
                jdbcDataSource.setUser((String) reference.get(ClassicConstants.USER_MDC_KEY).getContent());
                jdbcDataSource.setPassword((String) reference.get("password").getContent());
                jdbcDataSource.setDescription((String) reference.get("description").getContent());
                jdbcDataSource.setLoginTimeout(Integer.parseInt((String) reference.get("loginTimeout").getContent()));
                return jdbcDataSource;
            }
            return null;
        }
        return null;
    }

    public static TraceSystem getTraceSystem() {
        return traceSystem;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Trace getTrace() {
        return this.trace;
    }
}
