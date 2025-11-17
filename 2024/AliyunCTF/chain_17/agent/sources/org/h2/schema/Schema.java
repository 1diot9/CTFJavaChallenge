package org.h2.schema;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.api.ErrorCode;
import org.h2.command.ddl.CreateSynonymData;
import org.h2.command.ddl.CreateTableData;
import org.h2.constraint.Constraint;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.table.MaterializedView;
import org.h2.table.MetaTable;
import org.h2.table.Table;
import org.h2.table.TableLink;
import org.h2.table.TableSynonym;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/Schema.class */
public class Schema extends DbObject {
    private RightOwner owner;
    private final boolean system;
    private ArrayList<String> tableEngineParams;
    private final ConcurrentHashMap<String, Table> tablesAndViews;
    private final ConcurrentHashMap<String, Domain> domains;
    private final ConcurrentHashMap<String, TableSynonym> synonyms;
    private final ConcurrentHashMap<String, Index> indexes;
    private final ConcurrentHashMap<String, Sequence> sequences;
    private final ConcurrentHashMap<String, TriggerObject> triggers;
    private final ConcurrentHashMap<String, Constraint> constraints;
    private final ConcurrentHashMap<String, Constant> constants;
    private final ConcurrentHashMap<String, UserDefinedFunction> functionsAndAggregates;
    private final HashSet<String> temporaryUniqueNames;

    public Schema(Database database, int i, String str, RightOwner rightOwner, boolean z) {
        super(database, i, str, 8);
        this.temporaryUniqueNames = new HashSet<>();
        this.tablesAndViews = database.newConcurrentStringMap();
        this.domains = database.newConcurrentStringMap();
        this.synonyms = database.newConcurrentStringMap();
        this.indexes = database.newConcurrentStringMap();
        this.sequences = database.newConcurrentStringMap();
        this.triggers = database.newConcurrentStringMap();
        this.constraints = database.newConcurrentStringMap();
        this.constants = database.newConcurrentStringMap();
        this.functionsAndAggregates = database.newConcurrentStringMap();
        this.owner = rightOwner;
        this.system = z;
    }

    public boolean canDrop() {
        return !this.system;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        if (this.system) {
            return null;
        }
        StringBuilder sb = new StringBuilder("CREATE SCHEMA IF NOT EXISTS ");
        getSQL(sb, 0).append(" AUTHORIZATION ");
        this.owner.getSQL(sb, 0);
        return sb.toString();
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 10;
    }

    public boolean isEmpty() {
        return this.tablesAndViews.isEmpty() && this.domains.isEmpty() && this.synonyms.isEmpty() && this.indexes.isEmpty() && this.sequences.isEmpty() && this.triggers.isEmpty() && this.constraints.isEmpty() && this.constants.isEmpty() && this.functionsAndAggregates.isEmpty();
    }

    @Override // org.h2.engine.DbObject
    public ArrayList<DbObject> getChildren() {
        ArrayList<DbObject> newSmallArrayList = Utils.newSmallArrayList();
        Iterator<Right> it = this.database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            if (next.getGrantedObject() == this) {
                newSmallArrayList.add(next);
            }
        }
        return newSmallArrayList;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        removeChildrenFromMap(sessionLocal, this.triggers);
        removeChildrenFromMap(sessionLocal, this.constraints);
        boolean z = true;
        while (true) {
            boolean z2 = z;
            if (!this.tablesAndViews.isEmpty()) {
                boolean z3 = false;
                for (Table table : this.tablesAndViews.values()) {
                    if (table.getName() != null) {
                        Table dependentTable = this.database.getDependentTable(table, table);
                        if (dependentTable == null) {
                            this.database.removeSchemaObject(sessionLocal, table);
                            z3 = true;
                        } else {
                            if (dependentTable.getSchema() != this) {
                                throw DbException.get(ErrorCode.CANNOT_DROP_2, table.getTraceSQL(), dependentTable.getTraceSQL());
                            }
                            if (!z2) {
                                dependentTable.removeColumnExpressionsDependencies(sessionLocal);
                                dependentTable.setModified();
                                this.database.updateMeta(sessionLocal, dependentTable);
                            }
                        }
                    }
                }
                z = z3;
            } else {
                removeChildrenFromMap(sessionLocal, this.domains);
                removeChildrenFromMap(sessionLocal, this.indexes);
                removeChildrenFromMap(sessionLocal, this.sequences);
                removeChildrenFromMap(sessionLocal, this.constants);
                removeChildrenFromMap(sessionLocal, this.functionsAndAggregates);
                Iterator<Right> it = this.database.getAllRights().iterator();
                while (it.hasNext()) {
                    Right next = it.next();
                    if (next.getGrantedObject() == this) {
                        this.database.removeDatabaseObject(sessionLocal, next);
                    }
                }
                this.database.removeMeta(sessionLocal, getId());
                this.owner = null;
                invalidate();
                return;
            }
        }
    }

    private void removeChildrenFromMap(SessionLocal sessionLocal, ConcurrentHashMap<String, ? extends SchemaObject> concurrentHashMap) {
        if (!concurrentHashMap.isEmpty()) {
            for (SchemaObject schemaObject : concurrentHashMap.values()) {
                if (schemaObject.isValid()) {
                    this.database.removeSchemaObject(sessionLocal, schemaObject);
                }
            }
        }
    }

    public RightOwner getOwner() {
        return this.owner;
    }

    public ArrayList<String> getTableEngineParams() {
        return this.tableEngineParams;
    }

    public void setTableEngineParams(ArrayList<String> arrayList) {
        this.tableEngineParams = arrayList;
    }

    private Map<String, SchemaObject> getMap(int i) {
        AbstractMap abstractMap;
        switch (i) {
            case 0:
                abstractMap = this.tablesAndViews;
                break;
            case 1:
                abstractMap = this.indexes;
                break;
            case 2:
            case 6:
            case 7:
            case 8:
            case 10:
            case 13:
            default:
                throw DbException.getInternalError("type=" + i);
            case 3:
                abstractMap = this.sequences;
                break;
            case 4:
                abstractMap = this.triggers;
                break;
            case 5:
                abstractMap = this.constraints;
                break;
            case 9:
            case 14:
                abstractMap = this.functionsAndAggregates;
                break;
            case 11:
                abstractMap = this.constants;
                break;
            case 12:
                abstractMap = this.domains;
                break;
            case 15:
                abstractMap = this.synonyms;
                break;
        }
        return abstractMap;
    }

    public void add(SchemaObject schemaObject) {
        if (schemaObject.getSchema() != this) {
            throw DbException.getInternalError("wrong schema");
        }
        String name = schemaObject.getName();
        if (getMap(schemaObject.getType()).putIfAbsent(name, schemaObject) != null) {
            throw DbException.getInternalError("object already exists: " + name);
        }
        freeUniqueName(name);
    }

    public void rename(SchemaObject schemaObject, String str) {
        Map<String, SchemaObject> map = getMap(schemaObject.getType());
        if (SysProperties.CHECK) {
            if (!map.containsKey(schemaObject.getName()) && !(schemaObject instanceof MetaTable)) {
                throw DbException.getInternalError("not found: " + schemaObject.getName());
            }
            if (schemaObject.getName().equals(str) || map.containsKey(str)) {
                throw DbException.getInternalError("object already exists: " + str);
            }
        }
        schemaObject.checkRename();
        map.remove(schemaObject.getName());
        freeUniqueName(schemaObject.getName());
        schemaObject.rename(str);
        map.put(str, schemaObject);
        freeUniqueName(str);
    }

    public Table findTableOrView(SessionLocal sessionLocal, String str) {
        Table table = this.tablesAndViews.get(str);
        if (table == null && sessionLocal != null) {
            table = sessionLocal.findLocalTempTable(str);
        }
        return table;
    }

    public Table resolveTableOrView(SessionLocal sessionLocal, String str) {
        return resolveTableOrView(sessionLocal, str, true);
    }

    public Table resolveTableOrView(SessionLocal sessionLocal, String str, boolean z) {
        TableSynonym tableSynonym;
        Table findTableOrView = findTableOrView(sessionLocal, str);
        if (findTableOrView == null && (tableSynonym = this.synonyms.get(str)) != null) {
            return tableSynonym.getSynonymFor();
        }
        if (z && (findTableOrView instanceof MaterializedView)) {
            return ((MaterializedView) findTableOrView).getUnderlyingTable();
        }
        return findTableOrView;
    }

    public TableSynonym getSynonym(String str) {
        return this.synonyms.get(str);
    }

    public Domain findDomain(String str) {
        return this.domains.get(str);
    }

    public Index findIndex(SessionLocal sessionLocal, String str) {
        Index index = this.indexes.get(str);
        if (index == null) {
            index = sessionLocal.findLocalTempTableIndex(str);
        }
        return index;
    }

    public TriggerObject findTrigger(String str) {
        return this.triggers.get(str);
    }

    public Sequence findSequence(String str) {
        return this.sequences.get(str);
    }

    public Constraint findConstraint(SessionLocal sessionLocal, String str) {
        Constraint constraint = this.constraints.get(str);
        if (constraint == null) {
            constraint = sessionLocal.findLocalTempTableConstraint(str);
        }
        return constraint;
    }

    public Constant findConstant(String str) {
        return this.constants.get(str);
    }

    public FunctionAlias findFunction(String str) {
        UserDefinedFunction findFunctionOrAggregate = findFunctionOrAggregate(str);
        if (findFunctionOrAggregate instanceof FunctionAlias) {
            return (FunctionAlias) findFunctionOrAggregate;
        }
        return null;
    }

    public UserAggregate findAggregate(String str) {
        UserDefinedFunction findFunctionOrAggregate = findFunctionOrAggregate(str);
        if (findFunctionOrAggregate instanceof UserAggregate) {
            return (UserAggregate) findFunctionOrAggregate;
        }
        return null;
    }

    public UserDefinedFunction findFunctionOrAggregate(String str) {
        return this.functionsAndAggregates.get(str);
    }

    public void reserveUniqueName(String str) {
        if (str != null) {
            synchronized (this.temporaryUniqueNames) {
                this.temporaryUniqueNames.add(str);
            }
        }
    }

    public void freeUniqueName(String str) {
        if (str != null) {
            synchronized (this.temporaryUniqueNames) {
                this.temporaryUniqueNames.remove(str);
            }
        }
    }

    private String getUniqueName(DbObject dbObject, Map<String, ? extends SchemaObject> map, String str) {
        StringBuilder sb = new StringBuilder(str);
        String hexString = Integer.toHexString(dbObject.getName().hashCode());
        synchronized (this.temporaryUniqueNames) {
            int length = hexString.length();
            for (int i = 0; i < length; i++) {
                char charAt = hexString.charAt(i);
                String sb2 = sb.append(charAt >= 'a' ? (char) (charAt - ' ') : charAt).toString();
                if (!map.containsKey(sb2) && this.temporaryUniqueNames.add(sb2)) {
                    return sb2;
                }
            }
            int length2 = sb.append('_').length();
            int i2 = 0;
            while (true) {
                String sb3 = sb.append(i2).toString();
                if (!map.containsKey(sb3) && this.temporaryUniqueNames.add(sb3)) {
                    return sb3;
                }
                sb.setLength(length2);
                i2++;
            }
        }
    }

    public String getUniqueConstraintName(SessionLocal sessionLocal, Table table) {
        Map<String, ? extends SchemaObject> map;
        if (table.isTemporary() && !table.isGlobalTemporary()) {
            map = sessionLocal.getLocalTempTableConstraints();
        } else {
            map = this.constraints;
        }
        return getUniqueName(table, map, "CONSTRAINT_");
    }

    public String getUniqueDomainConstraintName(SessionLocal sessionLocal, Domain domain) {
        return getUniqueName(domain, this.constraints, "CONSTRAINT_");
    }

    public String getUniqueIndexName(SessionLocal sessionLocal, Table table, String str) {
        Map<String, ? extends SchemaObject> map;
        if (table.isTemporary() && !table.isGlobalTemporary()) {
            map = sessionLocal.getLocalTempTableIndexes();
        } else {
            map = this.indexes;
        }
        return getUniqueName(table, map, str);
    }

    public Table getTableOrView(SessionLocal sessionLocal, String str) {
        Table table = this.tablesAndViews.get(str);
        if (table == null) {
            if (sessionLocal != null) {
                table = sessionLocal.findLocalTempTable(str);
            }
            if (table == null) {
                throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, str);
            }
        }
        return table;
    }

    public Domain getDomain(String str) {
        Domain domain = this.domains.get(str);
        if (domain == null) {
            throw DbException.get(90120, str);
        }
        return domain;
    }

    public Index getIndex(String str) {
        Index index = this.indexes.get(str);
        if (index == null) {
            throw DbException.get(ErrorCode.INDEX_NOT_FOUND_1, str);
        }
        return index;
    }

    public Constraint getConstraint(String str) {
        Constraint constraint = this.constraints.get(str);
        if (constraint == null) {
            throw DbException.get(ErrorCode.CONSTRAINT_NOT_FOUND_1, str);
        }
        return constraint;
    }

    public Constant getConstant(String str) {
        Constant constant = this.constants.get(str);
        if (constant == null) {
            throw DbException.get(ErrorCode.CONSTANT_NOT_FOUND_1, str);
        }
        return constant;
    }

    public Sequence getSequence(String str) {
        Sequence sequence = this.sequences.get(str);
        if (sequence == null) {
            throw DbException.get(ErrorCode.SEQUENCE_NOT_FOUND_1, str);
        }
        return sequence;
    }

    public ArrayList<SchemaObject> getAll(ArrayList<SchemaObject> arrayList) {
        if (arrayList == null) {
            arrayList = Utils.newSmallArrayList();
        }
        arrayList.addAll(this.tablesAndViews.values());
        arrayList.addAll(this.domains.values());
        arrayList.addAll(this.synonyms.values());
        arrayList.addAll(this.sequences.values());
        arrayList.addAll(this.indexes.values());
        arrayList.addAll(this.triggers.values());
        arrayList.addAll(this.constraints.values());
        arrayList.addAll(this.constants.values());
        arrayList.addAll(this.functionsAndAggregates.values());
        return arrayList;
    }

    public void getAll(int i, ArrayList<SchemaObject> arrayList) {
        arrayList.addAll(getMap(i).values());
    }

    public Collection<Domain> getAllDomains() {
        return this.domains.values();
    }

    public Collection<Constraint> getAllConstraints() {
        return this.constraints.values();
    }

    public Collection<Constant> getAllConstants() {
        return this.constants.values();
    }

    public Collection<Sequence> getAllSequences() {
        return this.sequences.values();
    }

    public Collection<TriggerObject> getAllTriggers() {
        return this.triggers.values();
    }

    public Collection<Table> getAllTablesAndViews(SessionLocal sessionLocal) {
        return this.tablesAndViews.values();
    }

    public Collection<Index> getAllIndexes() {
        return this.indexes.values();
    }

    public Collection<TableSynonym> getAllSynonyms() {
        return this.synonyms.values();
    }

    public Collection<UserDefinedFunction> getAllFunctionsAndAggregates() {
        return this.functionsAndAggregates.values();
    }

    public Table getTableOrViewByName(SessionLocal sessionLocal, String str) {
        return this.tablesAndViews.get(str);
    }

    public void remove(SchemaObject schemaObject) {
        String name = schemaObject.getName();
        if (getMap(schemaObject.getType()).remove(name) == null) {
            throw DbException.getInternalError("not found: " + name);
        }
        freeUniqueName(name);
    }

    public Table createTable(CreateTableData createTableData) {
        synchronized (this.database) {
            if (!createTableData.temporary || createTableData.globalTemporary) {
                this.database.lockMeta(createTableData.session);
            }
            createTableData.schema = this;
            String str = createTableData.tableEngine;
            if (str == null) {
                str = this.database.getSettings().defaultTableEngine;
                if (str == null) {
                    return this.database.getStore().createTable(createTableData);
                }
                createTableData.tableEngine = str;
            }
            if (createTableData.tableEngineParams == null) {
                createTableData.tableEngineParams = this.tableEngineParams;
            }
            return this.database.getTableEngine(str).createTable(createTableData);
        }
    }

    public TableSynonym createSynonym(CreateSynonymData createSynonymData) {
        TableSynonym tableSynonym;
        synchronized (this.database) {
            this.database.lockMeta(createSynonymData.session);
            createSynonymData.schema = this;
            tableSynonym = new TableSynonym(createSynonymData);
        }
        return tableSynonym;
    }

    public TableLink createTableLink(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, boolean z, boolean z2) {
        TableLink tableLink;
        synchronized (this.database) {
            tableLink = new TableLink(this, i, str, str2, str3, str4, str5, str6, str7, z, z2);
        }
        return tableLink;
    }
}
