package org.springframework.beans.factory;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/BeanCreationException.class */
public class BeanCreationException extends FatalBeanException {

    @Nullable
    private final String beanName;

    @Nullable
    private final String resourceDescription;

    @Nullable
    private List<Throwable> relatedCauses;

    public BeanCreationException(String msg) {
        super(msg);
        this.beanName = null;
        this.resourceDescription = null;
    }

    public BeanCreationException(String msg, Throwable cause) {
        super(msg, cause);
        this.beanName = null;
        this.resourceDescription = null;
    }

    public BeanCreationException(String beanName, String msg) {
        super("Error creating bean with name '" + beanName + "': " + msg);
        this.beanName = beanName;
        this.resourceDescription = null;
    }

    public BeanCreationException(String beanName, String msg, Throwable cause) {
        this(beanName, msg);
        initCause(cause);
    }

    public BeanCreationException(@Nullable String resourceDescription, @Nullable String beanName, String msg) {
        super("Error creating bean with name '" + beanName + "'" + (resourceDescription != null ? " defined in " + resourceDescription : "") + ": " + msg);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
        this.relatedCauses = null;
    }

    public BeanCreationException(@Nullable String resourceDescription, String beanName, String msg, Throwable cause) {
        this(resourceDescription, beanName, msg);
        initCause(cause);
    }

    @Nullable
    public String getResourceDescription() {
        return this.resourceDescription;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }

    public void addRelatedCause(Throwable ex) {
        if (this.relatedCauses == null) {
            this.relatedCauses = new ArrayList();
        }
        this.relatedCauses.add(ex);
    }

    @Nullable
    public Throwable[] getRelatedCauses() {
        if (this.relatedCauses == null) {
            return null;
        }
        return (Throwable[]) this.relatedCauses.toArray(new Throwable[0]);
    }

    @Override // java.lang.Throwable
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (this.relatedCauses != null) {
            for (Throwable relatedCause : this.relatedCauses) {
                sb.append("\nRelated cause: ");
                sb.append(relatedCause);
            }
        }
        return sb.toString();
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream ps) {
        synchronized (ps) {
            super.printStackTrace(ps);
            if (this.relatedCauses != null) {
                for (Throwable relatedCause : this.relatedCauses) {
                    ps.println("Related cause:");
                    relatedCause.printStackTrace(ps);
                }
            }
        }
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter pw) {
        synchronized (pw) {
            super.printStackTrace(pw);
            if (this.relatedCauses != null) {
                for (Throwable relatedCause : this.relatedCauses) {
                    pw.println("Related cause:");
                    relatedCause.printStackTrace(pw);
                }
            }
        }
    }

    @Override // org.springframework.core.NestedRuntimeException
    public boolean contains(@Nullable Class<?> exClass) {
        if (super.contains(exClass)) {
            return true;
        }
        if (this.relatedCauses != null) {
            for (Throwable relatedCause : this.relatedCauses) {
                if (relatedCause instanceof NestedRuntimeException) {
                    NestedRuntimeException nested = (NestedRuntimeException) relatedCause;
                    if (nested.contains(exClass)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
}
