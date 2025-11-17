package org.jooq.impl;

import org.jooq.CSVFormat;
import org.jooq.ChartFormat;
import org.jooq.FormattingProvider;
import org.jooq.JSONFormat;
import org.jooq.TXTFormat;
import org.jooq.XMLFormat;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultFormattingProvider.class */
public class DefaultFormattingProvider implements FormattingProvider {
    @Override // org.jooq.FormattingProvider
    public TXTFormat txtFormat() {
        return TXTFormat.DEFAULT;
    }

    @Override // org.jooq.FormattingProvider
    public CSVFormat csvFormat() {
        return CSVFormat.DEFAULT;
    }

    @Override // org.jooq.FormattingProvider
    public JSONFormat jsonFormatForResults() {
        return JSONFormat.DEFAULT_FOR_RESULTS;
    }

    @Override // org.jooq.FormattingProvider
    public JSONFormat jsonFormatForRecords() {
        return JSONFormat.DEFAULT_FOR_RECORDS;
    }

    @Override // org.jooq.FormattingProvider
    public XMLFormat xmlFormatForResults() {
        return XMLFormat.DEFAULT_FOR_RESULTS;
    }

    @Override // org.jooq.FormattingProvider
    public XMLFormat xmlFormatForRecords() {
        return XMLFormat.DEFAULT_FOR_RECORDS;
    }

    @Override // org.jooq.FormattingProvider
    public ChartFormat chartFormat() {
        return ChartFormat.DEFAULT;
    }

    @Override // org.jooq.FormattingProvider
    public int width(String string) {
        return string.length();
    }
}
