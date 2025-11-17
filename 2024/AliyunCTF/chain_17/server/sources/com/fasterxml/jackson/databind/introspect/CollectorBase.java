package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/CollectorBase.class */
class CollectorBase {
    protected static final AnnotationMap[] NO_ANNOTATION_MAPS = new AnnotationMap[0];
    protected static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
    protected final AnnotationIntrospector _intr;

    /* JADX INFO: Access modifiers changed from: protected */
    public CollectorBase(AnnotationIntrospector intr) {
        this._intr = intr;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final AnnotationCollector collectAnnotations(Annotation[] anns) {
        AnnotationCollector c = AnnotationCollector.emptyCollector();
        for (Annotation ann : anns) {
            c = c.addOrOverride(ann);
            if (this._intr.isAnnotationBundle(ann)) {
                c = collectFromBundle(c, ann);
            }
        }
        return c;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final AnnotationCollector collectAnnotations(AnnotationCollector c, Annotation[] anns) {
        for (Annotation ann : anns) {
            c = c.addOrOverride(ann);
            if (this._intr.isAnnotationBundle(ann)) {
                c = collectFromBundle(c, ann);
            }
        }
        return c;
    }

    protected final AnnotationCollector collectFromBundle(AnnotationCollector c, Annotation bundle) {
        Annotation[] anns = ClassUtil.findClassAnnotations(bundle.annotationType());
        for (Annotation ann : anns) {
            if (!_ignorableAnnotation(ann)) {
                if (this._intr.isAnnotationBundle(ann)) {
                    if (!c.isPresent(ann)) {
                        c = collectFromBundle(c.addOrOverride(ann), ann);
                    }
                } else {
                    c = c.addOrOverride(ann);
                }
            }
        }
        return c;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final AnnotationCollector collectDefaultAnnotations(AnnotationCollector c, Annotation[] anns) {
        for (Annotation ann : anns) {
            if (!c.isPresent(ann)) {
                c = c.addOrOverride(ann);
                if (this._intr.isAnnotationBundle(ann)) {
                    c = collectDefaultFromBundle(c, ann);
                }
            }
        }
        return c;
    }

    protected final AnnotationCollector collectDefaultFromBundle(AnnotationCollector c, Annotation bundle) {
        Annotation[] anns = ClassUtil.findClassAnnotations(bundle.annotationType());
        for (Annotation ann : anns) {
            if (!_ignorableAnnotation(ann) && !c.isPresent(ann)) {
                c = c.addOrOverride(ann);
                if (this._intr.isAnnotationBundle(ann)) {
                    c = collectFromBundle(c, ann);
                }
            }
        }
        return c;
    }

    protected static final boolean _ignorableAnnotation(Annotation a) {
        return (a instanceof Target) || (a instanceof Retention);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AnnotationMap _emptyAnnotationMap() {
        return new AnnotationMap();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AnnotationMap[] _emptyAnnotationMaps(int count) {
        if (count == 0) {
            return NO_ANNOTATION_MAPS;
        }
        AnnotationMap[] maps = new AnnotationMap[count];
        for (int i = 0; i < count; i++) {
            maps[i] = _emptyAnnotationMap();
        }
        return maps;
    }
}
