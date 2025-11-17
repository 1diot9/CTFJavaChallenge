package cn.hutool.core.lang.generator;

import cn.hutool.core.lang.Snowflake;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/generator/SnowflakeGenerator.class */
public class SnowflakeGenerator implements Generator<Long> {
    private final Snowflake snowflake;

    public SnowflakeGenerator() {
        this(0L, 0L);
    }

    public SnowflakeGenerator(long workerId, long dataCenterId) {
        this.snowflake = new Snowflake(workerId, dataCenterId);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.lang.generator.Generator
    public Long next() {
        return Long.valueOf(this.snowflake.nextId());
    }
}
