package org.apache.logging.log4j;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/MarkerManager.class */
public final class MarkerManager {
    private static final ConcurrentMap<String, Marker> MARKERS = new ConcurrentHashMap();

    private MarkerManager() {
    }

    public static void clear() {
        MARKERS.clear();
    }

    public static boolean exists(final String key) {
        return MARKERS.containsKey(key);
    }

    public static Marker getMarker(final String name) {
        Marker result = MARKERS.get(name);
        if (result == null) {
            MARKERS.putIfAbsent(name, new Log4jMarker(name));
            result = MARKERS.get(name);
        }
        return result;
    }

    @Deprecated
    public static Marker getMarker(final String name, final String parent) {
        Marker parentMarker = MARKERS.get(parent);
        if (parentMarker == null) {
            throw new IllegalArgumentException("Parent Marker " + parent + " has not been defined");
        }
        return getMarker(name, parentMarker);
    }

    @Deprecated
    public static Marker getMarker(final String name, final Marker parent) {
        return getMarker(name).addParents(parent);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/MarkerManager$Log4jMarker.class */
    public static class Log4jMarker implements Marker, StringBuilderFormattable {
        private static final long serialVersionUID = 100;
        private final String name;
        private volatile Marker[] parents;

        private Log4jMarker() {
            this.name = null;
            this.parents = null;
        }

        public Log4jMarker(final String name) {
            MarkerManager.requireNonNull(name, "Marker name cannot be null.");
            this.name = name;
            this.parents = null;
        }

        @Override // org.apache.logging.log4j.Marker
        public synchronized Marker addParents(final Marker... parentMarkers) {
            MarkerManager.requireNonNull(parentMarkers, "A parent marker must be specified");
            Marker[] localParents = this.parents;
            int count = 0;
            int size = parentMarkers.length;
            if (localParents != null) {
                for (Marker parent : parentMarkers) {
                    if (!contains(parent, localParents) && !parent.isInstanceOf(this)) {
                        count++;
                    }
                }
                if (count == 0) {
                    return this;
                }
                size = localParents.length + count;
            }
            Marker[] markers = new Marker[size];
            if (localParents != null) {
                System.arraycopy(localParents, 0, markers, 0, localParents.length);
            }
            int index = localParents == null ? 0 : localParents.length;
            for (Marker parent2 : parentMarkers) {
                if (localParents == null || (!contains(parent2, localParents) && !parent2.isInstanceOf(this))) {
                    int i = index;
                    index++;
                    markers[i] = parent2;
                }
            }
            this.parents = markers;
            return this;
        }

        @Override // org.apache.logging.log4j.Marker
        public synchronized boolean remove(final Marker parent) {
            MarkerManager.requireNonNull(parent, "A parent marker must be specified");
            Marker[] localParents = this.parents;
            if (localParents == null) {
                return false;
            }
            int localParentsLength = localParents.length;
            if (localParentsLength == 1) {
                if (localParents[0].equals(parent)) {
                    this.parents = null;
                    return true;
                }
                return false;
            }
            int index = 0;
            Marker[] markers = new Marker[localParentsLength - 1];
            for (Marker marker : localParents) {
                if (!marker.equals(parent)) {
                    if (index == localParentsLength - 1) {
                        return false;
                    }
                    int i = index;
                    index++;
                    markers[i] = marker;
                }
            }
            this.parents = markers;
            return true;
        }

        @Override // org.apache.logging.log4j.Marker
        public Marker setParents(final Marker... markers) {
            if (markers == null || markers.length == 0) {
                this.parents = null;
            } else {
                Marker[] array = new Marker[markers.length];
                System.arraycopy(markers, 0, array, 0, markers.length);
                this.parents = array;
            }
            return this;
        }

        @Override // org.apache.logging.log4j.Marker
        public String getName() {
            return this.name;
        }

        @Override // org.apache.logging.log4j.Marker
        public Marker[] getParents() {
            Marker[] parentsSnapshot = this.parents;
            if (parentsSnapshot == null) {
                return null;
            }
            return (Marker[]) Arrays.copyOf(parentsSnapshot, parentsSnapshot.length);
        }

        @Override // org.apache.logging.log4j.Marker
        public boolean hasParents() {
            return this.parents != null;
        }

        @Override // org.apache.logging.log4j.Marker
        @PerformanceSensitive({"allocation", "unrolled"})
        public boolean isInstanceOf(final Marker marker) {
            MarkerManager.requireNonNull(marker, "A marker parameter is required");
            if (this == marker) {
                return true;
            }
            Marker[] localParents = this.parents;
            if (localParents != null) {
                int localParentsLength = localParents.length;
                if (localParentsLength == 1) {
                    return checkParent(localParents[0], marker);
                }
                if (localParentsLength == 2) {
                    return checkParent(localParents[0], marker) || checkParent(localParents[1], marker);
                }
                for (Marker localParent : localParents) {
                    if (checkParent(localParent, marker)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        @Override // org.apache.logging.log4j.Marker
        @PerformanceSensitive({"allocation", "unrolled"})
        public boolean isInstanceOf(final String markerName) {
            Marker[] localParents;
            MarkerManager.requireNonNull(markerName, "A marker name is required");
            if (!markerName.equals(getName())) {
                Marker marker = (Marker) MarkerManager.MARKERS.get(markerName);
                if (marker != null && (localParents = this.parents) != null) {
                    int localParentsLength = localParents.length;
                    if (localParentsLength == 1) {
                        return checkParent(localParents[0], marker);
                    }
                    if (localParentsLength == 2) {
                        return checkParent(localParents[0], marker) || checkParent(localParents[1], marker);
                    }
                    for (Marker localParent : localParents) {
                        if (checkParent(localParent, marker)) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return true;
        }

        @PerformanceSensitive({"allocation", "unrolled"})
        private static boolean checkParent(final Marker parent, final Marker marker) {
            if (parent == marker) {
                return true;
            }
            Marker[] localParents = parent instanceof Log4jMarker ? ((Log4jMarker) parent).parents : parent.getParents();
            if (localParents != null) {
                int localParentsLength = localParents.length;
                if (localParentsLength == 1) {
                    return checkParent(localParents[0], marker);
                }
                if (localParentsLength == 2) {
                    return checkParent(localParents[0], marker) || checkParent(localParents[1], marker);
                }
                for (Marker localParent : localParents) {
                    if (checkParent(localParent, marker)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        @PerformanceSensitive({"allocation"})
        private static boolean contains(final Marker parent, final Marker... localParents) {
            for (Marker marker : localParents) {
                if (marker == parent) {
                    return true;
                }
            }
            return false;
        }

        @Override // org.apache.logging.log4j.Marker
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof Marker)) {
                return false;
            }
            Marker marker = (Marker) o;
            return this.name.equals(marker.getName());
        }

        @Override // org.apache.logging.log4j.Marker
        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            formatTo(sb);
            return sb.toString();
        }

        @Override // org.apache.logging.log4j.util.StringBuilderFormattable
        public void formatTo(final StringBuilder sb) {
            sb.append(this.name);
            Marker[] localParents = this.parents;
            if (localParents != null) {
                addParentInfo(sb, localParents);
            }
        }

        @PerformanceSensitive({"allocation"})
        private static void addParentInfo(final StringBuilder sb, final Marker... parents) {
            sb.append("[ ");
            boolean first = true;
            for (Marker marker : parents) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(marker.getName());
                Marker[] p = marker instanceof Log4jMarker ? ((Log4jMarker) marker).parents : marker.getParents();
                if (p != null) {
                    addParentInfo(sb, p);
                }
            }
            sb.append(" ]");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void requireNonNull(final Object obj, final String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
