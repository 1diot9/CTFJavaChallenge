package org.springframework.boot.context.metrics.buffering;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.core.metrics.StartupStep;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/metrics/buffering/BufferedStartupStep.class */
class BufferedStartupStep implements StartupStep {
    private final String name;
    private final long id;
    private final BufferedStartupStep parent;
    private final Consumer<BufferedStartupStep> recorder;
    private final Instant startTime;
    private final List<StartupStep.Tag> tags = new ArrayList();
    private final AtomicBoolean ended = new AtomicBoolean();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BufferedStartupStep(BufferedStartupStep parent, String name, long id, Instant startTime, Consumer<BufferedStartupStep> recorder) {
        this.parent = parent;
        this.name = name;
        this.id = id;
        this.startTime = startTime;
        this.recorder = recorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BufferedStartupStep getParent() {
        return this.parent;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public String getName() {
        return this.name;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public long getId() {
        return this.id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Instant getStartTime() {
        return this.startTime;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public Long getParentId() {
        if (this.parent != null) {
            return Long.valueOf(this.parent.getId());
        }
        return null;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public StartupStep.Tags getTags() {
        List unmodifiableList = Collections.unmodifiableList(this.tags);
        Objects.requireNonNull(unmodifiableList);
        return unmodifiableList::iterator;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public StartupStep tag(String key, Supplier<String> value) {
        return tag(key, value.get());
    }

    @Override // org.springframework.core.metrics.StartupStep
    public StartupStep tag(String key, String value) {
        Assert.state(!this.ended.get(), "StartupStep has already ended.");
        this.tags.add(new DefaultTag(key, value));
        return this;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public void end() {
        this.ended.set(true);
        this.recorder.accept(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEnded() {
        return this.ended.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/metrics/buffering/BufferedStartupStep$DefaultTag.class */
    public static class DefaultTag implements StartupStep.Tag {
        private final String key;
        private final String value;

        DefaultTag(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override // org.springframework.core.metrics.StartupStep.Tag
        public String getKey() {
            return this.key;
        }

        @Override // org.springframework.core.metrics.StartupStep.Tag
        public String getValue() {
            return this.value;
        }
    }
}
