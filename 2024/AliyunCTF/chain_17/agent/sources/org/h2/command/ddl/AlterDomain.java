package org.h2.command.ddl;

import java.util.Iterator;
import java.util.function.BiPredicate;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterDomain.class */
public abstract class AlterDomain extends SchemaOwnerCommand {
    String domainName;
    boolean ifDomainExists;

    abstract long update(Schema schema, Domain domain);

    public static void forAllDependencies(SessionLocal sessionLocal, Domain domain, BiPredicate<Domain, Column> biPredicate, BiPredicate<Domain, Domain> biPredicate2, boolean z) {
        Database database = sessionLocal.getDatabase();
        for (Schema schema : database.getAllSchemasNoMeta()) {
            for (Domain domain2 : schema.getAllDomains()) {
                if (domain2.getDomain() == domain && (biPredicate2 == null || biPredicate2.test(domain, domain2))) {
                    if (z) {
                        domain.prepareExpressions(sessionLocal);
                    }
                    database.updateMeta(sessionLocal, domain2);
                }
            }
            for (Table table : schema.getAllTablesAndViews(null)) {
                if (forTable(sessionLocal, domain, biPredicate, z, table)) {
                    database.updateMeta(sessionLocal, table);
                }
            }
        }
        Iterator<Table> it = sessionLocal.getLocalTempTables().iterator();
        while (it.hasNext()) {
            forTable(sessionLocal, domain, biPredicate, z, it.next());
        }
    }

    private static boolean forTable(SessionLocal sessionLocal, Domain domain, BiPredicate<Domain, Column> biPredicate, boolean z, Table table) {
        boolean z2 = false;
        for (Column column : table.getColumns()) {
            if (column.getDomain() == domain) {
                if (biPredicate == null || biPredicate.test(domain, column)) {
                    if (z) {
                        column.prepareExpressions(sessionLocal);
                    }
                    z2 = true;
                }
            }
        }
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterDomain(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public final void setDomainName(String str) {
        this.domainName = str;
    }

    public final void setIfDomainExists(boolean z) {
        this.ifDomainExists = z;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    final long update(Schema schema) {
        Domain findDomain = getSchema().findDomain(this.domainName);
        if (findDomain == null) {
            if (this.ifDomainExists) {
                return 0L;
            }
            throw DbException.get(90120, this.domainName);
        }
        return update(schema, findDomain);
    }
}
