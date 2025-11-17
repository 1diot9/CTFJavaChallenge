package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XMLFormat.class */
public final class XMLFormat {
    public static final XMLFormat DEFAULT_FOR_RESULTS = new XMLFormat();
    public static final XMLFormat DEFAULT_FOR_RECORDS = new XMLFormat().header(false).xmlns(false);
    final boolean mutable;
    boolean xmlns;
    boolean format;
    String newline;
    int globalIndent;
    int indent;
    String[] indented;
    boolean header;
    RecordFormat recordFormat;
    boolean quoteNested;

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XMLFormat$RecordFormat.class */
    public enum RecordFormat {
        VALUE_ELEMENTS_WITH_FIELD_ATTRIBUTE,
        VALUE_ELEMENTS,
        COLUMN_NAME_ELEMENTS
    }

    public XMLFormat() {
        this(false, true, false, "\n", 0, 2, null, true, RecordFormat.VALUE_ELEMENTS_WITH_FIELD_ATTRIBUTE, false);
    }

    private XMLFormat(boolean mutable, boolean xmlns, boolean format, String newline, int globalIndent, int indent, String[] indented, boolean header, RecordFormat recordFormat, boolean quoteNested) {
        String[] strArr;
        this.mutable = mutable;
        this.xmlns = xmlns;
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
        this.quoteNested = quoteNested;
    }

    public final boolean mutable() {
        return this.mutable;
    }

    @NotNull
    public final XMLFormat mutable(boolean newMutable) {
        if (this.mutable ^ newMutable) {
            return new XMLFormat(newMutable, this.xmlns, this.format, this.newline, this.globalIndent, this.indent, this.indented, this.header, this.recordFormat, this.quoteNested);
        }
        return this;
    }

    @NotNull
    public final XMLFormat xmlns(boolean newXmlns) {
        if (this.mutable) {
            this.xmlns = newXmlns;
            return this;
        }
        return new XMLFormat(this.mutable, newXmlns, this.format, this.newline, this.globalIndent, this.indent, this.indented, this.header, this.recordFormat, this.quoteNested);
    }

    public final boolean xmlns() {
        return this.xmlns;
    }

    @NotNull
    public final XMLFormat format(boolean newFormat) {
        if (this.mutable) {
            this.format = newFormat;
            return this;
        }
        return new XMLFormat(this.mutable, this.xmlns, newFormat, this.newline, this.globalIndent, this.indent, null, this.header, this.recordFormat, this.quoteNested);
    }

    public final boolean format() {
        return this.format;
    }

    @NotNull
    public final XMLFormat newline(String newNewline) {
        if (this.mutable) {
            this.newline = newNewline;
            return this;
        }
        return new XMLFormat(this.mutable, this.xmlns, this.format, newNewline, this.globalIndent, this.indent, this.indented, this.header, this.recordFormat, this.quoteNested);
    }

    @NotNull
    public final String newline() {
        return this.format ? this.newline : "";
    }

    @NotNull
    public final XMLFormat globalIndent(int newGlobalIndent) {
        if (this.mutable) {
            this.globalIndent = newGlobalIndent;
            return this;
        }
        return new XMLFormat(this.mutable, this.xmlns, this.format, this.newline, newGlobalIndent, this.indent, null, this.header, this.recordFormat, this.quoteNested);
    }

    public final int globalIndent() {
        return this.globalIndent;
    }

    @NotNull
    public final XMLFormat indent(int newIndent) {
        if (this.mutable) {
            this.indent = newIndent;
            return this;
        }
        return new XMLFormat(this.mutable, this.xmlns, this.format, this.newline, this.globalIndent, newIndent, null, this.header, this.recordFormat, this.quoteNested);
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
    public final XMLFormat header(boolean newHeader) {
        if (this.mutable) {
            this.header = newHeader;
            return this;
        }
        return new XMLFormat(this.mutable, this.xmlns, this.format, this.newline, this.globalIndent, this.indent, this.indented, newHeader, this.recordFormat, this.quoteNested);
    }

    public final boolean header() {
        return this.header;
    }

    @NotNull
    public final XMLFormat recordFormat(RecordFormat newRecordFormat) {
        if (this.mutable) {
            this.recordFormat = newRecordFormat;
            return this;
        }
        return new XMLFormat(this.mutable, this.xmlns, this.format, this.newline, this.globalIndent, this.indent, this.indented, this.header, newRecordFormat, this.quoteNested);
    }

    @NotNull
    public final RecordFormat recordFormat() {
        return this.recordFormat;
    }

    @NotNull
    public final XMLFormat quoteNested(boolean newQuoteNested) {
        if (this.mutable) {
            this.quoteNested = newQuoteNested;
            return this;
        }
        return new XMLFormat(this.mutable, this.xmlns, this.format, this.newline, this.globalIndent, this.indent, this.indented, this.header, this.recordFormat, newQuoteNested);
    }

    public final boolean quoteNested() {
        return this.quoteNested;
    }
}
