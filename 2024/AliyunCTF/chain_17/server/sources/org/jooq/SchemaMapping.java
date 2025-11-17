package org.jooq;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;
import org.jooq.conf.MappedCatalog;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.MappedSchemaObject;
import org.jooq.conf.MappedTable;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.DSL;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;

@Deprecated(forRemoval = true, since = "2.0")
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SchemaMapping.class */
public class SchemaMapping implements Serializable {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) SchemaMapping.class);
    private static volatile boolean loggedDeprecation = false;
    private final Configuration configuration;
    private volatile transient Map<String, Catalog> catalogs;
    private volatile transient Map<String, Schema> schemata;
    private volatile transient Map<String, Table<?>> tables;
    private volatile transient Map<String, UDT<?>> udts;

    public SchemaMapping(Configuration configuration) {
        this.configuration = configuration;
    }

    private final RenderMapping mapping() {
        return SettingsTools.getRenderMapping(this.configuration.settings());
    }

    private final boolean renderCatalog() {
        return Boolean.TRUE.equals(this.configuration.settings().isRenderCatalog());
    }

    private final boolean renderSchema() {
        return Boolean.TRUE.equals(this.configuration.settings().isRenderSchema());
    }

    private static void logDeprecation() {
        if (!loggedDeprecation) {
            loggedDeprecation = true;
            log.warn("DEPRECATION", "org.jooq.SchemaMapping is deprecated as of jOOQ 2.0.5. Consider using jOOQ's runtime configuration org.jooq.conf.Settings instead");
        }
    }

    public void use(Schema schema) {
        use(schema.getName());
    }

    public void use(String schemaName) {
        logDeprecation();
        mapping().setDefaultSchema(schemaName);
    }

    public void add(String inputSchema, String outputSchema) {
        logDeprecation();
        MappedSchema schema = null;
        Iterator<MappedSchema> it = mapping().getSchemata().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            MappedSchema s = it.next();
            if (inputSchema.equals(s.getInput())) {
                schema = s;
                break;
            }
        }
        if (schema == null) {
            schema = new MappedSchema().withInput(inputSchema);
            mapping().getSchemata().add(schema);
        }
        schema.setOutput(outputSchema);
    }

    public void add(String inputSchema, Schema outputSchema) {
        add(inputSchema, outputSchema.getName());
    }

    public void add(Schema inputSchema, Schema outputSchema) {
        add(inputSchema.getName(), outputSchema.getName());
    }

    public void add(Schema inputSchema, String outputSchema) {
        add(inputSchema.getName(), outputSchema);
    }

    public void add(Table<?> inputTable, Table<?> outputTable) {
        add(inputTable, outputTable.getName());
    }

    public void add(Table<?> inputTable, String outputTable) {
        logDeprecation();
        MappedSchema schema = null;
        MappedTable table = null;
        Iterator<MappedSchema> it = mapping().getSchemata().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            MappedSchema s = it.next();
            if (inputTable.getSchema().getName().equals(s.getInput())) {
                Iterator<MappedTable> it2 = s.getTables().iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    MappedTable t = it2.next();
                    if (inputTable.getName().equals(t.getInput())) {
                        table = t;
                        break;
                    }
                }
                schema = s;
            }
        }
        if (schema == null) {
            schema = new MappedSchema().withInput(inputTable.getSchema().getName());
            mapping().getSchemata().add(schema);
        }
        if (table == null) {
            table = new MappedTable().withInput(inputTable.getName());
            schema.getTables().add(table);
        }
        table.setOutput(outputTable);
    }

    @Nullable
    public Catalog map(Catalog catalog) {
        if (!renderCatalog()) {
            return null;
        }
        if (catalog instanceof RenamedCatalog) {
            return catalog;
        }
        Catalog result = catalog;
        if (result == null) {
            result = DSL.catalog(DSL.name(""));
        }
        String catalogName = result.getName();
        RenderMapping m = mapping();
        if (!m.getCatalogs().isEmpty() || !StringUtils.isEmpty(m.getDefaultCatalog())) {
            if (!getCatalogs().containsKey(catalogName)) {
                synchronized (this) {
                    if (!getCatalogs().containsKey(catalogName)) {
                        Iterator<MappedCatalog> it = m.getCatalogs().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            MappedCatalog c = it.next();
                            if (matches(c, catalogName)) {
                                if (!StringUtils.isBlank(c.getOutput())) {
                                    if (c.getInput() != null && !c.getOutput().equals(catalogName)) {
                                        result = new RenamedCatalog(result, c.getOutput());
                                    } else if (c.getInputExpression() != null) {
                                        result = new RenamedCatalog(result, c.getInputExpression().matcher(catalogName).replaceAll(c.getOutput()));
                                    }
                                }
                            }
                        }
                        if ("".equals(result.getName()) || result.getName().equals(m.getDefaultCatalog())) {
                            result = null;
                        }
                        getCatalogs().put(catalogName, result);
                    }
                }
            }
            result = getCatalogs().get(catalogName);
        } else if ("".equals(result.getName())) {
            result = null;
        }
        return result;
    }

    @Nullable
    public Schema map(Schema schema) {
        if (!renderSchema()) {
            return null;
        }
        if (schema instanceof RenamedSchema) {
            return schema;
        }
        Schema result = schema;
        if (result == null) {
            result = DSL.schema(DSL.name(""));
        }
        RenderMapping m = mapping();
        if (!m.getSchemata().isEmpty() || !m.getCatalogs().isEmpty() || !StringUtils.isEmpty(m.getDefaultSchema()) || !StringUtils.isEmpty(m.getDefaultCatalog())) {
            Catalog catalog = result.getCatalog();
            if (catalog == null) {
                catalog = DSL.catalog(DSL.name(""));
            }
            String catalogName = catalog.getName();
            String schemaName = result.getName();
            String key = StringUtils.isEmpty(catalogName) ? schemaName : catalogName + "." + schemaName;
            if (!getSchemata().containsKey(key)) {
                synchronized (this) {
                    if (!getSchemata().containsKey(key)) {
                        Iterator<MappedCatalog> it = m.getCatalogs().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            MappedCatalog c = it.next();
                            if (matches(c, catalogName)) {
                                Iterator<MappedSchema> it2 = c.getSchemata().iterator();
                                while (true) {
                                    if (it2.hasNext()) {
                                        MappedSchema s = it2.next();
                                        if (matches(s, schemaName)) {
                                            if (!StringUtils.isBlank(s.getOutput())) {
                                                if (s.getInput() != null && !s.getOutput().equals(schemaName)) {
                                                    result = new RenamedSchema(map(catalog), result, s.getOutput());
                                                } else if (s.getInputExpression() != null) {
                                                    result = new RenamedSchema(map(catalog), result, s.getInputExpression().matcher(schemaName).replaceAll(s.getOutput()));
                                                }
                                            }
                                        }
                                    } else {
                                        result = new RenamedSchema(map(catalog), result, schemaName);
                                        break;
                                    }
                                }
                            }
                        }
                        if (!(result instanceof RenamedSchema)) {
                            Iterator<MappedSchema> it3 = m.getSchemata().iterator();
                            while (true) {
                                if (!it3.hasNext()) {
                                    break;
                                }
                                MappedSchema s2 = it3.next();
                                if (matches(s2, schemaName)) {
                                    if (!StringUtils.isBlank(s2.getOutput())) {
                                        if (s2.getInput() != null && !s2.getOutput().equals(schemaName)) {
                                            result = new RenamedSchema(map(catalog), result, s2.getOutput());
                                        } else if (s2.getInputExpression() != null) {
                                            result = new RenamedSchema(map(catalog), result, s2.getInputExpression().matcher(schemaName).replaceAll(s2.getOutput()));
                                        }
                                    }
                                }
                            }
                        }
                        if (result.getCatalog() != null && map(result.getCatalog()) == null) {
                            result = new RenamedSchema(null, result, result.getName());
                        }
                        if ("".equals(result.getName())) {
                            result = null;
                        } else if (result.getName().equals(m.getDefaultSchema()) && (result.getCatalog() == null || "".equals(result.getCatalog().getName()) || result.getCatalog().getName().equals(m.getDefaultCatalog()))) {
                            result = null;
                        }
                        getSchemata().put(key, result);
                    }
                }
            }
            result = getSchemata().get(key);
        } else if ("".equals(result.getName())) {
            result = null;
        }
        return result;
    }

    @Nullable
    public <R extends Record> Table<R> map(Table<R> table) {
        return (Table) map0(table, () -> {
            return getTables();
        }, s -> {
            return s.getTables();
        }, RenamedTable::new);
    }

    @Nullable
    public <R extends UDTRecord<R>> UDT<R> map(UDT<R> udt) {
        return (UDT) map0(udt, () -> {
            return getUDTs();
        }, s -> {
            return s.getUdts();
        }, RenamedUDT::new);
    }

    private <Q extends Qualified> Q map0(Q part, Supplier<Map<String, Q>> map, Function<MappedSchema, ? extends List<? extends MappedSchemaObject>> schemaObjects, Function3<Schema, Q, String, Q> rename) {
        String str;
        Q result = part;
        if (result != null && (!mapping().getSchemata().isEmpty() || !mapping().getCatalogs().isEmpty())) {
            Catalog catalog = result.getCatalog();
            Schema schema = result.getSchema();
            if (catalog == null) {
                catalog = DSL.catalog(DSL.name(""));
            }
            if (schema == null) {
                schema = DSL.schema(DSL.name(""));
            }
            String catalogName = catalog.getName();
            String schemaName = schema.getName();
            String name = result.getName();
            if (StringUtils.isEmpty(catalogName)) {
                str = StringUtils.isEmpty(schemaName) ? name : schemaName + "." + name;
            } else {
                str = catalogName + "." + schemaName + "." + name;
            }
            String key = str;
            if (!map.get().containsKey(key)) {
                synchronized (this) {
                    if (!map.get().containsKey(key)) {
                        Iterator<MappedCatalog> it = mapping().getCatalogs().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            MappedCatalog c = it.next();
                            if (matches(c, catalogName)) {
                                Iterator<MappedSchema> it2 = c.getSchemata().iterator();
                                while (true) {
                                    if (it2.hasNext()) {
                                        MappedSchema s = it2.next();
                                        if (matches(s, schemaName)) {
                                            for (MappedSchemaObject t : schemaObjects.apply(s)) {
                                                if (matches(t, name)) {
                                                    if (!StringUtils.isBlank(t.getOutput())) {
                                                        if (t.getInput() != null && !t.getOutput().equals(name)) {
                                                            result = rename.apply(map(schema), result, t.getOutput());
                                                        } else if (t.getInputExpression() != null) {
                                                            result = rename.apply(map(schema), result, t.getInputExpression().matcher(name).replaceAll(t.getOutput()));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        result = rename.apply(map(schema), result, name);
                                        break;
                                    }
                                }
                            }
                        }
                        if (!(result instanceof RenamedSchemaElement)) {
                            Iterator<MappedSchema> it3 = mapping().getSchemata().iterator();
                            while (true) {
                                if (!it3.hasNext()) {
                                    break;
                                }
                                MappedSchema s2 = it3.next();
                                if (matches(s2, schemaName)) {
                                    Iterator<? extends MappedSchemaObject> it4 = schemaObjects.apply(s2).iterator();
                                    while (true) {
                                        if (it4.hasNext()) {
                                            MappedSchemaObject t2 = it4.next();
                                            if (matches(t2, name)) {
                                                if (!StringUtils.isBlank(t2.getOutput())) {
                                                    if (t2.getInput() != null && !t2.getOutput().equals(name)) {
                                                        result = rename.apply(map(schema), result, t2.getOutput());
                                                    } else if (t2.getInputExpression() != null) {
                                                        result = rename.apply(map(schema), result, t2.getInputExpression().matcher(name).replaceAll(t2.getOutput()));
                                                    }
                                                }
                                            }
                                        } else {
                                            result = rename.apply(map(schema), result, name);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        map.get().put(key, result);
                    }
                }
            }
            result = map.get().get(key);
        }
        return result;
    }

    private final boolean matches(MappedCatalog c, String catalogName) {
        return (c.getInput() != null && catalogName.equals(c.getInput())) || (c.getInputExpression() != null && c.getInputExpression().matcher(catalogName).matches());
    }

    private final boolean matches(MappedSchema s, String schemaName) {
        return (s.getInput() != null && schemaName.equals(s.getInput())) || (s.getInputExpression() != null && s.getInputExpression().matcher(schemaName).matches());
    }

    private final boolean matches(MappedSchemaObject t, String tableName) {
        return (t.getInput() != null && tableName.equals(t.getInput())) || (t.getInputExpression() != null && t.getInputExpression().matcher(tableName).matches());
    }

    public void setDefaultSchema(String schema) {
        use(schema);
    }

    public void setSchemaMapping(Map<String, String> schemaMap) {
        schemaMap.forEach(this::add);
    }

    private final Map<String, Catalog> getCatalogs() {
        if (this.catalogs == null) {
            synchronized (this) {
                if (this.catalogs == null) {
                    this.catalogs = new HashMap();
                }
            }
        }
        return this.catalogs;
    }

    private final Map<String, Schema> getSchemata() {
        if (this.schemata == null) {
            synchronized (this) {
                if (this.schemata == null) {
                    this.schemata = new HashMap();
                }
            }
        }
        return this.schemata;
    }

    private final Map<String, Table<?>> getTables() {
        if (this.tables == null) {
            synchronized (this) {
                if (this.tables == null) {
                    this.tables = new HashMap();
                }
            }
        }
        return this.tables;
    }

    private final Map<String, UDT<?>> getUDTs() {
        if (this.udts == null) {
            synchronized (this) {
                if (this.udts == null) {
                    this.udts = new HashMap();
                }
            }
        }
        return this.udts;
    }

    public String toString() {
        return String.valueOf(mapping());
    }
}
