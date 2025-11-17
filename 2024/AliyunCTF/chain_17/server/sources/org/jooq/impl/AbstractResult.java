package org.jooq.impl;

import ch.qos.logback.classic.net.SyslogAppender;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jooq.CSVFormat;
import org.jooq.ChartFormat;
import org.jooq.Configuration;
import org.jooq.Constants;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.Formattable;
import org.jooq.FormattingProvider;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.JSONFormat;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Schema;
import org.jooq.TXTFormat;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableRecord;
import org.jooq.XML;
import org.jooq.XMLFormat;
import org.jooq.conf.SettingsTools;
import org.jooq.tools.StringUtils;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.interceptor.CacheOperationExpressionEvaluator;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractResult.class */
public abstract class AbstractResult<R extends Record> extends AbstractFormattable implements FieldsTrait, Iterable<R> {
    final AbstractRow<R> fields;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractResult(Configuration configuration, AbstractRow<R> row) {
        super(configuration);
        this.fields = row;
    }

    public final FieldsImpl<R> recordType() {
        return this.fields.fields;
    }

    @Override // org.jooq.Fields
    public final Row fieldsRow() {
        return this.fields;
    }

    @Override // org.jooq.Formattable
    public final void format(Writer writer, TXTFormat format) {
        String padded;
        String padded2;
        try {
            FormattingProvider fp = Tools.configuration(this).formattingProvider();
            int NUM_COL_MAX_WIDTH = format.maxColWidth() == Integer.MAX_VALUE ? Integer.MAX_VALUE : 2 * format.maxColWidth();
            int MAX_RECORDS = Math.min(50, format.maxRows());
            ArrayDeque<Record> arrayDeque = new ArrayDeque();
            Iterator<R> it = iterator();
            for (int i = 0; i < MAX_RECORDS && it.hasNext(); i++) {
                arrayDeque.offer(nullSafe(it.next()));
            }
            int size = this.fields.size();
            int[] decimalPlaces = new int[size];
            int[] widths = new int[size];
            for (int index = 0; index < size; index++) {
                if (Number.class.isAssignableFrom(this.fields.field(index).getType())) {
                    List<Integer> decimalPlacesList = new ArrayList<>(1 + arrayDeque.size());
                    decimalPlacesList.add(0);
                    for (Record record : arrayDeque) {
                        decimalPlacesList.add(Integer.valueOf(decimalPlaces(format0(record.get(index), record.changed(index), true))));
                    }
                    decimalPlaces[index] = ((Integer) Collections.max(decimalPlacesList)).intValue();
                }
            }
            for (int index2 = 0; index2 < size; index2++) {
                boolean isNumCol = Number.class.isAssignableFrom(this.fields.field(index2).getType());
                int colMaxWidth = isNumCol ? NUM_COL_MAX_WIDTH : format.maxColWidth();
                List<Integer> widthList = new ArrayList<>(1 + arrayDeque.size());
                widthList.add(Integer.valueOf(Math.min(colMaxWidth, Math.max(format.minColWidth(), fp.width(this.fields.field(index2).getName())))));
                for (Record record2 : arrayDeque) {
                    String value = format0(record2.get(index2), record2.changed(index2), true);
                    if (isNumCol) {
                        value = alignNumberValue(Integer.valueOf(decimalPlaces[index2]), value);
                    }
                    widthList.add(Integer.valueOf(Math.min(colMaxWidth, fp.width(value))));
                }
                widths[index2] = ((Integer) Collections.max(widthList)).intValue();
            }
            if (format.horizontalTableBorder()) {
                formatHorizontalLine(writer, format, widths);
            }
            if (format.verticalTableBorder()) {
                writer.append('|');
            }
            for (int index3 = 0; index3 < size; index3++) {
                if (index3 > 0) {
                    if (format.verticalCellBorder()) {
                        writer.append('|');
                    } else {
                        writer.append(' ');
                    }
                }
                String name = this.fields.field(index3).getName();
                int width = fp.width(name);
                if (Number.class.isAssignableFrom(this.fields.field(index3).getType())) {
                    padded2 = StringUtils.leftPad(name, width, widths[index3]);
                } else {
                    padded2 = StringUtils.rightPad(name, width, widths[index3]);
                }
                if (widths[index3] < 4) {
                    writer.append((CharSequence) padded2);
                } else {
                    writer.append((CharSequence) StringUtils.abbreviate(padded2, widths[index3]));
                }
            }
            if (format.verticalTableBorder()) {
                writer.append('|');
            }
            writer.append('\n');
            if (format.horizontalHeaderBorder()) {
                formatHorizontalLine(writer, format, widths);
            }
            int i2 = 0;
            while (i2 < format.maxRows()) {
                Record record3 = (Record) arrayDeque.pollFirst();
                if (record3 == null) {
                    if (!it.hasNext()) {
                        break;
                    } else {
                        record3 = nullSafe(it.next());
                    }
                }
                if (i2 > 0 && format.horizontalCellBorder()) {
                    formatHorizontalLine(writer, format, widths);
                }
                if (format.verticalTableBorder()) {
                    writer.append('|');
                }
                for (int index4 = 0; index4 < size; index4++) {
                    if (index4 > 0) {
                        if (format.verticalCellBorder()) {
                            writer.append('|');
                        } else {
                            writer.append(' ');
                        }
                    }
                    String value2 = StringUtils.replace(StringUtils.replace(StringUtils.replace(format0(record3.get(index4), record3.changed(index4), true), "\n", "{lf}"), "\r", "{cr}"), SyslogAppender.DEFAULT_STACKTRACE_PATTERN, "{tab}");
                    if (Number.class.isAssignableFrom(this.fields.field(index4).getType())) {
                        padded = StringUtils.leftPad(alignNumberValue(Integer.valueOf(decimalPlaces[index4]), value2), widths[index4]);
                    } else {
                        padded = StringUtils.rightPad(value2, fp.width(value2), widths[index4]);
                    }
                    if (widths[index4] < 4) {
                        writer.append((CharSequence) padded);
                    } else {
                        writer.append((CharSequence) StringUtils.abbreviate(padded, widths[index4]));
                    }
                }
                if (format.verticalTableBorder()) {
                    writer.append('|');
                }
                writer.append('\n');
                i2++;
            }
            if (format.horizontalTableBorder() && i2 > 0) {
                formatHorizontalLine(writer, format, widths);
            }
            if (it.hasNext()) {
                if (format.verticalTableBorder()) {
                    writer.append('|');
                }
                writer.append("...record(s) truncated...\n");
            }
            writer.flush();
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Exception while writing TEXT", e);
        }
    }

    private final R nullSafe(R r) {
        return r != null ? r : (R) Tools.configuration(this).dsl().newRecord(fields());
    }

    private final void formatHorizontalLine(Writer writer, TXTFormat format, int[] widths) throws IOException {
        if (format.verticalTableBorder()) {
            if (format.intersectLines()) {
                writer.append('+');
            } else {
                writer.append('-');
            }
        }
        int size = this.fields.size();
        for (int index = 0; index < size; index++) {
            if (index > 0) {
                if (format.verticalCellBorder()) {
                    if (format.intersectLines()) {
                        writer.append('+');
                    } else {
                        writer.append('-');
                    }
                } else {
                    writer.append(' ');
                }
            }
            writer.append((CharSequence) StringUtils.rightPad("", widths[index], "-"));
        }
        if (format.verticalTableBorder()) {
            if (format.intersectLines()) {
                writer.append('+');
            } else {
                writer.append('-');
            }
        }
        writer.append('\n');
    }

    private static final String alignNumberValue(Integer columnDecimalPlaces, String value) {
        if (!"{null}".equals(value) && columnDecimalPlaces.intValue() != 0) {
            int decimalPlaces = decimalPlaces(value);
            int rightPadSize = (value.length() + columnDecimalPlaces.intValue()) - decimalPlaces;
            if (decimalPlaces == 0) {
                value = StringUtils.rightPad(value, rightPadSize + 1);
            } else {
                value = StringUtils.rightPad(value, rightPadSize);
            }
        }
        return value;
    }

    private static final int decimalPlaces(String value) {
        int decimalPlaces = 0;
        int dotIndex = value.indexOf(".");
        if (dotIndex != -1) {
            decimalPlaces = (value.length() - dotIndex) - 1;
        }
        return decimalPlaces;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Formattable
    public final void formatCSV(Writer writer, CSVFormat format) {
        try {
            if (format.header()) {
                String sep1 = "";
                for (Field<?> field : this.fields.fields.fields) {
                    writer.append((CharSequence) sep1);
                    writer.append((CharSequence) formatCSV0(field.getName(), format));
                    sep1 = format.delimiter();
                }
                writer.append((CharSequence) format.newline());
            }
            Iterator it = iterator();
            while (it.hasNext()) {
                Record nullSafe = nullSafe((Record) it.next());
                String sep2 = "";
                int size = this.fields.size();
                for (int index = 0; index < size; index++) {
                    writer.append((CharSequence) sep2);
                    writer.append((CharSequence) formatCSV0(nullSafe.getValue(index), format));
                    sep2 = format.delimiter();
                }
                writer.append((CharSequence) format.newline());
            }
            writer.flush();
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Exception while writing CSV", e);
        }
    }

    private static final String formatCSV0(Object value, CSVFormat format) {
        String format0;
        if (value == null) {
            return format.nullString();
        }
        if ("".equals(value.toString())) {
            return format.emptyString();
        }
        if (value instanceof Formattable) {
            Formattable f = (Formattable) value;
            format0 = f.formatCSV(format);
        } else {
            format0 = format0(value, false, false);
        }
        String result = format0;
        switch (format.quote()) {
            case NEVER:
                return result;
            case SPECIAL_CHARACTERS:
                if (!StringUtils.containsAny(result, ',', ';', '\t', '\"', '\n', '\r', '\'', '\\')) {
                    return result;
                }
                break;
        }
        return format.quoteString() + StringUtils.replace(StringUtils.replace(result, "\\", "\\\\"), format.quoteString(), format.quoteString() + format.quoteString()) + format.quoteString();
    }

    @Override // org.jooq.impl.AbstractFormattable
    final JSONFormat defaultJSONFormat() {
        return Tools.configuration(this).formattingProvider().jsonFormatForResults();
    }

    @Override // org.jooq.Formattable
    public final void formatJSON(Writer writer, JSONFormat format) {
        JSONFormat format2 = format.mutable(true);
        try {
            int recordLevel = format2.header() ? 2 : 1;
            boolean hasRecords = false;
            if (format2.header()) {
                if (format2.format()) {
                    writer.append('{').append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(1)).append("\"fields\": [");
                } else {
                    writer.append("{\"fields\":[");
                }
                String separator = "";
                for (Field<?> field : this.fields.fields.fields) {
                    writer.append((CharSequence) separator);
                    if (format2.format()) {
                        writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(2));
                    }
                    writer.append('{');
                    if (format2.format()) {
                        writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(3));
                    }
                    if (field instanceof TableField) {
                        TableField<?, ?> f = (TableField) field;
                        Table<R> table = f.getTable();
                        if (table != null) {
                            Schema schema = table.getSchema();
                            if (schema != null) {
                                writer.append("\"schema\":");
                                if (format2.format()) {
                                    writer.append(' ');
                                }
                                org.jooq.tools.json.JSONValue.writeJSONString(schema.getName(), writer);
                                writer.append(',');
                                if (format2.format()) {
                                    writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(3));
                                }
                            }
                            writer.append("\"table\":");
                            if (format2.format()) {
                                writer.append(' ');
                            }
                            org.jooq.tools.json.JSONValue.writeJSONString(table.getName(), writer);
                            writer.append(',');
                            if (format2.format()) {
                                writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(3));
                            }
                        }
                    }
                    writer.append("\"name\":");
                    if (format2.format()) {
                        writer.append(' ');
                    }
                    org.jooq.tools.json.JSONValue.writeJSONString(field.getName(), writer);
                    writer.append(',');
                    if (format2.format()) {
                        writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(3));
                    }
                    writer.append("\"type\":");
                    if (format2.format()) {
                        writer.append(' ');
                    }
                    org.jooq.tools.json.JSONValue.writeJSONString(formatTypeName(field), writer);
                    if (format2.format()) {
                        writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(2));
                    }
                    writer.append('}');
                    separator = ",";
                }
                if (format2.format()) {
                    writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(1)).append("],").append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(1)).append("\"records\": ");
                } else {
                    writer.append("],\"records\":");
                }
            }
            writer.append('[');
            String separator2 = "";
            switch (format2.recordFormat()) {
                case ARRAY:
                    Iterator<R> it = iterator();
                    while (it.hasNext()) {
                        Record record = (Record) it.next();
                        if (record != null || format2.arrayNulls() != JSONFormat.NullFormat.ABSENT_ON_NULL) {
                            hasRecords = true;
                            writer.append((CharSequence) separator2);
                            if (format2.format()) {
                                writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(recordLevel));
                            }
                            formatJSONArray0(record, this.fields, format2, recordLevel, writer);
                            separator2 = ",";
                        }
                    }
                    break;
                case OBJECT:
                    Iterator<R> it2 = iterator();
                    while (it2.hasNext()) {
                        Record record2 = (Record) it2.next();
                        if (record2 != null || format2.objectNulls() != JSONFormat.NullFormat.ABSENT_ON_NULL) {
                            hasRecords = true;
                            writer.append((CharSequence) separator2);
                            if (format2.format()) {
                                writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(recordLevel));
                            }
                            formatJSONMap0(record2, this.fields, format2, recordLevel, writer);
                            separator2 = ",";
                        }
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Format not supported: " + String.valueOf(format2));
            }
            if (format2.format() && hasRecords) {
                writer.append((CharSequence) format2.newline());
                if (format2.header()) {
                    writer.append((CharSequence) format2.indentString(1));
                } else {
                    writer.append((CharSequence) format2.indentString(0));
                }
            }
            writer.append(']');
            if (format2.header()) {
                if (format2.format()) {
                    writer.append((CharSequence) format2.newline()).append((CharSequence) format2.indentString(0));
                }
                writer.append('}');
            }
            writer.flush();
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Exception while writing JSON", e);
        }
    }

    private final String formatTypeName(Field<?> field) {
        return ((AbstractDataType) field.getDataType()).typeName0().toUpperCase(SettingsTools.renderLocale(this.configuration.settings()));
    }

    static final void formatJSON0(Object value, Writer writer, JSONFormat format) throws IOException {
        if (value instanceof byte[]) {
            byte[] a = (byte[]) value;
            org.jooq.tools.json.JSONValue.writeJSONString(Base64.getEncoder().encodeToString(a), writer);
            return;
        }
        if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            writer.append('[');
            boolean first = true;
            for (Object o : array) {
                if (o != null || format.arrayNulls() != JSONFormat.NullFormat.ABSENT_ON_NULL) {
                    if (!first) {
                        writer.append(',');
                    }
                    formatJSON0(o, writer, format);
                    first = false;
                }
            }
            writer.append(']');
            return;
        }
        if (value instanceof Formattable) {
            Formattable f = (Formattable) value;
            f.formatJSON(writer, format);
        } else if ((value instanceof JSON) && !format.quoteNested()) {
            writer.write(((JSON) value).data());
        } else if ((value instanceof JSONB) && !format.quoteNested()) {
            writer.write(((JSONB) value).data());
        } else {
            org.jooq.tools.json.JSONValue.writeJSONString(value, writer);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void formatJSONMap0(Record record, AbstractRow<?> fields, JSONFormat format, int recordLevel, Writer writer) throws IOException {
        if (record == null) {
            writer.append("null");
            return;
        }
        String separator = "";
        int size = fields.size();
        boolean wrapRecords = format.wrapSingleColumnRecords() || size > 1;
        if (wrapRecords) {
            writer.append('{');
        }
        for (int index = 0; index < size; index++) {
            Object value = record.get(index);
            if (value != null || format.objectNulls() != JSONFormat.NullFormat.ABSENT_ON_NULL) {
                writer.append((CharSequence) separator);
                if (format.format()) {
                    if (size > 1) {
                        writer.append((CharSequence) format.newline()).append((CharSequence) format.indentString(recordLevel + 1));
                    } else if (format.wrapSingleColumnRecords()) {
                        writer.append(' ');
                    }
                }
                if (wrapRecords) {
                    org.jooq.tools.json.JSONValue.writeJSONString(record.field(index).getName(), writer);
                    writer.append(':');
                    if (format.format()) {
                        writer.append(' ');
                    }
                }
                int previous = format.globalIndent();
                formatJSON0(value, writer, format.globalIndent(format.globalIndent() + (format.indent() * (recordLevel + 1))));
                format.globalIndent(previous);
                if (format.format() && format.wrapSingleColumnRecords() && size == 1) {
                    writer.append(' ');
                }
                separator = ",";
            }
        }
        if (wrapRecords) {
            if (format.format() && size > 1) {
                writer.append((CharSequence) format.newline()).append((CharSequence) format.indentString(recordLevel)).append('}');
            } else {
                writer.append('}');
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void formatJSONArray0(Record record, AbstractRow<?> fields, JSONFormat format, int recordLevel, Writer writer) throws IOException {
        if (record == null) {
            writer.append("null");
            return;
        }
        String separator = "";
        int size = fields.size();
        boolean wrapRecords = format.wrapSingleColumnRecords() || size > 1;
        if (wrapRecords) {
            writer.append('[');
        }
        for (int index = 0; index < size; index++) {
            Object value = record.get(index);
            if (value != null || format.arrayNulls() != JSONFormat.NullFormat.ABSENT_ON_NULL) {
                writer.append((CharSequence) separator);
                if (format.format()) {
                    if (size > 1) {
                        writer.append((CharSequence) format.newline()).append((CharSequence) format.indentString(recordLevel + 1));
                    } else if (format.wrapSingleColumnRecords()) {
                        writer.append(' ');
                    }
                }
                int previous = format.globalIndent();
                formatJSON0(value, writer, format.globalIndent(format.globalIndent() + (format.indent() * (recordLevel + 1))));
                format.globalIndent(previous);
                if (format.format() && format.wrapSingleColumnRecords() && size == 1) {
                    writer.append(' ');
                }
                separator = ",";
            }
        }
        if (wrapRecords) {
            if (format.format() && size > 1) {
                writer.append((CharSequence) format.newline()).append((CharSequence) format.indentString(recordLevel)).append(']');
            } else {
                writer.append(']');
            }
        }
    }

    @Override // org.jooq.impl.AbstractFormattable
    final XMLFormat defaultXMLFormat() {
        return Tools.configuration(this).formattingProvider().xmlFormatForResults();
    }

    @Override // org.jooq.Formattable
    public final void formatXML(Writer writer, XMLFormat format) {
        XMLFormat format2 = format.mutable(true);
        String newline = format2.newline();
        int recordLevel = format2.header() ? 2 : 1;
        try {
            writer.append("<result");
            if (format2.xmlns()) {
                format2 = format2.xmlns(false);
                writer.append(" xmlns=\"http://www.jooq.org/xsd/jooq-export-3.10.0.xsd\"");
            }
            writer.append(">");
            if (format2.header()) {
                writer.append((CharSequence) newline).append((CharSequence) format2.indentString(1)).append("<fields>");
                for (Field<?> field : this.fields.fields.fields) {
                    writer.append((CharSequence) newline).append((CharSequence) format2.indentString(2)).append("<field");
                    if (field instanceof TableField) {
                        TableField<?, ?> f = (TableField) field;
                        Table<R> table = f.getTable();
                        if (table != null) {
                            Schema schema = table.getSchema();
                            if (schema != null) {
                                writer.append(" schema=\"");
                                writer.append((CharSequence) escapeXML(schema.getName()));
                                writer.append("\"");
                            }
                            writer.append(" table=\"");
                            writer.append((CharSequence) escapeXML(table.getName()));
                            writer.append("\"");
                        }
                    }
                    writer.append(" name=\"");
                    writer.append((CharSequence) escapeXML(field.getName()));
                    writer.append("\"");
                    writer.append(" type=\"");
                    writer.append((CharSequence) escapeXML(formatTypeName(field)));
                    writer.append("\"/>");
                }
                writer.append((CharSequence) newline).append((CharSequence) format2.indentString(1)).append("</fields>");
                writer.append((CharSequence) newline).append((CharSequence) format2.indentString(1)).append("<records>");
            }
            Iterator<R> it = iterator();
            while (it.hasNext()) {
                Record record = (Record) it.next();
                writer.append((CharSequence) newline).append((CharSequence) format2.indentString(recordLevel));
                formatXMLRecord(writer, format2, recordLevel, record, this.fields);
            }
            if (format2.header()) {
                writer.append((CharSequence) newline).append((CharSequence) format2.indentString(1)).append("</records>");
            }
            writer.append((CharSequence) newline).append((CharSequence) format2.indentString(0)).append("</result>");
            writer.flush();
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Exception while writing XML", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void formatXMLRecord(Writer writer, XMLFormat format, int recordLevel, Record record, AbstractRow<?> fields) throws IOException {
        String str;
        String newline = format.newline();
        writer.append("<record");
        if (format.xmlns()) {
            format = format.xmlns(false);
            writer.append(" xmlns=\"http://www.jooq.org/xsd/jooq-export-3.10.0.xsd\"");
        }
        if (record == null) {
            writer.append("/>");
            return;
        }
        writer.append(">");
        int size = fields.size();
        for (int index = 0; index < size; index++) {
            Object value = record.get(index);
            writer.append((CharSequence) newline).append((CharSequence) format.indentString(recordLevel + 1));
            if (format.recordFormat() == XMLFormat.RecordFormat.COLUMN_NAME_ELEMENTS) {
                str = escapeXML(fields.field(index).getName());
            } else {
                str = "value";
            }
            String tag = str;
            writer.append((CharSequence) ("<" + tag));
            if (format.recordFormat() == XMLFormat.RecordFormat.VALUE_ELEMENTS_WITH_FIELD_ATTRIBUTE) {
                writer.append(" field=\"");
                writer.append((CharSequence) escapeXML(fields.field(index).getName()));
                writer.append("\"");
            }
            if (value == null) {
                writer.append("/>");
            } else {
                writer.append(">");
                if (value instanceof Formattable) {
                    Formattable f = (Formattable) value;
                    writer.append((CharSequence) newline).append((CharSequence) format.indentString(recordLevel + 2));
                    int previous = format.globalIndent();
                    f.formatXML(writer, format.globalIndent(format.globalIndent() + (format.indent() * (recordLevel + 2))));
                    format.globalIndent(previous);
                    writer.append((CharSequence) newline).append((CharSequence) format.indentString(recordLevel + 1));
                } else if ((value instanceof XML) && !format.quoteNested()) {
                    writer.append((CharSequence) ((XML) value).data());
                } else {
                    writer.append((CharSequence) escapeXML(format0(value, false, false)));
                }
                writer.append((CharSequence) ("</" + tag + ">"));
            }
        }
        writer.append((CharSequence) newline).append((CharSequence) format.indentString(recordLevel)).append("</record>");
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Formattable
    public final void formatChart(Writer writer, ChartFormat format) {
        Result<R> result;
        int i;
        String format2;
        if (this instanceof Result) {
            result = (Result) this;
        } else if (this instanceof Cursor) {
            result = ((Cursor) this).fetch();
        } else {
            throw new IllegalStateException();
        }
        try {
            if (result.isEmpty()) {
                writer.append("No data available");
                return;
            }
            DSLContext ctx = this.configuration.dsl();
            FormattingProvider fp = this.configuration.formattingProvider();
            Field<?> category = this.fields.field(format.category());
            TreeMap treeMap = new TreeMap(result.intoGroups(format.category()));
            if (!format.categoryAsText() && Date.class.isAssignableFrom(category.getType())) {
                Date categoryMin = (Date) treeMap.firstKey();
                Date categoryMax = (Date) treeMap.lastKey();
                for (Date i2 = categoryMin; i2.before(categoryMax); i2 = new Date(i2.getYear(), i2.getMonth(), i2.getDate() + 1)) {
                    if (!treeMap.containsKey(i2)) {
                        treeMap.put(i2, ctx.newResult(this.fields.fields.fields));
                    }
                }
            }
            List<?> categories = new ArrayList<>(treeMap.keySet());
            int categoryWidth = 0;
            for (Object o : categories) {
                categoryWidth = Math.max(categoryWidth, fp.width(String.valueOf(o)));
            }
            double axisMin = Double.POSITIVE_INFINITY;
            double axisMax = Double.NEGATIVE_INFINITY;
            for (Result<R> values : treeMap.values()) {
                double sum = 0.0d;
                for (int i3 = 0; i3 < format.values().length; i3++) {
                    if (format.display() == ChartFormat.Display.DEFAULT) {
                        sum = 0.0d;
                    }
                    for (R r : values) {
                        sum += ((Double) r.get(format.values()[i3], Double.TYPE)).doubleValue();
                    }
                    if (sum < axisMin) {
                        axisMin = sum;
                    }
                    if (sum > axisMax) {
                        axisMax = sum;
                    }
                }
            }
            if (format.showVerticalLegend()) {
                if (format.display() == ChartFormat.Display.HUNDRED_PERCENT_STACKED) {
                    i = fp.width(format.percentFormat().format(100.0d));
                } else {
                    i = Math.max(format.numericFormat().format(axisMin).length(), format.numericFormat().format(axisMax).length());
                }
            } else {
                i = 0;
            }
            int verticalLegendWidth = i;
            int horizontalLegendHeight = format.showHorizontalLegend() ? 1 : 0;
            int verticalBorderWidth = format.showVerticalLegend() ? 1 : 0;
            int horizontalBorderHeight = format.showHorizontalLegend() ? 1 : 0;
            int chartHeight = (format.height() - horizontalLegendHeight) - horizontalBorderHeight;
            int chartWidth = (format.width() - verticalLegendWidth) - verticalBorderWidth;
            double barWidth = chartWidth / treeMap.size();
            double axisStep = (axisMax - axisMin) / (chartHeight - 1);
            for (int y = chartHeight - 1; y >= 0; y--) {
                double axisLegend = axisMax - (axisStep * ((chartHeight - 1) - y));
                double axisLegendPercent = (axisLegend - axisMin) / (axisMax - axisMin);
                if (format.showVerticalLegend()) {
                    if (format.display() == ChartFormat.Display.HUNDRED_PERCENT_STACKED) {
                        format2 = format.percentFormat().format(axisLegendPercent * 100.0d);
                    } else {
                        format2 = format.numericFormat().format(axisLegend);
                    }
                    String axisLegendString = format2;
                    for (int x = fp.width(axisLegendString); x < verticalLegendWidth; x++) {
                        writer.write(32);
                    }
                    writer.write(axisLegendString);
                    for (int x2 = 0; x2 < verticalBorderWidth; x2++) {
                        writer.write(124);
                    }
                }
                for (int x3 = 0; x3 < chartWidth; x3++) {
                    int index = (int) (x3 / barWidth);
                    Result<R> group = (Result) treeMap.get(categories.get(index));
                    double[] values2 = new double[format.values().length];
                    for (R record : group) {
                        for (int i4 = 0; i4 < values2.length; i4++) {
                            values2[i4] = values2[i4] + ((Double) record.get(format.values()[i4], Double.TYPE)).doubleValue();
                        }
                    }
                    if (format.display() == ChartFormat.Display.STACKED || format.display() == ChartFormat.Display.HUNDRED_PERCENT_STACKED) {
                        for (int i5 = 1; i5 < values2.length; i5++) {
                            values2[i5] = values2[i5] + values2[i5 - 1];
                        }
                    }
                    if (format.display() == ChartFormat.Display.HUNDRED_PERCENT_STACKED) {
                        for (int i6 = 0; i6 < values2.length; i6++) {
                            values2[i6] = values2[i6] / values2[values2.length - 1];
                        }
                    }
                    int shadeIndex = -1;
                    for (int i7 = values2.length - 1; i7 >= 0; i7--) {
                        if ((format.display() == ChartFormat.Display.HUNDRED_PERCENT_STACKED ? axisLegendPercent : axisLegend) > values2[i7]) {
                            break;
                        }
                        shadeIndex = i7;
                    }
                    if (shadeIndex == -1) {
                        writer.write(32);
                    } else {
                        writer.write(format.shades()[shadeIndex % format.shades().length]);
                    }
                }
                writer.write(format.newline());
            }
            if (format.showHorizontalLegend()) {
                for (int y2 = 0; y2 < horizontalBorderHeight; y2++) {
                    if (format.showVerticalLegend()) {
                        for (int x4 = 0; x4 < verticalLegendWidth; x4++) {
                            writer.write(45);
                        }
                        for (int x5 = 0; x5 < verticalBorderWidth; x5++) {
                            writer.write(43);
                        }
                    }
                    for (int x6 = 0; x6 < chartWidth; x6++) {
                        writer.write(45);
                    }
                    writer.write(format.newline());
                }
                for (int y3 = 0; y3 < horizontalLegendHeight; y3++) {
                    if (format.showVerticalLegend()) {
                        for (int x7 = 0; x7 < verticalLegendWidth; x7++) {
                            writer.write(32);
                        }
                        for (int x8 = 0; x8 < verticalBorderWidth; x8++) {
                            writer.write(124);
                        }
                    }
                    double rounding = 0.0d;
                    double x9 = 0.0d;
                    while (x9 < chartWidth) {
                        String label = String.valueOf(categories.get((int) (x9 / barWidth)));
                        int width = fp.width(label);
                        double padding = Math.max(1, (barWidth - width) / 2.0d);
                        double rounding2 = ((rounding + padding) - Math.floor(padding)) % 1.0d;
                        double x10 = x9 + padding + rounding2;
                        for (int i8 = 0; i8 < ((int) (padding + rounding2)); i8++) {
                            writer.write(32);
                        }
                        double x11 = x10 + width;
                        if (x11 >= chartWidth) {
                            break;
                        }
                        writer.write(label);
                        rounding = ((rounding2 + padding) - Math.floor(padding)) % 1.0d;
                        x9 = x11 + padding + rounding;
                        for (int i9 = 0; i9 < ((int) (padding + rounding)); i9++) {
                            writer.write(32);
                        }
                    }
                    writer.write(format.newline());
                }
            }
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Exception while writing Chart", e);
        }
    }

    @Override // org.jooq.Formattable
    public final void formatInsert(Writer writer) {
        formatInsert(writer, (Table<?>) null, this.fields.fields.fields);
    }

    @Override // org.jooq.Formattable
    public final void formatInsert(Writer writer, Table<?> table, Field<?>... f) {
        DSLContext ctx = this.configuration.dsl();
        try {
            Iterator<R> it = iterator();
            while (it.hasNext()) {
                Record record = (Record) it.next();
                if (table == null) {
                    if (record instanceof TableRecord) {
                        TableRecord<?> r = (TableRecord) record;
                        table = r.getTable();
                    } else {
                        table = DSL.table(DSL.name("UNKNOWN_TABLE"));
                    }
                }
                writer.append((CharSequence) ctx.renderInlined(DSL.insertInto(table, f).values(record.intoArray()))).append(";\n");
            }
            writer.flush();
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Exception while writing INSERTs", e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Formattable
    public final void formatHTML(Writer writer) {
        try {
            writer.append("<table>");
            writer.append("<thead>");
            writer.append("<tr>");
            for (Field<?> field : this.fields.fields.fields) {
                writer.append("<th>");
                writer.append((CharSequence) escapeXML(field.getName()));
                writer.append("</th>");
            }
            writer.append("</tr>");
            writer.append("</thead>");
            writer.append("<tbody>");
            Iterator it = iterator();
            while (it.hasNext()) {
                Record nullSafe = nullSafe((Record) it.next());
                writer.append("<tr>");
                int size = this.fields.size();
                for (int index = 0; index < size; index++) {
                    writer.append("<td>");
                    writer.append((CharSequence) escapeXML(format0(nullSafe.getValue(index), false, true)));
                    writer.append("</td>");
                }
                writer.append("</tr>");
            }
            writer.append("</tbody>");
            writer.append("</table>");
            writer.flush();
        } catch (IOException e) {
            throw new org.jooq.exception.IOException("Exception while writing HTML", e);
        }
    }

    @Override // org.jooq.Formattable
    public final Document intoXML(XMLFormat format) {
        String str;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document newDocument = builder.newDocument();
            Element createElement = newDocument.createElement(CacheOperationExpressionEvaluator.RESULT_VARIABLE);
            if (format.xmlns()) {
                createElement.setAttribute("xmlns", Constants.NS_EXPORT);
            }
            newDocument.appendChild(createElement);
            Element eRecordParent = createElement;
            if (format.header()) {
                Element eFields = newDocument.createElement("fields");
                createElement.appendChild(eFields);
                for (Field<?> field : this.fields.fields.fields) {
                    Element eField = newDocument.createElement("field");
                    if (field instanceof TableField) {
                        TableField<?, ?> f = (TableField) field;
                        Table<R> table = f.getTable();
                        if (table != null) {
                            Schema schema = table.getSchema();
                            if (schema != null) {
                                eField.setAttribute("schema", schema.getName());
                            }
                            eField.setAttribute("table", table.getName());
                        }
                    }
                    eField.setAttribute("name", field.getName());
                    eField.setAttribute("type", formatTypeName(field));
                    eFields.appendChild(eField);
                }
                Element eRecords = newDocument.createElement("records");
                createElement.appendChild(eRecords);
                eRecordParent = eRecords;
            }
            Iterator<R> it = iterator();
            while (it.hasNext()) {
                Record record = (Record) it.next();
                Element eRecord = newDocument.createElement("record");
                eRecordParent.appendChild(eRecord);
                int size = this.fields.size();
                for (int index = 0; index < size; index++) {
                    Field<?> field2 = this.fields.field(index);
                    Object value = record.get(index);
                    if (format.recordFormat() == XMLFormat.RecordFormat.COLUMN_NAME_ELEMENTS) {
                        str = escapeXML(this.fields.field(index).getName());
                    } else {
                        str = "value";
                    }
                    String tag = str;
                    Element eValue = newDocument.createElement(tag);
                    if (format.recordFormat() == XMLFormat.RecordFormat.VALUE_ELEMENTS_WITH_FIELD_ATTRIBUTE) {
                        eValue.setAttribute("field", field2.getName());
                    }
                    eRecord.appendChild(eValue);
                    if (value != null) {
                        if ((value instanceof XML) && !format.quoteNested()) {
                            eValue.appendChild(createContent(builder, newDocument, ((XML) value).data()));
                        } else {
                            eValue.setTextContent(format0(value, false, false));
                        }
                    }
                }
            }
            return newDocument;
        } catch (ParserConfigurationException ignore) {
            throw new RuntimeException(ignore);
        }
    }

    static final DocumentFragment createContent(DocumentBuilder builder, Document doc, String text) {
        if (text == null) {
            return null;
        }
        if (text.contains("<") || text.contains(BeanFactory.FACTORY_BEAN_PREFIX)) {
            builder.setErrorHandler(new DefaultHandler());
            try {
                String text2 = text.trim();
                if (text2.startsWith("<?xml")) {
                    Document parsed = builder.parse(new InputSource(new StringReader(text2)));
                    DocumentFragment fragment = parsed.createDocumentFragment();
                    fragment.appendChild(parsed.getDocumentElement());
                    return (DocumentFragment) doc.importNode(fragment, true);
                }
                String wrapped = "<dummy>" + text2 + "</dummy>";
                Document parsed2 = builder.parse(new InputSource(new StringReader(wrapped)));
                DocumentFragment fragment2 = parsed2.createDocumentFragment();
                NodeList children = parsed2.getDocumentElement().getChildNodes();
                while (children.getLength() > 0) {
                    fragment2.appendChild(children.item(0));
                }
                return (DocumentFragment) doc.importNode(fragment2, true);
            } catch (IOException e) {
                return null;
            } catch (SAXException e2) {
                return null;
            }
        }
        return null;
    }

    @Override // org.jooq.Formattable
    public final <H extends ContentHandler> H intoXML(H handler, XMLFormat format) throws SAXException {
        String str;
        Attributes empty = new AttributesImpl();
        handler.startDocument();
        if (format.xmlns()) {
            handler.startPrefixMapping("", Constants.NS_EXPORT);
        }
        handler.startElement("", "", CacheOperationExpressionEvaluator.RESULT_VARIABLE, empty);
        if (format.header()) {
            handler.startElement("", "", "fields", empty);
            for (Field<?> field : this.fields.fields.fields) {
                AttributesImpl attrs = new AttributesImpl();
                if (field instanceof TableField) {
                    TableField<?, ?> f = (TableField) field;
                    Table<R> table = f.getTable();
                    if (table != null) {
                        Schema schema = table.getSchema();
                        if (schema != null) {
                            attrs.addAttribute("", "", "schema", "CDATA", schema.getName());
                        }
                        attrs.addAttribute("", "", "table", "CDATA", table.getName());
                    }
                }
                attrs.addAttribute("", "", "name", "CDATA", field.getName());
                attrs.addAttribute("", "", "type", "CDATA", formatTypeName(field));
                handler.startElement("", "", "field", attrs);
                handler.endElement("", "", "field");
            }
            handler.endElement("", "", "fields");
            handler.startElement("", "", "records", empty);
        }
        Iterator<R> it = iterator();
        while (it.hasNext()) {
            Record record = (Record) it.next();
            handler.startElement("", "", "record", empty);
            int size = this.fields.size();
            for (int index = 0; index < size; index++) {
                Field<?> field2 = this.fields.field(index);
                Object value = record.get(index);
                if (format.recordFormat() == XMLFormat.RecordFormat.COLUMN_NAME_ELEMENTS) {
                    str = escapeXML(this.fields.field(index).getName());
                } else {
                    str = "value";
                }
                String tag = str;
                AttributesImpl attrs2 = new AttributesImpl();
                if (format.recordFormat() == XMLFormat.RecordFormat.VALUE_ELEMENTS_WITH_FIELD_ATTRIBUTE) {
                    attrs2.addAttribute("", "", "field", "CDATA", field2.getName());
                }
                handler.startElement("", "", tag, attrs2);
                if (value != null) {
                    char[] chars = format0(value, false, false).toCharArray();
                    handler.characters(chars, 0, chars.length);
                }
                handler.endElement("", "", tag);
            }
            handler.endElement("", "", "record");
        }
        if (format.header()) {
            handler.endElement("", "", "records");
        }
        if (format.xmlns()) {
            handler.endPrefixMapping("");
        }
        handler.endDocument();
        return handler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String format0(Object value, boolean changed, boolean visual) {
        String formatted;
        String formatted2 = (changed && visual) ? "*" : "";
        if (value == null) {
            formatted = formatted2 + (visual ? "{null}" : null);
        } else if (value instanceof byte[]) {
            byte[] a = (byte[]) value;
            formatted = formatted2 + Base64.getEncoder().encodeToString(a);
        } else if (value instanceof Object[]) {
            Object[] a2 = (Object[]) value;
            formatted = formatted2 + ((String) Arrays.stream(a2).map(f -> {
                return format0(f, false, visual);
            }).collect(Collectors.joining(", ", PropertyAccessor.PROPERTY_KEY_PREFIX, "]")));
        } else if (value instanceof EnumType) {
            EnumType e = (EnumType) value;
            formatted = formatted2 + e.getLiteral();
        } else if (value instanceof List) {
            List<?> l = (List) value;
            formatted = formatted2 + ((String) l.stream().map(f2 -> {
                return format0(f2, false, visual);
            }).collect(Collectors.joining(", ", PropertyAccessor.PROPERTY_KEY_PREFIX, "]")));
        } else if (value instanceof Record) {
            Record r = (Record) value;
            formatted = formatted2 + ((String) Arrays.stream(r.intoArray()).map(f3 -> {
                return format0(f3, false, visual);
            }).collect(Collectors.joining(", ", "(", ")")));
        } else if (value instanceof Param) {
            Param<?> p = (Param) value;
            formatted = formatted2 + format0(p.getValue(), false, visual);
        } else if (value instanceof Date) {
            Date d = (Date) value;
            String date = value.toString();
            if (Date.valueOf(date).equals(value)) {
                formatted = formatted2 + date;
            } else {
                formatted = formatted2 + String.valueOf(new Timestamp(d.getTime()));
            }
        } else {
            formatted = formatted2 + value.toString();
        }
        return formatted;
    }

    private static final String escapeXML(String string) {
        return StringUtils.replaceEach(string, new String[]{"\"", "'", "<", ">", BeanFactory.FACTORY_BEAN_PREFIX}, new String[]{"&quot;", "&apos;", "&lt;", "&gt;", "&amp;"});
    }
}
