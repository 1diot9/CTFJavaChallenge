package cn.hutool.core.text;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import java.io.Serializable;
import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/StrBuilder.class */
public class StrBuilder implements CharSequence, Appendable, Serializable {
    private static final long serialVersionUID = 6341229705927508451L;
    public static final int DEFAULT_CAPACITY = 16;
    private char[] value;
    private int position;

    public static StrBuilder create() {
        return new StrBuilder();
    }

    public static StrBuilder create(int initialCapacity) {
        return new StrBuilder(initialCapacity);
    }

    public static StrBuilder create(CharSequence... strs) {
        return new StrBuilder(strs);
    }

    public StrBuilder() {
        this(16);
    }

    public StrBuilder(int initialCapacity) {
        this.value = new char[initialCapacity];
    }

    public StrBuilder(CharSequence... strs) {
        this(ArrayUtil.isEmpty((Object[]) strs) ? 16 : totalLength(strs) + 16);
        for (CharSequence str : strs) {
            append(str);
        }
    }

    public StrBuilder append(Object obj) {
        return insert(this.position, obj);
    }

    @Override // java.lang.Appendable
    public StrBuilder append(char c) {
        return insert(this.position, c);
    }

    public StrBuilder append(char[] src) {
        if (ArrayUtil.isEmpty(src)) {
            return this;
        }
        return append(src, 0, src.length);
    }

    public StrBuilder append(char[] src, int srcPos, int length) {
        return insert(this.position, src, srcPos, length);
    }

    @Override // java.lang.Appendable
    public StrBuilder append(CharSequence csq) {
        return insert(this.position, csq);
    }

    @Override // java.lang.Appendable
    public StrBuilder append(CharSequence csq, int start, int end) {
        return insert(this.position, csq, start, end);
    }

    public StrBuilder insert(int index, Object obj) {
        if (obj instanceof CharSequence) {
            return insert(index, (CharSequence) obj);
        }
        return insert(index, (CharSequence) Convert.toStr(obj));
    }

    public StrBuilder insert(int index, char c) {
        if (index < 0) {
            index = this.position + index;
        }
        if (index < 0) {
            throw new StringIndexOutOfBoundsException(index);
        }
        moveDataAfterIndex(index, 1);
        this.value[index] = c;
        this.position = Math.max(this.position, index) + 1;
        return this;
    }

    public StrBuilder insert(int index, char[] src) {
        if (ArrayUtil.isEmpty(src)) {
            return this;
        }
        return insert(index, src, 0, src.length);
    }

    public StrBuilder insert(int index, char[] src, int srcPos, int length) {
        if (ArrayUtil.isEmpty(src) || srcPos > src.length || length <= 0) {
            return this;
        }
        if (index < 0) {
            index = this.position + index;
        }
        if (index < 0) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (srcPos < 0) {
            srcPos = 0;
        } else if (srcPos + length > src.length) {
            length = src.length - srcPos;
        }
        moveDataAfterIndex(index, length);
        System.arraycopy(src, srcPos, this.value, index, length);
        this.position = Math.max(this.position, index) + length;
        return this;
    }

    public StrBuilder insert(int index, CharSequence csq) {
        if (index < 0) {
            index = this.position + index;
        }
        if (index < 0) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (null == csq) {
            csq = "";
        }
        int len = csq.length();
        moveDataAfterIndex(index, csq.length());
        if (csq instanceof String) {
            ((String) csq).getChars(0, len, this.value, index);
        } else if (csq instanceof StringBuilder) {
            ((StringBuilder) csq).getChars(0, len, this.value, index);
        } else if (csq instanceof StringBuffer) {
            ((StringBuffer) csq).getChars(0, len, this.value, index);
        } else if (csq instanceof StrBuilder) {
            ((StrBuilder) csq).getChars(0, len, this.value, index);
        } else {
            int i = 0;
            int j = this.position;
            while (i < len) {
                this.value[j] = csq.charAt(i);
                i++;
                j++;
            }
        }
        this.position = Math.max(this.position, index) + len;
        return this;
    }

    public StrBuilder insert(int index, CharSequence csq, int start, int end) {
        if (csq == null) {
            csq = "null";
        }
        int csqLen = csq.length();
        if (start > csqLen) {
            return this;
        }
        if (start < 0) {
            start = 0;
        }
        if (end > csqLen) {
            end = csqLen;
        }
        if (start >= end) {
            return this;
        }
        if (index < 0) {
            index = this.position + index;
        }
        if (index < 0) {
            throw new StringIndexOutOfBoundsException(index);
        }
        int length = end - start;
        moveDataAfterIndex(index, length);
        int i = start;
        int j = this.position;
        while (i < end) {
            this.value[j] = csq.charAt(i);
            i++;
            j++;
        }
        this.position = Math.max(this.position, index) + length;
        return this;
    }

    public StrBuilder getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        if (srcBegin < 0) {
            srcBegin = 0;
        }
        if (srcEnd < 0) {
            srcEnd = 0;
        } else if (srcEnd > this.position) {
            srcEnd = this.position;
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
        }
        System.arraycopy(this.value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
        return this;
    }

    public boolean hasContent() {
        return this.position > 0;
    }

    public boolean isEmpty() {
        return this.position == 0;
    }

    public StrBuilder clear() {
        return reset();
    }

    public StrBuilder reset() {
        this.position = 0;
        return this;
    }

    public StrBuilder delTo(int newPosition) {
        if (newPosition < 0) {
            newPosition = 0;
        }
        return del(newPosition, this.position);
    }

    public StrBuilder del(int start, int end) throws StringIndexOutOfBoundsException {
        if (start < 0) {
            start = 0;
        }
        if (end >= this.position) {
            this.position = start;
            return this;
        }
        if (end < 0) {
            end = 0;
        }
        int len = end - start;
        if (len > 0) {
            System.arraycopy(this.value, start + len, this.value, start, this.position - end);
            this.position -= len;
        } else if (len < 0) {
            throw new StringIndexOutOfBoundsException("Start is greater than End.");
        }
        return this;
    }

    public String toString(boolean isReset) {
        if (this.position > 0) {
            String s = new String(this.value, 0, this.position);
            if (isReset) {
                reset();
            }
            return s;
        }
        return "";
    }

    public String toStringAndReset() {
        return toString(true);
    }

    @Override // java.lang.CharSequence
    public String toString() {
        return toString(false);
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.position;
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        if (index < 0) {
            index = this.position + index;
        }
        if (index < 0 || index > this.position) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return this.value[index];
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        return subString(start, end);
    }

    public String subString(int start) {
        return subString(start, this.position);
    }

    public String subString(int start, int end) {
        return new String(this.value, start, end - start);
    }

    private void moveDataAfterIndex(int index, int length) {
        ensureCapacity(Math.max(this.position, index) + length);
        if (index < this.position) {
            System.arraycopy(this.value, index, this.value, index + length, this.position - index);
        } else if (index > this.position) {
            Arrays.fill(this.value, this.position, index, ' ');
        }
    }

    private void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity - this.value.length > 0) {
            expandCapacity(minimumCapacity);
        }
    }

    private void expandCapacity(int minimumCapacity) {
        int newCapacity = (this.value.length << 1) + 2;
        if (newCapacity - minimumCapacity < 0) {
            newCapacity = minimumCapacity;
        }
        if (newCapacity < 0) {
            throw new OutOfMemoryError("Capacity is too long and max than Integer.MAX");
        }
        this.value = Arrays.copyOf(this.value, newCapacity);
    }

    private static int totalLength(CharSequence... strs) {
        int totalLength = 0;
        int length = strs.length;
        for (int i = 0; i < length; i++) {
            CharSequence str = strs[i];
            totalLength += null == str ? 0 : str.length();
        }
        return totalLength;
    }
}
