package ch.qos.logback.classic.spi;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.OptionHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/ThrowableProxy.class */
public class ThrowableProxy implements IThrowableProxy {
    private Throwable throwable;
    private String className;
    private String message;
    StackTraceElementProxy[] stackTraceElementProxyArray;
    int commonFrames;
    private ThrowableProxy cause;
    private ThrowableProxy[] suppressed;
    private transient PackagingDataCalculator packagingDataCalculator;
    private boolean calculatedPackageData;
    private boolean cyclic;
    static final StackTraceElementProxy[] EMPTY_STEP = new StackTraceElementProxy[0];
    private static final ThrowableProxy[] NO_SUPPRESSED = new ThrowableProxy[0];

    public ThrowableProxy(Throwable throwable) {
        this(throwable, (Set<Throwable>) Collections.newSetFromMap(new IdentityHashMap()));
    }

    private ThrowableProxy(Throwable circular, boolean isCyclic) {
        this.suppressed = NO_SUPPRESSED;
        this.calculatedPackageData = false;
        this.throwable = circular;
        this.className = circular.getClass().getName();
        this.message = circular.getMessage();
        this.stackTraceElementProxyArray = EMPTY_STEP;
        this.cyclic = true;
    }

    public ThrowableProxy(Throwable throwable, Set<Throwable> alreadyProcessedSet) {
        this.suppressed = NO_SUPPRESSED;
        this.calculatedPackageData = false;
        this.throwable = throwable;
        this.className = throwable.getClass().getName();
        this.message = throwable.getMessage();
        this.stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(throwable.getStackTrace());
        this.cyclic = false;
        alreadyProcessedSet.add(throwable);
        Throwable nested = throwable.getCause();
        if (nested != null) {
            if (alreadyProcessedSet.contains(nested)) {
                this.cause = new ThrowableProxy(nested, true);
            } else {
                this.cause = new ThrowableProxy(nested, alreadyProcessedSet);
                this.cause.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(nested.getStackTrace(), this.stackTraceElementProxyArray);
            }
        }
        Throwable[] throwableSuppressed = throwable.getSuppressed();
        if (OptionHelper.isNotEmtpy(throwableSuppressed)) {
            List<ThrowableProxy> suppressedList = new ArrayList<>(throwableSuppressed.length);
            for (Throwable sup : throwableSuppressed) {
                if (alreadyProcessedSet.contains(sup)) {
                    suppressedList.add(new ThrowableProxy(sup, true));
                } else {
                    ThrowableProxy throwableProxy = new ThrowableProxy(sup, alreadyProcessedSet);
                    throwableProxy.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(sup.getStackTrace(), this.stackTraceElementProxyArray);
                    suppressedList.add(throwableProxy);
                }
            }
            this.suppressed = (ThrowableProxy[]) suppressedList.toArray(new ThrowableProxy[suppressedList.size()]);
        }
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public String getMessage() {
        return this.message;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public String getClassName() {
        return this.className;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public StackTraceElementProxy[] getStackTraceElementProxyArray() {
        return this.stackTraceElementProxyArray;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public boolean isCyclic() {
        return this.cyclic;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public int getCommonFrames() {
        return this.commonFrames;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public IThrowableProxy getCause() {
        return this.cause;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public IThrowableProxy[] getSuppressed() {
        return this.suppressed;
    }

    public PackagingDataCalculator getPackagingDataCalculator() {
        if (this.throwable != null && this.packagingDataCalculator == null) {
            this.packagingDataCalculator = new PackagingDataCalculator();
        }
        return this.packagingDataCalculator;
    }

    public void calculatePackagingData() {
        PackagingDataCalculator pdc;
        if (!this.calculatedPackageData && (pdc = getPackagingDataCalculator()) != null) {
            this.calculatedPackageData = true;
            pdc.calculate(this);
        }
    }

    public void fullDump() {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElementProxy step : this.stackTraceElementProxyArray) {
            String string = step.toString();
            builder.append('\t').append(string);
            ThrowableProxyUtil.subjoinPackagingData(builder, step);
            builder.append(CoreConstants.LINE_SEPARATOR);
        }
        System.out.println(builder.toString());
    }
}
