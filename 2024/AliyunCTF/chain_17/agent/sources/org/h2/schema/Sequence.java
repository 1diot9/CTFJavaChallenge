package org.h2.schema;

import org.h2.api.ErrorCode;
import org.h2.command.ddl.SequenceOptions;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/Sequence.class */
public final class Sequence extends SchemaObject {
    public static final int DEFAULT_CACHE_SIZE = 32;
    private long baseValue;
    private long margin;
    private TypeInfo dataType;
    private long increment;
    private long cacheSize;
    private long startValue;
    private long minValue;
    private long maxValue;
    private Cycle cycle;
    private boolean belongsToTable;
    private boolean writeWithMargin;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/Sequence$Cycle.class */
    public enum Cycle {
        CYCLE,
        NO_CYCLE,
        EXHAUSTED;

        public boolean isCycle() {
            return this == CYCLE;
        }
    }

    public Sequence(SessionLocal sessionLocal, Schema schema, int i, String str, SequenceOptions sequenceOptions, boolean z) {
        super(schema, i, str, 9);
        long j;
        boolean z2;
        this.dataType = sequenceOptions.getDataType();
        if (this.dataType == null) {
            TypeInfo typeInfo = sessionLocal.getMode().decimalSequences ? TypeInfo.TYPE_NUMERIC_BIGINT : TypeInfo.TYPE_BIGINT;
            this.dataType = typeInfo;
            sequenceOptions.setDataType(typeInfo);
        }
        long[] bounds = sequenceOptions.getBounds();
        Long increment = sequenceOptions.getIncrement(sessionLocal);
        long longValue = increment != null ? increment.longValue() : 1L;
        Long startValue = sequenceOptions.getStartValue(sessionLocal);
        Long minValue = sequenceOptions.getMinValue(null, sessionLocal);
        Long maxValue = sequenceOptions.getMaxValue(null, sessionLocal);
        long longValue2 = minValue != null ? minValue.longValue() : getDefaultMinValue(startValue, longValue, bounds);
        long longValue3 = maxValue != null ? maxValue.longValue() : getDefaultMaxValue(startValue, longValue, bounds);
        long longValue4 = startValue != null ? startValue.longValue() : longValue >= 0 ? longValue2 : longValue3;
        Long restartValue = sequenceOptions.getRestartValue(sessionLocal, longValue4);
        long longValue5 = restartValue != null ? restartValue.longValue() : longValue4;
        Long cacheSize = sequenceOptions.getCacheSize(sessionLocal);
        if (cacheSize != null) {
            j = cacheSize.longValue();
            z2 = false;
        } else {
            j = 32;
            z2 = true;
        }
        long checkOptions = checkOptions(longValue5, longValue4, longValue2, longValue3, longValue, j, z2);
        Cycle cycle = sequenceOptions.getCycle();
        if (cycle == null) {
            cycle = Cycle.NO_CYCLE;
        } else if (cycle == Cycle.EXHAUSTED) {
            longValue5 = longValue4;
        }
        long j2 = longValue5;
        this.baseValue = j2;
        this.margin = j2;
        this.increment = longValue;
        this.cacheSize = checkOptions;
        this.startValue = longValue4;
        this.minValue = longValue2;
        this.maxValue = longValue3;
        this.cycle = cycle;
        this.belongsToTable = z;
    }

    public synchronized void modify(Long l, Long l2, Long l3, Long l4, Long l5, Cycle cycle, Long l6) {
        long j;
        boolean z;
        long longValue = l != null ? l.longValue() : this.baseValue;
        long longValue2 = l2 != null ? l2.longValue() : this.startValue;
        long longValue3 = l3 != null ? l3.longValue() : this.minValue;
        long longValue4 = l4 != null ? l4.longValue() : this.maxValue;
        long longValue5 = l5 != null ? l5.longValue() : this.increment;
        if (l6 != null) {
            j = l6.longValue();
            z = false;
        } else {
            j = this.cacheSize;
            z = true;
        }
        long checkOptions = checkOptions(longValue, longValue2, longValue3, longValue4, longValue5, j, z);
        if (cycle == null) {
            cycle = this.cycle;
            if (cycle == Cycle.EXHAUSTED && l != null) {
                cycle = Cycle.NO_CYCLE;
            }
        } else if (cycle == Cycle.EXHAUSTED) {
            longValue = longValue2;
        }
        long j2 = longValue;
        this.baseValue = j2;
        this.margin = j2;
        this.startValue = longValue2;
        this.minValue = longValue3;
        this.maxValue = longValue4;
        this.increment = longValue5;
        this.cacheSize = checkOptions;
        this.cycle = cycle;
    }

    private long checkOptions(long j, long j2, long j3, long j4, long j5, long j6, boolean z) {
        if (j3 <= j && j <= j4 && j3 <= j2 && j2 <= j4 && j3 < j4 && j5 != 0) {
            long j7 = j4 - j3;
            if (Long.compareUnsigned(Math.abs(j5), j7) <= 0 && j6 >= 0) {
                if (j6 <= 1) {
                    return 1L;
                }
                long maxCacheSize = getMaxCacheSize(j7, j5);
                if (j6 <= maxCacheSize) {
                    return j6;
                }
                if (z) {
                    return maxCacheSize;
                }
            }
        }
        throw DbException.get(ErrorCode.SEQUENCE_ATTRIBUTES_INVALID_7, getName(), Long.toString(j), Long.toString(j2), Long.toString(j3), Long.toString(j4), Long.toString(j5), Long.toString(j6));
    }

    private static long getMaxCacheSize(long j, long j2) {
        long j3;
        if (j2 <= 0) {
            long j4 = -j;
            if (j4 > 0) {
                j3 = Long.MIN_VALUE;
            } else {
                j3 = j4 + j2;
                if (j3 >= 0) {
                    j3 = Long.MIN_VALUE;
                }
            }
        } else if (j < 0) {
            j3 = Long.MAX_VALUE;
        } else {
            j3 = j + j2;
            if (j3 < 0) {
                j3 = Long.MAX_VALUE;
            }
        }
        return j3 / j2;
    }

    public static long getDefaultMinValue(Long l, long j, long[] jArr) {
        long j2 = j >= 0 ? 1L : jArr[0];
        if (l != null && j >= 0 && l.longValue() < j2) {
            j2 = l.longValue();
        }
        return j2;
    }

    public static long getDefaultMaxValue(Long l, long j, long[] jArr) {
        long j2 = j >= 0 ? jArr[1] : -1L;
        if (l != null && j < 0 && l.longValue() > j2) {
            j2 = l.longValue();
        }
        return j2;
    }

    public boolean getBelongsToTable() {
        return this.belongsToTable;
    }

    public TypeInfo getDataType() {
        return this.dataType;
    }

    public int getEffectivePrecision() {
        TypeInfo typeInfo = this.dataType;
        switch (typeInfo.getValueType()) {
            case 13:
                int precision = (int) typeInfo.getPrecision();
                int scale = typeInfo.getScale();
                if (precision - scale > 19) {
                    return 19 + scale;
                }
                return precision;
            case 16:
                return Math.min((int) typeInfo.getPrecision(), 19);
            default:
                return (int) typeInfo.getPrecision();
        }
    }

    public long getIncrement() {
        return this.increment;
    }

    public long getStartValue() {
        return this.startValue;
    }

    public long getMinValue() {
        return this.minValue;
    }

    public long getMaxValue() {
        return this.maxValue;
    }

    public Cycle getCycle() {
        return this.cycle;
    }

    @Override // org.h2.engine.DbObject
    public String getDropSQL() {
        if (getBelongsToTable()) {
            return null;
        }
        return getSQL(new StringBuilder("DROP SEQUENCE IF EXISTS "), 0).toString();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder sql = getSQL(new StringBuilder("CREATE SEQUENCE "), 0);
        if (this.dataType.getValueType() != 12) {
            this.dataType.getSQL(sql.append(" AS "), 0);
        }
        sql.append(' ');
        synchronized (this) {
            getSequenceOptionsSQL(sql, this.writeWithMargin ? this.margin : this.baseValue);
        }
        if (this.belongsToTable) {
            sql.append(" BELONGS_TO_TABLE");
        }
        return sql.toString();
    }

    public synchronized StringBuilder getSequenceOptionsSQL(StringBuilder sb) {
        return getSequenceOptionsSQL(sb, this.baseValue);
    }

    private StringBuilder getSequenceOptionsSQL(StringBuilder sb, long j) {
        sb.append("START WITH ").append(this.startValue);
        if (j != this.startValue && this.cycle != Cycle.EXHAUSTED) {
            sb.append(" RESTART WITH ").append(j);
        }
        if (this.increment != 1) {
            sb.append(" INCREMENT BY ").append(this.increment);
        }
        long[] bounds = SequenceOptions.getBounds(this.dataType);
        if (this.minValue != getDefaultMinValue(Long.valueOf(j), this.increment, bounds)) {
            sb.append(" MINVALUE ").append(this.minValue);
        }
        if (this.maxValue != getDefaultMaxValue(Long.valueOf(j), this.increment, bounds)) {
            sb.append(" MAXVALUE ").append(this.maxValue);
        }
        if (this.cycle == Cycle.CYCLE) {
            sb.append(" CYCLE");
        } else if (this.cycle == Cycle.EXHAUSTED) {
            sb.append(" EXHAUSTED");
        }
        if (this.cacheSize != 32) {
            if (this.cacheSize == 1) {
                sb.append(" NO CACHE");
            } else if (this.cacheSize > 32 || this.cacheSize != getMaxCacheSize(this.maxValue - this.minValue, this.increment)) {
                sb.append(" CACHE ").append(this.cacheSize);
            }
        }
        return sb;
    }

    public Value getNext(SessionLocal sessionLocal) {
        long j;
        boolean increment;
        synchronized (this) {
            if (this.cycle == Cycle.EXHAUSTED) {
                throw DbException.get(ErrorCode.SEQUENCE_EXHAUSTED, getName());
            }
            j = this.baseValue;
            long j2 = j + this.increment;
            increment = this.increment > 0 ? increment(j, j2) : decrement(j, j2);
        }
        if (increment) {
            flush(sessionLocal);
        }
        return ValueBigint.get(j).castTo(this.dataType, sessionLocal);
    }

    private boolean increment(long j, long j2) {
        boolean z = false;
        if (j2 > this.maxValue || ((j ^ (-1)) & j2) < 0) {
            j2 = this.minValue;
            z = true;
            if (this.cycle == Cycle.CYCLE) {
                this.margin = j2 + (this.increment * (this.cacheSize - 1));
            } else {
                this.margin = j2;
                this.cycle = Cycle.EXHAUSTED;
            }
        } else if (j2 > this.margin) {
            long j3 = j2 + (this.increment * (this.cacheSize - 1));
            if (j3 > this.maxValue || ((j2 ^ (-1)) & j3) < 0) {
                j3 = j2;
            }
            this.margin = j3;
            z = true;
        }
        this.baseValue = j2;
        return z;
    }

    private boolean decrement(long j, long j2) {
        boolean z = false;
        if (j2 < this.minValue || (j & (j2 ^ (-1))) < 0) {
            j2 = this.maxValue;
            z = true;
            if (this.cycle == Cycle.CYCLE) {
                this.margin = j2 + (this.increment * (this.cacheSize - 1));
            } else {
                this.margin = j2;
                this.cycle = Cycle.EXHAUSTED;
            }
        } else if (j2 < this.margin) {
            long j3 = j2 + (this.increment * (this.cacheSize - 1));
            if (j3 < this.minValue || (j2 & (j3 ^ (-1))) < 0) {
                j3 = j2;
            }
            this.margin = j3;
            z = true;
        }
        this.baseValue = j2;
        return z;
    }

    public void flushWithoutMargin() {
        if (this.margin != this.baseValue) {
            this.margin = this.baseValue;
            flush(null);
        }
    }

    public void flush(SessionLocal sessionLocal) {
        if (isTemporary()) {
            return;
        }
        if (sessionLocal == null || !this.database.isSysTableLockedBy(sessionLocal)) {
            SessionLocal systemSession = this.database.getSystemSession();
            systemSession.lock();
            try {
                flushInternal(systemSession);
                systemSession.commit(false);
                return;
            } finally {
                systemSession.unlock();
            }
        }
        sessionLocal.lock();
        try {
            flushInternal(sessionLocal);
            sessionLocal.unlock();
        } catch (Throwable th) {
            sessionLocal.unlock();
            throw th;
        }
    }

    private void flushInternal(SessionLocal sessionLocal) {
        boolean lockMeta = this.database.lockMeta(sessionLocal);
        try {
            this.writeWithMargin = true;
            this.database.updateMeta(sessionLocal, this);
        } finally {
            this.writeWithMargin = false;
            if (!lockMeta) {
                this.database.unlockMeta(sessionLocal);
            }
        }
    }

    public void close() {
        flushWithoutMargin();
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 3;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.database.removeMeta(sessionLocal, getId());
        invalidate();
    }

    public synchronized long getBaseValue() {
        return this.baseValue;
    }

    public synchronized long getCurrentValue() {
        return this.baseValue - this.increment;
    }

    public void setBelongsToTable(boolean z) {
        this.belongsToTable = z;
    }

    public long getCacheSize() {
        return this.cacheSize;
    }
}
