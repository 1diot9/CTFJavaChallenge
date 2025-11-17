package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.NumberInput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonPointer.class */
public class JsonPointer implements Serializable {
    private static final long serialVersionUID = 1;
    public static final char SEPARATOR = '/';
    protected static final JsonPointer EMPTY = new JsonPointer();
    protected final JsonPointer _nextSegment;
    protected volatile JsonPointer _head;
    protected final String _asString;
    protected final int _asStringOffset;
    protected final String _matchingPropertyName;
    protected final int _matchingElementIndex;
    protected int _hashCode;

    protected JsonPointer() {
        this._nextSegment = null;
        this._matchingPropertyName = null;
        this._matchingElementIndex = -1;
        this._asString = "";
        this._asStringOffset = 0;
    }

    protected JsonPointer(String fullString, int fullStringOffset, String segment, JsonPointer next) {
        this._asString = fullString;
        this._asStringOffset = fullStringOffset;
        this._nextSegment = next;
        this._matchingPropertyName = segment;
        this._matchingElementIndex = _parseIndex(segment);
    }

    protected JsonPointer(String fullString, int fullStringOffset, String segment, int matchIndex, JsonPointer next) {
        this._asString = fullString;
        this._asStringOffset = fullStringOffset;
        this._nextSegment = next;
        this._matchingPropertyName = segment;
        this._matchingElementIndex = matchIndex;
    }

    public static JsonPointer compile(String expr) throws IllegalArgumentException {
        if (expr == null || expr.length() == 0) {
            return EMPTY;
        }
        if (expr.charAt(0) != '/') {
            throw new IllegalArgumentException("Invalid input: JSON Pointer expression must start with '/': \"" + expr + "\"");
        }
        return _parseTail(expr);
    }

    public static JsonPointer valueOf(String expr) {
        return compile(expr);
    }

    public static JsonPointer empty() {
        return EMPTY;
    }

    public static JsonPointer forPath(JsonStreamContext context, boolean includeRoot) {
        JsonPointer jsonPointer;
        if (context == null) {
            return EMPTY;
        }
        if (!context.hasPathSegment() && (!includeRoot || !context.inRoot() || !context.hasCurrentIndex())) {
            context = context.getParent();
        }
        PointerSegment next = null;
        int approxLength = 0;
        while (context != null) {
            if (context.inObject()) {
                String propName = context.getCurrentName();
                if (propName == null) {
                    propName = "";
                }
                approxLength += 2 + propName.length();
                next = new PointerSegment(next, propName, -1);
            } else if (context.inArray() || includeRoot) {
                int ix = context.getCurrentIndex();
                approxLength += 6;
                next = new PointerSegment(next, null, ix);
            }
            context = context.getParent();
        }
        if (next == null) {
            return EMPTY;
        }
        StringBuilder pathBuilder = new StringBuilder(approxLength);
        PointerSegment last = null;
        while (next != null) {
            last = next;
            next.pathOffset = pathBuilder.length();
            pathBuilder.append('/');
            if (next.property != null) {
                _appendEscaped(pathBuilder, next.property);
            } else {
                pathBuilder.append(next.index);
            }
            next = next.next;
        }
        String fullPath = pathBuilder.toString();
        JsonPointer currPtr = EMPTY;
        for (PointerSegment currSegment = last; currSegment != null; currSegment = currSegment.prev) {
            if (currSegment.property != null) {
                jsonPointer = new JsonPointer(fullPath, currSegment.pathOffset, currSegment.property, currPtr);
            } else {
                int index = currSegment.index;
                jsonPointer = new JsonPointer(fullPath, currSegment.pathOffset, String.valueOf(index), index, currPtr);
            }
            currPtr = jsonPointer;
        }
        return currPtr;
    }

    private static void _appendEscaped(StringBuilder sb, String segment) {
        int end = segment.length();
        for (int i = 0; i < end; i++) {
            char c = segment.charAt(i);
            if (c == '/') {
                sb.append("~1");
            } else if (c == '~') {
                sb.append("~0");
            } else {
                sb.append(c);
            }
        }
    }

    public int length() {
        return this._asString.length() - this._asStringOffset;
    }

    public boolean matches() {
        return this._nextSegment == null;
    }

    public String getMatchingProperty() {
        return this._matchingPropertyName;
    }

    public int getMatchingIndex() {
        return this._matchingElementIndex;
    }

    public boolean mayMatchProperty() {
        return this._matchingPropertyName != null;
    }

    public boolean mayMatchElement() {
        return this._matchingElementIndex >= 0;
    }

    public JsonPointer last() {
        JsonPointer current = this;
        if (current == EMPTY) {
            return null;
        }
        while (true) {
            JsonPointer next = current._nextSegment;
            if (next != EMPTY) {
                current = next;
            } else {
                return current;
            }
        }
    }

    public JsonPointer append(JsonPointer tail) {
        if (this == EMPTY) {
            return tail;
        }
        if (tail == EMPTY) {
            return this;
        }
        String currentJsonPointer = this._asString;
        if (currentJsonPointer.endsWith("/")) {
            currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
        }
        return compile(currentJsonPointer + tail._asString);
    }

    public JsonPointer appendProperty(String property) {
        if (property == null || property.isEmpty()) {
            return this;
        }
        if (property.charAt(0) != '/') {
            property = '/' + property;
        }
        String currentJsonPointer = this._asString;
        if (currentJsonPointer.endsWith("/")) {
            currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
        }
        return compile(currentJsonPointer + property);
    }

    public JsonPointer appendIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Negative index cannot be appended");
        }
        String currentJsonPointer = this._asString;
        if (currentJsonPointer.endsWith("/")) {
            currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
        }
        return compile(currentJsonPointer + '/' + index);
    }

    public boolean matchesProperty(String name) {
        return this._nextSegment != null && this._matchingPropertyName.equals(name);
    }

    public JsonPointer matchProperty(String name) {
        if (this._nextSegment != null && this._matchingPropertyName.equals(name)) {
            return this._nextSegment;
        }
        return null;
    }

    public boolean matchesElement(int index) {
        return index == this._matchingElementIndex && index >= 0;
    }

    public JsonPointer matchElement(int index) {
        if (index != this._matchingElementIndex || index < 0) {
            return null;
        }
        return this._nextSegment;
    }

    public JsonPointer tail() {
        return this._nextSegment;
    }

    public JsonPointer head() {
        JsonPointer h = this._head;
        if (h == null) {
            if (this != EMPTY) {
                h = _constructHead();
            }
            this._head = h;
        }
        return h;
    }

    public String toString() {
        if (this._asStringOffset <= 0) {
            return this._asString;
        }
        return this._asString.substring(this._asStringOffset);
    }

    public int hashCode() {
        int h = this._hashCode;
        if (h == 0) {
            h = toString().hashCode();
            if (h == 0) {
                h = -1;
            }
            this._hashCode = h;
        }
        return h;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof JsonPointer)) {
            return false;
        }
        JsonPointer other = (JsonPointer) o;
        return _compare(this._asString, this._asStringOffset, other._asString, other._asStringOffset);
    }

    private final boolean _compare(String str1, int offset1, String str2, int offset2) {
        int end1 = str1.length();
        if (end1 - offset1 != str2.length() - offset2) {
            return false;
        }
        while (offset1 < end1) {
            int i = offset1;
            offset1++;
            int i2 = offset2;
            offset2++;
            if (str1.charAt(i) != str2.charAt(i2)) {
                return false;
            }
        }
        return true;
    }

    private static final int _parseIndex(String str) {
        int len = str.length();
        if (len == 0 || len > 10) {
            return -1;
        }
        char c = str.charAt(0);
        if (c <= '0') {
            return (len == 1 && c == '0') ? 0 : -1;
        }
        if (c > '9') {
            return -1;
        }
        for (int i = 1; i < len; i++) {
            char c2 = str.charAt(i);
            if (c2 > '9' || c2 < '0') {
                return -1;
            }
        }
        if (len == 10) {
            long l = NumberInput.parseLong(str);
            if (l > 2147483647L) {
                return -1;
            }
        }
        return NumberInput.parseInt(str);
    }

    protected static JsonPointer _parseTail(String fullPath) {
        PointerParent parent = null;
        int i = 1;
        int end = fullPath.length();
        int startOffset = 0;
        while (i < end) {
            char c = fullPath.charAt(i);
            if (c == '/') {
                parent = new PointerParent(parent, startOffset, fullPath.substring(startOffset + 1, i));
                startOffset = i;
                i++;
            } else {
                i++;
                if (c == '~' && i < end) {
                    StringBuilder sb = new StringBuilder(32);
                    int i2 = _extractEscapedSegment(fullPath, startOffset + 1, i, sb);
                    String segment = sb.toString();
                    if (i2 < 0) {
                        return _buildPath(fullPath, startOffset, segment, parent);
                    }
                    parent = new PointerParent(parent, startOffset, segment);
                    startOffset = i2;
                    i = i2 + 1;
                }
            }
        }
        return _buildPath(fullPath, startOffset, fullPath.substring(startOffset + 1), parent);
    }

    private static JsonPointer _buildPath(String fullPath, int fullPathOffset, String segment, PointerParent parent) {
        JsonPointer curr = new JsonPointer(fullPath, fullPathOffset, segment, EMPTY);
        while (parent != null) {
            curr = new JsonPointer(fullPath, parent.fullPathOffset, parent.segment, curr);
            parent = parent.parent;
        }
        return curr;
    }

    protected static int _extractEscapedSegment(String input, int firstCharOffset, int i, StringBuilder sb) {
        int end = input.length();
        int toCopy = (i - 1) - firstCharOffset;
        if (toCopy > 0) {
            sb.append((CharSequence) input, firstCharOffset, i - 1);
        }
        int i2 = i + 1;
        _appendEscape(sb, input.charAt(i));
        while (i2 < end) {
            char c = input.charAt(i2);
            if (c == '/') {
                return i2;
            }
            i2++;
            if (c == '~' && i2 < end) {
                i2++;
                _appendEscape(sb, input.charAt(i2));
            } else {
                sb.append(c);
            }
        }
        return -1;
    }

    private static void _appendEscape(StringBuilder sb, char c) {
        if (c == '0') {
            c = '~';
        } else if (c == '1') {
            c = '/';
        } else {
            sb.append('~');
        }
        sb.append(c);
    }

    protected JsonPointer _constructHead() {
        JsonPointer last = last();
        if (last == this) {
            return EMPTY;
        }
        int suffixLength = last.length();
        JsonPointer next = this._nextSegment;
        String fullString = toString();
        return new JsonPointer(fullString.substring(0, fullString.length() - suffixLength), 0, this._matchingPropertyName, this._matchingElementIndex, next._constructHead(suffixLength, last));
    }

    protected JsonPointer _constructHead(int suffixLength, JsonPointer last) {
        if (this == last) {
            return EMPTY;
        }
        JsonPointer next = this._nextSegment;
        String str = toString();
        return new JsonPointer(str.substring(0, str.length() - suffixLength), 0, this._matchingPropertyName, this._matchingElementIndex, next._constructHead(suffixLength, last));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonPointer$PointerParent.class */
    public static class PointerParent {
        public final PointerParent parent;
        public final int fullPathOffset;
        public final String segment;

        PointerParent(PointerParent pp, int fpo, String sgm) {
            this.parent = pp;
            this.fullPathOffset = fpo;
            this.segment = sgm;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonPointer$PointerSegment.class */
    public static class PointerSegment {
        public final PointerSegment next;
        public final String property;
        public final int index;
        public int pathOffset;
        public PointerSegment prev;

        public PointerSegment(PointerSegment next, String pn, int ix) {
            this.next = next;
            this.property = pn;
            this.index = ix;
            if (next != null) {
                next.prev = this;
            }
        }
    }

    private Object writeReplace() {
        return new Serialization(toString());
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonPointer$Serialization.class */
    static class Serialization implements Externalizable {
        private String _fullPath;

        public Serialization() {
        }

        Serialization(String fullPath) {
            this._fullPath = fullPath;
        }

        @Override // java.io.Externalizable
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(this._fullPath);
        }

        @Override // java.io.Externalizable
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this._fullPath = in.readUTF();
        }

        private Object readResolve() throws ObjectStreamException {
            return JsonPointer.compile(this._fullPath);
        }
    }
}
