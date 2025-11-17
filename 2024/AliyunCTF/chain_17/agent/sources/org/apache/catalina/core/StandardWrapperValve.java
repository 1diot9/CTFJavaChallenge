package org.apache.catalina.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardWrapperValve.class */
public final class StandardWrapperValve extends ValveBase {
    private static final StringManager sm = StringManager.getManager((Class<?>) StandardWrapperValve.class);
    private final LongAdder processingTime;
    private volatile long maxTime;
    private volatile long minTime;
    private final AtomicInteger requestCount;
    private final AtomicInteger errorCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardWrapperValve() {
        super(true);
        this.processingTime = new LongAdder();
        this.minTime = Long.MAX_VALUE;
        this.requestCount = new AtomicInteger(0);
        this.errorCount = new AtomicInteger(0);
    }

    /* JADX WARN: Removed duplicated region for block: B:104:0x0511  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0521  */
    /* JADX WARN: Removed duplicated region for block: B:109:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:110:0x04a8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:138:0x073d  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x074d  */
    /* JADX WARN: Removed duplicated region for block: B:143:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:144:0x06d4 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:172:0x0983  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x0993  */
    /* JADX WARN: Removed duplicated region for block: B:177:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:178:0x091a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:209:0x03ed  */
    /* JADX WARN: Removed duplicated region for block: B:212:0x03fd  */
    /* JADX WARN: Removed duplicated region for block: B:214:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:215:0x0384 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:243:0x0626  */
    /* JADX WARN: Removed duplicated region for block: B:246:0x0636  */
    /* JADX WARN: Removed duplicated region for block: B:248:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:249:0x05bd A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:310:0x0a5f  */
    /* JADX WARN: Removed duplicated region for block: B:313:0x0a6f  */
    /* JADX WARN: Removed duplicated region for block: B:316:0x09f6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0869  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0879  */
    /* JADX WARN: Removed duplicated region for block: B:72:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0800 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // org.apache.catalina.Valve
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void invoke(org.apache.catalina.connector.Request r9, org.apache.catalina.connector.Response r10) throws java.io.IOException, jakarta.servlet.ServletException {
        /*
            Method dump skipped, instructions count: 2681
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.core.StandardWrapperValve.invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response):void");
    }

    private void checkWrapperAvailable(Response response, StandardWrapper wrapper) throws IOException {
        long available = wrapper.getAvailable();
        if (available > 0 && available < Long.MAX_VALUE) {
            response.setDateHeader(HttpHeaders.RETRY_AFTER, available);
            response.sendError(503, sm.getString("standardWrapper.isUnavailable", wrapper.getName()));
        } else if (available == Long.MAX_VALUE) {
            response.sendError(404, sm.getString("standardWrapper.notFound", wrapper.getName()));
        }
    }

    private void exception(Request request, Response response, Throwable exception) {
        exception(request, response, exception, 500);
    }

    private void exception(Request request, Response response, Throwable exception, int errorCode) {
        request.setAttribute("jakarta.servlet.error.exception", exception);
        response.setStatus(errorCode);
        response.setError();
    }

    public long getProcessingTime() {
        return this.processingTime.sum();
    }

    public long getMaxTime() {
        return this.maxTime;
    }

    public long getMinTime() {
        return this.minTime;
    }

    @Deprecated
    public int getRequestCount() {
        return this.requestCount.get();
    }

    @Deprecated
    public int getErrorCount() {
        return this.errorCount.get();
    }

    public void incrementErrorCount() {
        this.errorCount.incrementAndGet();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
    }
}
