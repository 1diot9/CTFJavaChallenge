package org.h2.command.dml;

import cn.hutool.core.text.StrPool;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import org.h2.api.ErrorCode;
import org.h2.constraint.Constraint;
import org.h2.engine.Comment;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.engine.Setting;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.schema.Constant;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.schema.UserDefinedFunction;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/ScriptCommand.class */
public class ScriptCommand extends ScriptBase {
    private static final Comparator<? super DbObject> BY_NAME_COMPARATOR = (dbObject, dbObject2) -> {
        int compareTo;
        if ((dbObject instanceof SchemaObject) && (dbObject2 instanceof SchemaObject) && (compareTo = ((SchemaObject) dbObject).getSchema().getName().compareTo(((SchemaObject) dbObject2).getSchema().getName())) != 0) {
            return compareTo;
        }
        return dbObject.getName().compareTo(dbObject2.getName());
    };
    private Charset charset;
    private java.util.Set<String> schemaNames;
    private Collection<Table> tables;
    private boolean passwords;
    private boolean data;
    private boolean settings;
    private boolean drop;
    private boolean simple;
    private boolean withColumns;
    private boolean version;
    private LocalResult result;
    private String lineSeparatorString;
    private byte[] lineSeparator;
    private byte[] buffer;
    private boolean tempLobTableCreated;
    private int nextLobId;
    private int lobBlockSize;

    @Override // org.h2.command.dml.ScriptBase
    public /* bridge */ /* synthetic */ void setCompressionAlgorithm(String str) {
        super.setCompressionAlgorithm(str);
    }

    @Override // org.h2.command.dml.ScriptBase, org.h2.command.Prepared
    public /* bridge */ /* synthetic */ boolean needRecompile() {
        return super.needRecompile();
    }

    @Override // org.h2.command.dml.ScriptBase, org.h2.command.Prepared
    public /* bridge */ /* synthetic */ boolean isTransactional() {
        return super.isTransactional();
    }

    @Override // org.h2.command.dml.ScriptBase
    public /* bridge */ /* synthetic */ void setFileNameExpr(Expression expression) {
        super.setFileNameExpr(expression);
    }

    @Override // org.h2.command.dml.ScriptBase
    public /* bridge */ /* synthetic */ void setPassword(Expression expression) {
        super.setPassword(expression);
    }

    @Override // org.h2.command.dml.ScriptBase
    public /* bridge */ /* synthetic */ void setCipher(String str) {
        super.setCipher(str);
    }

    public ScriptCommand(SessionLocal sessionLocal) {
        super(sessionLocal);
        this.charset = StandardCharsets.UTF_8;
        this.version = true;
        this.lobBlockSize = 4096;
    }

    @Override // org.h2.command.Prepared
    public boolean isQuery() {
        return true;
    }

    public void setSchemaNames(java.util.Set<String> set) {
        this.schemaNames = set;
    }

    public void setTables(Collection<Table> collection) {
        this.tables = collection;
    }

    public void setData(boolean z) {
        this.data = z;
    }

    public void setPasswords(boolean z) {
        this.passwords = z;
    }

    public void setSettings(boolean z) {
        this.settings = z;
    }

    public void setLobBlockSize(long j) {
        this.lobBlockSize = MathUtils.convertLongToInt(j);
    }

    public void setDrop(boolean z) {
        this.drop = z;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        LocalResult createResult = createResult();
        createResult.done();
        return createResult;
    }

    private LocalResult createResult() {
        return new LocalResult(this.session, new Expression[]{new ExpressionColumn(getDatabase(), new Column("SCRIPT", TypeInfo.TYPE_VARCHAR))}, 1, 1);
    }

    @Override // org.h2.command.Prepared
    public ResultInterface query(long j) {
        Constraint.Type constraintType;
        this.session.getUser().checkAdmin();
        reset();
        Database database = getDatabase();
        if (this.schemaNames != null) {
            for (String str : this.schemaNames) {
                if (database.findSchema(str) == null) {
                    throw DbException.get(ErrorCode.SCHEMA_NOT_FOUND_1, str);
                }
            }
        }
        try {
            try {
                this.result = createResult();
                deleteStore();
                openOutput();
                if (this.out != null) {
                    this.buffer = new byte[4096];
                }
                if (this.version) {
                    add("-- H2 " + Constants.VERSION, true);
                }
                if (this.settings) {
                    for (Setting setting : database.getAllSettings()) {
                        if (!setting.getName().equals(SetTypes.getTypeName(28))) {
                            add(setting.getCreateSQL(), false);
                        }
                    }
                }
                if (this.out != null) {
                    add("", true);
                }
                RightOwner[] rightOwnerArr = (RightOwner[]) database.getAllUsersAndRoles().toArray(new RightOwner[0]);
                Arrays.sort(rightOwnerArr, (rightOwner, rightOwner2) -> {
                    boolean isAdmin;
                    boolean z = rightOwner instanceof User;
                    if (z != (rightOwner2 instanceof User)) {
                        return z ? -1 : 1;
                    }
                    if (!z || (isAdmin = ((User) rightOwner).isAdmin()) == ((User) rightOwner2).isAdmin()) {
                        return rightOwner.getName().compareTo(rightOwner2.getName());
                    }
                    return isAdmin ? -1 : 1;
                });
                for (RightOwner rightOwner3 : rightOwnerArr) {
                    if (rightOwner3 instanceof User) {
                        add(((User) rightOwner3).getCreateSQL(this.passwords), false);
                    } else {
                        add(((Role) rightOwner3).getCreateSQL(true), false);
                    }
                }
                ArrayList<Schema> arrayList = new ArrayList<>();
                for (Schema schema : database.getAllSchemas()) {
                    if (!excludeSchema(schema)) {
                        arrayList.add(schema);
                        add(schema.getCreateSQL(), false);
                    }
                }
                dumpDomains(arrayList);
                Iterator<Schema> it = arrayList.iterator();
                while (it.hasNext()) {
                    for (Constant constant : (Constant[]) sorted(it.next().getAllConstants(), Constant.class)) {
                        add(constant.getCreateSQL(), false);
                    }
                }
                ArrayList<Table> allTablesAndViews = database.getAllTablesAndViews();
                allTablesAndViews.sort(Comparator.comparingInt((v0) -> {
                    return v0.getId();
                }));
                Iterator<Table> it2 = allTablesAndViews.iterator();
                while (it2.hasNext()) {
                    Table next = it2.next();
                    if (!excludeSchema(next.getSchema()) && !excludeTable(next) && !next.isHidden()) {
                        next.lock(this.session, 0);
                        if (next.getCreateSQL() != null) {
                            if (this.drop) {
                                add(next.getDropSQL(), false);
                            }
                        }
                    }
                }
                Iterator<Schema> it3 = arrayList.iterator();
                while (it3.hasNext()) {
                    for (UserDefinedFunction userDefinedFunction : (UserDefinedFunction[]) sorted(it3.next().getAllFunctionsAndAggregates(), UserDefinedFunction.class)) {
                        if (this.drop) {
                            add(userDefinedFunction.getDropSQL(), false);
                        }
                        add(userDefinedFunction.getCreateSQL(), false);
                    }
                }
                Iterator<Schema> it4 = arrayList.iterator();
                while (it4.hasNext()) {
                    for (Sequence sequence : (Sequence[]) sorted(it4.next().getAllSequences(), Sequence.class)) {
                        if (!sequence.getBelongsToTable()) {
                            if (this.drop) {
                                add(sequence.getDropSQL(), false);
                            }
                            add(sequence.getCreateSQL(), false);
                        }
                    }
                }
                int i = 0;
                Iterator<Table> it5 = allTablesAndViews.iterator();
                while (it5.hasNext()) {
                    Table next2 = it5.next();
                    if (!excludeSchema(next2.getSchema()) && !excludeTable(next2) && !next2.isHidden()) {
                        next2.lock(this.session, 0);
                        String createSQL = next2.getCreateSQL();
                        if (createSQL != null) {
                            TableType tableType = next2.getTableType();
                            add(createSQL, false);
                            ArrayList<Constraint> constraints = next2.getConstraints();
                            if (constraints != null) {
                                Iterator<Constraint> it6 = constraints.iterator();
                                while (it6.hasNext()) {
                                    Constraint next3 = it6.next();
                                    if (Constraint.Type.PRIMARY_KEY == next3.getConstraintType()) {
                                        add(next3.getCreateSQLWithoutIndexes(), false);
                                    }
                                }
                            }
                            if (TableType.TABLE == tableType) {
                                if (next2.canGetRowCount(this.session)) {
                                    StringBuilder append = new StringBuilder("-- ").append(next2.getRowCountApproximation(this.session)).append(" +/- SELECT COUNT(*) FROM ");
                                    next2.getSQL(append, 3);
                                    add(append.toString(), false);
                                }
                                if (this.data) {
                                    i = generateInsertValues(i, next2);
                                }
                            }
                            ArrayList<Index> indexes = next2.getIndexes();
                            for (int i2 = 0; indexes != null && i2 < indexes.size(); i2++) {
                                Index index = indexes.get(i2);
                                if (!index.getIndexType().getBelongsToConstraint()) {
                                    add(index.getCreateSQL(), false);
                                }
                            }
                        }
                    }
                }
                if (this.tempLobTableCreated) {
                    add("DROP TABLE IF EXISTS SYSTEM_LOB_STREAM", true);
                    add("DROP ALIAS IF EXISTS SYSTEM_COMBINE_CLOB", true);
                    add("DROP ALIAS IF EXISTS SYSTEM_COMBINE_BLOB", true);
                    this.tempLobTableCreated = false;
                }
                ArrayList arrayList2 = new ArrayList();
                Iterator<Schema> it7 = arrayList.iterator();
                while (it7.hasNext()) {
                    for (Constraint constraint : it7.next().getAllConstraints()) {
                        if (!excludeTable(constraint.getTable()) && ((constraintType = constraint.getConstraintType()) == Constraint.Type.DOMAIN || !constraint.getTable().isHidden())) {
                            if (constraintType != Constraint.Type.PRIMARY_KEY) {
                                arrayList2.add(constraint);
                            }
                        }
                    }
                }
                arrayList2.sort(null);
                Iterator it8 = arrayList2.iterator();
                while (it8.hasNext()) {
                    add(((Constraint) it8.next()).getCreateSQLWithoutIndexes(), false);
                }
                Iterator<Schema> it9 = arrayList.iterator();
                while (it9.hasNext()) {
                    for (TriggerObject triggerObject : it9.next().getAllTriggers()) {
                        if (!excludeTable(triggerObject.getTable())) {
                            add(triggerObject.getCreateSQL(), false);
                        }
                    }
                }
                dumpRights(database);
                Iterator<Comment> it10 = database.getAllComments().iterator();
                while (it10.hasNext()) {
                    add(it10.next().getCreateSQL(), false);
                }
                if (this.out != null) {
                    this.out.close();
                }
                this.result.done();
                LocalResult localResult = this.result;
                reset();
                return localResult;
            } catch (IOException e) {
                throw DbException.convertIOException(e, getFileName());
            }
        } finally {
            closeIO();
        }
    }

    private void dumpDomains(ArrayList<Schema> arrayList) throws IOException {
        TreeMap treeMap = new TreeMap(BY_NAME_COMPARATOR);
        TreeSet treeSet = new TreeSet(BY_NAME_COMPARATOR);
        Iterator<Schema> it = arrayList.iterator();
        while (it.hasNext()) {
            for (Domain domain : (Domain[]) sorted(it.next().getAllDomains(), Domain.class)) {
                Domain domain2 = domain.getDomain();
                if (domain2 == null) {
                    addDomain(domain);
                } else {
                    TreeSet treeSet2 = (TreeSet) treeMap.get(domain2);
                    if (treeSet2 == null) {
                        treeSet2 = new TreeSet(BY_NAME_COMPARATOR);
                        treeMap.put(domain2, treeSet2);
                    }
                    treeSet2.add(domain);
                    if (domain2.getDomain() == null || !arrayList.contains(domain2.getSchema())) {
                        treeSet.add(domain2);
                    }
                }
            }
        }
        while (!treeMap.isEmpty()) {
            TreeSet treeSet3 = new TreeSet(BY_NAME_COMPARATOR);
            Iterator it2 = treeSet.iterator();
            while (it2.hasNext()) {
                TreeSet treeSet4 = (TreeSet) treeMap.remove((Domain) it2.next());
                if (treeSet4 != null) {
                    Iterator it3 = treeSet4.iterator();
                    while (it3.hasNext()) {
                        Domain domain3 = (Domain) it3.next();
                        addDomain(domain3);
                        treeSet3.add(domain3);
                    }
                }
            }
            treeSet = treeSet3;
        }
    }

    private void dumpRights(Database database) throws IOException {
        Right[] rightArr = (Right[]) database.getAllRights().toArray(new Right[0]);
        Arrays.sort(rightArr, (right, right2) -> {
            Role grantedRole = right.getGrantedRole();
            Role grantedRole2 = right2.getGrantedRole();
            if ((grantedRole == null) != (grantedRole2 == null)) {
                return grantedRole == null ? -1 : 1;
            }
            if (grantedRole == null) {
                DbObject grantedObject = right.getGrantedObject();
                DbObject grantedObject2 = right2.getGrantedObject();
                if ((grantedObject == null) != (grantedObject2 == null)) {
                    return grantedObject == null ? -1 : 1;
                }
                if (grantedObject != null) {
                    if ((grantedObject instanceof Schema) != (grantedObject2 instanceof Schema)) {
                        return grantedObject instanceof Schema ? -1 : 1;
                    }
                    int compareTo = grantedObject.getName().compareTo(grantedObject2.getName());
                    if (compareTo != 0) {
                        return compareTo;
                    }
                }
            } else {
                int compareTo2 = grantedRole.getName().compareTo(grantedRole2.getName());
                if (compareTo2 != 0) {
                    return compareTo2;
                }
            }
            return right.getGrantee().getName().compareTo(right2.getGrantee().getName());
        });
        for (Right right3 : rightArr) {
            DbObject grantedObject = right3.getGrantedObject();
            if (grantedObject != null) {
                if (grantedObject instanceof Schema) {
                    if (excludeSchema((Schema) grantedObject)) {
                    }
                } else if (grantedObject instanceof Table) {
                    Table table = (Table) grantedObject;
                    if (!excludeSchema(table.getSchema())) {
                        if (excludeTable(table)) {
                        }
                    }
                }
            }
            add(right3.getCreateSQL(), false);
        }
    }

    private void addDomain(Domain domain) throws IOException {
        if (this.drop) {
            add(domain.getDropSQL(), false);
        }
        add(domain.getCreateSQL(), false);
    }

    private static <T extends DbObject> T[] sorted(Collection<T> collection, Class<T> cls) {
        T[] tArr = (T[]) ((DbObject[]) collection.toArray((DbObject[]) Array.newInstance((Class<?>) cls, 0)));
        Arrays.sort(tArr, BY_NAME_COMPARATOR);
        return tArr;
    }

    private int generateInsertValues(int i, Table table) throws IOException {
        Cursor find = table.getBestPlanItem(this.session, null, null, -1, null, null).getIndex().find(this.session, null, null);
        Column[] columns = table.getColumns();
        boolean z = false;
        boolean z2 = false;
        for (Column column : columns) {
            if (column.isGeneratedAlways()) {
                if (column.isIdentity()) {
                    z2 = true;
                } else {
                    z = true;
                }
            }
        }
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        table.getSQL(sb, 0);
        if (z || z2 || this.withColumns) {
            sb.append('(');
            boolean z3 = false;
            for (Column column2 : columns) {
                if (!column2.isGenerated()) {
                    if (z3) {
                        sb.append(", ");
                    }
                    z3 = true;
                    column2.getSQL(sb, 0);
                }
            }
            sb.append(')');
            if (z2) {
                sb.append(" OVERRIDING SYSTEM VALUE");
            }
        }
        sb.append(" VALUES");
        if (!this.simple) {
            sb.append('\n');
        }
        sb.append('(');
        String sb2 = sb.toString();
        StringBuilder sb3 = null;
        int length = columns.length;
        while (find.next()) {
            Row row = find.get();
            if (sb3 == null) {
                sb3 = new StringBuilder(sb2);
            } else {
                sb3.append(",\n(");
            }
            boolean z4 = false;
            for (int i2 = 0; i2 < length; i2++) {
                if (!columns[i2].isGenerated()) {
                    if (z4) {
                        sb3.append(", ");
                    }
                    z4 = true;
                    Value value = row.getValue(i2);
                    if (value.getType().getPrecision() > this.lobBlockSize) {
                        if (value.getValueType() == 3) {
                            sb3.append("SYSTEM_COMBINE_CLOB(").append(writeLobStream(value)).append(')');
                        } else if (value.getValueType() == 7) {
                            sb3.append("SYSTEM_COMBINE_BLOB(").append(writeLobStream(value)).append(')');
                        } else {
                            value.getSQL(sb3, 4);
                        }
                    } else {
                        value.getSQL(sb3, 4);
                    }
                }
            }
            sb3.append(')');
            i++;
            if ((i & 127) == 0) {
                checkCanceled();
            }
            if (this.simple || sb3.length() > 4096) {
                add(sb3.toString(), true);
                sb3 = null;
            }
        }
        if (sb3 != null) {
            add(sb3.toString(), true);
        }
        return i;
    }

    private int writeLobStream(Value value) throws IOException {
        if (!this.tempLobTableCreated) {
            add("CREATE CACHED LOCAL TEMPORARY TABLE IF NOT EXISTS SYSTEM_LOB_STREAM(ID INT NOT NULL, PART INT NOT NULL, CDATA VARCHAR, BDATA VARBINARY)", true);
            add("ALTER TABLE SYSTEM_LOB_STREAM ADD CONSTRAINT SYSTEM_LOB_STREAM_PRIMARY_KEY PRIMARY KEY(ID, PART)", true);
            String name = getClass().getName();
            add("CREATE ALIAS IF NOT EXISTS SYSTEM_COMBINE_CLOB FOR '" + name + ".combineClob'", true);
            add("CREATE ALIAS IF NOT EXISTS SYSTEM_COMBINE_BLOB FOR '" + name + ".combineBlob'", true);
            this.tempLobTableCreated = true;
        }
        int i = this.nextLobId;
        this.nextLobId = i + 1;
        switch (value.getValueType()) {
            case 3:
                char[] cArr = new char[this.lobBlockSize];
                Reader reader = value.getReader();
                Throwable th = null;
                int i2 = 0;
                while (true) {
                    try {
                        try {
                            StringBuilder sb = new StringBuilder(this.lobBlockSize * 2);
                            sb.append("INSERT INTO SYSTEM_LOB_STREAM VALUES(").append(i).append(", ").append(i2).append(", ");
                            int readFully = IOUtils.readFully(reader, cArr, this.lobBlockSize);
                            if (readFully != 0) {
                                StringUtils.quoteStringSQL(sb, new String(cArr, 0, readFully)).append(", NULL)");
                                add(sb.toString(), true);
                                i2++;
                            } else if (reader != null) {
                                if (0 == 0) {
                                    reader.close();
                                    break;
                                } else {
                                    try {
                                        reader.close();
                                        break;
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                        break;
                                    }
                                }
                            }
                        } finally {
                        }
                    } catch (Throwable th3) {
                        if (reader != null) {
                            if (th != null) {
                                try {
                                    reader.close();
                                } catch (Throwable th4) {
                                    th.addSuppressed(th4);
                                }
                            } else {
                                reader.close();
                            }
                        }
                        throw th3;
                    }
                }
                break;
            case 7:
                byte[] bArr = new byte[this.lobBlockSize];
                InputStream inputStream = value.getInputStream();
                Throwable th5 = null;
                int i3 = 0;
                while (true) {
                    try {
                        try {
                            StringBuilder sb2 = new StringBuilder(this.lobBlockSize * 2);
                            sb2.append("INSERT INTO SYSTEM_LOB_STREAM VALUES(").append(i).append(", ").append(i3).append(", NULL, X'");
                            int readFully2 = IOUtils.readFully(inputStream, bArr, this.lobBlockSize);
                            if (readFully2 > 0) {
                                StringUtils.convertBytesToHex(sb2, bArr, readFully2).append("')");
                                add(sb2.toString(), true);
                                i3++;
                            } else if (inputStream != null) {
                                if (0 == 0) {
                                    inputStream.close();
                                    break;
                                } else {
                                    try {
                                        inputStream.close();
                                        break;
                                    } catch (Throwable th6) {
                                        th5.addSuppressed(th6);
                                        break;
                                    }
                                }
                            }
                        } catch (Throwable th7) {
                            if (inputStream != null) {
                                if (th5 != null) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable th8) {
                                        th5.addSuppressed(th8);
                                    }
                                } else {
                                    inputStream.close();
                                }
                            }
                            throw th7;
                        }
                    } finally {
                    }
                }
                break;
            default:
                throw DbException.getInternalError("type:" + value.getValueType());
        }
        return i;
    }

    public static InputStream combineBlob(Connection connection, int i) throws SQLException {
        if (i < 0) {
            return null;
        }
        final ResultSet lobStream = getLobStream(connection, "BDATA", i);
        return new InputStream() { // from class: org.h2.command.dml.ScriptCommand.1
            private InputStream current;
            private boolean closed;

            @Override // java.io.InputStream
            public int read() throws IOException {
                while (true) {
                    try {
                        if (this.current == null) {
                            if (this.closed) {
                                return -1;
                            }
                            if (!lobStream.next()) {
                                close();
                                return -1;
                            }
                            this.current = lobStream.getBinaryStream(1);
                            this.current = new BufferedInputStream(this.current);
                        }
                        int read = this.current.read();
                        if (read >= 0) {
                            return read;
                        }
                        this.current = null;
                    } catch (SQLException e) {
                        throw DataUtils.convertToIOException(e);
                    }
                }
            }

            @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                if (this.closed) {
                    return;
                }
                this.closed = true;
                try {
                    lobStream.close();
                } catch (SQLException e) {
                    throw DataUtils.convertToIOException(e);
                }
            }
        };
    }

    public static Reader combineClob(Connection connection, int i) throws SQLException {
        if (i < 0) {
            return null;
        }
        final ResultSet lobStream = getLobStream(connection, "CDATA", i);
        return new Reader() { // from class: org.h2.command.dml.ScriptCommand.2
            private Reader current;
            private boolean closed;

            @Override // java.io.Reader
            public int read() throws IOException {
                while (true) {
                    try {
                        if (this.current == null) {
                            if (this.closed) {
                                return -1;
                            }
                            if (!lobStream.next()) {
                                close();
                                return -1;
                            }
                            this.current = lobStream.getCharacterStream(1);
                            this.current = new BufferedReader(this.current);
                        }
                        int read = this.current.read();
                        if (read >= 0) {
                            return read;
                        }
                        this.current = null;
                    } catch (SQLException e) {
                        throw DataUtils.convertToIOException(e);
                    }
                }
            }

            @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                if (this.closed) {
                    return;
                }
                this.closed = true;
                try {
                    lobStream.close();
                } catch (SQLException e) {
                    throw DataUtils.convertToIOException(e);
                }
            }

            @Override // java.io.Reader
            public int read(char[] cArr, int i2, int i3) throws IOException {
                int read;
                if (i3 == 0) {
                    return 0;
                }
                int read2 = read();
                if (read2 == -1) {
                    return -1;
                }
                cArr[i2] = (char) read2;
                int i4 = 1;
                while (i4 < i3 && (read = read()) != -1) {
                    cArr[i2 + i4] = (char) read;
                    i4++;
                }
                return i4;
            }
        };
    }

    private static ResultSet getLobStream(Connection connection, String str, int i) throws SQLException {
        PreparedStatement prepareStatement = connection.prepareStatement("SELECT " + str + " FROM SYSTEM_LOB_STREAM WHERE ID=? ORDER BY PART");
        prepareStatement.setInt(1, i);
        return prepareStatement.executeQuery();
    }

    private void reset() {
        this.result = null;
        this.buffer = null;
        this.lineSeparatorString = System.lineSeparator();
        this.lineSeparator = this.lineSeparatorString.getBytes(this.charset);
    }

    private boolean excludeSchema(Schema schema) {
        if (this.schemaNames != null && !this.schemaNames.contains(schema.getName())) {
            return true;
        }
        if (this.tables != null) {
            Iterator<Table> it = schema.getAllTablesAndViews(this.session).iterator();
            while (it.hasNext()) {
                if (this.tables.contains(it.next())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean excludeTable(Table table) {
        return (this.tables == null || this.tables.contains(table)) ? false : true;
    }

    private void add(String str, boolean z) throws IOException {
        if (str == null) {
            return;
        }
        if (this.lineSeparator.length > 1 || this.lineSeparator[0] != 10) {
            str = StringUtils.replaceAll(str, StrPool.LF, this.lineSeparatorString);
        }
        String str2 = str + ";";
        if (this.out != null) {
            byte[] bytes = str2.getBytes(this.charset);
            int roundUpInt = MathUtils.roundUpInt(bytes.length + this.lineSeparator.length, 16);
            this.buffer = Utils.copy(bytes, this.buffer);
            if (roundUpInt > this.buffer.length) {
                this.buffer = new byte[roundUpInt];
            }
            System.arraycopy(bytes, 0, this.buffer, 0, bytes.length);
            for (int length = bytes.length; length < roundUpInt - this.lineSeparator.length; length++) {
                this.buffer[length] = 32;
            }
            int i = 0;
            int length2 = roundUpInt - this.lineSeparator.length;
            while (length2 < roundUpInt) {
                this.buffer[length2] = this.lineSeparator[i];
                length2++;
                i++;
            }
            this.out.write(this.buffer, 0, roundUpInt);
            if (!z) {
                this.result.addRow(ValueVarchar.get(str2));
                return;
            }
            return;
        }
        this.result.addRow(ValueVarchar.get(str2));
    }

    public void setSimple(boolean z) {
        this.simple = z;
    }

    public void setWithColumns(boolean z) {
        this.withColumns = z;
    }

    public void setVersion(boolean z) {
        this.version = z;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 65;
    }
}
