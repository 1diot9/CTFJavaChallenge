package org.jooq.impl;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.tools.StringUtils;
import org.jooq.tools.json.ContainerFactory;
import org.jooq.tools.json.JSONParser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONReader.class */
public final class JSONReader<R extends Record> {
    private final DSLContext ctx;
    private final AbstractRow<R> row;
    private final Class<? extends R> recordType;
    private final boolean multiset;
    private static final Set<SQLDialect> ENCODE_BINARY_AS_HEX = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> ENCODE_BINARY_AS_TEXT = SQLDialect.supportedBy(SQLDialect.MARIADB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONReader(DSLContext ctx, AbstractRow<R> row, Class<? extends R> recordType, boolean multiset) {
        this.ctx = ctx;
        this.row = row;
        this.recordType = recordType != null ? recordType : Record.class;
        this.multiset = multiset;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Result<R> read(String string) {
        return read(new StringReader(string));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Result<R> read(Reader reader) {
        return read(reader, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Result<R> read(Reader reader, boolean multiset) {
        try {
            Object root = new JSONParser().parse(reader, new ContainerFactory() { // from class: org.jooq.impl.JSONReader.1
                @Override // org.jooq.tools.json.ContainerFactory
                public Map createObjectContainer() {
                    return new LinkedHashMap();
                }

                @Override // org.jooq.tools.json.ContainerFactory
                public List createArrayContainer() {
                    return new ArrayList();
                }
            });
            return read(this.ctx, this.row, this.recordType, multiset, root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v60, types: [org.jooq.impl.AbstractRow] */
    /* JADX WARN: Type inference failed for: r3v5, types: [org.jooq.impl.AbstractRow] */
    /* JADX WARN: Type inference failed for: r3v9, types: [org.jooq.impl.AbstractRow] */
    private static final <R extends Record> Result<R> read(DSLContext ctx, AbstractRow<R> actualRow, Class<? extends R> recordType, boolean multiset, Object root) {
        List<?> records;
        List<Object> list;
        List<Field<?>> header = new ArrayList<>();
        Result<R> result = null;
        if (root instanceof Map) {
            Map<String, Object> o1 = (Map) root;
            List<Map<String, String>> fields = (List) o1.get("fields");
            if (fields != null) {
                for (Map<String, String> field : fields) {
                    String catalog = field.get("catalog");
                    String schema = field.get("schema");
                    String table = field.get("table");
                    String name = field.get("name");
                    String type = field.get("type");
                    header.add(DSL.field(DSL.name(catalog, schema, table, name), DefaultDataType.getDataType((SQLDialect) null, StringUtils.defaultIfBlank(type, "VARCHAR"))));
                }
            }
            records = (List) o1.get("records");
        } else {
            records = (List) root;
        }
        if (actualRow == null && !header.isEmpty()) {
            actualRow = Tools.row0(header);
        }
        if (actualRow != null) {
            result = new ResultImpl<>(ctx.configuration(), actualRow);
        }
        if (records != null) {
            for (Object o3 : records) {
                if (o3 instanceof Map) {
                    Map<String, Object> record = (Map) o3;
                    if (result == null) {
                        if (header.isEmpty()) {
                            for (String name2 : record.keySet()) {
                                header.add(DSL.field(DSL.name(name2), SQLDataType.VARCHAR));
                            }
                        }
                        Configuration configuration = ctx.configuration();
                        ?? row0 = Tools.row0(header);
                        actualRow = row0;
                        result = new ResultImpl<>(configuration, (AbstractRow) row0);
                    }
                    if (multiset) {
                        list = patchRecord(ctx, multiset, actualRow, sortedValues(record));
                    } else {
                        list = null;
                    }
                    List<Object> list2 = list;
                    result.add(Tools.newRecord(true, recordType, actualRow, ctx.configuration()).operate(r -> {
                        if (multiset) {
                            r.from(list2);
                        } else {
                            r.fromMap(record);
                        }
                        r.changed(false);
                        return r;
                    }));
                } else {
                    List<Object> record2 = (List) o3;
                    if (result == null) {
                        if (header.isEmpty()) {
                            header.addAll(Arrays.asList(Tools.fields(record2.size())));
                        }
                        Configuration configuration2 = ctx.configuration();
                        ?? row02 = Tools.row0(header);
                        actualRow = row02;
                        result = new ResultImpl<>(configuration2, (AbstractRow) row02);
                    }
                    patchRecord(ctx, multiset, actualRow, record2);
                    if (record2 == null) {
                        result.add(null);
                    } else {
                        result.add(Tools.newRecord(true, recordType, actualRow, ctx.configuration()).operate(r2 -> {
                            r2.from(record2);
                            r2.changed(false);
                            return r2;
                        }));
                    }
                }
            }
        }
        return result;
    }

    private static final List<Object> sortedValues(Map<String, Object> record) {
        List<Object> result = Arrays.asList(new Object[record.size()]);
        for (Map.Entry<String, Object> e : record.entrySet()) {
            result.set(Integer.parseInt(e.getKey().substring(1)), e.getValue());
        }
        return result;
    }

    private static final List<Object> patchRecord(DSLContext ctx, boolean multiset, Fields result, List<Object> record) {
        for (int i = 0; i < result.fields().length; i++) {
            Field<?> field = result.field(i);
            if (field.getType() == byte[].class && (record.get(i) instanceof String)) {
                String s = (String) record.get(i);
                if (multiset) {
                    if (ENCODE_BINARY_AS_HEX.contains(ctx.dialect())) {
                        if (s.startsWith("\\x")) {
                            record.set(i, Tools.convertHexToBytes(s, 1, Integer.MAX_VALUE));
                        } else {
                            record.set(i, Tools.convertHexToBytes(s));
                        }
                    } else if (ENCODE_BINARY_AS_TEXT.contains(ctx.dialect())) {
                        record.set(i, s);
                    } else if (s.startsWith("base64:type15:")) {
                        record.set(i, Base64.getDecoder().decode(s.substring(14)));
                    } else {
                        record.set(i, Base64.getDecoder().decode(s));
                    }
                } else {
                    record.set(i, Base64.getDecoder().decode(s));
                }
            } else if (multiset && field.getDataType().isMultiset()) {
                record.set(i, read(ctx, (AbstractRow) field.getDataType().getRow(), field.getDataType().getRecordType(), multiset, record.get(i)));
            } else if (multiset && field.getDataType().isRecord() && (record.get(i) instanceof List)) {
                AbstractRow<? extends Record> actualRow = (AbstractRow) field.getDataType().getRow();
                Class<? extends Record> recordType = field.getDataType().getRecordType();
                List<Object> l = (List) record.get(i);
                patchRecord(ctx, multiset, actualRow, l);
                record.set(i, Tools.newRecord(true, recordType, actualRow, ctx.configuration()).operate(r -> {
                    r.from(l);
                    r.changed(false);
                    return r;
                }));
            }
        }
        return record;
    }
}
