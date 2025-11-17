package cn.hutool.core.lang;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.Date;
import org.springframework.util.backoff.ExponentialBackOff;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/Snowflake.class */
public class Snowflake implements Serializable {
    private static final long serialVersionUID = 1;
    public static long DEFAULT_TWEPOCH = 1288834974657L;
    public static long DEFAULT_TIME_OFFSET = ExponentialBackOff.DEFAULT_INITIAL_INTERVAL;
    private static final long WORKER_ID_BITS = 5;
    private static final long MAX_WORKER_ID = 31;
    private static final long DATA_CENTER_ID_BITS = 5;
    private static final long MAX_DATA_CENTER_ID = 31;
    private static final long SEQUENCE_BITS = 12;
    private static final long WORKER_ID_SHIFT = 12;
    private static final long DATA_CENTER_ID_SHIFT = 17;
    private static final long TIMESTAMP_LEFT_SHIFT = 22;
    private static final long SEQUENCE_MASK = 4095;
    private final long twepoch;
    private final long workerId;
    private final long dataCenterId;
    private final boolean useSystemClock;
    private final long timeOffset;
    private final long randomSequenceLimit;
    private long sequence;
    private long lastTimestamp;

    public Snowflake() {
        this(IdUtil.getWorkerId(IdUtil.getDataCenterId(31L), 31L));
    }

    public Snowflake(long workerId) {
        this(workerId, IdUtil.getDataCenterId(31L));
    }

    public Snowflake(long workerId, long dataCenterId) {
        this(workerId, dataCenterId, false);
    }

    public Snowflake(long workerId, long dataCenterId, boolean isUseSystemClock) {
        this(null, workerId, dataCenterId, isUseSystemClock);
    }

    public Snowflake(Date epochDate, long workerId, long dataCenterId, boolean isUseSystemClock) {
        this(epochDate, workerId, dataCenterId, isUseSystemClock, DEFAULT_TIME_OFFSET);
    }

    public Snowflake(Date epochDate, long workerId, long dataCenterId, boolean isUseSystemClock, long timeOffset) {
        this(epochDate, workerId, dataCenterId, isUseSystemClock, timeOffset, 0L);
    }

    public Snowflake(Date epochDate, long workerId, long dataCenterId, boolean isUseSystemClock, long timeOffset, long randomSequenceLimit) {
        this.sequence = 0L;
        this.lastTimestamp = -1L;
        this.twepoch = null != epochDate ? epochDate.getTime() : DEFAULT_TWEPOCH;
        this.workerId = Assert.checkBetween(workerId, 0L, 31L);
        this.dataCenterId = Assert.checkBetween(dataCenterId, 0L, 31L);
        this.useSystemClock = isUseSystemClock;
        this.timeOffset = timeOffset;
        this.randomSequenceLimit = Assert.checkBetween(randomSequenceLimit, 0L, SEQUENCE_MASK);
    }

    public long getWorkerId(long id) {
        return (id >> 12) & 31;
    }

    public long getDataCenterId(long id) {
        return (id >> DATA_CENTER_ID_SHIFT) & 31;
    }

    public long getGenerateDateTime(long id) {
        return ((id >> TIMESTAMP_LEFT_SHIFT) & 2199023255551L) + this.twepoch;
    }

    public synchronized long nextId() {
        long timestamp = genTime();
        if (timestamp < this.lastTimestamp) {
            if (this.lastTimestamp - timestamp < this.timeOffset) {
                timestamp = this.lastTimestamp;
            } else {
                throw new IllegalStateException(StrUtil.format("Clock moved backwards. Refusing to generate id for {}ms", Long.valueOf(this.lastTimestamp - timestamp)));
            }
        }
        if (timestamp == this.lastTimestamp) {
            long sequence = (this.sequence + serialVersionUID) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(this.lastTimestamp);
            }
            this.sequence = sequence;
        } else if (this.randomSequenceLimit > serialVersionUID) {
            this.sequence = RandomUtil.randomLong(this.randomSequenceLimit);
        } else {
            this.sequence = 0L;
        }
        this.lastTimestamp = timestamp;
        return ((timestamp - this.twepoch) << TIMESTAMP_LEFT_SHIFT) | (this.dataCenterId << DATA_CENTER_ID_SHIFT) | (this.workerId << 12) | this.sequence;
    }

    public String nextIdStr() {
        return Long.toString(nextId());
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp;
        long genTime = genTime();
        while (true) {
            timestamp = genTime;
            if (timestamp != lastTimestamp) {
                break;
            }
            genTime = genTime();
        }
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(StrUtil.format("Clock moved backwards. Refusing to generate id for {}ms", Long.valueOf(lastTimestamp - timestamp)));
        }
        return timestamp;
    }

    private long genTime() {
        return this.useSystemClock ? SystemClock.now() : System.currentTimeMillis();
    }
}
