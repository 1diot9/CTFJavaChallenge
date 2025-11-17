package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.table.Column;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterSequence.class */
public class AlterSequence extends SchemaOwnerCommand {
    private boolean ifExists;
    private Column column;
    private Boolean always;
    private String sequenceName;
    private Sequence sequence;
    private SequenceOptions options;

    public AlterSequence(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
        this.transactional = true;
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setSequenceName(String str) {
        this.sequenceName = str;
    }

    public void setOptions(SequenceOptions sequenceOptions) {
        this.options = sequenceOptions;
    }

    @Override // org.h2.command.ddl.DefineCommand, org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    public void setColumn(Column column, Boolean bool) {
        this.column = column;
        this.always = bool;
        this.sequence = column.getSequence();
        if (this.sequence == null && !this.ifExists) {
            throw DbException.get(ErrorCode.SEQUENCE_NOT_FOUND_1, column.getTraceSQL());
        }
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        if (this.sequence == null) {
            this.sequence = schema.findSequence(this.sequenceName);
            if (this.sequence == null) {
                if (!this.ifExists) {
                    throw DbException.get(ErrorCode.SEQUENCE_NOT_FOUND_1, this.sequenceName);
                }
                return 0L;
            }
        }
        if (this.column != null) {
            this.session.getUser().checkTableRight(this.column.getTable(), 32);
        }
        this.options.setDataType(this.sequence.getDataType());
        Long startValue = this.options.getStartValue(this.session);
        this.sequence.modify(this.options.getRestartValue(this.session, startValue != null ? startValue.longValue() : this.sequence.getStartValue()), startValue, this.options.getMinValue(this.sequence, this.session), this.options.getMaxValue(this.sequence, this.session), this.options.getIncrement(this.session), this.options.getCycle(), this.options.getCacheSize(this.session));
        this.sequence.flush(this.session);
        if (this.column != null && this.always != null) {
            this.column.setSequence(this.sequence, this.always.booleanValue());
            getDatabase().updateMeta(this.session, this.column.getTable());
            return 0L;
        }
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 54;
    }
}
