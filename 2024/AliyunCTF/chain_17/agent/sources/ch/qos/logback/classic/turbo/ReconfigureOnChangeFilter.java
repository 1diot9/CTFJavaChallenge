package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.net.URL;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/turbo/ReconfigureOnChangeFilter.class */
public class ReconfigureOnChangeFilter extends TurboFilter {
    public static final long DEFAULT_REFRESH_PERIOD = 60000;
    URL mainConfigurationURL;
    protected volatile long nextCheck;
    ConfigurationWatchList configurationWatchList;
    private static final int MAX_MASK = 65535;
    private static final long MASK_INCREASE_THRESHOLD = 100;
    private static final long MASK_DECREASE_THRESHOLD = 800;
    long refreshPeriod = 60000;
    private long invocationCounter = 0;
    private volatile long mask = 15;
    private volatile long lastMaskCheck = System.currentTimeMillis();

    /*  JADX ERROR: Failed to decode insn: 0x0010: MOVE_MULTI, method: ch.qos.logback.classic.turbo.ReconfigureOnChangeFilter.decide(org.slf4j.Marker, ch.qos.logback.classic.Logger, ch.qos.logback.classic.Level, java.lang.String, java.lang.Object[], java.lang.Throwable):ch.qos.logback.core.spi.FilterReply
        java.lang.ArrayIndexOutOfBoundsException: arraycopy: source index -1 out of bounds for object array[8]
        	at java.base/java.lang.System.arraycopy(Native Method)
        	at jadx.plugins.input.java.data.code.StackState.insert(StackState.java:49)
        	at jadx.plugins.input.java.data.code.CodeDecodeState.insert(CodeDecodeState.java:118)
        	at jadx.plugins.input.java.data.code.JavaInsnsRegister.dup2x1(JavaInsnsRegister.java:313)
        	at jadx.plugins.input.java.data.code.JavaInsnData.decode(JavaInsnData.java:46)
        	at jadx.core.dex.instructions.InsnDecoder.lambda$process$0(InsnDecoder.java:54)
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:81)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:50)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:156)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:443)
        	at jadx.core.ProcessClass.process(ProcessClass.java:70)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
        	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
        	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
        */
    @Override // ch.qos.logback.classic.turbo.TurboFilter
    public ch.qos.logback.core.spi.FilterReply decide(org.slf4j.Marker r9, ch.qos.logback.classic.Logger r10, ch.qos.logback.classic.Level r11, java.lang.String r12, java.lang.Object[] r13, java.lang.Throwable r14) {
        /*
            r8 = this;
            r0 = r8
            boolean r0 = r0.isStarted()
            if (r0 != 0) goto Lb
            ch.qos.logback.core.spi.FilterReply r0 = ch.qos.logback.core.spi.FilterReply.NEUTRAL
            return r0
            r0 = r8
            r1 = r0
            long r1 = r1.invocationCounter
            // decode failed: arraycopy: source index -1 out of bounds for object array[8]
            r2 = 1
            long r1 = r1 + r2
            r0.invocationCounter = r1
            r0 = r8
            long r0 = r0.mask
            long r-1 = r-1 & r0
            r0 = r8
            long r0 = r0.mask
            int r-1 = (r-1 > r0 ? 1 : (r-1 == r0 ? 0 : -1))
            if (r-1 == 0) goto L27
            ch.qos.logback.core.spi.FilterReply r-1 = ch.qos.logback.core.spi.FilterReply.NEUTRAL
            return r-1
            java.lang.System.currentTimeMillis()
            r15 = r-1
            r-1 = r8
            ch.qos.logback.core.joran.spi.ConfigurationWatchList r-1 = r-1.configurationWatchList
            r0 = r-1
            r17 = r0
            monitor-enter(r-1)
            r-1 = r8
            r0 = r15
            r-1.updateMaskIfNecessary(r0)
            r-1 = r8
            r0 = r15
            r-1.changeDetected(r0)
            if (r-1 == 0) goto L4b
            r-1 = r8
            r-1.disableSubsequentReconfiguration()
            r-1 = r8
            r-1.detachReconfigurationToNewThread()
            r-1 = r17
            monitor-exit(r-1)
            goto L59
            r18 = move-exception
            r0 = r17
            monitor-exit(r0)
            r0 = r18
            throw r0
            ch.qos.logback.core.spi.FilterReply r-1 = ch.qos.logback.core.spi.FilterReply.NEUTRAL
            return r-1
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.turbo.ReconfigureOnChangeFilter.decide(org.slf4j.Marker, ch.qos.logback.classic.Logger, ch.qos.logback.classic.Level, java.lang.String, java.lang.Object[], java.lang.Throwable):ch.qos.logback.core.spi.FilterReply");
    }

    @Override // ch.qos.logback.classic.turbo.TurboFilter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
        if (this.configurationWatchList != null) {
            this.mainConfigurationURL = this.configurationWatchList.getMainURL();
            if (this.mainConfigurationURL == null) {
                addWarn("Due to missing top level configuration file, automatic reconfiguration is impossible.");
                return;
            }
            List<File> watchList = this.configurationWatchList.getCopyOfFileWatchList();
            long inSeconds = this.refreshPeriod / 1000;
            addInfo("Will scan for changes in [" + String.valueOf(watchList) + "] every " + inSeconds + " seconds. ");
            synchronized (this.configurationWatchList) {
                updateNextCheck(System.currentTimeMillis());
            }
            super.start();
            return;
        }
        addWarn("Empty ConfigurationWatchList in context");
    }

    public String toString() {
        return "ReconfigureOnChangeFilter{invocationCounter=" + this.invocationCounter + "}";
    }

    private void updateMaskIfNecessary(long now) {
        long timeElapsedSinceLastMaskUpdateCheck = now - this.lastMaskCheck;
        this.lastMaskCheck = now;
        if (timeElapsedSinceLastMaskUpdateCheck < 100 && this.mask < 65535) {
            this.mask = (this.mask << 1) | 1;
        } else if (timeElapsedSinceLastMaskUpdateCheck > MASK_DECREASE_THRESHOLD) {
            this.mask >>>= 2;
        }
    }

    void detachReconfigurationToNewThread() {
        addInfo("Detected change in [" + String.valueOf(this.configurationWatchList.getCopyOfFileWatchList()) + "]");
        this.context.getExecutorService().submit(new ReconfiguringThread());
    }

    void updateNextCheck(long now) {
        this.nextCheck = now + this.refreshPeriod;
    }

    protected boolean changeDetected(long now) {
        if (now >= this.nextCheck) {
            updateNextCheck(now);
            return this.configurationWatchList.changeDetected();
        }
        return false;
    }

    void disableSubsequentReconfiguration() {
        this.nextCheck = Long.MAX_VALUE;
    }

    public long getRefreshPeriod() {
        return this.refreshPeriod;
    }

    public void setRefreshPeriod(long refreshPeriod) {
        this.refreshPeriod = refreshPeriod;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/turbo/ReconfigureOnChangeFilter$ReconfiguringThread.class */
    class ReconfiguringThread implements Runnable {
        ReconfiguringThread() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ReconfigureOnChangeFilter.this.mainConfigurationURL != null) {
                LoggerContext lc = (LoggerContext) ReconfigureOnChangeFilter.this.context;
                ReconfigureOnChangeFilter.this.addInfo("Will reset and reconfigure context named [" + ReconfigureOnChangeFilter.this.context.getName() + "]");
                if (ReconfigureOnChangeFilter.this.mainConfigurationURL.toString().endsWith("xml")) {
                    performXMLConfiguration(lc);
                    return;
                } else {
                    if (ReconfigureOnChangeFilter.this.mainConfigurationURL.toString().endsWith("groovy")) {
                        ReconfigureOnChangeFilter.this.addError("Groovy configuration disabled due to Java 9 compilation issues.");
                        return;
                    }
                    return;
                }
            }
            ReconfigureOnChangeFilter.this.addInfo("Due to missing top level configuration file, skipping reconfiguration");
        }

        private void performXMLConfiguration(LoggerContext lc) {
            JoranConfigurator jc = new JoranConfigurator();
            jc.setContext(ReconfigureOnChangeFilter.this.context);
            StatusUtil statusUtil = new StatusUtil(ReconfigureOnChangeFilter.this.context);
            Model failSafeTop = jc.recallSafeConfiguration();
            URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(ReconfigureOnChangeFilter.this.context);
            lc.reset();
            long threshold = System.currentTimeMillis();
            try {
                jc.doConfigure(ReconfigureOnChangeFilter.this.mainConfigurationURL);
                if (statusUtil.hasXMLParsingErrors(threshold)) {
                    fallbackConfiguration(lc, failSafeTop, mainURL);
                }
            } catch (JoranException e) {
                fallbackConfiguration(lc, failSafeTop, mainURL);
            }
        }

        private void fallbackConfiguration(LoggerContext lc, Model failSafeTop, URL mainURL) {
            JoranConfigurator joranConfigurator = new JoranConfigurator();
            joranConfigurator.setContext(ReconfigureOnChangeFilter.this.context);
            if (failSafeTop != null) {
                ReconfigureOnChangeFilter.this.addWarn("Falling back to previously registered safe configuration.");
                try {
                    lc.reset();
                    JoranConfigurator.informContextOfURLUsedForConfiguration(ReconfigureOnChangeFilter.this.context, mainURL);
                    ModelUtil.resetForReuse(failSafeTop);
                    joranConfigurator.processModel(failSafeTop);
                    ReconfigureOnChangeFilter.this.addInfo("Re-registering previous fallback configuration once more as a fallback configuration point");
                    joranConfigurator.registerSafeConfiguration(failSafeTop);
                    return;
                } catch (Exception e) {
                    ReconfigureOnChangeFilter.this.addError("Unexpected exception thrown by a configuration considered safe.", e);
                    return;
                }
            }
            ReconfigureOnChangeFilter.this.addWarn("No previous configuration to fall back on.");
        }
    }
}
