package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultOrRows;
import org.jooq.Results;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ResultsImpl.class */
final class ResultsImpl extends AbstractList<Result<Record>> implements Results {
    private Configuration configuration;
    final List<ResultOrRows> resultsOrRows = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultsImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override // org.jooq.Results
    public final List<ResultOrRows> resultsOrRows() {
        return this.resultsOrRows;
    }

    @Override // org.jooq.Results, org.jooq.Attachable
    public final void attach(Configuration c) {
        this.configuration = c;
        Iterator<Result<Record>> it = iterator();
        while (it.hasNext()) {
            Result<?> result = it.next();
            if (result != null) {
                result.attach(c);
            }
        }
    }

    @Override // org.jooq.Results, org.jooq.Attachable
    public final void detach() {
        attach(null);
    }

    @Override // org.jooq.Attachable
    public final Configuration configuration() {
        return this.configuration;
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String separator = "";
        for (ResultOrRows result : this.resultsOrRows) {
            if (result.result() != null) {
                sb.append(separator).append("Result set:\n").append(result.result());
            } else if (result.exception() != null) {
                sb.append(separator).append("Exception: ").append(result.exception().getMessage());
            } else {
                sb.append(separator).append("Update count: ").append(result.rows());
            }
            separator = "\n";
        }
        return sb.toString();
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public int hashCode() {
        return this.resultsOrRows.hashCode();
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ResultsImpl) {
            ResultsImpl r = (ResultsImpl) obj;
            return this.resultsOrRows.equals(r.resultsOrRows);
        }
        return false;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return list().size();
    }

    @Override // java.util.AbstractList, java.util.List
    public final Result<Record> get(int index) {
        return list().get(index);
    }

    @Override // java.util.AbstractList, java.util.List
    public Result<Record> set(int index, Result<Record> element) {
        return this.resultsOrRows.set(translatedIndex(index), new ResultOrRowsImpl(element)).result();
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int index, Result<Record> element) {
        this.resultsOrRows.add(translatedIndex(index), new ResultOrRowsImpl(element));
    }

    @Override // java.util.AbstractList, java.util.List
    public Result<Record> remove(int index) {
        return this.resultsOrRows.remove(translatedIndex(index)).result();
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        this.resultsOrRows.clear();
    }

    private final List<Result<Record>> list() {
        List<Result<Record>> list = new ArrayList<>();
        for (ResultOrRows result : this.resultsOrRows) {
            if (result.result() != null) {
                list.add(result.result());
            }
        }
        return list;
    }

    private final int translatedIndex(int index) {
        int i;
        int translated = 0;
        for (int i2 = 0; i2 < index; i2++) {
            do {
                i = translated;
                translated++;
            } while (this.resultsOrRows.get(i).result() == null);
        }
        return translated;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ResultsImpl$ResultOrRowsImpl.class */
    public static final class ResultOrRowsImpl extends Record implements ResultOrRows {
        private final Result<Record> result;
        private final int rows;
        private final DataAccessException exception;

        ResultOrRowsImpl(Result<Record> result, int rows, DataAccessException exception) {
            this.result = result;
            this.rows = rows;
            this.exception = exception;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ResultOrRowsImpl.class), ResultOrRowsImpl.class, "result;rows;exception", "FIELD:Lorg/jooq/impl/ResultsImpl$ResultOrRowsImpl;->result:Lorg/jooq/Result;", "FIELD:Lorg/jooq/impl/ResultsImpl$ResultOrRowsImpl;->rows:I", "FIELD:Lorg/jooq/impl/ResultsImpl$ResultOrRowsImpl;->exception:Lorg/jooq/exception/DataAccessException;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ResultOrRowsImpl.class, Object.class), ResultOrRowsImpl.class, "result;rows;exception", "FIELD:Lorg/jooq/impl/ResultsImpl$ResultOrRowsImpl;->result:Lorg/jooq/Result;", "FIELD:Lorg/jooq/impl/ResultsImpl$ResultOrRowsImpl;->rows:I", "FIELD:Lorg/jooq/impl/ResultsImpl$ResultOrRowsImpl;->exception:Lorg/jooq/exception/DataAccessException;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        @Override // org.jooq.ResultOrRows
        public Result<Record> result() {
            return this.result;
        }

        @Override // org.jooq.ResultOrRows
        public int rows() {
            return this.rows;
        }

        @Override // org.jooq.ResultOrRows
        public DataAccessException exception() {
            return this.exception;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ResultOrRowsImpl(Result<Record> result) {
            this(result, result != null ? result.size() : 0, null);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ResultOrRowsImpl(int rows) {
            this(null, rows, null);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ResultOrRowsImpl(DataAccessException exception) {
            this(null, 0, exception);
        }

        @Override // java.lang.Record
        public String toString() {
            if (this.exception != null) {
                return this.exception.toString();
            }
            if (this.result != null) {
                return this.result.toString();
            }
            return this.rows;
        }
    }
}
