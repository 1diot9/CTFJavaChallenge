package org.jooq.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.JooqLogger;
import org.springframework.cache.interceptor.CacheOperationExpressionEvaluator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLHandler.class */
public final class XMLHandler<R extends Record> extends DefaultHandler {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) XMLHandler.class);
    private static final boolean debug = false;
    private final DSLContext ctx;
    private final Deque<State<R>> states = new ArrayDeque();
    private State<R> s;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLHandler$State.class */
    public static class State<R extends Record> {
        final DSLContext ctx;
        AbstractRow<R> row;
        final Class<? extends R> recordType;
        boolean inResult;
        boolean inFields;
        int inRecord;
        boolean inColumn;
        boolean inElement;
        Result<R> result;
        final List<Field<?>> fields;
        final List<Object> values;
        List<Object> elements;
        int column;

        State(DSLContext ctx, AbstractRow<R> row, Class<? extends R> recordType) {
            this.ctx = ctx;
            this.row = row;
            this.recordType = recordType != null ? recordType : Record.class;
            this.fields = new ArrayList();
            this.values = new ArrayList();
        }

        final R into(R r) {
            for (int i = 0; i < this.fields.size(); i++) {
                if (this.fields.get(i).getDataType().isBinary()) {
                    Object obj = this.values.get(i);
                    if (obj instanceof String) {
                        String s = (String) obj;
                        this.values.set(i, Base64.getDecoder().decode(s));
                    }
                }
            }
            r.from(this.values);
            r.changed(false);
            return r;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLHandler(DSLContext ctx, AbstractRow<R> row, Class<? extends R> recordType) {
        this.ctx = ctx;
        this.s = new State<>(ctx, row, recordType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Result<R> read(String string) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            } catch (ParserConfigurationException | SAXNotRecognizedException e) {
            }
            try {
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            } catch (ParserConfigurationException | SAXNotRecognizedException e2) {
            }
            try {
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (ParserConfigurationException | SAXNotRecognizedException e3) {
            }
            try {
                factory.setXIncludeAware(false);
            } catch (UnsupportedOperationException e4) {
            }
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new ByteArrayInputStream(string.getBytes(this.ctx.configuration().charsetProvider().provide())), this);
            return this.s.result;
        } catch (Exception e5) {
            throw new DataAccessException("Could not read the XML string", e5);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:63:0x0245, code lost:            if (r0 != null) goto L61;     */
    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void startElement(java.lang.String r8, java.lang.String r9, java.lang.String r10, org.xml.sax.Attributes r11) throws org.xml.sax.SAXException {
        /*
            Method dump skipped, instructions count: 808
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.XMLHandler.startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes):void");
    }

    private final boolean isNil(Attributes attributes) {
        return "true".equals(attributes.getValue("xsi:nil"));
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public final void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.states.isEmpty() && this.s.inResult && this.s.inRecord == 0 && CacheOperationExpressionEvaluator.RESULT_VARIABLE.equalsIgnoreCase(qName)) {
            if (this.s.result == null) {
                initResult();
            }
            this.s.inResult = false;
            return;
        }
        if (this.s.inResult && this.s.inFields && "fields".equalsIgnoreCase(qName)) {
            this.s.inFields = false;
            initResult();
            return;
        }
        if (!this.s.inResult || !this.s.inFields || !"field".equalsIgnoreCase(qName)) {
            if (!this.s.inResult || !"records".equalsIgnoreCase(qName)) {
                if (this.s.inRecord > 0 && "record".equalsIgnoreCase(qName)) {
                    this.s.inRecord--;
                    initResult();
                    Result<R> result = this.s.result;
                    RecordDelegate newRecord = Tools.newRecord(true, this.s.recordType, this.s.row, this.ctx.configuration());
                    State<R> state = this.s;
                    Objects.requireNonNull(state);
                    result.add(newRecord.operate(state::into));
                    this.s.values.clear();
                    this.s.column = 0;
                    return;
                }
                if (this.s.inColumn && "element".equalsIgnoreCase(qName) && this.s.elements != null) {
                    this.s.inElement = false;
                    this.s.elements.add(this.s.values.get(this.s.column));
                    this.s.values.remove(this.s.column);
                    return;
                }
                if (!this.states.isEmpty()) {
                    State<R> peek = this.states.peek();
                    Field<?> f = peek.row.field(peek.column);
                    if ("record".equalsIgnoreCase(qName) && f.getDataType().isRecord()) {
                        List<Object> list = peek.values;
                        RecordDelegate newRecord2 = Tools.newRecord(true, this.s.recordType, this.s.row, this.ctx.configuration());
                        State<R> state2 = this.s;
                        Objects.requireNonNull(state2);
                        list.add(newRecord2.operate(state2::into));
                        this.s = this.states.pop();
                        return;
                    }
                    if (CacheOperationExpressionEvaluator.RESULT_VARIABLE.equalsIgnoreCase(qName) && f.getDataType().isMultiset()) {
                        initResult();
                        peek.values.add(this.s.result);
                        this.s = this.states.pop();
                        return;
                    }
                } else if (this.s.elements != null) {
                    this.s.values.add(this.s.elements);
                    this.s.elements = null;
                }
                this.s.inColumn = false;
                this.s.column++;
            }
        }
    }

    private void initResult() {
        if (this.s.result == null) {
            if (this.s.row == null) {
                if (onlyValueFields(this.s.fields)) {
                    this.s.row = (AbstractRow<R>) Tools.row0(Tools.fields(this.s.fields.size()));
                } else {
                    this.s.row = (AbstractRow<R>) Tools.row0((Field<?>[]) this.s.fields.toArray(Tools.EMPTY_FIELD));
                }
            }
            this.s.result = new ResultImpl(this.ctx.configuration(), this.s.row);
        }
    }

    private static boolean onlyValueFields(List<Field<?>> fields) {
        if (fields.size() <= 1) {
            return false;
        }
        return Tools.allMatch(fields, f -> {
            return "value".equalsIgnoreCase(f.getName());
        });
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public final void characters(char[] ch2, int start, int length) throws SAXException {
        if (this.s.inColumn) {
            DataType<?> t = this.s.fields.get(this.s.column).getDataType();
            if (!t.isRecord() && !t.isMultiset()) {
                if (!t.isArray() || this.s.inElement) {
                    String value = new String(ch2, start, length);
                    if (this.s.values.size() == this.s.column) {
                        this.s.values.add(value);
                        return;
                    }
                    Object old = this.s.values.get(this.s.column);
                    if (old == null) {
                        this.s.values.set(this.s.column, value);
                    } else {
                        this.s.values.set(this.s.column, String.valueOf(old) + value);
                    }
                }
            }
        }
    }
}
