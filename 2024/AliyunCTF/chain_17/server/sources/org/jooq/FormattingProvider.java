package org.jooq;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.CallbackFormattingProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/FormattingProvider.class */
public interface FormattingProvider {
    @NotNull
    TXTFormat txtFormat();

    @NotNull
    CSVFormat csvFormat();

    @NotNull
    JSONFormat jsonFormatForResults();

    @NotNull
    JSONFormat jsonFormatForRecords();

    @NotNull
    XMLFormat xmlFormatForResults();

    @NotNull
    XMLFormat xmlFormatForRecords();

    @NotNull
    ChartFormat chartFormat();

    int width(String str);

    @NotNull
    static CallbackFormattingProvider onTxtFormat(Supplier<? extends TXTFormat> newOnTxtFormat) {
        return new CallbackFormattingProvider().onTxtFormat(newOnTxtFormat);
    }

    @NotNull
    static CallbackFormattingProvider onCsvFormat(Supplier<? extends CSVFormat> newOnCsvFormat) {
        return new CallbackFormattingProvider().onCsvFormat(newOnCsvFormat);
    }

    @NotNull
    static CallbackFormattingProvider onJsonFormatForResults(Supplier<? extends JSONFormat> newOnJsonFormatForResults) {
        return new CallbackFormattingProvider().onJsonFormatForResults(newOnJsonFormatForResults);
    }

    @NotNull
    static CallbackFormattingProvider onJsonFormatForRecords(Supplier<? extends JSONFormat> newOnJsonFormatForRecords) {
        return new CallbackFormattingProvider().onJsonFormatForRecords(newOnJsonFormatForRecords);
    }

    @NotNull
    static CallbackFormattingProvider onXmlFormatForResults(Supplier<? extends XMLFormat> newOnXmlFormatForResults) {
        return new CallbackFormattingProvider().onXmlFormatForResults(newOnXmlFormatForResults);
    }

    @NotNull
    static CallbackFormattingProvider onXmlFormatForRecords(Supplier<? extends XMLFormat> newOnXmlFormatForRecords) {
        return new CallbackFormattingProvider().onXmlFormatForRecords(newOnXmlFormatForRecords);
    }

    @NotNull
    static CallbackFormattingProvider onChartFormat(Supplier<? extends ChartFormat> newOnChartFormat) {
        return new CallbackFormattingProvider().onChartFormat(newOnChartFormat);
    }

    @NotNull
    static CallbackFormattingProvider onWidth(ToIntFunction<? super String> newOnWidth) {
        return new CallbackFormattingProvider().onWidth(newOnWidth);
    }
}
