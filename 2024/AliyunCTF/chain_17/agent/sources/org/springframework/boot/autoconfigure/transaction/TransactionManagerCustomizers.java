package org.springframework.boot.autoconfigure.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/TransactionManagerCustomizers.class */
public class TransactionManagerCustomizers {
    private final List<? extends TransactionManagerCustomizer<?>> customizers;

    @Deprecated(since = "3.2.0", forRemoval = true)
    public TransactionManagerCustomizers(Collection<? extends PlatformTransactionManagerCustomizer<?>> customizers) {
        this((List<? extends TransactionManagerCustomizer<?>>) (customizers != null ? new ArrayList(customizers) : Collections.emptyList()));
    }

    private TransactionManagerCustomizers(List<? extends TransactionManagerCustomizer<?>> customizers) {
        this.customizers = customizers;
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public void customize(PlatformTransactionManager platformTransactionManager) {
        customize((TransactionManager) platformTransactionManager);
    }

    public void customize(TransactionManager transactionManager) {
        ((LambdaSafe.Callbacks) LambdaSafe.callbacks(TransactionManagerCustomizer.class, this.customizers, transactionManager, new Object[0]).withLogger(TransactionManagerCustomizers.class)).invoke(customizer -> {
            customizer.customize(transactionManager);
        });
    }

    public static TransactionManagerCustomizers of(Collection<? extends TransactionManagerCustomizer<?>> customizers) {
        return new TransactionManagerCustomizers((List<? extends TransactionManagerCustomizer<?>>) (customizers != null ? new ArrayList(customizers) : Collections.emptyList()));
    }
}
