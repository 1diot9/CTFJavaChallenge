package org.springframework.boot.context.metrics.buffering;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/metrics/buffering/BufferingApplicationStartup.class */
public class BufferingApplicationStartup implements ApplicationStartup {
    private final int capacity;
    private final Clock clock;
    private Instant startTime;
    private final AtomicInteger idSeq;
    private Predicate<StartupStep> filter;
    private final AtomicReference<BufferedStartupStep> current;
    private final AtomicInteger estimatedSize;
    private final ConcurrentLinkedQueue<StartupTimeline.TimelineEvent> events;

    public BufferingApplicationStartup(int capacity) {
        this(capacity, Clock.systemDefaultZone());
    }

    BufferingApplicationStartup(int capacity, Clock clock) {
        this.idSeq = new AtomicInteger();
        this.filter = step -> {
            return true;
        };
        this.current = new AtomicReference<>();
        this.estimatedSize = new AtomicInteger();
        this.events = new ConcurrentLinkedQueue<>();
        this.capacity = capacity;
        this.clock = clock;
        this.startTime = clock.instant();
    }

    public void startRecording() {
        Assert.state(this.events.isEmpty(), "Cannot restart recording once steps have been buffered.");
        this.startTime = this.clock.instant();
    }

    public void addFilter(Predicate<StartupStep> filter) {
        this.filter = this.filter.and(filter);
    }

    @Override // org.springframework.core.metrics.ApplicationStartup
    public StartupStep start(String name) {
        BufferedStartupStep current;
        BufferedStartupStep next;
        int id = this.idSeq.getAndIncrement();
        Instant start = this.clock.instant();
        do {
            current = this.current.get();
            BufferedStartupStep parent = getLatestActive(current);
            next = new BufferedStartupStep(parent, name, id, start, this::record);
        } while (!this.current.compareAndSet(current, next));
        return next;
    }

    private void record(BufferedStartupStep step) {
        BufferedStartupStep current;
        BufferedStartupStep next;
        if (this.filter.test(step) && this.estimatedSize.get() < this.capacity) {
            this.estimatedSize.incrementAndGet();
            this.events.add(new StartupTimeline.TimelineEvent(step, this.clock.instant()));
        }
        do {
            current = this.current.get();
            next = getLatestActive(current);
        } while (!this.current.compareAndSet(current, next));
    }

    private BufferedStartupStep getLatestActive(BufferedStartupStep step) {
        while (step != null && step.isEnded()) {
            step = step.getParent();
        }
        return step;
    }

    public StartupTimeline getBufferedTimeline() {
        return new StartupTimeline(this.startTime, new ArrayList(this.events));
    }

    public StartupTimeline drainBufferedTimeline() {
        List<StartupTimeline.TimelineEvent> events = new ArrayList<>();
        Iterator<StartupTimeline.TimelineEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            events.add(iterator.next());
            iterator.remove();
        }
        this.estimatedSize.set(0);
        return new StartupTimeline(this.startTime, events);
    }
}
