package org.h2.command.dml;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.mvstore.db.Store;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.value.TypeInfo;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/Explain.class */
public class Explain extends Prepared {
    private Prepared command;
    private LocalResult result;
    private boolean executeCommand;

    public Explain(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setCommand(Prepared prepared) {
        this.command = prepared;
    }

    public Prepared getCommand() {
        return this.command;
    }

    @Override // org.h2.command.Prepared
    public void prepare() {
        this.command.prepare();
    }

    public void setExecuteCommand(boolean z) {
        this.executeCommand = z;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return query(-1L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.command.Prepared
    public void checkParameters() {
        if (this.executeCommand) {
            super.checkParameters();
        }
    }

    @Override // org.h2.command.Prepared
    public ResultInterface query(long j) {
        String planSQL;
        Database database = getDatabase();
        this.result = new LocalResult(this.session, new Expression[]{new ExpressionColumn(database, new Column("PLAN", TypeInfo.TYPE_VARCHAR))}, 1, 1);
        if (j >= 0) {
            if (!this.executeCommand) {
                planSQL = this.command.getPlanSQL(8);
            } else {
                Store store = null;
                if (database.isPersistent()) {
                    store = database.getStore();
                    store.statisticsStart();
                }
                if (this.command.isQuery()) {
                    this.command.query(j);
                } else {
                    this.command.update();
                }
                planSQL = this.command.getPlanSQL(8);
                Map<String, Integer> map = null;
                if (store != null) {
                    map = store.statisticsEnd();
                }
                if (map != null) {
                    int i = 0;
                    Iterator<Integer> it = map.values().iterator();
                    while (it.hasNext()) {
                        i += it.next().intValue();
                    }
                    if (i > 0) {
                        TreeMap treeMap = new TreeMap(map);
                        StringBuilder sb = new StringBuilder();
                        if (treeMap.size() > 1) {
                            sb.append("total: ").append(i).append('\n');
                        }
                        for (Map.Entry entry : treeMap.entrySet()) {
                            int intValue = ((Integer) entry.getValue()).intValue();
                            int i2 = (int) ((100 * intValue) / i);
                            sb.append((String) entry.getKey()).append(": ").append(intValue);
                            if (treeMap.size() > 1) {
                                sb.append(" (").append(i2).append("%)");
                            }
                            sb.append('\n');
                        }
                        planSQL = planSQL + "\n/*\n" + ((Object) sb) + "*/";
                    }
                }
            }
            add(planSQL);
        }
        this.result.done();
        return this.result;
    }

    private void add(String str) {
        this.result.addRow(ValueVarchar.get(str));
    }

    @Override // org.h2.command.Prepared
    public boolean isQuery() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean isReadOnly() {
        return this.command.isReadOnly();
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return this.executeCommand ? 86 : 60;
    }

    @Override // org.h2.command.Prepared
    public void collectDependencies(HashSet<DbObject> hashSet) {
        this.command.collectDependencies(hashSet);
    }
}
