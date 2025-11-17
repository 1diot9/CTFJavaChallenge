package cn.hutool.core.annotation;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.util.ObjectUtil;
import java.util.Collection;
import java.util.Comparator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/CacheableSynthesizedAnnotationAttributeProcessor.class */
public class CacheableSynthesizedAnnotationAttributeProcessor implements SynthesizedAnnotationAttributeProcessor {
    private final Table<String, Class<?>, Object> valueCaches;
    private final Comparator<Hierarchical> annotationComparator;

    public CacheableSynthesizedAnnotationAttributeProcessor(Comparator<Hierarchical> annotationComparator) {
        this.valueCaches = new RowKeyTable();
        Assert.notNull(annotationComparator, "annotationComparator must not null", new Object[0]);
        this.annotationComparator = annotationComparator;
    }

    public CacheableSynthesizedAnnotationAttributeProcessor() {
        this(Hierarchical.DEFAULT_HIERARCHICAL_COMPARATOR);
    }

    @Override // cn.hutool.core.annotation.SynthesizedAnnotationAttributeProcessor
    public <T> T getAttributeValue(String str, Class<T> cls, Collection<? extends SynthesizedAnnotation> collection) {
        T t = (T) this.valueCaches.get(str, cls);
        if (ObjectUtil.isNotNull(t)) {
            return t;
        }
        T t2 = (T) collection.stream().filter(ma -> {
            return ma.hasAttribute(str, cls);
        }).min(this.annotationComparator).map(ma2 -> {
            return ma2.getAttributeValue(str);
        }).orElse(null);
        this.valueCaches.put(str, cls, t2);
        return t2;
    }
}
