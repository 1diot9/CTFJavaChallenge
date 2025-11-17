package org.springframework.web.util;

import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/DisconnectedClientHelper.class */
public class DisconnectedClientHelper {
    private static final Set<String> EXCEPTION_PHRASES = Set.of("broken pipe", "connection reset by peer");
    private static final Set<String> EXCEPTION_TYPE_NAMES = Set.of("AbortedException", "ClientAbortException", "EOFException", "EofException");
    private final Log logger;

    public DisconnectedClientHelper(String logCategory) {
        Assert.notNull(logCategory, "'logCategory' is required");
        this.logger = LogFactory.getLog(logCategory);
    }

    public boolean checkAndLogClientDisconnectedException(Throwable ex) {
        if (isClientDisconnectedException(ex)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Looks like the client has gone away", ex);
                return true;
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Looks like the client has gone away: " + ex + " (For a full stack trace, set the log category '" + this.logger + "' to TRACE level.)");
                return true;
            }
            return true;
        }
        return false;
    }

    public static boolean isClientDisconnectedException(Throwable ex) {
        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
        if (message != null) {
            String text = message.toLowerCase();
            for (String phrase : EXCEPTION_PHRASES) {
                if (text.contains(phrase)) {
                    return true;
                }
            }
        }
        return EXCEPTION_TYPE_NAMES.contains(ex.getClass().getSimpleName());
    }
}
