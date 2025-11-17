package org.springframework.core.metrics.jfr;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.NonNull;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/jfr/FlightRecorderStartupStep.class */
class FlightRecorderStartupStep implements StartupStep {
    private final FlightRecorderStartupEvent event;
    private final FlightRecorderTags tags = new FlightRecorderTags();
    private final Consumer<FlightRecorderStartupStep> recordingCallback;

    public FlightRecorderStartupStep(long id, String name, long parentId, Consumer<FlightRecorderStartupStep> recordingCallback) {
        this.event = new FlightRecorderStartupEvent(id, name, parentId);
        this.event.begin();
        this.recordingCallback = recordingCallback;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public String getName() {
        return this.event.name;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public long getId() {
        return this.event.eventId;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public Long getParentId() {
        return Long.valueOf(this.event.parentId);
    }

    @Override // org.springframework.core.metrics.StartupStep
    public StartupStep tag(String key, String value) {
        this.tags.add(key, value);
        return this;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public StartupStep tag(String key, Supplier<String> value) {
        this.tags.add(key, value.get());
        return this;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public StartupStep.Tags getTags() {
        return this.tags;
    }

    @Override // org.springframework.core.metrics.StartupStep
    public void end() {
        this.event.end();
        if (this.event.shouldCommit()) {
            StringBuilder builder = new StringBuilder();
            this.tags.forEach(tag -> {
                builder.append(tag.getKey()).append('=').append(tag.getValue()).append(',');
            });
            this.event.setTags(builder.toString());
        }
        this.event.commit();
        this.recordingCallback.accept(this);
    }

    protected FlightRecorderStartupEvent getEvent() {
        return this.event;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/jfr/FlightRecorderStartupStep$FlightRecorderTags.class */
    public static class FlightRecorderTags implements StartupStep.Tags {
        private StartupStep.Tag[] tags = new StartupStep.Tag[0];

        FlightRecorderTags() {
        }

        public void add(String key, String value) {
            StartupStep.Tag[] newTags = new StartupStep.Tag[this.tags.length + 1];
            System.arraycopy(this.tags, 0, newTags, 0, this.tags.length);
            newTags[newTags.length - 1] = new FlightRecorderTag(key, value);
            this.tags = newTags;
        }

        public void add(String key, Supplier<String> value) {
            add(key, value.get());
        }

        @Override // java.lang.Iterable
        @NonNull
        public Iterator<StartupStep.Tag> iterator() {
            return new TagsIterator();
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/jfr/FlightRecorderStartupStep$FlightRecorderTags$TagsIterator.class */
        private class TagsIterator implements Iterator<StartupStep.Tag> {
            private int idx = 0;

            private TagsIterator() {
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.idx < FlightRecorderTags.this.tags.length;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public StartupStep.Tag next() {
                StartupStep.Tag[] tagArr = FlightRecorderTags.this.tags;
                int i = this.idx;
                this.idx = i + 1;
                return tagArr[i];
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException("tags are append only");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/metrics/jfr/FlightRecorderStartupStep$FlightRecorderTag.class */
    public static class FlightRecorderTag implements StartupStep.Tag {
        private final String key;
        private final String value;

        public FlightRecorderTag(String key, String value) {
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
