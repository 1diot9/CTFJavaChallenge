package ch.qos.logback.core.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/AbstractComponentTracker.class */
public abstract class AbstractComponentTracker<C> implements ComponentTracker<C> {
    private static final boolean ACCESS_ORDERED = true;
    public static final long LINGERING_TIMEOUT = 10000;
    public static final long WAIT_BETWEEN_SUCCESSIVE_REMOVAL_ITERATIONS = 1000;
    protected int maxComponents = Integer.MAX_VALUE;
    protected long timeout = 1800000;
    LinkedHashMap<String, Entry<C>> liveMap = new LinkedHashMap<>(32, 0.75f, true);
    LinkedHashMap<String, Entry<C>> lingerersMap = new LinkedHashMap<>(16, 0.75f, true);
    long lastCheck = 0;
    private RemovalPredicator<C> byExcedent = new RemovalPredicator<C>() { // from class: ch.qos.logback.core.spi.AbstractComponentTracker.1
        @Override // ch.qos.logback.core.spi.AbstractComponentTracker.RemovalPredicator
        public boolean isSlatedForRemoval(Entry<C> entry, long timestamp) {
            return AbstractComponentTracker.this.liveMap.size() > AbstractComponentTracker.this.maxComponents;
        }
    };
    private RemovalPredicator<C> byTimeout = new RemovalPredicator<C>() { // from class: ch.qos.logback.core.spi.AbstractComponentTracker.2
        @Override // ch.qos.logback.core.spi.AbstractComponentTracker.RemovalPredicator
        public boolean isSlatedForRemoval(Entry<C> entry, long timestamp) {
            return AbstractComponentTracker.this.isEntryStale(entry, timestamp);
        }
    };
    private RemovalPredicator<C> byLingering = new RemovalPredicator<C>() { // from class: ch.qos.logback.core.spi.AbstractComponentTracker.3
        @Override // ch.qos.logback.core.spi.AbstractComponentTracker.RemovalPredicator
        public boolean isSlatedForRemoval(Entry<C> entry, long timestamp) {
            return AbstractComponentTracker.this.isEntryDoneLingering(entry, timestamp);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/AbstractComponentTracker$RemovalPredicator.class */
    public interface RemovalPredicator<C> {
        boolean isSlatedForRemoval(Entry<C> entry, long j);
    }

    protected abstract void processPriorToRemoval(C c);

    protected abstract C buildComponent(String str);

    protected abstract boolean isComponentStale(C c);

    @Override // ch.qos.logback.core.spi.ComponentTracker
    public int getComponentCount() {
        return this.liveMap.size() + this.lingerersMap.size();
    }

    private Entry<C> getFromEitherMap(String key) {
        Entry<C> entry = this.liveMap.get(key);
        if (entry != null) {
            return entry;
        }
        return this.lingerersMap.get(key);
    }

    @Override // ch.qos.logback.core.spi.ComponentTracker
    public synchronized C find(String key) {
        Entry<C> entry = getFromEitherMap(key);
        if (entry == null) {
            return null;
        }
        return entry.component;
    }

    @Override // ch.qos.logback.core.spi.ComponentTracker
    public synchronized C getOrCreate(String key, long timestamp) {
        Entry<C> entry = getFromEitherMap(key);
        if (entry == null) {
            C c = buildComponent(key);
            entry = new Entry<>(key, c, timestamp);
            this.liveMap.put(key, entry);
        } else {
            entry.setTimestamp(timestamp);
        }
        return entry.component;
    }

    @Override // ch.qos.logback.core.spi.ComponentTracker
    public void endOfLife(String key) {
        Entry<C> entry = this.liveMap.remove(key);
        if (entry == null) {
            return;
        }
        this.lingerersMap.put(key, entry);
    }

    @Override // ch.qos.logback.core.spi.ComponentTracker
    public synchronized void removeStaleComponents(long now) {
        if (isTooSoonForRemovalIteration(now)) {
            return;
        }
        removeExcedentComponents();
        removeStaleComponentsFromMainMap(now);
        removeStaleComponentsFromLingerersMap(now);
    }

    private void removeExcedentComponents() {
        genericStaleComponentRemover(this.liveMap, 0L, this.byExcedent);
    }

    private void removeStaleComponentsFromMainMap(long now) {
        genericStaleComponentRemover(this.liveMap, now, this.byTimeout);
    }

    private void removeStaleComponentsFromLingerersMap(long now) {
        genericStaleComponentRemover(this.lingerersMap, now, this.byLingering);
    }

    private void genericStaleComponentRemover(LinkedHashMap<String, Entry<C>> map, long now, RemovalPredicator<C> removalPredicator) {
        Iterator<Map.Entry<String, Entry<C>>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Entry<C>> mapEntry = iter.next();
            Entry<C> entry = mapEntry.getValue();
            if (removalPredicator.isSlatedForRemoval(entry, now)) {
                iter.remove();
                C c = entry.component;
                processPriorToRemoval(c);
            } else {
                return;
            }
        }
    }

    private boolean isTooSoonForRemovalIteration(long now) {
        if (this.lastCheck + 1000 > now) {
            return true;
        }
        this.lastCheck = now;
        return false;
    }

    private boolean isEntryStale(Entry<C> entry, long now) {
        C c = entry.component;
        return isComponentStale(c) || entry.timestamp + this.timeout < now;
    }

    private boolean isEntryDoneLingering(Entry<C> entry, long now) {
        return entry.timestamp + LINGERING_TIMEOUT < now;
    }

    @Override // ch.qos.logback.core.spi.ComponentTracker
    public Set<String> allKeys() {
        HashSet<String> allKeys = new HashSet<>(this.liveMap.keySet());
        allKeys.addAll(this.lingerersMap.keySet());
        return allKeys;
    }

    @Override // ch.qos.logback.core.spi.ComponentTracker
    public Collection<C> allComponents() {
        List<C> allComponents = new ArrayList<>();
        for (Entry<C> e : this.liveMap.values()) {
            allComponents.add(e.component);
        }
        for (Entry<C> e2 : this.lingerersMap.values()) {
            allComponents.add(e2.component);
        }
        return allComponents;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getMaxComponents() {
        return this.maxComponents;
    }

    public void setMaxComponents(int maxComponents) {
        this.maxComponents = maxComponents;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/AbstractComponentTracker$Entry.class */
    public static class Entry<C> {
        String key;
        C component;
        long timestamp;

        Entry(String k, C c, long timestamp) {
            this.key = k;
            this.component = c;
            this.timestamp = timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public int hashCode() {
            return this.key.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Entry<C> other = (Entry) obj;
            if (this.key == null) {
                if (other.key != null) {
                    return false;
                }
            } else if (!this.key.equals(other.key)) {
                return false;
            }
            if (this.component == null) {
                if (other.component != null) {
                    return false;
                }
                return true;
            }
            if (!this.component.equals(other.component)) {
                return false;
            }
            return true;
        }

        public String toString() {
            return "(" + this.key + ", " + String.valueOf(this.component) + ")";
        }
    }
}
