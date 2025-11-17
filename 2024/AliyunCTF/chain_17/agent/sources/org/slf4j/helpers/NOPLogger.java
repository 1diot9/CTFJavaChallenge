package org.slf4j.helpers;

import org.slf4j.Logger;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/NOPLogger.class */
public class NOPLogger extends NamedLoggerBase implements Logger {
    private static final long serialVersionUID = -517220405410904473L;
    public static final NOPLogger NOP_LOGGER = new NOPLogger();

    protected NOPLogger() {
    }

    @Override // org.slf4j.helpers.NamedLoggerBase, org.slf4j.Logger
    public String getName() {
        return "NOP";
    }

    @Override // org.slf4j.Logger
    public final boolean isTraceEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void trace(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void trace(String format, Object arg) {
    }

    @Override // org.slf4j.Logger
    public final void trace(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void trace(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void trace(String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isDebugEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void debug(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void debug(String format, Object arg) {
    }

    @Override // org.slf4j.Logger
    public final void debug(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void debug(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void debug(String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isInfoEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void info(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void info(String format, Object arg1) {
    }

    @Override // org.slf4j.Logger
    public final void info(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void info(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void info(String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isWarnEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void warn(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void warn(String format, Object arg1) {
    }

    @Override // org.slf4j.Logger
    public final void warn(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void warn(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void warn(String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isErrorEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void error(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void error(String format, Object arg1) {
    }

    @Override // org.slf4j.Logger
    public final void error(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void error(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void error(String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void trace(Marker marker, String msg) {
    }

    @Override // org.slf4j.Logger
    public final void trace(Marker marker, String format, Object arg) {
    }

    @Override // org.slf4j.Logger
    public final void trace(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void trace(Marker marker, String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void trace(Marker marker, String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void debug(Marker marker, String msg) {
    }

    @Override // org.slf4j.Logger
    public final void debug(Marker marker, String format, Object arg) {
    }

    @Override // org.slf4j.Logger
    public final void debug(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void debug(Marker marker, String format, Object... arguments) {
    }

    @Override // org.slf4j.Logger
    public final void debug(Marker marker, String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void info(Marker marker, String msg) {
    }

    @Override // org.slf4j.Logger
    public final void info(Marker marker, String format, Object arg) {
    }

    @Override // org.slf4j.Logger
    public final void info(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void info(Marker marker, String format, Object... arguments) {
    }

    @Override // org.slf4j.Logger
    public final void info(Marker marker, String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void warn(Marker marker, String msg) {
    }

    @Override // org.slf4j.Logger
    public final void warn(Marker marker, String format, Object arg) {
    }

    @Override // org.slf4j.Logger
    public final void warn(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void warn(Marker marker, String format, Object... arguments) {
    }

    @Override // org.slf4j.Logger
    public final void warn(Marker marker, String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void error(Marker marker, String msg) {
    }

    @Override // org.slf4j.Logger
    public final void error(Marker marker, String format, Object arg) {
    }

    @Override // org.slf4j.Logger
    public final void error(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void error(Marker marker, String format, Object... arguments) {
    }

    @Override // org.slf4j.Logger
    public final void error(Marker marker, String msg, Throwable t) {
    }
}
