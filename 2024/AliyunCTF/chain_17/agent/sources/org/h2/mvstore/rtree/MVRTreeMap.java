package org.h2.mvstore.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.h2.mvstore.CursorPos;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.Page;
import org.h2.mvstore.RootReference;
import org.h2.mvstore.type.DataType;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/rtree/MVRTreeMap.class */
public final class MVRTreeMap<V> extends MVMap<Spatial, V> {
    private final SpatialDataType keyType;
    private boolean quadraticSplit;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.h2.mvstore.MVMap
    public /* bridge */ /* synthetic */ Object operate(Spatial spatial, Object obj, MVMap.DecisionMaker decisionMaker) {
        return operate2(spatial, (Spatial) obj, (MVMap.DecisionMaker<? super Spatial>) decisionMaker);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.h2.mvstore.MVMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ Object put(Object obj, Object obj2) {
        return put((Spatial) obj, (Spatial) obj2);
    }

    public MVRTreeMap(Map<String, Object> map, SpatialDataType spatialDataType, DataType<V> dataType) {
        super(map, spatialDataType, dataType);
        this.keyType = spatialDataType;
        this.quadraticSplit = Boolean.parseBoolean(String.valueOf(map.get("quadraticSplit")));
    }

    private MVRTreeMap(MVRTreeMap<V> mVRTreeMap) {
        super(mVRTreeMap);
        this.keyType = mVRTreeMap.keyType;
        this.quadraticSplit = mVRTreeMap.quadraticSplit;
    }

    @Override // org.h2.mvstore.MVMap
    public MVRTreeMap<V> cloneIt() {
        return new MVRTreeMap<>(this);
    }

    public RTreeCursor<V> findIntersectingKeys(Spatial spatial) {
        return new IntersectsRTreeCursor(getRootPage(), spatial, this.keyType);
    }

    public RTreeCursor<V> findContainedKeys(Spatial spatial) {
        return new ContainsRTreeCursor(getRootPage(), spatial, this.keyType);
    }

    private boolean contains(Page<Spatial, V> page, int i, Spatial spatial) {
        return this.keyType.contains(page.getKey(i), spatial);
    }

    @Override // org.h2.mvstore.MVMap
    public V get(Page<Spatial, V> page, Spatial spatial) {
        V v;
        int keyCount = page.getKeyCount();
        if (!page.isLeaf()) {
            for (int i = 0; i < keyCount; i++) {
                if (contains(page, i, spatial) && (v = get((Page) page.getChildPage(i), spatial)) != null) {
                    return v;
                }
            }
            return null;
        }
        for (int i2 = 0; i2 < keyCount; i2++) {
            if (this.keyType.equals(page.getKey(i2), spatial)) {
                return page.getValue(i2);
            }
        }
        return null;
    }

    @Override // org.h2.mvstore.MVMap, java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        return operate2((Spatial) obj, (Spatial) null, (MVMap.DecisionMaker<? super Spatial>) MVMap.DecisionMaker.REMOVE);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: operate, reason: avoid collision after fix types in other method */
    public V operate2(Spatial spatial, V v, MVMap.DecisionMaker<? super V> decisionMaker) {
        int i = 0;
        ArrayList<Page> arrayList = isPersistent() ? new ArrayList() : null;
        while (true) {
            RootReference flushAndGetRoot = flushAndGetRoot();
            int i2 = i;
            i++;
            if (i2 == 0 && !flushAndGetRoot.isLockedByCurrentThread()) {
                beforeWrite();
            }
            Page<K, V> page = flushAndGetRoot.root;
            if (arrayList != null && page.getTotalCount() > 0) {
                arrayList.add(page);
            }
            Page copy = page.copy();
            V v2 = (V) operate(copy, spatial, v, decisionMaker, arrayList);
            if (!copy.isLeaf() && copy.getTotalCount() == 0) {
                if (arrayList != null) {
                    arrayList.add(copy);
                }
                copy = createEmptyLeaf();
            } else if (copy.getKeyCount() > this.store.getKeysPerPage() || (copy.getMemory() > this.store.getMaxPageSize() && copy.getKeyCount() > 3)) {
                long totalCount = copy.getTotalCount();
                Page split = split(copy);
                Spatial bounds = getBounds(copy);
                Spatial bounds2 = getBounds(split);
                Spatial[] spatialArr = (Spatial[]) copy.createKeyStorage(2);
                spatialArr[0] = bounds;
                spatialArr[1] = bounds2;
                Page.PageReference[] createRefStorage = Page.createRefStorage(3);
                createRefStorage[0] = new Page.PageReference(copy);
                createRefStorage[1] = new Page.PageReference(split);
                createRefStorage[2] = Page.PageReference.empty();
                copy = Page.createNode(this, spatialArr, createRefStorage, totalCount, 0);
                registerUnsavedMemory(copy.getMemory());
            }
            if (arrayList == null) {
                if (updateRoot(flushAndGetRoot, copy, i)) {
                    return v2;
                }
            } else {
                RootReference tryLock = tryLock(flushAndGetRoot, i);
                if (tryLock != null) {
                    try {
                        long j = tryLock.version;
                        int i3 = 0;
                        for (Page page2 : arrayList) {
                            if (!page2.isRemoved()) {
                                i3 += page2.removePage(j);
                            }
                        }
                        registerUnsavedMemory(i3);
                        unlockRoot(copy);
                        return v2;
                    } catch (Throwable th) {
                        unlockRoot(copy);
                        throw th;
                    }
                }
                arrayList.clear();
            }
            decisionMaker.reset();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v3, types: [V, java.lang.Object] */
    private V operate(Page<Spatial, V> page, Spatial spatial, V v, MVMap.DecisionMaker<? super V> decisionMaker, Collection<Page<Spatial, V>> collection) {
        V operate;
        if (page.isLeaf()) {
            int i = -1;
            int keyCount = page.getKeyCount();
            for (int i2 = 0; i2 < keyCount; i2++) {
                if (this.keyType.equals((Spatial) page.getKey(i2), spatial)) {
                    i = i2;
                }
            }
            ?? r13 = (Object) (i < 0 ? null : page.getValue(i));
            switch (decisionMaker.decide(r13, v)) {
                case REMOVE:
                    if (i >= 0) {
                        page.remove(i);
                        break;
                    }
                    break;
                case PUT:
                    Object selectValue = decisionMaker.selectValue(r13, v);
                    if (i < 0) {
                        page.insertLeaf(page.getKeyCount(), spatial, selectValue);
                        break;
                    } else {
                        page.setKey(i, spatial);
                        page.setValue(i, selectValue);
                        break;
                    }
            }
            return r13;
        }
        int i3 = -1;
        int i4 = 0;
        while (true) {
            if (i4 >= page.getKeyCount()) {
                break;
            }
            if (contains(page, i4, spatial)) {
                if (get((Page) page.getChildPage(i4), spatial) != null) {
                    i3 = i4;
                    break;
                }
                if (i3 < 0) {
                    i3 = i4;
                }
            }
            i4++;
        }
        if (i3 < 0) {
            float f = Float.MAX_VALUE;
            for (int i5 = 0; i5 < page.getKeyCount(); i5++) {
                float areaIncrease = this.keyType.getAreaIncrease((Spatial) page.getKey(i5), spatial);
                if (areaIncrease < f) {
                    i3 = i5;
                    f = areaIncrease;
                }
            }
        }
        Page<Spatial, V> childPage = page.getChildPage(i3);
        if (collection != null) {
            collection.add(childPage);
        }
        Page<Spatial, V> copy = childPage.copy();
        if (copy.getKeyCount() > this.store.getKeysPerPage() || (copy.getMemory() > this.store.getMaxPageSize() && copy.getKeyCount() > 4)) {
            Page<Spatial, V> split = split(copy);
            page.setKey(i3, getBounds(copy));
            page.setChild(i3, copy);
            page.insertNode(i3, getBounds(split), split);
            operate = operate(page, spatial, v, decisionMaker, collection);
        } else {
            operate = operate(copy, spatial, v, decisionMaker, collection);
            Spatial spatial2 = (Spatial) page.getKey(i3);
            if (!this.keyType.contains(spatial2, spatial)) {
                Spatial createBoundingBox = this.keyType.createBoundingBox(spatial2);
                this.keyType.increaseBounds(createBoundingBox, spatial);
                page.setKey(i3, createBoundingBox);
            }
            if (copy.getTotalCount() > 0) {
                page.setChild(i3, copy);
            } else {
                page.remove(i3);
            }
        }
        return operate;
    }

    private Spatial getBounds(Page<Spatial, V> page) {
        Spatial createBoundingBox = this.keyType.createBoundingBox(page.getKey(0));
        int keyCount = page.getKeyCount();
        for (int i = 1; i < keyCount; i++) {
            this.keyType.increaseBounds(createBoundingBox, page.getKey(i));
        }
        return createBoundingBox;
    }

    public V put(Spatial spatial, V v) {
        return operate2(spatial, (Spatial) v, (MVMap.DecisionMaker<? super Spatial>) MVMap.DecisionMaker.PUT);
    }

    public void add(Spatial spatial, V v) {
        operate2(spatial, (Spatial) v, (MVMap.DecisionMaker<? super Spatial>) MVMap.DecisionMaker.PUT);
    }

    private Page<Spatial, V> split(Page<Spatial, V> page) {
        if (this.quadraticSplit) {
            return splitQuadratic(page);
        }
        return splitLinear(page);
    }

    private Page<Spatial, V> splitLinear(Page<Spatial, V> page) {
        int keyCount = page.getKeyCount();
        ArrayList<Spatial> arrayList = new ArrayList<>(keyCount);
        for (int i = 0; i < keyCount; i++) {
            arrayList.add(page.getKey(i));
        }
        int[] extremes = this.keyType.getExtremes(arrayList);
        if (extremes == null) {
            return splitQuadratic(page);
        }
        Page<Spatial, V> newPage = newPage(page.isLeaf());
        Page<Spatial, V> newPage2 = newPage(page.isLeaf());
        move(page, newPage, extremes[0]);
        if (extremes[1] > extremes[0]) {
            extremes[1] = extremes[1] - 1;
        }
        move(page, newPage2, extremes[1]);
        Spatial createBoundingBox = this.keyType.createBoundingBox(newPage.getKey(0));
        Spatial createBoundingBox2 = this.keyType.createBoundingBox(newPage2.getKey(0));
        while (page.getKeyCount() > 0) {
            Spatial key = page.getKey(0);
            if (this.keyType.getAreaIncrease(createBoundingBox, key) < this.keyType.getAreaIncrease(createBoundingBox2, key)) {
                this.keyType.increaseBounds(createBoundingBox, key);
                move(page, newPage, 0);
            } else {
                this.keyType.increaseBounds(createBoundingBox2, key);
                move(page, newPage2, 0);
            }
        }
        while (newPage2.getKeyCount() > 0) {
            move(newPage2, page, 0);
        }
        return newPage;
    }

    private Page<Spatial, V> splitQuadratic(Page<Spatial, V> page) {
        Page<Spatial, V> newPage = newPage(page.isLeaf());
        Page<Spatial, V> newPage2 = newPage(page.isLeaf());
        float f = Float.MIN_VALUE;
        int i = 0;
        int i2 = 0;
        int keyCount = page.getKeyCount();
        for (int i3 = 0; i3 < keyCount; i3++) {
            Spatial key = page.getKey(i3);
            for (int i4 = 0; i4 < keyCount; i4++) {
                if (i3 != i4) {
                    float combinedArea = this.keyType.getCombinedArea(key, page.getKey(i4));
                    if (combinedArea > f) {
                        f = combinedArea;
                        i = i3;
                        i2 = i4;
                    }
                }
            }
        }
        move(page, newPage, i);
        if (i < i2) {
            i2--;
        }
        move(page, newPage2, i2);
        Spatial createBoundingBox = this.keyType.createBoundingBox(newPage.getKey(0));
        Spatial createBoundingBox2 = this.keyType.createBoundingBox(newPage2.getKey(0));
        while (page.getKeyCount() > 0) {
            float f2 = 0.0f;
            float f3 = 0.0f;
            float f4 = 0.0f;
            int i5 = 0;
            int keyCount2 = page.getKeyCount();
            for (int i6 = 0; i6 < keyCount2; i6++) {
                Spatial key2 = page.getKey(i6);
                float areaIncrease = this.keyType.getAreaIncrease(createBoundingBox, key2);
                float areaIncrease2 = this.keyType.getAreaIncrease(createBoundingBox2, key2);
                float abs = Math.abs(areaIncrease - areaIncrease2);
                if (abs > f2) {
                    f2 = abs;
                    f3 = areaIncrease;
                    f4 = areaIncrease2;
                    i5 = i6;
                }
            }
            if (f3 < f4) {
                this.keyType.increaseBounds(createBoundingBox, page.getKey(i5));
                move(page, newPage, i5);
            } else {
                this.keyType.increaseBounds(createBoundingBox2, page.getKey(i5));
                move(page, newPage2, i5);
            }
        }
        while (newPage2.getKeyCount() > 0) {
            move(newPage2, page, 0);
        }
        return newPage;
    }

    private Page<Spatial, V> newPage(boolean z) {
        Page<Spatial, V> createEmptyLeaf = z ? createEmptyLeaf() : createEmptyNode();
        registerUnsavedMemory(createEmptyLeaf.getMemory());
        return createEmptyLeaf;
    }

    private static <V> void move(Page<Spatial, V> page, Page<Spatial, V> page2, int i) {
        Spatial key = page.getKey(i);
        if (page.isLeaf()) {
            page2.insertLeaf(0, key, page.getValue(i));
        } else {
            page2.insertNode(0, key, page.getChildPage(i));
        }
        page.remove(i);
    }

    public void addNodeKeys(ArrayList<Spatial> arrayList, Page<Spatial, V> page) {
        if (page != null && !page.isLeaf()) {
            int keyCount = page.getKeyCount();
            for (int i = 0; i < keyCount; i++) {
                arrayList.add(page.getKey(i));
                addNodeKeys(arrayList, page.getChildPage(i));
            }
        }
    }

    public boolean isQuadraticSplit() {
        return this.quadraticSplit;
    }

    public void setQuadraticSplit(boolean z) {
        this.quadraticSplit = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.mvstore.MVMap
    public int getChildPageCount(Page<Spatial, V> page) {
        return page.getRawChildPageCount() - 1;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/rtree/MVRTreeMap$RTreeCursor.class */
    public static abstract class RTreeCursor<V> implements Iterator<Spatial> {
        private final Spatial filter;
        private CursorPos<Spatial, V> pos;
        private Spatial current;
        private final Page<Spatial, V> root;
        private boolean initialized;

        protected abstract boolean check(boolean z, Spatial spatial, Spatial spatial2);

        /* JADX INFO: Access modifiers changed from: protected */
        public RTreeCursor(Page<Spatial, V> page, Spatial spatial) {
            this.root = page;
            this.filter = spatial;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (!this.initialized) {
                this.pos = new CursorPos<>(this.root, 0, null);
                fetchNext();
                this.initialized = true;
            }
            return this.current != null;
        }

        public void skip(long j) {
            while (hasNext()) {
                long j2 = j;
                j = j2 - 1;
                if (j2 > 0) {
                    fetchNext();
                } else {
                    return;
                }
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Spatial next() {
            if (!hasNext()) {
                return null;
            }
            Spatial spatial = this.current;
            fetchNext();
            return spatial;
        }

        void fetchNext() {
            while (this.pos != null) {
                Page<Spatial, V> page = this.pos.page;
                if (page.isLeaf()) {
                    while (this.pos.index < page.getKeyCount()) {
                        CursorPos<Spatial, V> cursorPos = this.pos;
                        int i = cursorPos.index;
                        cursorPos.index = i + 1;
                        Spatial key = page.getKey(i);
                        if (this.filter == null || check(true, key, this.filter)) {
                            this.current = key;
                            return;
                        }
                    }
                } else {
                    boolean z = false;
                    while (this.pos.index < page.getKeyCount()) {
                        CursorPos<Spatial, V> cursorPos2 = this.pos;
                        int i2 = cursorPos2.index;
                        cursorPos2.index = i2 + 1;
                        Spatial key2 = page.getKey(i2);
                        if (this.filter == null || check(false, key2, this.filter)) {
                            this.pos = new CursorPos<>(this.pos.page.getChildPage(i2), 0, this.pos);
                            z = true;
                            break;
                        }
                    }
                    if (z) {
                    }
                }
                this.pos = this.pos.parent;
            }
            this.current = null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/rtree/MVRTreeMap$IntersectsRTreeCursor.class */
    private static final class IntersectsRTreeCursor<V> extends RTreeCursor<V> {
        private final SpatialDataType keyType;

        public IntersectsRTreeCursor(Page<Spatial, V> page, Spatial spatial, SpatialDataType spatialDataType) {
            super(page, spatial);
            this.keyType = spatialDataType;
        }

        @Override // org.h2.mvstore.rtree.MVRTreeMap.RTreeCursor
        protected boolean check(boolean z, Spatial spatial, Spatial spatial2) {
            return this.keyType.isOverlap(spatial, spatial2);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/rtree/MVRTreeMap$ContainsRTreeCursor.class */
    private static final class ContainsRTreeCursor<V> extends RTreeCursor<V> {
        private final SpatialDataType keyType;

        public ContainsRTreeCursor(Page<Spatial, V> page, Spatial spatial, SpatialDataType spatialDataType) {
            super(page, spatial);
            this.keyType = spatialDataType;
        }

        @Override // org.h2.mvstore.rtree.MVRTreeMap.RTreeCursor
        protected boolean check(boolean z, Spatial spatial, Spatial spatial2) {
            if (z) {
                return this.keyType.isInside(spatial, spatial2);
            }
            return this.keyType.isOverlap(spatial, spatial2);
        }
    }

    @Override // org.h2.mvstore.MVMap
    public String getType() {
        return "rtree";
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/rtree/MVRTreeMap$Builder.class */
    public static class Builder<V> extends MVMap.BasicBuilder<MVRTreeMap<V>, Spatial, V> {
        private int dimensions = 2;

        @Override // org.h2.mvstore.MVMap.BasicBuilder
        public /* bridge */ /* synthetic */ MVMap create(Map map) {
            return create((Map<String, Object>) map);
        }

        public Builder() {
            setKeyType(new SpatialDataType(this.dimensions));
        }

        public Builder<V> dimensions(int i) {
            this.dimensions = i;
            setKeyType(new SpatialDataType(i));
            return this;
        }

        @Override // org.h2.mvstore.MVMap.BasicBuilder
        public Builder<V> valueType(DataType<? super V> dataType) {
            setValueType(dataType);
            return this;
        }

        @Override // org.h2.mvstore.MVMap.BasicBuilder
        public MVRTreeMap<V> create(Map<String, Object> map) {
            return new MVRTreeMap<>(map, (SpatialDataType) getKeyType(), getValueType());
        }
    }
}
