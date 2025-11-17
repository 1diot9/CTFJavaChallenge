package ch.qos.logback.core;

import ch.qos.logback.core.joran.spi.ConsoleTarget;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.Loader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/ConsoleAppender.class */
public class ConsoleAppender<E> extends OutputStreamAppender<E> {
    protected ConsoleTarget target = ConsoleTarget.SystemOut;
    protected boolean withJansi = false;
    private static final String AnsiConsole_CLASS_NAME = "org.fusesource.jansi.AnsiConsole";
    private static final String JANSI2_OUT_METHOD_NAME = "out";
    private static final String JANSI2_ERR_METHOD_NAME = "err";
    private static final String wrapSystemOut_METHOD_NAME = "wrapSystemOut";
    private static final String wrapSystemErr_METHOD_NAME = "wrapSystemErr";
    private static final Class<?>[] ARGUMENT_TYPES = {PrintStream.class};

    public void setTarget(String value) {
        ConsoleTarget t = ConsoleTarget.findByName(value.trim());
        if (t == null) {
            targetWarn(value);
        } else {
            this.target = t;
        }
    }

    public String getTarget() {
        return this.target.getName();
    }

    private void targetWarn(String val) {
        Status status = new WarnStatus("[" + val + "] should be one of " + Arrays.toString(ConsoleTarget.values()), this);
        status.add(new WarnStatus("Using previously set target, System.out by default.", this));
        addStatus(status);
    }

    @Override // ch.qos.logback.core.OutputStreamAppender, ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        OutputStream targetStream = this.target.getStream();
        if (this.withJansi) {
            targetStream = wrapWithJansi(targetStream);
        }
        setOutputStream(targetStream);
        super.start();
    }

    private OutputStream wrapWithJansi(OutputStream targetStream) {
        try {
            addInfo("Enabling JANSI AnsiPrintStream for the console.");
            ClassLoader classLoader = Loader.getClassLoaderOfObject(this.context);
            Class<?> classObj = classLoader.loadClass(AnsiConsole_CLASS_NAME);
            String methodNameJansi2 = this.target == ConsoleTarget.SystemOut ? JANSI2_OUT_METHOD_NAME : JANSI2_ERR_METHOD_NAME;
            Optional<Method> optOutMethod = Arrays.stream(classObj.getMethods()).filter(m -> {
                return m.getName().equals(methodNameJansi2);
            }).filter(m2 -> {
                return m2.getParameters().length == 0;
            }).filter(m3 -> {
                return Modifier.isStatic(m3.getModifiers());
            }).filter(m4 -> {
                return PrintStream.class.isAssignableFrom(m4.getReturnType());
            }).findAny();
            if (optOutMethod.isPresent()) {
                Method outMethod = optOutMethod.orElseThrow(() -> {
                    return new NoSuchElementException("No value present");
                });
                return (PrintStream) outMethod.invoke(null, new Object[0]);
            }
            String methodName = this.target == ConsoleTarget.SystemOut ? wrapSystemOut_METHOD_NAME : wrapSystemErr_METHOD_NAME;
            Method method = classObj.getMethod(methodName, ARGUMENT_TYPES);
            return (OutputStream) method.invoke(null, new PrintStream(targetStream));
        } catch (Exception e) {
            addWarn("Failed to create AnsiPrintStream. Falling back on the default stream.", e);
            return targetStream;
        }
    }

    public boolean isWithJansi() {
        return this.withJansi;
    }

    public void setWithJansi(boolean withJansi) {
        this.withJansi = withJansi;
    }
}
