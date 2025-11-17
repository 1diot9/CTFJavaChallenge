package org.slf4j.helpers;

import java.io.PrintStream;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/Reporter.class */
public class Reporter {
    static final String SLF4J_INFO_PREFIX = "SLF4J(I): ";
    static final String SLF4J_WARN_PREFIX = "SLF4J(W): ";
    static final String SLF4J_ERROR_PREFIX = "SLF4J(E): ";
    public static final String SLF4J_INTERNAL_REPORT_STREAM_KEY = "slf4j.internal.report.stream";
    public static final String SLF4J_INTERNAL_VERBOSITY_KEY = "slf4j.internal.verbosity";
    private static final String[] SYSOUT_KEYS = {"System.out", "stdout", "sysout"};
    private static final TargetChoice TARGET_CHOICE = initTargetChoice();
    private static final Level INTERNAL_VERBOSITY = initVerbosity();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/Reporter$TargetChoice.class */
    public enum TargetChoice {
        Stderr,
        Stdout
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/Reporter$Level.class */
    public enum Level {
        INFO(1),
        WARN(2),
        ERROR(3);

        int levelInt;

        Level(int levelInt) {
            this.levelInt = levelInt;
        }

        private int getLevelInt() {
            return this.levelInt;
        }
    }

    private static TargetChoice initTargetChoice() {
        String reportStreamStr = System.getProperty(SLF4J_INTERNAL_REPORT_STREAM_KEY);
        if (reportStreamStr == null || reportStreamStr.isEmpty()) {
            return TargetChoice.Stderr;
        }
        for (String s : SYSOUT_KEYS) {
            if (s.equalsIgnoreCase(reportStreamStr)) {
                return TargetChoice.Stdout;
            }
        }
        return TargetChoice.Stderr;
    }

    private static Level initVerbosity() {
        String verbosityStr = System.getProperty(SLF4J_INTERNAL_VERBOSITY_KEY);
        if (verbosityStr == null || verbosityStr.isEmpty()) {
            return Level.INFO;
        }
        if (verbosityStr.equalsIgnoreCase("ERROR")) {
            return Level.ERROR;
        }
        if (verbosityStr.equalsIgnoreCase("WARN")) {
            return Level.WARN;
        }
        return Level.INFO;
    }

    static boolean isEnabledFor(Level level) {
        return level.levelInt >= INTERNAL_VERBOSITY.levelInt;
    }

    private static PrintStream getTarget() {
        switch (TARGET_CHOICE) {
            case Stderr:
            default:
                return System.err;
            case Stdout:
                return System.out;
        }
    }

    public static void info(String msg) {
        if (isEnabledFor(Level.INFO)) {
            getTarget().println(SLF4J_INFO_PREFIX + msg);
        }
    }

    public static final void warn(String msg) {
        if (isEnabledFor(Level.WARN)) {
            getTarget().println(SLF4J_WARN_PREFIX + msg);
        }
    }

    public static final void error(String msg, Throwable t) {
        getTarget().println(SLF4J_ERROR_PREFIX + msg);
        getTarget().println("SLF4J(E): Reported exception:");
        t.printStackTrace(getTarget());
    }

    public static final void error(String msg) {
        getTarget().println(SLF4J_ERROR_PREFIX + msg);
    }
}
