package org.springframework.boot.loader.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.zip.Inflater;
import org.springframework.boot.loader.zip.ZipContent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:org/springframework/boot/loader/jar/NestedJarFileResources.class */
public class NestedJarFileResources implements Runnable {
    private static final int INFLATER_CACHE_LIMIT = 20;
    private ZipContent zipContent;
    private ZipContent zipContentForManifest;
    private final Set<InputStream> inputStreams = Collections.newSetFromMap(new WeakHashMap());
    private Deque<Inflater> inflaterCache = new ArrayDeque();

    /* JADX INFO: Access modifiers changed from: package-private */
    public NestedJarFileResources(File file, String nestedEntryName) throws IOException {
        this.zipContent = ZipContent.open(file.toPath(), nestedEntryName);
        this.zipContentForManifest = this.zipContent.getKind() != ZipContent.Kind.NESTED_DIRECTORY ? null : ZipContent.open(file.toPath());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZipContent zipContent() {
        return this.zipContent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZipContent zipContentForManifest() {
        return this.zipContentForManifest != null ? this.zipContentForManifest : this.zipContent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addInputStream(InputStream inputStream) {
        synchronized (this.inputStreams) {
            this.inputStreams.add(inputStream);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeInputStream(InputStream inputStream) {
        synchronized (this.inputStreams) {
            this.inputStreams.remove(inputStream);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Runnable createInflatorCleanupAction(Inflater inflater) {
        return () -> {
            endOrCacheInflater(inflater);
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Inflater getOrCreateInflater() {
        Deque<Inflater> inflaterCache = this.inflaterCache;
        if (inflaterCache != null) {
            synchronized (inflaterCache) {
                Inflater inflater = this.inflaterCache.poll();
                if (inflater != null) {
                    return inflater;
                }
            }
        }
        return new Inflater(true);
    }

    private void endOrCacheInflater(Inflater inflater) {
        Deque<Inflater> inflaterCache = this.inflaterCache;
        if (inflaterCache != null) {
            synchronized (inflaterCache) {
                if (this.inflaterCache == inflaterCache && inflaterCache.size() < 20) {
                    inflater.reset();
                    this.inflaterCache.add(inflater);
                    return;
                }
            }
        }
        inflater.end();
    }

    @Override // java.lang.Runnable
    public void run() {
        releaseAll();
    }

    private void releaseAll() {
        IOException exceptionChain = releaseZipContentForManifest(releaseZipContent(releaseInputStreams(releaseInflators(null))));
        if (exceptionChain != null) {
            throw new UncheckedIOException(exceptionChain);
        }
    }

    private IOException releaseInflators(IOException exceptionChain) {
        Deque<Inflater> inflaterCache = this.inflaterCache;
        if (inflaterCache != null) {
            try {
                synchronized (inflaterCache) {
                    inflaterCache.forEach((v0) -> {
                        v0.end();
                    });
                }
            } finally {
                this.inflaterCache = null;
            }
        }
        return exceptionChain;
    }

    private IOException releaseInputStreams(IOException exceptionChain) {
        synchronized (this.inputStreams) {
            for (InputStream inputStream : List.copyOf(this.inputStreams)) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    exceptionChain = addToExceptionChain(exceptionChain, ex);
                }
            }
            this.inputStreams.clear();
        }
        return exceptionChain;
    }

    private IOException releaseZipContent(IOException exceptionChain) {
        ZipContent zipContent = this.zipContent;
        if (zipContent != null) {
            try {
                try {
                    zipContent.close();
                    this.zipContent = null;
                } catch (IOException ex) {
                    exceptionChain = addToExceptionChain(exceptionChain, ex);
                    this.zipContent = null;
                }
            } catch (Throwable th) {
                this.zipContent = null;
                throw th;
            }
        }
        return exceptionChain;
    }

    private IOException releaseZipContentForManifest(IOException exceptionChain) {
        ZipContent zipContentForManifest = this.zipContentForManifest;
        if (zipContentForManifest != null) {
            try {
                try {
                    zipContentForManifest.close();
                    this.zipContentForManifest = null;
                } catch (IOException ex) {
                    exceptionChain = addToExceptionChain(exceptionChain, ex);
                    this.zipContentForManifest = null;
                }
            } catch (Throwable th) {
                this.zipContentForManifest = null;
                throw th;
            }
        }
        return exceptionChain;
    }

    private IOException addToExceptionChain(IOException exceptionChain, IOException ex) {
        if (exceptionChain != null) {
            exceptionChain.addSuppressed(ex);
            return exceptionChain;
        }
        return ex;
    }
}
