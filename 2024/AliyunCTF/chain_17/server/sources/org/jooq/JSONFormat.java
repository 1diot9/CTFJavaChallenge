package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONFormat.class */
public final class JSONFormat {
    public static final JSONFormat DEFAULT_FOR_RESULTS = new JSONFormat();
    public static final JSONFormat DEFAULT_FOR_RECORDS = new JSONFormat().header(false);
    final boolean mutable;
    boolean format;
    String newline;
    int globalIndent;
    int indent;
    String[] indented;
    boolean header;
    RecordFormat recordFormat;
    NullFormat objectNulls;
    NullFormat arrayNulls;
    boolean wrapSingleColumnRecords;
    boolean quoteNested;

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONFormat$NullFormat.class */
    public enum NullFormat {
        NULL_ON_NULL,
        ABSENT_ON_NULL
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONFormat$RecordFormat.class */
    public enum RecordFormat {
        ARRAY,
        OBJECT
    }

    public JSONFormat() {
        this(false, false, "\n", 0, 2, null, true, RecordFormat.ARRAY, NullFormat.NULL_ON_NULL, NullFormat.NULL_ON_NULL, true, false);
    }

    private JSONFormat(boolean mutable, boolean format, String newline, int globalIndent, int indent, String[] indented, boolean header, RecordFormat recordFormat, NullFormat objectNulls, NullFormat arrayNulls, boolean wrapSingleColumnRecords, boolean quoteNested) {
        String[] strArr;
        this.mutable = mutable;
        this.format = format;
        this.newline = newline;
        this.globalIndent = globalIndent;
        this.indent = indent;
        if (indented != null) {
            strArr = indented;
        } else {
            strArr = new String[4];
            strArr[0] = "";
            strArr[1] = format ? StringUtils.rightPad("", indent * 1) : "";
            strArr[2] = format ? StringUtils.rightPad("", indent * 2) : "";
            strArr[3] = format ? StringUtils.rightPad("", indent * 3) : "";
        }
        this.indented = strArr;
        this.header = header;
        this.recordFormat = recordFormat;
        this.objectNulls = objectNulls;
        this.arrayNulls = arrayNulls;
        this.wrapSingleColumnRecords = wrapSingleColumnRecords;
        this.quoteNested = quoteNested;
    }

    public final boolean mutable() {
        return this.mutable;
    }

    @NotNull
    public final JSONFormat mutable(boolean newMutable) {
        if (this.mutable ^ newMutable) {
            return new JSONFormat(newMutable, this.format, this.newline, this.globalIndent, this.indent, null, this.header, this.recordFormat, this.objectNulls, this.arrayNulls, this.wrapSingleColumnRecords, this.quoteNested);
        }
        return this;
    }

    @NotNull
    public final JSONFormat format(boolean newFormat) {
        if (this.mutable) {
            this.format = newFormat;
            return this;
        }
        return new JSONFormat(this.mutable, newFormat, this.newline, this.globalIndent, this.indent, null, this.header, this.recordFormat, this.objectNulls, this.arrayNulls, this.wrapSingleColumnRecords, this.quoteNested);
    }

    public final boolean format() {
        return this.format;
    }

    @NotNull
    public final JSONFormat newline(String newNewline) {
        if (this.mutable) {
            this.newline = newNewline;
            return this;
        }
        return new JSONFormat(this.mutable, this.format, newNewline, this.globalIndent, this.indent, this.indented, this.header, this.recordFormat, this.objectNulls, this.arrayNulls, this.wrapSingleColumnRecords, this.quoteNested);
    }

    @NotNull
    public final String newline() {
        return this.format ? this.newline : "";
    }

    @NotNull
    public final JSONFormat globalIndent(int newGlobalIndent) {
        if (this.mutable) {
            this.globalIndent = newGlobalIndent;
            return this;
        }
        return new JSONFormat(this.mutable, this.format, this.newline, newGlobalIndent, this.indent, null, this.header, this.recordFormat, this.objectNulls, this.arrayNulls, this.wrapSingleColumnRecords, this.quoteNested);
    }

    public final int globalIndent() {
        return this.globalIndent;
    }

    @NotNull
    public final JSONFormat indent(int newIndent) {
        if (this.mutable) {
            this.indent = newIndent;
            return this;
        }
        return new JSONFormat(this.mutable, this.format, this.newline, this.globalIndent, newIndent, null, this.header, this.recordFormat, this.objectNulls, this.arrayNulls, this.wrapSingleColumnRecords, this.quoteNested);
    }

    public final int indent() {
        return this.indent;
    }

    @NotNull
    public final String indentString(int level) {
        int i = level + (this.globalIndent / this.indent);
        if (i < this.indented.length) {
            return this.indented[i];
        }
        if (this.format) {
            return StringUtils.rightPad("", this.globalIndent + (this.indent * level));
        }
        return "";
    }

    @NotNull
    public final JSONFormat header(boolean newHeader) {
        if (this.mutable) {
            this.header = newHeader;
            return this;
        }
        return new JSONFormat(this.mutable, this.format, this.newline, this.globalIndent, this.indent, this.indented, newHeader, this.recordFormat, this.objectNulls, this.arrayNulls, this.wrapSingleColumnRecords, this.quoteNested);
    }

    public final boolean header() {
        return this.header;
    }

    @NotNull
    public final JSONFormat recordFormat(RecordFormat newRecordFormat) {
        if (this.mutable) {
            this.recordFormat = newRecordFormat;
            return this;
        }
        return new JSONFormat(this.mutable, this.format, this.newline, this.globalIndent, this.indent, this.indented, this.header, newRecordFormat, this.objectNulls, this.arrayNulls, this.wrapSingleColumnRecords, this.quoteNested);
    }

    @NotNull
    public final RecordFormat recordFormat() {
        return this.recordFormat;
    }

    @NotNull
    public final JSONFormat objectNulls(NullFormat newObjectNulls) {
        if (this.mutable) {
            this.objectNulls = newObjectNulls;
            return this;
        }
        return new JSONFormat(this.mutable, this.format, this.newline, this.globalIndent, this.indent, this.indented, this.header, this.recordFormat, newObjectNulls, this.arrayNulls, this.wrapSingleColumnRecords, this.quoteNested);
    }

    @NotNull
    public final NullFormat objectNulls() {
        return this.objectNulls;
    }

    @NotNull
    public final JSONFormat arrayNulls(NullFormat newArrayNulls) {
        if (this.mutable) {
            this.arrayNulls = newArrayNulls;
            return this;
        }
        return new JSONFormat(this.mutable, this.format, this.newline, this.globalIndent, this.indent, this.indented, this.header, this.recordFormat, this.objectNulls, newArrayNulls, this.wrapSingleColumnRecords, this.quoteNested);
    }

    @NotNull
    public final NullFormat arrayNulls() {
        return this.arrayNulls;
    }

    @NotNull
    public final JSONFormat wrapSingleColumnRecords(boolean newWrapSingleColumnRecords) {
        if (this.mutable) {
            this.wrapSingleColumnRecords = newWrapSingleColumnRecords;
            return this;
        }
        return new JSONFormat(this.mutable, this.format, this.newline, this.globalIndent, this.indent, this.indented, this.header, this.recordFormat, this.objectNulls, this.arrayNulls, newWrapSingleColumnRecords, this.quoteNested);
    }

    public final boolean wrapSingleColumnRecords() {
        return this.wrapSingleColumnRecords;
    }

    @NotNull
    public final JSONFormat quoteNested(boolean newQuoteNested) {
        if (this.mutable) {
            this.quoteNested = newQuoteNested;
            return this;
        }
        return new JSONFormat(this.mutable, this.format, this.newline, this.globalIndent, this.indent, this.indented, this.header, this.recordFormat, this.objectNulls, this.arrayNulls, this.wrapSingleColumnRecords, newQuoteNested);
    }

    public final boolean quoteNested() {
        return this.quoteNested;
    }
}
