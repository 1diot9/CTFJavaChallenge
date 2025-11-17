package org.jooq.impl;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import org.jetbrains.annotations.NotNull;
import org.jooq.CSVFormat;
import org.jooq.ChartFormat;
import org.jooq.FormattingProvider;
import org.jooq.JSONFormat;
import org.jooq.TXTFormat;
import org.jooq.XMLFormat;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CallbackFormattingProvider.class */
public final class CallbackFormattingProvider implements FormattingProvider {
    private static final DefaultFormattingProvider DEFAULT = new DefaultFormattingProvider();
    private final Supplier<? extends TXTFormat> onTxtFormat;
    private final Supplier<? extends CSVFormat> onCsvFormat;
    private final Supplier<? extends JSONFormat> onJsonFormatForResults;
    private final Supplier<? extends JSONFormat> onJsonFormatForRecords;
    private final Supplier<? extends XMLFormat> onXmlFormatForResults;
    private final Supplier<? extends XMLFormat> onXmlFormatForRecords;
    private final Supplier<? extends ChartFormat> onChartFormat;
    private final ToIntFunction<? super String> onWidth;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public CallbackFormattingProvider() {
        /*
            r11 = this;
            r0 = r11
            org.jooq.impl.DefaultFormattingProvider r1 = org.jooq.impl.CallbackFormattingProvider.DEFAULT
            r2 = r1
            java.lang.Object r2 = java.util.Objects.requireNonNull(r2)
            void r1 = r1::txtFormat
            org.jooq.impl.DefaultFormattingProvider r2 = org.jooq.impl.CallbackFormattingProvider.DEFAULT
            r3 = r2
            java.lang.Object r3 = java.util.Objects.requireNonNull(r3)
            void r2 = r2::csvFormat
            org.jooq.impl.DefaultFormattingProvider r3 = org.jooq.impl.CallbackFormattingProvider.DEFAULT
            r4 = r3
            java.lang.Object r4 = java.util.Objects.requireNonNull(r4)
            void r3 = r3::jsonFormatForResults
            org.jooq.impl.DefaultFormattingProvider r4 = org.jooq.impl.CallbackFormattingProvider.DEFAULT
            r5 = r4
            java.lang.Object r5 = java.util.Objects.requireNonNull(r5)
            void r4 = r4::jsonFormatForRecords
            org.jooq.impl.DefaultFormattingProvider r5 = org.jooq.impl.CallbackFormattingProvider.DEFAULT
            r6 = r5
            java.lang.Object r6 = java.util.Objects.requireNonNull(r6)
            void r5 = r5::xmlFormatForResults
            org.jooq.impl.DefaultFormattingProvider r6 = org.jooq.impl.CallbackFormattingProvider.DEFAULT
            r7 = r6
            java.lang.Object r7 = java.util.Objects.requireNonNull(r7)
            void r6 = r6::xmlFormatForRecords
            org.jooq.impl.DefaultFormattingProvider r7 = org.jooq.impl.CallbackFormattingProvider.DEFAULT
            r8 = r7
            java.lang.Object r8 = java.util.Objects.requireNonNull(r8)
            void r7 = r7::chartFormat
            org.jooq.impl.DefaultFormattingProvider r8 = org.jooq.impl.CallbackFormattingProvider.DEFAULT
            r9 = r8
            java.lang.Object r9 = java.util.Objects.requireNonNull(r9)
            void r8 = r8::width
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.CallbackFormattingProvider.<init>():void");
    }

    private CallbackFormattingProvider(Supplier<? extends TXTFormat> onTxtFormat, Supplier<? extends CSVFormat> onCsvFormat, Supplier<? extends JSONFormat> onJsonFormatForResults, Supplier<? extends JSONFormat> onJsonFormatForRecords, Supplier<? extends XMLFormat> onXmlFormatForResults, Supplier<? extends XMLFormat> onXmlFormatForRecords, Supplier<? extends ChartFormat> onChartFormat, ToIntFunction<? super String> onWidth) {
        this.onTxtFormat = onTxtFormat;
        this.onCsvFormat = onCsvFormat;
        this.onJsonFormatForResults = onJsonFormatForResults;
        this.onJsonFormatForRecords = onJsonFormatForRecords;
        this.onXmlFormatForResults = onXmlFormatForResults;
        this.onXmlFormatForRecords = onXmlFormatForRecords;
        this.onChartFormat = onChartFormat;
        this.onWidth = onWidth;
    }

    @Override // org.jooq.FormattingProvider
    @NotNull
    public final TXTFormat txtFormat() {
        return this.onTxtFormat.get();
    }

    @Override // org.jooq.FormattingProvider
    @NotNull
    public final CSVFormat csvFormat() {
        return this.onCsvFormat.get();
    }

    @Override // org.jooq.FormattingProvider
    @NotNull
    public final JSONFormat jsonFormatForResults() {
        return this.onJsonFormatForResults.get();
    }

    @Override // org.jooq.FormattingProvider
    @NotNull
    public final JSONFormat jsonFormatForRecords() {
        return this.onJsonFormatForRecords.get();
    }

    @Override // org.jooq.FormattingProvider
    @NotNull
    public final XMLFormat xmlFormatForResults() {
        return this.onXmlFormatForResults.get();
    }

    @Override // org.jooq.FormattingProvider
    @NotNull
    public final XMLFormat xmlFormatForRecords() {
        return this.onXmlFormatForRecords.get();
    }

    @Override // org.jooq.FormattingProvider
    @NotNull
    public final ChartFormat chartFormat() {
        return this.onChartFormat.get();
    }

    @Override // org.jooq.FormattingProvider
    public final int width(String string) {
        return this.onWidth.applyAsInt(string);
    }

    @NotNull
    public final CallbackFormattingProvider onTxtFormat(Supplier<? extends TXTFormat> newOnTxtFormat) {
        return new CallbackFormattingProvider(newOnTxtFormat, this.onCsvFormat, this.onJsonFormatForResults, this.onJsonFormatForRecords, this.onXmlFormatForResults, this.onXmlFormatForRecords, this.onChartFormat, this.onWidth);
    }

    @NotNull
    public final CallbackFormattingProvider onCsvFormat(Supplier<? extends CSVFormat> newOnCsvFormat) {
        return new CallbackFormattingProvider(this.onTxtFormat, newOnCsvFormat, this.onJsonFormatForResults, this.onJsonFormatForRecords, this.onXmlFormatForResults, this.onXmlFormatForRecords, this.onChartFormat, this.onWidth);
    }

    @NotNull
    public final CallbackFormattingProvider onJsonFormatForResults(Supplier<? extends JSONFormat> newOnJsonFormatForResults) {
        return new CallbackFormattingProvider(this.onTxtFormat, this.onCsvFormat, newOnJsonFormatForResults, this.onJsonFormatForRecords, this.onXmlFormatForResults, this.onXmlFormatForRecords, this.onChartFormat, this.onWidth);
    }

    @NotNull
    public final CallbackFormattingProvider onJsonFormatForRecords(Supplier<? extends JSONFormat> newOnJsonFormatForRecords) {
        return new CallbackFormattingProvider(this.onTxtFormat, this.onCsvFormat, this.onJsonFormatForResults, newOnJsonFormatForRecords, this.onXmlFormatForResults, this.onXmlFormatForRecords, this.onChartFormat, this.onWidth);
    }

    @NotNull
    public final CallbackFormattingProvider onXmlFormatForResults(Supplier<? extends XMLFormat> newOnXmlFormatForResults) {
        return new CallbackFormattingProvider(this.onTxtFormat, this.onCsvFormat, this.onJsonFormatForResults, this.onJsonFormatForRecords, newOnXmlFormatForResults, this.onXmlFormatForRecords, this.onChartFormat, this.onWidth);
    }

    @NotNull
    public final CallbackFormattingProvider onXmlFormatForRecords(Supplier<? extends XMLFormat> newOnXmlFormatForRecords) {
        return new CallbackFormattingProvider(this.onTxtFormat, this.onCsvFormat, this.onJsonFormatForResults, this.onJsonFormatForRecords, this.onXmlFormatForResults, newOnXmlFormatForRecords, this.onChartFormat, this.onWidth);
    }

    @NotNull
    public final CallbackFormattingProvider onChartFormat(Supplier<? extends ChartFormat> newOnChartFormat) {
        return new CallbackFormattingProvider(this.onTxtFormat, this.onCsvFormat, this.onJsonFormatForResults, this.onJsonFormatForRecords, this.onXmlFormatForResults, this.onXmlFormatForRecords, newOnChartFormat, this.onWidth);
    }

    @NotNull
    public final CallbackFormattingProvider onWidth(ToIntFunction<? super String> newOnWidth) {
        return new CallbackFormattingProvider(this.onTxtFormat, this.onCsvFormat, this.onJsonFormatForResults, this.onJsonFormatForRecords, this.onXmlFormatForResults, this.onXmlFormatForRecords, this.onChartFormat, newOnWidth);
    }
}
