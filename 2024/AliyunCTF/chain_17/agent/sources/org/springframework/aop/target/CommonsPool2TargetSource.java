package org.springframework.aop.target;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/CommonsPool2TargetSource.class */
public class CommonsPool2TargetSource extends AbstractPoolingTargetSource implements PooledObjectFactory<Object> {
    private int maxIdle = 8;
    private int minIdle = 0;
    private long maxWait = -1;
    private long timeBetweenEvictionRunsMillis = -1;
    private long minEvictableIdleTimeMillis = 1800000;
    private boolean blockWhenExhausted = true;

    @Nullable
    private ObjectPool pool;

    public CommonsPool2TargetSource() {
        setMaxSize(8);
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxIdle() {
        return this.maxIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMinIdle() {
        return this.minIdle;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public long getMaxWait() {
        return this.maxWait;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public boolean isBlockWhenExhausted() {
        return this.blockWhenExhausted;
    }

    @Override // org.springframework.aop.target.AbstractPoolingTargetSource
    protected final void createPool() {
        this.logger.debug("Creating Commons object pool");
        this.pool = createObjectPool();
    }

    protected ObjectPool createObjectPool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(getMaxSize());
        config.setMaxIdle(getMaxIdle());
        config.setMinIdle(getMinIdle());
        config.setMaxWaitMillis(getMaxWait());
        config.setTimeBetweenEvictionRunsMillis(getTimeBetweenEvictionRunsMillis());
        config.setMinEvictableIdleTimeMillis(getMinEvictableIdleTimeMillis());
        config.setBlockWhenExhausted(isBlockWhenExhausted());
        return new GenericObjectPool(this, config);
    }

    @Override // org.springframework.aop.target.AbstractPoolingTargetSource, org.springframework.aop.TargetSource
    public Object getTarget() throws Exception {
        Assert.state(this.pool != null, "No Commons ObjectPool available");
        return this.pool.borrowObject();
    }

    @Override // org.springframework.aop.target.AbstractPoolingTargetSource, org.springframework.aop.TargetSource
    public void releaseTarget(Object target) throws Exception {
        if (this.pool != null) {
            this.pool.returnObject(target);
        }
    }

    @Override // org.springframework.aop.target.PoolingConfig
    public int getActiveCount() throws UnsupportedOperationException {
        if (this.pool != null) {
            return this.pool.getNumActive();
        }
        return 0;
    }

    @Override // org.springframework.aop.target.PoolingConfig
    public int getIdleCount() throws UnsupportedOperationException {
        if (this.pool != null) {
            return this.pool.getNumIdle();
        }
        return 0;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        if (this.pool != null) {
            this.logger.debug("Closing Commons ObjectPool");
            this.pool.close();
        }
    }

    public PooledObject<Object> makeObject() throws Exception {
        return new DefaultPooledObject(newPrototypeInstance());
    }

    public void destroyObject(PooledObject<Object> p) throws Exception {
        destroyPrototypeInstance(p.getObject());
    }

    public boolean validateObject(PooledObject<Object> p) {
        return true;
    }

    public void activateObject(PooledObject<Object> p) throws Exception {
    }

    public void passivateObject(PooledObject<Object> p) throws Exception {
    }
}
