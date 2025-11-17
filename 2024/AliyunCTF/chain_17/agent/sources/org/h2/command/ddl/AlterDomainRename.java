package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterDomainRename.class */
public class AlterDomainRename extends AlterDomain {
    private String newDomainName;

    public AlterDomainRename(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setNewDomainName(String str) {
        this.newDomainName = str;
    }

    @Override // org.h2.command.ddl.AlterDomain
    long update(Schema schema, Domain domain) {
        Domain findDomain = schema.findDomain(this.newDomainName);
        if (findDomain != null) {
            if (domain != findDomain) {
                throw DbException.get(90119, this.newDomainName);
            }
            if (this.newDomainName.equals(domain.getName())) {
                return 0L;
            }
        }
        getDatabase().renameSchemaObject(this.session, domain, this.newDomainName);
        forAllDependencies(this.session, domain, null, null, false);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 96;
    }
}
