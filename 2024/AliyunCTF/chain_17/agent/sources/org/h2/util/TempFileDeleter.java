package org.h2.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/TempFileDeleter.class */
public class TempFileDeleter {
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final HashMap<PhantomReference<?>, Object> refMap = new HashMap<>();

    private TempFileDeleter() {
    }

    public static TempFileDeleter getInstance() {
        return new TempFileDeleter();
    }

    public synchronized Reference<?> addFile(Object obj, Object obj2) {
        if (!(obj instanceof String) && !(obj instanceof AutoCloseable)) {
            throw DbException.getUnsupportedException("Unsupported resource " + obj);
        }
        IOUtils.trace("TempFileDeleter.addFile", obj instanceof String ? (String) obj : "-", obj2);
        PhantomReference<?> phantomReference = new PhantomReference<>(obj2, this.queue);
        this.refMap.put(phantomReference, obj);
        deleteUnused();
        return phantomReference;
    }

    public synchronized void deleteFile(Reference<?> reference, Object obj) {
        Object remove;
        if (reference != null && (remove = this.refMap.remove(reference)) != null) {
            if (SysProperties.CHECK && obj != null && !remove.equals(obj)) {
                throw DbException.getInternalError("f2:" + remove + " f:" + obj);
            }
            obj = remove;
        }
        if (obj instanceof String) {
            String str = (String) obj;
            if (FileUtils.exists(str)) {
                try {
                    IOUtils.trace("TempFileDeleter.deleteFile", str, null);
                    FileUtils.tryDelete(str);
                    return;
                } catch (Exception e) {
                    return;
                }
            }
            return;
        }
        if (obj instanceof AutoCloseable) {
            AutoCloseable autoCloseable = (AutoCloseable) obj;
            try {
                IOUtils.trace("TempFileDeleter.deleteCloseable", "-", null);
                autoCloseable.close();
            } catch (Exception e2) {
            }
        }
    }

    public void deleteAll() {
        Iterator it = new ArrayList(this.refMap.values()).iterator();
        while (it.hasNext()) {
            deleteFile(null, it.next());
        }
        deleteUnused();
    }

    public void deleteUnused() {
        while (true) {
            Reference<? extends Object> poll = this.queue.poll();
            if (poll != null) {
                deleteFile(poll, null);
            } else {
                return;
            }
        }
    }

    public void stopAutoDelete(Reference<?> reference, Object obj) {
        IOUtils.trace("TempFileDeleter.stopAutoDelete", obj instanceof String ? (String) obj : "-", reference);
        if (reference != null) {
            Object remove = this.refMap.remove(reference);
            if (SysProperties.CHECK && (remove == null || !remove.equals(obj))) {
                throw DbException.getInternalError("f2:" + remove + ' ' + (remove == null ? "" : remove) + " f:" + obj);
            }
        }
        deleteUnused();
    }
}
