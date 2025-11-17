package org.h2.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/CloseWatcher.class */
public class CloseWatcher extends PhantomReference<Object> {
    private static final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private static final Set<CloseWatcher> refs = Collections.synchronizedSet(new HashSet());
    private String openStackTrace;
    private AutoCloseable closeable;

    public CloseWatcher(Object obj, ReferenceQueue<Object> referenceQueue, AutoCloseable autoCloseable) {
        super(obj, referenceQueue);
        this.closeable = autoCloseable;
    }

    public static CloseWatcher pollUnclosed() {
        CloseWatcher closeWatcher;
        do {
            closeWatcher = (CloseWatcher) queue.poll();
            if (closeWatcher == null) {
                return null;
            }
            if (refs != null) {
                refs.remove(closeWatcher);
            }
        } while (closeWatcher.closeable == null);
        return closeWatcher;
    }

    public static CloseWatcher register(Object obj, AutoCloseable autoCloseable, boolean z) {
        CloseWatcher closeWatcher = new CloseWatcher(obj, queue, autoCloseable);
        if (z) {
            Exception exc = new Exception("Open Stack Trace");
            StringWriter stringWriter = new StringWriter();
            exc.printStackTrace(new PrintWriter(stringWriter));
            closeWatcher.openStackTrace = stringWriter.toString();
        }
        refs.add(closeWatcher);
        return closeWatcher;
    }

    public static void unregister(CloseWatcher closeWatcher) {
        closeWatcher.closeable = null;
        refs.remove(closeWatcher);
    }

    public String getOpenStackTrace() {
        return this.openStackTrace;
    }

    public AutoCloseable getCloseable() {
        return this.closeable;
    }
}
