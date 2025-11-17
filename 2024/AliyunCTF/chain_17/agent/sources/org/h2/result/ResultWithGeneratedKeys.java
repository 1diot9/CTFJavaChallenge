package org.h2.result;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/ResultWithGeneratedKeys.class */
public class ResultWithGeneratedKeys {
    private final long updateCount;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/ResultWithGeneratedKeys$WithKeys.class */
    public static final class WithKeys extends ResultWithGeneratedKeys {
        private final ResultInterface generatedKeys;

        public WithKeys(long j, ResultInterface resultInterface) {
            super(j);
            this.generatedKeys = resultInterface;
        }

        @Override // org.h2.result.ResultWithGeneratedKeys
        public ResultInterface getGeneratedKeys() {
            return this.generatedKeys;
        }
    }

    public static ResultWithGeneratedKeys of(long j) {
        return new ResultWithGeneratedKeys(j);
    }

    ResultWithGeneratedKeys(long j) {
        this.updateCount = j;
    }

    public ResultInterface getGeneratedKeys() {
        return null;
    }

    public long getUpdateCount() {
        return this.updateCount;
    }
}
