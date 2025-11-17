package io.micrometer.observation;

import io.micrometer.observation.Observation;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationTextPublisher.class */
public class ObservationTextPublisher implements ObservationHandler<Observation.Context> {
    private final Consumer<String> consumer;
    private final Predicate<Observation.Context> supportsContextPredicate;
    private final Function<Observation.Context, String> converter;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public ObservationTextPublisher() {
        /*
            r5 = this;
            r0 = r5
            java.lang.Class<io.micrometer.observation.ObservationTextPublisher> r1 = io.micrometer.observation.ObservationTextPublisher.class
            io.micrometer.common.util.internal.logging.InternalLogger r1 = io.micrometer.common.util.internal.logging.InternalLoggerFactory.getInstance(r1)
            r2 = r1
            java.lang.Object r2 = java.util.Objects.requireNonNull(r2)
            void r1 = r1::info
            void r2 = (v0) -> { // java.util.function.Predicate.test(java.lang.Object):boolean
                return lambda$new$0(v0);
            }
            void r3 = (v0) -> { // java.util.function.Function.apply(java.lang.Object):java.lang.Object
                return java.lang.String.valueOf(v0);
            }
            r0.<init>(r1, r2, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: io.micrometer.observation.ObservationTextPublisher.<init>():void");
    }

    public ObservationTextPublisher(Consumer<String> consumer) {
        this(consumer, context -> {
            return true;
        }, (v0) -> {
            return String.valueOf(v0);
        });
    }

    public ObservationTextPublisher(Consumer<String> consumer, Predicate<Observation.Context> supportsContextPredicate) {
        this(consumer, supportsContextPredicate, (v0) -> {
            return String.valueOf(v0);
        });
    }

    public ObservationTextPublisher(Consumer<String> consumer, Predicate<Observation.Context> supportsContextPredicate, Function<Observation.Context, String> converter) {
        this.consumer = consumer;
        this.supportsContextPredicate = supportsContextPredicate;
        this.converter = converter;
    }

    @Override // io.micrometer.observation.ObservationHandler
    public void onStart(Observation.Context context) {
        publish("START", context);
    }

    @Override // io.micrometer.observation.ObservationHandler
    public void onError(Observation.Context context) {
        publish("ERROR", context);
    }

    @Override // io.micrometer.observation.ObservationHandler
    public void onEvent(Observation.Event event, Observation.Context context) {
        publishUnformatted(String.format("%5s - %s, %s", "EVENT", event, this.converter.apply(context)));
    }

    @Override // io.micrometer.observation.ObservationHandler
    public void onScopeOpened(Observation.Context context) {
        publish("OPEN", context);
    }

    @Override // io.micrometer.observation.ObservationHandler
    public void onScopeClosed(Observation.Context context) {
        publish("CLOSE", context);
    }

    @Override // io.micrometer.observation.ObservationHandler
    public void onStop(Observation.Context context) {
        publish("STOP", context);
    }

    @Override // io.micrometer.observation.ObservationHandler
    public boolean supportsContext(Observation.Context context) {
        return this.supportsContextPredicate.test(context);
    }

    private void publish(String event, Observation.Context context) {
        publishUnformatted(String.format("%5s - %s", event, this.converter.apply(context)));
    }

    private void publishUnformatted(String event) {
        this.consumer.accept(event);
    }
}
