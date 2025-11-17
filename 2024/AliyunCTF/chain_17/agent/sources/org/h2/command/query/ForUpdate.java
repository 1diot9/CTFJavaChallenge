package org.h2.command.query;

import org.h2.message.DbException;
import org.h2.util.HasSQL;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/ForUpdate.class */
public final class ForUpdate implements HasSQL {
    public static final ForUpdate DEFAULT = new ForUpdate(Type.DEFAULT, -1);
    public static final ForUpdate NOWAIT = new ForUpdate(Type.NOWAIT, 0);
    public static final ForUpdate SKIP_LOCKED = new ForUpdate(Type.SKIP_LOCKED, -2);
    private final Type type;
    private final int timeoutMillis;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/ForUpdate$Type.class */
    public enum Type {
        DEFAULT,
        WAIT,
        NOWAIT,
        SKIP_LOCKED
    }

    public static final ForUpdate wait(int i) {
        if (i < 0) {
            throw DbException.getInvalidValueException("timeout", Integer.valueOf(i));
        }
        if (i == 0) {
            return NOWAIT;
        }
        return new ForUpdate(Type.WAIT, i);
    }

    private ForUpdate(Type type, int i) {
        this.type = type;
        this.timeoutMillis = i;
    }

    public Type getType() {
        return this.type;
    }

    public int getTimeoutMillis() {
        return this.timeoutMillis;
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append(" FOR UPDATE");
        switch (this.type) {
            case WAIT:
                sb.append(" WAIT ").append(this.timeoutMillis / 1000);
                int i2 = this.timeoutMillis % 1000;
                if (i2 > 0) {
                    StringUtils.appendZeroPadded(sb.append('.'), 3, i2);
                    break;
                }
                break;
            case NOWAIT:
                sb.append(" NOWAIT");
                break;
            case SKIP_LOCKED:
                sb.append(" SKIP LOCKED");
                break;
        }
        return sb;
    }
}
