package org.slf4j.event;

import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/event/KeyValuePair.class */
public class KeyValuePair {
    public final String key;
    public final Object value;

    public KeyValuePair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.key) + "=\"" + String.valueOf(this.value) + "\"";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyValuePair that = (KeyValuePair) o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }
}
