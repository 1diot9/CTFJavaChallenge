package org.jooq;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.xml.sax.InputSource;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderSourceStep.class */
public interface LoaderSourceStep<R extends Record> {
    @Support
    @CheckReturnValue
    @NotNull
    LoaderRowsStep<R> loadArrays(Object[]... objArr);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderRowsStep<R> loadArrays(Iterable<? extends Object[]> iterable);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderRowsStep<R> loadArrays(Iterator<? extends Object[]> it);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderRowsStep<R> loadArrays(Stream<? extends Object[]> stream);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderRowsStep<R> loadRecords(Record... recordArr);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderRowsStep<R> loadRecords(Iterable<? extends Record> iterable);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderRowsStep<R> loadRecords(Iterator<? extends Record> it);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderRowsStep<R> loadRecords(Stream<? extends Record> stream);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(java.io.File file);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(java.io.File file, String str);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(java.io.File file, Charset charset);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(java.io.File file, CharsetDecoder charsetDecoder);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(String str);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(InputStream inputStream);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(InputStream inputStream, String str);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(InputStream inputStream, Charset charset);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(InputStream inputStream, CharsetDecoder charsetDecoder);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(Reader reader);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVStep<R> loadCSV(Source source);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(java.io.File file);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(java.io.File file, String str);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(java.io.File file, Charset charset);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(java.io.File file, CharsetDecoder charsetDecoder);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(String str);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(InputStream inputStream);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(InputStream inputStream, String str);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(InputStream inputStream, Charset charset);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(InputStream inputStream, CharsetDecoder charsetDecoder);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(Reader reader);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(InputSource inputSource);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderXMLStep<R> loadXML(Source source);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(java.io.File file);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(java.io.File file, String str);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(java.io.File file, Charset charset);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(java.io.File file, CharsetDecoder charsetDecoder);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(String str);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(InputStream inputStream);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(InputStream inputStream, String str);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(InputStream inputStream, Charset charset);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(InputStream inputStream, CharsetDecoder charsetDecoder);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(Reader reader);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderJSONStep<R> loadJSON(Source source);
}
