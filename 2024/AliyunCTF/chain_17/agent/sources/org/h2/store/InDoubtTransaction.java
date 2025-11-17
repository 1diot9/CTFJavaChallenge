package org.h2.store;

import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/InDoubtTransaction.class */
public interface InDoubtTransaction {
    public static final int IN_DOUBT = 0;
    public static final int COMMIT = 1;
    public static final int ROLLBACK = 2;

    void setState(int i);

    int getState();

    String getTransactionName();

    default String getStateDescription() {
        int state = getState();
        switch (state) {
            case 0:
                return "IN_DOUBT";
            case 1:
                return "COMMIT";
            case 2:
                return "ROLLBACK";
            default:
                throw DbException.getInternalError("state=" + state);
        }
    }
}
