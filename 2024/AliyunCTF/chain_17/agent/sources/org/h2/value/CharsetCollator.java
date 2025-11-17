package org.h2.value;

import java.nio.charset.Charset;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import org.apache.tomcat.util.bcel.Const;
import org.h2.util.Bits;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/CharsetCollator.class */
public class CharsetCollator extends Collator {
    static final Comparator<byte[]> COMPARATOR = Bits::compareNotNullSigned;
    private final Charset charset;

    public CharsetCollator(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return this.charset;
    }

    @Override // java.text.Collator
    public int compare(String str, String str2) {
        return COMPARATOR.compare(toBytes(str), toBytes(str2));
    }

    byte[] toBytes(String str) {
        if (getStrength() <= 1) {
            str = str.toUpperCase(Locale.ROOT);
        }
        return str.getBytes(this.charset);
    }

    @Override // java.text.Collator
    public CollationKey getCollationKey(String str) {
        return new CharsetCollationKey(str);
    }

    @Override // java.text.Collator
    public int hashCode() {
        return Const.MAX_ARRAY_DIMENSIONS;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/CharsetCollator$CharsetCollationKey.class */
    private class CharsetCollationKey extends CollationKey {
        private final byte[] bytes;

        CharsetCollationKey(String str) {
            super(str);
            this.bytes = CharsetCollator.this.toBytes(str);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.Comparable
        public int compareTo(CollationKey collationKey) {
            return CharsetCollator.COMPARATOR.compare(this.bytes, collationKey.toByteArray());
        }

        @Override // java.text.CollationKey
        public byte[] toByteArray() {
            return this.bytes;
        }
    }
}
