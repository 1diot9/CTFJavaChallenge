package com.fasterxml.jackson.databind.util;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ViewMatcher.class */
public class ViewMatcher implements Serializable {
    private static final long serialVersionUID = 1;
    protected static final ViewMatcher EMPTY = new ViewMatcher();

    public boolean isVisibleForView(Class<?> activeView) {
        return false;
    }

    public static ViewMatcher construct(Class<?>[] views) {
        if (views == null) {
            return EMPTY;
        }
        switch (views.length) {
            case 0:
                return EMPTY;
            case 1:
                return new Single(views[0]);
            default:
                return new Multi(views);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ViewMatcher$Single.class */
    private static final class Single extends ViewMatcher {
        private static final long serialVersionUID = 1;
        private final Class<?> _view;

        public Single(Class<?> v) {
            this._view = v;
        }

        @Override // com.fasterxml.jackson.databind.util.ViewMatcher
        public boolean isVisibleForView(Class<?> activeView) {
            return activeView == this._view || this._view.isAssignableFrom(activeView);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ViewMatcher$Multi.class */
    private static final class Multi extends ViewMatcher implements Serializable {
        private static final long serialVersionUID = 1;
        private final Class<?>[] _views;

        public Multi(Class<?>[] v) {
            this._views = v;
        }

        @Override // com.fasterxml.jackson.databind.util.ViewMatcher
        public boolean isVisibleForView(Class<?> activeView) {
            int len = this._views.length;
            for (int i = 0; i < len; i++) {
                Class<?> view = this._views[i];
                if (activeView == view || view.isAssignableFrom(activeView)) {
                    return true;
                }
            }
            return false;
        }
    }
}
