package org.h2.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Parameter;
import org.h2.expression.ParameterInterface;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/CommandList.class */
public class CommandList extends Command {
    private CommandContainer command;
    private final ArrayList<Prepared> commands;
    private final ArrayList<Parameter> parameters;
    private String remaining;
    private Command remainingCommand;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommandList(SessionLocal sessionLocal, String str, CommandContainer commandContainer, ArrayList<Prepared> arrayList, ArrayList<Parameter> arrayList2, String str2) {
        super(sessionLocal, str);
        this.command = commandContainer;
        this.commands = arrayList;
        this.parameters = arrayList2;
        this.remaining = str2;
    }

    @Override // org.h2.command.Command, org.h2.command.CommandInterface
    public ArrayList<? extends ParameterInterface> getParameters() {
        return this.parameters;
    }

    private void executeRemaining() {
        Iterator<Prepared> it = this.commands.iterator();
        while (it.hasNext()) {
            Prepared next = it.next();
            next.prepare();
            if (next.isQuery()) {
                next.query(0L);
            } else {
                next.update();
            }
        }
        if (this.remaining != null) {
            this.remainingCommand = this.session.prepareLocal(this.remaining);
            this.remaining = null;
            if (this.remainingCommand.isQuery()) {
                this.remainingCommand.query(0L);
            } else {
                this.remainingCommand.update(null);
            }
        }
    }

    @Override // org.h2.command.Command
    public ResultWithGeneratedKeys update(Object obj) {
        ResultWithGeneratedKeys executeUpdate = this.command.executeUpdate(null);
        executeRemaining();
        return executeUpdate;
    }

    @Override // org.h2.command.Command
    public ResultInterface query(long j) {
        ResultInterface query = this.command.query(j);
        executeRemaining();
        return query;
    }

    @Override // org.h2.command.Command, org.h2.command.CommandInterface
    public void stop() {
        this.command.stop();
        Iterator<Prepared> it = this.commands.iterator();
        while (it.hasNext()) {
            CommandContainer.clearCTE(this.session, it.next());
        }
        if (this.remainingCommand != null) {
            this.remainingCommand.stop();
        }
    }

    @Override // org.h2.command.Command, org.h2.command.CommandInterface
    public boolean isQuery() {
        return this.command.isQuery();
    }

    @Override // org.h2.command.Command
    public boolean isTransactional() {
        return true;
    }

    @Override // org.h2.command.Command
    public boolean isReadOnly() {
        return false;
    }

    @Override // org.h2.command.Command
    public ResultInterface queryMeta() {
        return this.command.queryMeta();
    }

    @Override // org.h2.command.CommandInterface
    public int getCommandType() {
        return this.command.getCommandType();
    }

    @Override // org.h2.command.Command
    public Set<DbObject> getDependencies() {
        HashSet<DbObject> hashSet = new HashSet<>();
        Iterator<Prepared> it = this.commands.iterator();
        while (it.hasNext()) {
            it.next().collectDependencies(hashSet);
        }
        return hashSet;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.command.Command
    public boolean isRetryable() {
        if (!this.command.isRetryable()) {
            return false;
        }
        Iterator<Prepared> it = this.commands.iterator();
        while (it.hasNext()) {
            if (!it.next().isRetryable()) {
                return false;
            }
        }
        return this.remainingCommand == null || this.remainingCommand.isRetryable();
    }
}
