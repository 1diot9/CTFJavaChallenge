package org.h2.value;

import java.text.CollationKey;
import java.text.Collator;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.util.SmallLRUCache;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/CompareModeDefault.class */
public class CompareModeDefault extends CompareMode {
    private final Collator collator;
    private final SmallLRUCache<String, CollationKey> collationKeys;
    private volatile CompareModeDefault caseInsensitive;

    /* JADX INFO: Access modifiers changed from: protected */
    public CompareModeDefault(String str, int i) {
        super(str, i);
        this.collator = CompareMode.getCollator(str);
        if (this.collator == null) {
            throw DbException.getInternalError(str);
        }
        this.collator.setStrength(i);
        int i2 = SysProperties.COLLATOR_CACHE_SIZE;
        if (i2 != 0) {
            this.collationKeys = SmallLRUCache.newInstance(i2);
        } else {
            this.collationKeys = null;
        }
    }

    @Override // org.h2.value.CompareMode
    public int compareString(String str, String str2, boolean z) {
        int compare;
        if (z && getStrength() > 1) {
            CompareModeDefault compareModeDefault = this.caseInsensitive;
            if (compareModeDefault == null) {
                CompareModeDefault compareModeDefault2 = new CompareModeDefault(getName(), 1);
                compareModeDefault = compareModeDefault2;
                this.caseInsensitive = compareModeDefault2;
            }
            return compareModeDefault.compareString(str, str2, false);
        }
        if (this.collationKeys != null) {
            compare = getKey(str).compareTo(getKey(str2));
        } else {
            compare = this.collator.compare(str, str2);
        }
        return compare;
    }

    @Override // org.h2.value.CompareMode
    public boolean equalsChars(String str, int i, String str2, int i2, boolean z) {
        return compareString(str.substring(i, i + 1), str2.substring(i2, i2 + 1), z) == 0;
    }

    private CollationKey getKey(String str) {
        CollationKey collationKey;
        synchronized (this.collationKeys) {
            CollationKey collationKey2 = this.collationKeys.get(str);
            if (collationKey2 == null) {
                collationKey2 = this.collator.getCollationKey(str);
                this.collationKeys.put(str, collationKey2);
            }
            collationKey = collationKey2;
        }
        return collationKey;
    }
}
