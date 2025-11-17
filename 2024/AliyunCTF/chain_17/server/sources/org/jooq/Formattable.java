package org.jooq;

import java.io.OutputStream;
import java.io.Writer;
import org.jetbrains.annotations.NotNull;
import org.jooq.exception.IOException;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Formattable.class */
public interface Formattable {
    @NotNull
    String format();

    @NotNull
    String format(int i);

    @NotNull
    String format(TXTFormat tXTFormat);

    @NotNull
    String formatHTML();

    @NotNull
    String formatCSV();

    @NotNull
    String formatCSV(char c);

    @NotNull
    String formatCSV(char c, String str);

    @NotNull
    String formatCSV(boolean z);

    @NotNull
    String formatCSV(boolean z, char c);

    @NotNull
    String formatCSV(boolean z, char c, String str);

    @NotNull
    String formatCSV(CSVFormat cSVFormat);

    @NotNull
    String formatJSON();

    @NotNull
    String formatJSON(JSONFormat jSONFormat);

    @NotNull
    String formatXML();

    @NotNull
    String formatXML(XMLFormat xMLFormat);

    @NotNull
    String formatChart();

    @NotNull
    String formatChart(ChartFormat chartFormat);

    @NotNull
    String formatInsert();

    @NotNull
    String formatInsert(Table<?> table, Field<?>... fieldArr);

    void format(OutputStream outputStream) throws IOException;

    void format(OutputStream outputStream, int i) throws IOException;

    void format(OutputStream outputStream, TXTFormat tXTFormat) throws IOException;

    void formatHTML(OutputStream outputStream) throws IOException;

    void formatCSV(OutputStream outputStream) throws IOException;

    void formatCSV(OutputStream outputStream, char c) throws IOException;

    void formatCSV(OutputStream outputStream, char c, String str) throws IOException;

    void formatCSV(OutputStream outputStream, boolean z) throws IOException;

    void formatCSV(OutputStream outputStream, boolean z, char c) throws IOException;

    void formatCSV(OutputStream outputStream, boolean z, char c, String str) throws IOException;

    void formatCSV(OutputStream outputStream, CSVFormat cSVFormat) throws IOException;

    void formatJSON(OutputStream outputStream) throws IOException;

    void formatJSON(OutputStream outputStream, JSONFormat jSONFormat) throws IOException;

    void formatXML(OutputStream outputStream) throws IOException;

    void formatXML(OutputStream outputStream, XMLFormat xMLFormat) throws IOException;

    void formatChart(OutputStream outputStream) throws IOException;

    void formatChart(OutputStream outputStream, ChartFormat chartFormat) throws IOException;

    void formatInsert(OutputStream outputStream) throws IOException;

    void formatInsert(OutputStream outputStream, Table<?> table, Field<?>... fieldArr) throws IOException;

    void format(Writer writer) throws IOException;

    void format(Writer writer, int i) throws IOException;

    void format(Writer writer, TXTFormat tXTFormat) throws IOException;

    void formatHTML(Writer writer) throws IOException;

    void formatCSV(Writer writer) throws IOException;

    void formatCSV(Writer writer, char c) throws IOException;

    void formatCSV(Writer writer, char c, String str) throws IOException;

    void formatCSV(Writer writer, boolean z) throws IOException;

    void formatCSV(Writer writer, boolean z, char c) throws IOException;

    void formatCSV(Writer writer, boolean z, char c, String str) throws IOException;

    void formatCSV(Writer writer, CSVFormat cSVFormat) throws IOException;

    void formatJSON(Writer writer) throws IOException;

    void formatJSON(Writer writer, JSONFormat jSONFormat) throws IOException;

    void formatXML(Writer writer) throws IOException;

    void formatXML(Writer writer, XMLFormat xMLFormat) throws IOException;

    void formatChart(Writer writer) throws IOException;

    void formatChart(Writer writer, ChartFormat chartFormat) throws IOException;

    void formatInsert(Writer writer) throws IOException;

    void formatInsert(Writer writer, Table<?> table, Field<?>... fieldArr) throws IOException;

    @NotNull
    Document intoXML();

    @NotNull
    Document intoXML(XMLFormat xMLFormat);

    <H extends ContentHandler> H intoXML(H h) throws SAXException;

    <H extends ContentHandler> H intoXML(H h, XMLFormat xMLFormat) throws SAXException;
}
