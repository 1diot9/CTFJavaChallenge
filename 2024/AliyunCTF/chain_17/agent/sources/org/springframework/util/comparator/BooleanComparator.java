package org.springframework.util.comparator;

import java.io.Serializable;
import java.util.Comparator;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/comparator/BooleanComparator.class */
public class BooleanComparator implements Comparator<Boolean>, Serializable {
    public static final BooleanComparator TRUE_LOW = new BooleanComparator(true);
    public static final BooleanComparator TRUE_HIGH = new BooleanComparator(false);
    private final boolean trueLow;

    public BooleanComparator(boolean trueLow) {
        this.trueLow = trueLow;
    }

    @Override // java.util.Comparator
    public int compare(Boolean left, Boolean right) {
        int multiplier = this.trueLow ? -1 : 1;
        return multiplier * Boolean.compare(left.booleanValue(), right.booleanValue());
    }

    @Override // java.util.Comparator
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof BooleanComparator) {
                BooleanComparator that = (BooleanComparator) other;
                if (this.trueLow == that.trueLow) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Boolean.hashCode(this.trueLow);
    }

    public String toString() {
        return "BooleanComparator: " + (this.trueLow ? "true low" : "true high");
    }
}
