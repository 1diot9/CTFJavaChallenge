package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.table.TableView;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterView.class */
public class AlterView extends DefineCommand {
    private boolean ifExists;
    private TableView view;

    public AlterView(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setView(TableView tableView) {
        this.view = tableView;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        if (this.view == null && this.ifExists) {
            return 0L;
        }
        this.session.getUser().checkSchemaOwner(this.view.getSchema());
        DbException recompile = this.view.recompile(this.session, false, true);
        if (recompile != null) {
            throw recompile;
        }
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 20;
    }
}
