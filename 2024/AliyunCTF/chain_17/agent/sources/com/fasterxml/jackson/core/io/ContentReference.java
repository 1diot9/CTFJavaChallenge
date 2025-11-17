package com.fasterxml.jackson.core.io;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/ContentReference.class */
public class ContentReference implements Serializable {
    private static final long serialVersionUID = 1;
    protected static final ContentReference UNKNOWN_CONTENT = new ContentReference(false, null);
    public static final int DEFAULT_MAX_CONTENT_SNIPPET = 500;
    protected final transient Object _rawContent;
    protected final int _offset;
    protected final int _length;
    protected final boolean _isContentTextual;

    protected ContentReference(boolean isContentTextual, Object rawContent) {
        this(isContentTextual, rawContent, -1, -1);
    }

    protected ContentReference(boolean isContentTextual, Object rawContent, int offset, int length) {
        this._isContentTextual = isContentTextual;
        this._rawContent = rawContent;
        this._offset = offset;
        this._length = length;
    }

    public static ContentReference unknown() {
        return UNKNOWN_CONTENT;
    }

    public static ContentReference construct(boolean isContentTextual, Object rawContent) {
        return new ContentReference(isContentTextual, rawContent);
    }

    public static ContentReference construct(boolean isContentTextual, Object rawContent, int offset, int length) {
        return new ContentReference(isContentTextual, rawContent, offset, length);
    }

    public static ContentReference rawReference(boolean isContentTextual, Object rawContent) {
        if (rawContent instanceof ContentReference) {
            return (ContentReference) rawContent;
        }
        return new ContentReference(isContentTextual, rawContent);
    }

    public static ContentReference rawReference(Object rawContent) {
        return rawReference(false, rawContent);
    }

    private void readObject(ObjectInputStream in) throws IOException {
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
    }

    protected Object readResolve() {
        return UNKNOWN_CONTENT;
    }

    public boolean hasTextualContent() {
        return this._isContentTextual;
    }

    public Object getRawContent() {
        return this._rawContent;
    }

    public int contentOffset() {
        return this._offset;
    }

    public int contentLength() {
        return this._length;
    }

    protected int maxContentSnippetLength() {
        return 500;
    }

    public String buildSourceDescription() {
        return appendSourceDescription(new StringBuilder(200)).toString();
    }

    public StringBuilder appendSourceDescription(StringBuilder sb) {
        String trimmed;
        Object srcRef = getRawContent();
        if (srcRef == null) {
            sb.append("UNKNOWN");
            return sb;
        }
        Class<?> srcType = srcRef instanceof Class ? (Class) srcRef : srcRef.getClass();
        String tn = srcType.getName();
        if (tn.startsWith("java.")) {
            tn = srcType.getSimpleName();
        } else if (srcRef instanceof byte[]) {
            tn = "byte[]";
        } else if (srcRef instanceof char[]) {
            tn = "char[]";
        }
        sb.append('(').append(tn).append(')');
        if (hasTextualContent()) {
            String unitStr = " chars";
            int maxLen = maxContentSnippetLength();
            int[] offsets = {contentOffset(), contentLength()};
            if (srcRef instanceof CharSequence) {
                trimmed = _truncate((CharSequence) srcRef, offsets, maxLen);
            } else if (srcRef instanceof char[]) {
                trimmed = _truncate((char[]) srcRef, offsets, maxLen);
            } else if (srcRef instanceof byte[]) {
                trimmed = _truncate((byte[]) srcRef, offsets, maxLen);
                unitStr = " bytes";
            } else {
                trimmed = null;
            }
            if (trimmed != null) {
                _append(sb, trimmed);
                if (offsets[1] > maxLen) {
                    sb.append("[truncated ").append(offsets[1] - maxLen).append(unitStr).append(']');
                }
            }
        } else if (srcRef instanceof byte[]) {
            int length = contentLength();
            if (length < 0) {
                length = ((byte[]) srcRef).length;
            }
            sb.append('[').append(length).append(" bytes]");
        }
        return sb;
    }

    protected String _truncate(CharSequence cs, int[] offsets, int maxSnippetLen) {
        _truncateOffsets(offsets, cs.length());
        int start = offsets[0];
        int length = Math.min(offsets[1], maxSnippetLen);
        return cs.subSequence(start, start + length).toString();
    }

    protected String _truncate(char[] cs, int[] offsets, int maxSnippetLen) {
        _truncateOffsets(offsets, cs.length);
        int start = offsets[0];
        int length = Math.min(offsets[1], maxSnippetLen);
        return new String(cs, start, length);
    }

    protected String _truncate(byte[] b, int[] offsets, int maxSnippetLen) {
        _truncateOffsets(offsets, b.length);
        int start = offsets[0];
        int length = Math.min(offsets[1], maxSnippetLen);
        return new String(b, start, length, StandardCharsets.UTF_8);
    }

    protected void _truncateOffsets(int[] offsets, int actualLength) {
        int start = offsets[0];
        if (start < 0) {
            start = 0;
        } else if (start >= actualLength) {
            start = actualLength;
        }
        offsets[0] = start;
        int length = offsets[1];
        int maxLength = actualLength - start;
        if (length < 0 || length > maxLength) {
            offsets[1] = maxLength;
        }
    }

    protected int _append(StringBuilder sb, String content) {
        sb.append('\"');
        int end = content.length();
        for (int i = 0; i < end; i++) {
            char ch2 = content.charAt(i);
            if (!Character.isISOControl(ch2) || !_appendEscaped(sb, ch2)) {
                sb.append(ch2);
            }
        }
        sb.append('\"');
        return content.length();
    }

    protected boolean _appendEscaped(StringBuilder sb, int ctrlChar) {
        if (ctrlChar == 13 || ctrlChar == 10) {
            return false;
        }
        sb.append('\\');
        sb.append('u');
        sb.append(CharTypes.hexToChar((ctrlChar >> 12) & 15));
        sb.append(CharTypes.hexToChar((ctrlChar >> 8) & 15));
        sb.append(CharTypes.hexToChar((ctrlChar >> 4) & 15));
        sb.append(CharTypes.hexToChar(ctrlChar & 15));
        return true;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || !(other instanceof ContentReference)) {
            return false;
        }
        ContentReference otherSrc = (ContentReference) other;
        if (this._offset != otherSrc._offset || this._length != otherSrc._length) {
            return false;
        }
        Object otherRaw = otherSrc._rawContent;
        if (this._rawContent == null) {
            return otherRaw == null;
        }
        if (otherRaw == null) {
            return false;
        }
        if ((this._rawContent instanceof File) || (this._rawContent instanceof URL) || (this._rawContent instanceof URI)) {
            return this._rawContent.equals(otherRaw);
        }
        return this._rawContent == otherSrc._rawContent;
    }

    public int hashCode() {
        return Objects.hashCode(this._rawContent);
    }
}
