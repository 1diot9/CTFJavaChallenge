package org.springframework.web.server.session;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.util.Assert;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/session/InMemoryWebSessionStore.class */
public class InMemoryWebSessionStore implements WebSessionStore {
    private static final IdGenerator idGenerator = new JdkIdGenerator();
    private int maxSessions = 10000;
    private Clock clock = Clock.system(ZoneId.of("GMT"));
    private final Map<String, InMemoryWebSession> sessions = new ConcurrentHashMap();
    private final ExpiredSessionChecker expiredSessionChecker = new ExpiredSessionChecker();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/session/InMemoryWebSessionStore$State.class */
    public enum State {
        NEW,
        STARTED,
        EXPIRED
    }

    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    public int getMaxSessions() {
        return this.maxSessions;
    }

    public void setClock(Clock clock) {
        Assert.notNull(clock, "Clock is required");
        this.clock = clock;
        removeExpiredSessions();
    }

    public Clock getClock() {
        return this.clock;
    }

    public Map<String, WebSession> getSessions() {
        return Collections.unmodifiableMap(this.sessions);
    }

    @Override // org.springframework.web.server.session.WebSessionStore
    public Mono<WebSession> createWebSession() {
        Instant now = this.clock.instant();
        this.expiredSessionChecker.checkIfNecessary(now);
        return Mono.fromSupplier(() -> {
            return new InMemoryWebSession(now);
        }).subscribeOn(Schedulers.boundedElastic()).publishOn(Schedulers.parallel());
    }

    @Override // org.springframework.web.server.session.WebSessionStore
    public Mono<WebSession> retrieveSession(String id) {
        Instant now = this.clock.instant();
        this.expiredSessionChecker.checkIfNecessary(now);
        InMemoryWebSession session = this.sessions.get(id);
        if (session == null) {
            return Mono.empty();
        }
        if (session.isExpired(now)) {
            this.sessions.remove(id);
            return Mono.empty();
        }
        session.updateLastAccessTime(now);
        return Mono.just(session);
    }

    @Override // org.springframework.web.server.session.WebSessionStore
    public Mono<Void> removeSession(String id) {
        this.sessions.remove(id);
        return Mono.empty();
    }

    @Override // org.springframework.web.server.session.WebSessionStore
    public Mono<WebSession> updateLastAccessTime(WebSession session) {
        return Mono.fromSupplier(() -> {
            Assert.isInstanceOf(InMemoryWebSession.class, session);
            ((InMemoryWebSession) session).updateLastAccessTime(this.clock.instant());
            return session;
        });
    }

    public void removeExpiredSessions() {
        this.expiredSessionChecker.removeExpiredSessions(this.clock.instant());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/session/InMemoryWebSessionStore$InMemoryWebSession.class */
    public class InMemoryWebSession implements WebSession {
        private final Instant creationTime;
        private volatile Instant lastAccessTime;
        private final AtomicReference<String> id = new AtomicReference<>(String.valueOf(InMemoryWebSessionStore.idGenerator.generateId()));
        private final Map<String, Object> attributes = new ConcurrentHashMap();
        private volatile Duration maxIdleTime = Duration.ofMinutes(30);
        private final AtomicReference<State> state = new AtomicReference<>(State.NEW);

        public InMemoryWebSession(Instant creationTime) {
            this.creationTime = creationTime;
            this.lastAccessTime = this.creationTime;
        }

        @Override // org.springframework.web.server.WebSession
        public String getId() {
            return this.id.get();
        }

        @Override // org.springframework.web.server.WebSession
        public Map<String, Object> getAttributes() {
            return this.attributes;
        }

        @Override // org.springframework.web.server.WebSession
        public Instant getCreationTime() {
            return this.creationTime;
        }

        @Override // org.springframework.web.server.WebSession
        public Instant getLastAccessTime() {
            return this.lastAccessTime;
        }

        @Override // org.springframework.web.server.WebSession
        public void setMaxIdleTime(Duration maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        @Override // org.springframework.web.server.WebSession
        public Duration getMaxIdleTime() {
            return this.maxIdleTime;
        }

        @Override // org.springframework.web.server.WebSession
        public void start() {
            this.state.compareAndSet(State.NEW, State.STARTED);
        }

        @Override // org.springframework.web.server.WebSession
        public boolean isStarted() {
            return this.state.get().equals(State.STARTED) || !getAttributes().isEmpty();
        }

        @Override // org.springframework.web.server.WebSession
        public Mono<Void> changeSessionId() {
            return Mono.defer(() -> {
                String currentId = this.id.get();
                InMemoryWebSessionStore.this.sessions.remove(currentId);
                String newId = String.valueOf(InMemoryWebSessionStore.idGenerator.generateId());
                this.id.set(newId);
                InMemoryWebSessionStore.this.sessions.put(getId(), this);
                return Mono.empty();
            }).subscribeOn(Schedulers.boundedElastic()).publishOn(Schedulers.parallel()).then();
        }

        @Override // org.springframework.web.server.WebSession
        public Mono<Void> invalidate() {
            this.state.set(State.EXPIRED);
            getAttributes().clear();
            InMemoryWebSessionStore.this.sessions.remove(this.id.get());
            return Mono.empty();
        }

        @Override // org.springframework.web.server.WebSession
        public Mono<Void> save() {
            checkMaxSessionsLimit();
            if (!getAttributes().isEmpty()) {
                this.state.compareAndSet(State.NEW, State.STARTED);
            }
            if (isStarted()) {
                InMemoryWebSessionStore.this.sessions.put(getId(), this);
                if (this.state.get().equals(State.EXPIRED)) {
                    InMemoryWebSessionStore.this.sessions.remove(getId());
                    return Mono.error(new IllegalStateException("Session was invalidated"));
                }
            }
            return Mono.empty();
        }

        private void checkMaxSessionsLimit() {
            if (InMemoryWebSessionStore.this.sessions.size() >= InMemoryWebSessionStore.this.maxSessions) {
                InMemoryWebSessionStore.this.expiredSessionChecker.removeExpiredSessions(InMemoryWebSessionStore.this.clock.instant());
                if (InMemoryWebSessionStore.this.sessions.size() >= InMemoryWebSessionStore.this.maxSessions) {
                    throw new IllegalStateException("Max sessions limit reached: " + InMemoryWebSessionStore.this.sessions.size());
                }
            }
        }

        @Override // org.springframework.web.server.WebSession
        public boolean isExpired() {
            return isExpired(InMemoryWebSessionStore.this.clock.instant());
        }

        private boolean isExpired(Instant now) {
            if (this.state.get().equals(State.EXPIRED)) {
                return true;
            }
            if (checkExpired(now)) {
                this.state.set(State.EXPIRED);
                return true;
            }
            return false;
        }

        private boolean checkExpired(Instant currentTime) {
            return isStarted() && !this.maxIdleTime.isNegative() && currentTime.minus((TemporalAmount) this.maxIdleTime).isAfter(this.lastAccessTime);
        }

        private void updateLastAccessTime(Instant currentTime) {
            this.lastAccessTime = currentTime;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/session/InMemoryWebSessionStore$ExpiredSessionChecker.class */
    public class ExpiredSessionChecker {
        private static final int CHECK_PERIOD = 60000;
        private final ReentrantLock lock = new ReentrantLock();
        private Instant checkTime;

        private ExpiredSessionChecker() {
            this.checkTime = InMemoryWebSessionStore.this.clock.instant().plus(60000L, (TemporalUnit) ChronoUnit.MILLIS);
        }

        public void checkIfNecessary(Instant now) {
            if (this.checkTime.isBefore(now)) {
                removeExpiredSessions(now);
            }
        }

        public void removeExpiredSessions(Instant now) {
            if (!InMemoryWebSessionStore.this.sessions.isEmpty() && this.lock.tryLock()) {
                try {
                    Iterator<InMemoryWebSession> iterator = InMemoryWebSessionStore.this.sessions.values().iterator();
                    while (iterator.hasNext()) {
                        InMemoryWebSession session = iterator.next();
                        if (session.isExpired(now)) {
                            iterator.remove();
                            session.invalidate();
                        }
                    }
                } finally {
                    this.checkTime = now.plus(60000L, (TemporalUnit) ChronoUnit.MILLIS);
                    this.lock.unlock();
                }
            }
        }
    }
}
