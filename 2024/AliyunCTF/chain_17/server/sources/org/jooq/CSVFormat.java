package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CSVFormat.class */
public final class CSVFormat {
    public static final CSVFormat DEFAULT = new CSVFormat();
    final String delimiter;
    final String nullString;
    final String emptyString;
    final String newline;
    final String quoteString;
    final Quote quote;
    final boolean header;

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CSVFormat$Quote.class */
    public enum Quote {
        ALWAYS,
        SPECIAL_CHARACTERS,
        NEVER
    }

    public CSVFormat() {
        this(",", "\"\"", "\"\"", "\n", "\"", Quote.SPECIAL_CHARACTERS, true);
    }

    private CSVFormat(String delimiter, String nullString, String emptyString, String newline, String quoteString, Quote quote, boolean header) {
        this.delimiter = delimiter;
        this.nullString = nullString;
        this.emptyString = emptyString;
        this.newline = newline;
        this.quoteString = quoteString;
        this.quote = quote;
        this.header = header;
    }

    @NotNull
    public CSVFormat delimiter(String newDelimiter) {
        return new CSVFormat(newDelimiter, this.nullString, this.emptyString, this.newline, this.quoteString, this.quote, this.header);
    }

    @NotNull
    public CSVFormat delimiter(char newDelimiter) {
        return delimiter(newDelimiter);
    }

    @NotNull
    public String delimiter() {
        return this.delimiter;
    }

    @NotNull
    public CSVFormat nullString(String newNullString) {
        return new CSVFormat(this.delimiter, newNullString, this.emptyString, this.newline, this.quoteString, this.quote, this.header);
    }

    @NotNull
    public String nullString() {
        return this.nullString;
    }

    @NotNull
    public CSVFormat emptyString(String newEmptyString) {
        return new CSVFormat(this.delimiter, this.nullString, newEmptyString, this.newline, this.quoteString, this.quote, this.header);
    }

    @NotNull
    public String emptyString() {
        return this.emptyString;
    }

    @NotNull
    public CSVFormat newline(String newNewline) {
        return new CSVFormat(this.delimiter, this.nullString, this.emptyString, newNewline, this.quoteString, this.quote, this.header);
    }

    @NotNull
    public String newline() {
        return this.newline;
    }

    @NotNull
    public CSVFormat quoteString(String newQuoteString) {
        return new CSVFormat(this.delimiter, this.nullString, this.emptyString, this.newline, newQuoteString, this.quote, this.header);
    }

    @NotNull
    public String quoteString() {
        return this.quoteString;
    }

    @NotNull
    public CSVFormat quote(Quote newQuote) {
        return new CSVFormat(this.delimiter, this.nullString, this.emptyString, this.newline, this.quoteString, newQuote, this.header);
    }

    @NotNull
    public Quote quote() {
        return this.quote;
    }

    @NotNull
    public CSVFormat header(boolean newHeader) {
        return new CSVFormat(this.delimiter, this.nullString, this.emptyString, this.newline, this.quoteString, this.quote, newHeader);
    }

    public boolean header() {
        return this.header;
    }
}
