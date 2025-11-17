package org.h2.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.coyote.http11.Constants;
import org.h2.engine.SessionRemote;
import org.h2.engine.SysProperties;
import org.h2.expression.ParameterInterface;
import org.h2.expression.ParameterRemote;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.result.ResultInterface;
import org.h2.result.ResultRemote;
import org.h2.util.Utils;
import org.h2.value.Transfer;
import org.h2.value.Value;
import org.h2.value.ValueLob;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/CommandRemote.class */
public class CommandRemote implements CommandInterface {
    private final ArrayList<Transfer> transferList;
    private final Trace trace;
    private final String sql;
    private final int fetchSize;
    private SessionRemote session;
    private int id;
    private boolean isQuery;
    private boolean readonly;
    private final int created;
    private int cmdType = 0;
    private final ArrayList<ParameterInterface> parameters = Utils.newSmallArrayList();

    public CommandRemote(SessionRemote sessionRemote, ArrayList<Transfer> arrayList, String str, int i) {
        this.transferList = arrayList;
        this.trace = sessionRemote.getTrace();
        this.sql = str;
        prepare(sessionRemote, true);
        this.session = sessionRemote;
        this.fetchSize = i;
        this.created = sessionRemote.getLastReconnect();
    }

    @Override // org.h2.command.CommandInterface
    public void stop() {
    }

    private void prepare(SessionRemote sessionRemote, boolean z) {
        this.id = sessionRemote.getNextId();
        int i = 0;
        int i2 = 0;
        while (i < this.transferList.size()) {
            try {
                Transfer transfer = this.transferList.get(i);
                if (z) {
                    sessionRemote.traceOperation("SESSION_PREPARE_READ_PARAMS2", this.id);
                    transfer.writeInt(18).writeInt(this.id).writeString(this.sql);
                } else {
                    sessionRemote.traceOperation("SESSION_PREPARE", this.id);
                    transfer.writeInt(0).writeInt(this.id).writeString(this.sql);
                }
                sessionRemote.done(transfer);
                this.isQuery = transfer.readBoolean();
                this.readonly = transfer.readBoolean();
                this.cmdType = z ? transfer.readInt() : 0;
                int readInt = transfer.readInt();
                if (z) {
                    this.parameters.clear();
                    for (int i3 = 0; i3 < readInt; i3++) {
                        ParameterRemote parameterRemote = new ParameterRemote(i3);
                        parameterRemote.readMetaData(transfer);
                        this.parameters.add(parameterRemote);
                    }
                }
            } catch (IOException e) {
                int i4 = i;
                i--;
                i2++;
                sessionRemote.removeServer(e, i4, i2);
            }
            i++;
        }
    }

    @Override // org.h2.command.CommandInterface
    public boolean isQuery() {
        return this.isQuery;
    }

    @Override // org.h2.command.CommandInterface
    public ArrayList<ParameterInterface> getParameters() {
        return this.parameters;
    }

    private void prepareIfRequired() {
        if (this.session.getLastReconnect() != this.created) {
            this.id = Integer.MIN_VALUE;
        }
        this.session.checkClosed();
        if (this.id <= this.session.getCurrentId() - SysProperties.SERVER_CACHED_OBJECTS) {
            prepare(this.session, false);
        }
    }

    @Override // org.h2.command.CommandInterface
    public ResultInterface getMetaData() {
        SessionRemote sessionRemote = this.session;
        sessionRemote.lock();
        try {
            if (!this.isQuery) {
                return null;
            }
            int nextId = sessionRemote.getNextId();
            ResultRemote resultRemote = null;
            int i = 0;
            for (int i2 = 0; i2 < this.transferList.size(); i2 = (i2 - 1) + 1) {
                prepareIfRequired();
                Transfer transfer = this.transferList.get(i2);
                try {
                    sessionRemote.traceOperation("COMMAND_GET_META_DATA", this.id);
                    transfer.writeInt(10).writeInt(this.id).writeInt(nextId);
                    sessionRemote.done(transfer);
                    resultRemote = new ResultRemote(sessionRemote, transfer, nextId, transfer.readInt(), Integer.MAX_VALUE);
                    break;
                } catch (IOException e) {
                    i++;
                    sessionRemote.removeServer(e, i2, i);
                }
            }
            sessionRemote.autoCommitIfCluster();
            ResultRemote resultRemote2 = resultRemote;
            sessionRemote.unlock();
            return resultRemote2;
        } finally {
            sessionRemote.unlock();
        }
    }

    @Override // org.h2.command.CommandInterface
    public ResultInterface executeQuery(long j, boolean z) {
        int i;
        checkParameters();
        SessionRemote sessionRemote = this.session;
        sessionRemote.lock();
        try {
            int nextId = sessionRemote.getNextId();
            ResultRemote resultRemote = null;
            int i2 = 0;
            int i3 = 0;
            while (i2 < this.transferList.size()) {
                prepareIfRequired();
                Transfer transfer = this.transferList.get(i2);
                try {
                    sessionRemote.traceOperation("COMMAND_EXECUTE_QUERY", this.id);
                    transfer.writeInt(2).writeInt(this.id).writeInt(nextId);
                    transfer.writeRowCount(j);
                    if (sessionRemote.isClustered() || z) {
                        i = Integer.MAX_VALUE;
                    } else {
                        i = this.fetchSize;
                    }
                    transfer.writeInt(i);
                    sendParameters(transfer);
                    sessionRemote.done(transfer);
                    int readInt = transfer.readInt();
                    if (resultRemote != null) {
                        resultRemote.close();
                    }
                    resultRemote = new ResultRemote(sessionRemote, transfer, nextId, readInt, i);
                } catch (IOException e) {
                    int i4 = i2;
                    i2--;
                    i3++;
                    sessionRemote.removeServer(e, i4, i3);
                }
                if (this.readonly) {
                    break;
                }
                i2++;
            }
            sessionRemote.autoCommitIfCluster();
            sessionRemote.readSessionState();
            ResultRemote resultRemote2 = resultRemote;
            sessionRemote.unlock();
            return resultRemote2;
        } catch (Throwable th) {
            sessionRemote.unlock();
            throw th;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:15:0x0083. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:31:0x012f A[Catch: IOException -> 0x0159, all -> 0x01ac, TryCatch #0 {IOException -> 0x0159, blocks: (B:14:0x005c, B:15:0x0083, B:16:0x009c, B:19:0x00c1, B:23:0x00d9, B:26:0x00fe, B:29:0x0116, B:31:0x012f, B:33:0x013b, B:34:0x0143), top: B:13:0x005c, outer: #1 }] */
    @Override // org.h2.command.CommandInterface
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.result.ResultWithGeneratedKeys executeUpdate(java.lang.Object r9) {
        /*
            Method dump skipped, instructions count: 438
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.CommandRemote.executeUpdate(java.lang.Object):org.h2.result.ResultWithGeneratedKeys");
    }

    private void checkParameters() {
        if (this.cmdType != 60) {
            Iterator<ParameterInterface> it = this.parameters.iterator();
            while (it.hasNext()) {
                it.next().checkSet();
            }
        }
    }

    private void sendParameters(Transfer transfer) throws IOException {
        transfer.writeInt(this.parameters.size());
        Iterator<ParameterInterface> it = this.parameters.iterator();
        while (it.hasNext()) {
            Value paramValue = it.next().getParamValue();
            if (paramValue == null && this.cmdType == 60) {
                paramValue = ValueNull.INSTANCE;
            }
            transfer.writeValue(paramValue);
        }
    }

    @Override // org.h2.command.CommandInterface, java.lang.AutoCloseable
    public void close() {
        SessionRemote sessionRemote = this.session;
        if (sessionRemote == null || sessionRemote.isClosed()) {
            return;
        }
        sessionRemote.lock();
        try {
            sessionRemote.traceOperation("COMMAND_CLOSE", this.id);
            Iterator<Transfer> it = this.transferList.iterator();
            while (it.hasNext()) {
                try {
                    it.next().writeInt(4).writeInt(this.id);
                } catch (IOException e) {
                    this.trace.error(e, Constants.CLOSE);
                }
            }
            this.session = null;
            try {
                Iterator<ParameterInterface> it2 = this.parameters.iterator();
                while (it2.hasNext()) {
                    Value paramValue = it2.next().getParamValue();
                    if (paramValue instanceof ValueLob) {
                        ((ValueLob) paramValue).remove();
                    }
                }
            } catch (DbException e2) {
                this.trace.error(e2, Constants.CLOSE);
            }
            this.parameters.clear();
        } finally {
            sessionRemote.unlock();
        }
    }

    @Override // org.h2.command.CommandInterface
    public void cancel() {
        this.session.cancelStatement(this.id);
    }

    public String toString() {
        return this.sql + Trace.formatParams(getParameters());
    }

    @Override // org.h2.command.CommandInterface
    public int getCommandType() {
        return this.cmdType;
    }
}
