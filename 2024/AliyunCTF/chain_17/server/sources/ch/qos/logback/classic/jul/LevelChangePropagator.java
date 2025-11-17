package ch.qos.logback.classic.jul;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/jul/LevelChangePropagator.class */
public class LevelChangePropagator extends ContextAwareBase implements LoggerContextListener, LifeCycle {
    private Set<Logger> julLoggerSet = new HashSet();
    boolean isStarted = false;
    boolean resetJUL = false;

    public void setResetJUL(boolean resetJUL) {
        this.resetJUL = resetJUL;
    }

    @Override // ch.qos.logback.classic.spi.LoggerContextListener
    public boolean isResetResistant() {
        return false;
    }

    @Override // ch.qos.logback.classic.spi.LoggerContextListener
    public void onStart(LoggerContext context) {
    }

    @Override // ch.qos.logback.classic.spi.LoggerContextListener
    public void onReset(LoggerContext context) {
    }

    @Override // ch.qos.logback.classic.spi.LoggerContextListener
    public void onStop(LoggerContext context) {
    }

    @Override // ch.qos.logback.classic.spi.LoggerContextListener
    public void onLevelChange(ch.qos.logback.classic.Logger logger, Level level) {
        propagate(logger, level);
    }

    private void propagate(ch.qos.logback.classic.Logger logger, Level level) {
        addInfo("Propagating " + String.valueOf(level) + " level on " + String.valueOf(logger) + " onto the JUL framework");
        Logger julLogger = JULHelper.asJULLogger(logger);
        this.julLoggerSet.add(julLogger);
        java.util.logging.Level julLevel = JULHelper.asJULLevel(level);
        julLogger.setLevel(julLevel);
    }

    public void resetJULLevels() {
        LogManager lm = LogManager.getLogManager();
        Enumeration<String> e = lm.getLoggerNames();
        while (e.hasMoreElements()) {
            String loggerName = e.nextElement();
            Logger julLogger = lm.getLogger(loggerName);
            if (JULHelper.isRegularNonRootLogger(julLogger) && julLogger.getLevel() != null) {
                addInfo("Setting level of jul logger [" + loggerName + "] to null");
                julLogger.setLevel(null);
            }
        }
    }

    private void propagateExistingLoggerLevels() {
        LoggerContext loggerContext = (LoggerContext) this.context;
        List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
        for (ch.qos.logback.classic.Logger l : loggerList) {
            if (l.getLevel() != null) {
                propagate(l, l.getLevel());
            }
        }
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.resetJUL) {
            resetJULLevels();
        }
        propagateExistingLoggerLevels();
        this.isStarted = true;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.isStarted = false;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.isStarted;
    }
}
