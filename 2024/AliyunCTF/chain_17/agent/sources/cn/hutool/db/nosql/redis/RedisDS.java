package cn.hutool.db.nosql.redis;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import java.io.Closeable;
import java.io.Serializable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/nosql/redis/RedisDS.class */
public class RedisDS implements Closeable, Serializable {
    private static final long serialVersionUID = -5605411972456177456L;
    public static final String REDIS_CONFIG_PATH = "config/redis.setting";
    private Setting setting;
    private JedisPool pool;

    public static RedisDS create() {
        return new RedisDS();
    }

    public static RedisDS create(String group) {
        return new RedisDS(group);
    }

    public static RedisDS create(Setting setting, String group) {
        return new RedisDS(setting, group);
    }

    public RedisDS() {
        this(null, null);
    }

    public RedisDS(String group) {
        this(null, group);
    }

    public RedisDS(Setting setting, String group) {
        this.setting = setting;
        init(group);
    }

    public RedisDS init(String group) {
        if (null == this.setting) {
            this.setting = new Setting(REDIS_CONFIG_PATH, true);
        }
        JedisPoolConfig config = new JedisPoolConfig();
        this.setting.toBean((Setting) config);
        if (StrUtil.isNotBlank(group)) {
            this.setting.toBean(group, (String) config);
        }
        Long maxWaitMillis = this.setting.getLong("maxWaitMillis");
        if (null != maxWaitMillis) {
            config.setMaxWaitMillis(maxWaitMillis.longValue());
        }
        this.pool = new JedisPool(config, this.setting.getStr("host", group, "127.0.0.1"), this.setting.getInt("port", group, 6379).intValue(), this.setting.getInt("connectionTimeout", group, this.setting.getInt("timeout", group, 2000)).intValue(), this.setting.getInt("soTimeout", group, this.setting.getInt("timeout", group, 2000)).intValue(), this.setting.getStr("password", group, null), this.setting.getInt("database", group, 0).intValue(), this.setting.getStr("clientName", group, "Hutool"), this.setting.getBool("ssl", group, false).booleanValue(), (SSLSocketFactory) null, (SSLParameters) null, (HostnameVerifier) null);
        return this;
    }

    public Jedis getJedis() {
        return this.pool.getResource();
    }

    public String getStr(String key) {
        Jedis jedis = getJedis();
        Throwable th = null;
        try {
            try {
                String str = jedis.get(key);
                if (jedis != null) {
                    if (0 != 0) {
                        try {
                            jedis.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        jedis.close();
                    }
                }
                return str;
            } finally {
            }
        } catch (Throwable th3) {
            if (jedis != null) {
                if (th != null) {
                    try {
                        jedis.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    jedis.close();
                }
            }
            throw th3;
        }
    }

    public String setStr(String key, String value) {
        Jedis jedis = getJedis();
        Throwable th = null;
        try {
            try {
                String str = jedis.set(key, value);
                if (jedis != null) {
                    if (0 != 0) {
                        try {
                            jedis.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        jedis.close();
                    }
                }
                return str;
            } finally {
            }
        } catch (Throwable th3) {
            if (jedis != null) {
                if (th != null) {
                    try {
                        jedis.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    jedis.close();
                }
            }
            throw th3;
        }
    }

    public Long del(String... keys) {
        Jedis jedis = getJedis();
        Throwable th = null;
        try {
            try {
                Long valueOf = Long.valueOf(jedis.del(keys));
                if (jedis != null) {
                    if (0 != 0) {
                        try {
                            jedis.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        jedis.close();
                    }
                }
                return valueOf;
            } finally {
            }
        } catch (Throwable th3) {
            if (jedis != null) {
                if (th != null) {
                    try {
                        jedis.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    jedis.close();
                }
            }
            throw th3;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        IoUtil.close((Closeable) this.pool);
    }
}
