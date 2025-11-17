package ch.qos.logback.core.joran.spi;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/HostClassAndPropertyDouble.class */
public class HostClassAndPropertyDouble {
    final Class<?> hostClass;
    final String propertyName;

    public HostClassAndPropertyDouble(Class<?> hostClass, String propertyName) {
        this.hostClass = hostClass;
        this.propertyName = propertyName;
    }

    public Class<?> getHostClass() {
        return this.hostClass;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.hostClass == null ? 0 : this.hostClass.hashCode());
        return (31 * result) + (this.propertyName == null ? 0 : this.propertyName.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HostClassAndPropertyDouble other = (HostClassAndPropertyDouble) obj;
        if (this.hostClass == null) {
            if (other.hostClass != null) {
                return false;
            }
        } else if (!this.hostClass.equals(other.hostClass)) {
            return false;
        }
        if (this.propertyName == null) {
            if (other.propertyName != null) {
                return false;
            }
            return true;
        }
        if (!this.propertyName.equals(other.propertyName)) {
            return false;
        }
        return true;
    }
}
