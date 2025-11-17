package io.micrometer.observation;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.Observation.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationHandler.class */
public interface ObservationHandler<T extends Observation.Context> {

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationHandler$CompositeObservationHandler.class */
    public interface CompositeObservationHandler extends ObservationHandler<Observation.Context> {
        List<ObservationHandler<Observation.Context>> getHandlers();
    }

    boolean supportsContext(Observation.Context context);

    default void onStart(T context) {
    }

    default void onError(T context) {
    }

    default void onEvent(Observation.Event event, T context) {
    }

    default void onScopeOpened(T context) {
    }

    default void onScopeClosed(T context) {
    }

    default void onScopeReset(T context) {
    }

    default void onStop(T context) {
    }

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationHandler$FirstMatchingCompositeObservationHandler.class */
    public static class FirstMatchingCompositeObservationHandler implements CompositeObservationHandler {
        private final List<ObservationHandler<Observation.Context>> handlers;

        @SafeVarargs
        public FirstMatchingCompositeObservationHandler(ObservationHandler<? extends Observation.Context>... handlers) {
            this((List<? extends ObservationHandler<? extends Observation.Context>>) Arrays.asList(handlers));
        }

        public FirstMatchingCompositeObservationHandler(List<? extends ObservationHandler<? extends Observation.Context>> handlers) {
            ArrayList arrayList = new ArrayList(handlers.size());
            for (ObservationHandler<? extends Observation.Context> handler : handlers) {
                arrayList.add(handler);
            }
            this.handlers = arrayList;
        }

        @Override // io.micrometer.observation.ObservationHandler.CompositeObservationHandler
        public List<ObservationHandler<Observation.Context>> getHandlers() {
            return this.handlers;
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onStart(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onStart(context);
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onError(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onError(context);
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onEvent(Observation.Event event, Observation.Context context) {
            ObservationHandler<Observation.Context> handler = getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onEvent(event, context);
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onScopeOpened(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onScopeOpened(context);
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onScopeClosed(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onScopeClosed(context);
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onScopeReset(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onScopeReset(context);
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onStop(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onStop(context);
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public boolean supportsContext(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = getFirstApplicableHandler(context);
            return handler != null;
        }

        @Nullable
        private ObservationHandler<Observation.Context> getFirstApplicableHandler(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (handler.supportsContext(context)) {
                    return handler;
                }
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/ObservationHandler$AllMatchingCompositeObservationHandler.class */
    public static class AllMatchingCompositeObservationHandler implements CompositeObservationHandler {
        private final List<ObservationHandler<Observation.Context>> handlers;

        @SafeVarargs
        public AllMatchingCompositeObservationHandler(ObservationHandler<? extends Observation.Context>... handlers) {
            this((List<? extends ObservationHandler<? extends Observation.Context>>) Arrays.asList(handlers));
        }

        public AllMatchingCompositeObservationHandler(List<? extends ObservationHandler<? extends Observation.Context>> handlers) {
            ArrayList arrayList = new ArrayList(handlers.size());
            for (ObservationHandler<? extends Observation.Context> handler : handlers) {
                arrayList.add(handler);
            }
            this.handlers = arrayList;
        }

        @Override // io.micrometer.observation.ObservationHandler.CompositeObservationHandler
        public List<ObservationHandler<Observation.Context>> getHandlers() {
            return this.handlers;
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onStart(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (handler.supportsContext(context)) {
                    handler.onStart(context);
                }
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onError(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (handler.supportsContext(context)) {
                    handler.onError(context);
                }
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onEvent(Observation.Event event, Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (handler.supportsContext(context)) {
                    handler.onEvent(event, context);
                }
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onScopeOpened(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (handler.supportsContext(context)) {
                    handler.onScopeOpened(context);
                }
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onScopeClosed(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (handler.supportsContext(context)) {
                    handler.onScopeClosed(context);
                }
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onScopeReset(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (handler.supportsContext(context)) {
                    handler.onScopeReset(context);
                }
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public void onStop(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (handler.supportsContext(context)) {
                    handler.onStop(context);
                }
            }
        }

        @Override // io.micrometer.observation.ObservationHandler
        public boolean supportsContext(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (handler.supportsContext(context)) {
                    return true;
                }
            }
            return false;
        }
    }
}
