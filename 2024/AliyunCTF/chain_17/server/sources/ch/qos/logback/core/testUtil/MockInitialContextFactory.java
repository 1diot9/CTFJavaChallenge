package ch.qos.logback.core.testUtil;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/MockInitialContextFactory.class */
public class MockInitialContextFactory implements InitialContextFactory {
    static MockInitialContext mic;

    static {
        System.out.println("MockInitialContextFactory static called");
        initialize();
    }

    public static void initialize() {
        try {
            mic = new MockInitialContext();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        return mic;
    }

    public static MockInitialContext getContext() {
        return mic;
    }
}
