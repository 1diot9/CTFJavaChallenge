package org.apache.catalina.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/LifecycleBase.class */
public abstract class LifecycleBase implements Lifecycle {
    private static final Log log = LogFactory.getLog((Class<?>) LifecycleBase.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) LifecycleBase.class);
    private final List<LifecycleListener> lifecycleListeners = new CopyOnWriteArrayList();
    private volatile LifecycleState state = LifecycleState.NEW;
    private boolean throwOnFailure = true;

    protected abstract void initInternal() throws LifecycleException;

    protected abstract void startInternal() throws LifecycleException;

    protected abstract void stopInternal() throws LifecycleException;

    protected abstract void destroyInternal() throws LifecycleException;

    public boolean getThrowOnFailure() {
        return this.throwOnFailure;
    }

    public void setThrowOnFailure(boolean throwOnFailure) {
        this.throwOnFailure = throwOnFailure;
    }

    @Override // org.apache.catalina.Lifecycle
    public void addLifecycleListener(LifecycleListener listener) {
        this.lifecycleListeners.add(listener);
    }

    @Override // org.apache.catalina.Lifecycle
    public LifecycleListener[] findLifecycleListeners() {
        return (LifecycleListener[]) this.lifecycleListeners.toArray(new LifecycleListener[0]);
    }

    @Override // org.apache.catalina.Lifecycle
    public void removeLifecycleListener(LifecycleListener listener) {
        this.lifecycleListeners.remove(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(this, type, data);
        for (LifecycleListener listener : this.lifecycleListeners) {
            listener.lifecycleEvent(event);
        }
    }

    @Override // org.apache.catalina.Lifecycle
    public final synchronized void init() throws LifecycleException {
        if (!this.state.equals(LifecycleState.NEW)) {
            invalidTransition(Lifecycle.BEFORE_INIT_EVENT);
        }
        try {
            setStateInternal(LifecycleState.INITIALIZING, null, false);
            initInternal();
            setStateInternal(LifecycleState.INITIALIZED, null, false);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.initFail", toString());
        }
    }

    @Override // org.apache.catalina.Lifecycle
    public final synchronized void start() throws LifecycleException {
        if (LifecycleState.STARTING_PREP.equals(this.state) || LifecycleState.STARTING.equals(this.state) || LifecycleState.STARTED.equals(this.state)) {
            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyStarted", toString()), e);
                return;
            } else {
                if (log.isInfoEnabled()) {
                    log.info(sm.getString("lifecycleBase.alreadyStarted", toString()));
                    return;
                }
                return;
            }
        }
        if (this.state.equals(LifecycleState.NEW)) {
            init();
        } else if (this.state.equals(LifecycleState.FAILED)) {
            stop();
        } else if (!this.state.equals(LifecycleState.INITIALIZED) && !this.state.equals(LifecycleState.STOPPED)) {
            invalidTransition(Lifecycle.BEFORE_START_EVENT);
        }
        try {
            setStateInternal(LifecycleState.STARTING_PREP, null, false);
            startInternal();
            if (this.state.equals(LifecycleState.FAILED)) {
                stop();
            } else if (!this.state.equals(LifecycleState.STARTING)) {
                invalidTransition(Lifecycle.AFTER_START_EVENT);
            } else {
                setStateInternal(LifecycleState.STARTED, null, false);
            }
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.startFail", toString());
        }
    }

    @Override // org.apache.catalina.Lifecycle
    public final synchronized void stop() throws LifecycleException {
        if (LifecycleState.STOPPING_PREP.equals(this.state) || LifecycleState.STOPPING.equals(this.state) || LifecycleState.STOPPED.equals(this.state)) {
            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyStopped", toString()), e);
                return;
            } else {
                if (log.isInfoEnabled()) {
                    log.info(sm.getString("lifecycleBase.alreadyStopped", toString()));
                    return;
                }
                return;
            }
        }
        if (this.state.equals(LifecycleState.NEW)) {
            this.state = LifecycleState.STOPPED;
            return;
        }
        if (!this.state.equals(LifecycleState.STARTED) && !this.state.equals(LifecycleState.FAILED)) {
            invalidTransition(Lifecycle.BEFORE_STOP_EVENT);
        }
        try {
            try {
                if (this.state.equals(LifecycleState.FAILED)) {
                    fireLifecycleEvent(Lifecycle.BEFORE_STOP_EVENT, null);
                } else {
                    setStateInternal(LifecycleState.STOPPING_PREP, null, false);
                }
                stopInternal();
                if (!this.state.equals(LifecycleState.STOPPING) && !this.state.equals(LifecycleState.FAILED)) {
                    invalidTransition(Lifecycle.AFTER_STOP_EVENT);
                }
                setStateInternal(LifecycleState.STOPPED, null, false);
                if (this instanceof Lifecycle.SingleUse) {
                    setStateInternal(LifecycleState.STOPPED, null, false);
                    destroy();
                }
            } catch (Throwable t) {
                handleSubClassException(t, "lifecycleBase.stopFail", toString());
                if (this instanceof Lifecycle.SingleUse) {
                    setStateInternal(LifecycleState.STOPPED, null, false);
                    destroy();
                }
            }
        } catch (Throwable th) {
            if (this instanceof Lifecycle.SingleUse) {
                setStateInternal(LifecycleState.STOPPED, null, false);
                destroy();
            }
            throw th;
        }
    }

    @Override // org.apache.catalina.Lifecycle
    public final synchronized void destroy() throws LifecycleException {
        if (LifecycleState.FAILED.equals(this.state)) {
            try {
                stop();
            } catch (LifecycleException e) {
                log.error(sm.getString("lifecycleBase.destroyStopFail", toString()), e);
            }
        }
        if (LifecycleState.DESTROYING.equals(this.state) || LifecycleState.DESTROYED.equals(this.state)) {
            if (log.isDebugEnabled()) {
                Exception e2 = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyDestroyed", toString()), e2);
                return;
            } else {
                if (log.isInfoEnabled() && !(this instanceof Lifecycle.SingleUse)) {
                    log.info(sm.getString("lifecycleBase.alreadyDestroyed", toString()));
                    return;
                }
                return;
            }
        }
        if (!this.state.equals(LifecycleState.STOPPED) && !this.state.equals(LifecycleState.FAILED) && !this.state.equals(LifecycleState.NEW) && !this.state.equals(LifecycleState.INITIALIZED)) {
            invalidTransition(Lifecycle.BEFORE_DESTROY_EVENT);
        }
        try {
            setStateInternal(LifecycleState.DESTROYING, null, false);
            destroyInternal();
            setStateInternal(LifecycleState.DESTROYED, null, false);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.destroyFail", toString());
        }
    }

    @Override // org.apache.catalina.Lifecycle
    public LifecycleState getState() {
        return this.state;
    }

    @Override // org.apache.catalina.Lifecycle
    public String getStateName() {
        return getState().toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void setState(LifecycleState state) throws LifecycleException {
        setStateInternal(state, null, true);
    }

    protected synchronized void setState(LifecycleState state, Object data) throws LifecycleException {
        setStateInternal(state, data, true);
    }

    private synchronized void setStateInternal(LifecycleState state, Object data, boolean check) throws LifecycleException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("lifecycleBase.setState", this, state));
        }
        if (check) {
            if (state == null) {
                invalidTransition("null");
                return;
            }
            if (state != LifecycleState.FAILED && ((this.state != LifecycleState.STARTING_PREP || state != LifecycleState.STARTING) && ((this.state != LifecycleState.STOPPING_PREP || state != LifecycleState.STOPPING) && (this.state != LifecycleState.FAILED || state != LifecycleState.STOPPING)))) {
                invalidTransition(state.name());
            }
        }
        this.state = state;
        String lifecycleEvent = state.getLifecycleEvent();
        if (lifecycleEvent != null) {
            fireLifecycleEvent(lifecycleEvent, data);
        }
    }

    private void invalidTransition(String type) throws LifecycleException {
        String msg = sm.getString("lifecycleBase.invalidTransition", type, toString(), this.state);
        throw new LifecycleException(msg);
    }

    private void handleSubClassException(Throwable t, String key, Object... args) throws LifecycleException {
        setStateInternal(LifecycleState.FAILED, null, false);
        ExceptionUtils.handleThrowable(t);
        String msg = sm.getString(key, args);
        if (getThrowOnFailure()) {
            if (!(t instanceof LifecycleException)) {
                t = new LifecycleException(msg, t);
            }
            throw ((LifecycleException) t);
        }
        log.error(msg, t);
    }
}
