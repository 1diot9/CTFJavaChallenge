package org.apache.tomcat.util.modeler;

import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/OperationInfo.class */
public class OperationInfo extends FeatureInfo {
    private static final long serialVersionUID = 4418342922072614875L;
    protected String impact = "UNKNOWN";
    protected String role = "operation";
    protected final ReadWriteLock parametersLock = new ReentrantReadWriteLock();
    protected ParameterInfo[] parameters = new ParameterInfo[0];

    public String getImpact() {
        return this.impact;
    }

    public void setImpact(String impact) {
        if (impact == null) {
            this.impact = null;
        } else {
            this.impact = impact.toUpperCase(Locale.ENGLISH);
        }
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReturnType() {
        if (this.type == null) {
            this.type = "void";
        }
        return this.type;
    }

    public void setReturnType(String returnType) {
        this.type = returnType;
    }

    public ParameterInfo[] getSignature() {
        Lock readLock = this.parametersLock.readLock();
        readLock.lock();
        try {
            return this.parameters;
        } finally {
            readLock.unlock();
        }
    }

    public void addParameter(ParameterInfo parameter) {
        Lock writeLock = this.parametersLock.writeLock();
        writeLock.lock();
        try {
            ParameterInfo[] results = new ParameterInfo[this.parameters.length + 1];
            System.arraycopy(this.parameters, 0, results, 0, this.parameters.length);
            results[this.parameters.length] = parameter;
            this.parameters = results;
            this.info = null;
            writeLock.unlock();
        } catch (Throwable th) {
            writeLock.unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MBeanOperationInfo createOperationInfo() {
        if (this.info == null) {
            int impact = 3;
            if ("ACTION".equals(getImpact())) {
                impact = 1;
            } else if ("ACTION_INFO".equals(getImpact())) {
                impact = 2;
            } else if ("INFO".equals(getImpact())) {
                impact = 0;
            }
            this.info = new MBeanOperationInfo(getName(), getDescription(), getMBeanParameterInfo(), getReturnType(), impact);
        }
        return this.info;
    }

    protected MBeanParameterInfo[] getMBeanParameterInfo() {
        ParameterInfo[] params = getSignature();
        MBeanParameterInfo[] parameters = new MBeanParameterInfo[params.length];
        for (int i = 0; i < params.length; i++) {
            parameters[i] = params[i].createParameterInfo();
        }
        return parameters;
    }
}
