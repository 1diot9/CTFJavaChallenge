package ch.qos.logback.classic.spi;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/StackTraceElementProxy.class */
public class StackTraceElementProxy implements Serializable {
    private static final long serialVersionUID = -2374374378980555982L;
    final StackTraceElement ste;
    private transient String steAsString;

    @Deprecated
    ClassPackagingData classPackagingData;

    public StackTraceElementProxy(StackTraceElement ste) {
        if (ste == null) {
            throw new IllegalArgumentException("ste cannot be null");
        }
        this.ste = ste;
    }

    public String getSTEAsString() {
        if (this.steAsString == null) {
            this.steAsString = "at " + this.ste.toString();
        }
        return this.steAsString;
    }

    public StackTraceElement getStackTraceElement() {
        return this.ste;
    }

    public void setClassPackagingData(ClassPackagingData cpd) {
        if (this.classPackagingData != null) {
            throw new IllegalStateException("Packaging data has been already set");
        }
        this.classPackagingData = cpd;
    }

    public ClassPackagingData getClassPackagingData() {
        return this.classPackagingData;
    }

    public int hashCode() {
        return this.ste.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StackTraceElementProxy other = (StackTraceElementProxy) obj;
        if (!this.ste.equals(other.ste)) {
            return false;
        }
        if (this.classPackagingData == null) {
            if (other.classPackagingData != null) {
                return false;
            }
            return true;
        }
        if (!this.classPackagingData.equals(other.classPackagingData)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return getSTEAsString();
    }
}
