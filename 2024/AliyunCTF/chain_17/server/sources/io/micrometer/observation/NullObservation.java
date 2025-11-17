package io.micrometer.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/NullObservation.class */
public class NullObservation extends SimpleObservation {
    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.ObservationView
    public /* bridge */ /* synthetic */ ObservationRegistry getObservationRegistry() {
        return super.getObservationRegistry();
    }

    @Override // io.micrometer.observation.SimpleObservation
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.ObservationView
    @Nullable
    public /* bridge */ /* synthetic */ Observation.Scope getEnclosingScope() {
        return super.getEnclosingScope();
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ Observation.Scope openScope() {
        return super.openScope();
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ void stop() {
        super.stop();
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ Observation.Context getContext() {
        return super.getContext();
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ Observation event(Observation.Event event) {
        return super.event(event);
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ Observation error(Throwable th) {
        return super.error(th);
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ Observation observationConvention(ObservationConvention observationConvention) {
        return super.observationConvention(observationConvention);
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ Observation highCardinalityKeyValue(KeyValue keyValue) {
        return super.highCardinalityKeyValue(keyValue);
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ Observation lowCardinalityKeyValue(KeyValue keyValue) {
        return super.lowCardinalityKeyValue(keyValue);
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ Observation parentObservation(@Nullable Observation observation) {
        return super.parentObservation(observation);
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public /* bridge */ /* synthetic */ Observation contextualName(@Nullable String str) {
        return super.contextualName(str);
    }

    public NullObservation(ObservationRegistry registry) {
        super("null", registry, new Observation.Context());
    }

    @Override // io.micrometer.observation.SimpleObservation
    void notifyOnObservationStarted() {
    }

    @Override // io.micrometer.observation.SimpleObservation
    void notifyOnError() {
    }

    @Override // io.micrometer.observation.SimpleObservation
    void notifyOnEvent(Observation.Event event) {
    }

    @Override // io.micrometer.observation.SimpleObservation
    void notifyOnScopeMakeCurrent() {
    }

    @Override // io.micrometer.observation.SimpleObservation
    void notifyOnScopeReset() {
    }

    @Override // io.micrometer.observation.SimpleObservation
    void notifyOnObservationStopped(Observation.Context context) {
    }

    @Override // io.micrometer.observation.SimpleObservation, io.micrometer.observation.Observation
    public Observation start() {
        return this;
    }
}
