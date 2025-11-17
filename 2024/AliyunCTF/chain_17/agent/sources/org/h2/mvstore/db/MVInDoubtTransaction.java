package org.h2.mvstore.db;

import org.h2.mvstore.MVStore;
import org.h2.mvstore.tx.Transaction;
import org.h2.store.InDoubtTransaction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVInDoubtTransaction.class */
public final class MVInDoubtTransaction implements InDoubtTransaction {
    private final MVStore store;
    private final Transaction transaction;
    private int state = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MVInDoubtTransaction(MVStore mVStore, Transaction transaction) {
        this.store = mVStore;
        this.transaction = transaction;
    }

    @Override // org.h2.store.InDoubtTransaction
    public void setState(int i) {
        if (i == 1) {
            this.transaction.commit();
        } else {
            this.transaction.rollback();
        }
        this.store.commit();
        this.state = i;
    }

    @Override // org.h2.store.InDoubtTransaction
    public int getState() {
        return this.state;
    }

    @Override // org.h2.store.InDoubtTransaction
    public String getTransactionName() {
        return this.transaction.getName();
    }
}
