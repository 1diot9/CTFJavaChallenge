package org.h2.mvstore.rtree;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/rtree/SpatialDataType.class */
public class SpatialDataType extends BasicDataType<Spatial> {
    private final int dimensions;

    public SpatialDataType(int i) {
        DataUtils.checkArgument(i >= 1 && i < 32, "Dimensions must be between 1 and 31, is {0}", Integer.valueOf(i));
        this.dimensions = i;
    }

    protected Spatial create(long j, float... fArr) {
        return new DefaultSpatial(j, fArr);
    }

    @Override // org.h2.mvstore.type.DataType
    public Spatial[] createStorage(int i) {
        return new Spatial[i];
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(Spatial spatial, Spatial spatial2) {
        if (spatial == spatial2) {
            return 0;
        }
        if (spatial == null) {
            return -1;
        }
        if (spatial2 == null) {
            return 1;
        }
        return Long.compare(spatial.getId(), spatial2.getId());
    }

    public boolean equals(Spatial spatial, Spatial spatial2) {
        if (spatial == spatial2) {
            return true;
        }
        return (spatial == null || spatial2 == null || spatial.getId() != spatial2.getId()) ? false : true;
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int getMemory(Spatial spatial) {
        return 40 + (this.dimensions * 4);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, Spatial spatial) {
        if (spatial.isNull()) {
            writeBuffer.putVarInt(-1);
            writeBuffer.putVarLong(spatial.getId());
            return;
        }
        int i = 0;
        for (int i2 = 0; i2 < this.dimensions; i2++) {
            if (spatial.min(i2) == spatial.max(i2)) {
                i |= 1 << i2;
            }
        }
        writeBuffer.putVarInt(i);
        for (int i3 = 0; i3 < this.dimensions; i3++) {
            writeBuffer.putFloat(spatial.min(i3));
            if ((i & (1 << i3)) == 0) {
                writeBuffer.putFloat(spatial.max(i3));
            }
        }
        writeBuffer.putVarLong(spatial.getId());
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public Spatial read(ByteBuffer byteBuffer) {
        float f;
        int readVarInt = DataUtils.readVarInt(byteBuffer);
        if (readVarInt == -1) {
            return create(DataUtils.readVarLong(byteBuffer), new float[0]);
        }
        float[] fArr = new float[this.dimensions * 2];
        for (int i = 0; i < this.dimensions; i++) {
            float f2 = byteBuffer.getFloat();
            if ((readVarInt & (1 << i)) != 0) {
                f = f2;
            } else {
                f = byteBuffer.getFloat();
            }
            fArr[i + i] = f2;
            fArr[i + i + 1] = f;
        }
        return create(DataUtils.readVarLong(byteBuffer), fArr);
    }

    public boolean isOverlap(Spatial spatial, Spatial spatial2) {
        if (spatial.isNull() || spatial2.isNull()) {
            return false;
        }
        for (int i = 0; i < this.dimensions; i++) {
            if (spatial.max(i) < spatial2.min(i) || spatial.min(i) > spatial2.max(i)) {
                return false;
            }
        }
        return true;
    }

    public void increaseBounds(Spatial spatial, Spatial spatial2) {
        if (spatial2.isNull() || spatial.isNull()) {
            return;
        }
        for (int i = 0; i < this.dimensions; i++) {
            float min = spatial2.min(i);
            if (min < spatial.min(i)) {
                spatial.setMin(i, min);
            }
            float max = spatial2.max(i);
            if (max > spatial.max(i)) {
                spatial.setMax(i, max);
            }
        }
    }

    public float getAreaIncrease(Spatial spatial, Spatial spatial2) {
        if (spatial.isNull() || spatial2.isNull()) {
            return 0.0f;
        }
        float min = spatial.min(0);
        float max = spatial.max(0);
        float f = max - min;
        float max2 = Math.max(max, spatial2.max(0)) - Math.min(min, spatial2.min(0));
        for (int i = 1; i < this.dimensions; i++) {
            float min2 = spatial.min(i);
            float max3 = spatial.max(i);
            f *= max3 - min2;
            max2 *= Math.max(max3, spatial2.max(i)) - Math.min(min2, spatial2.min(i));
        }
        return max2 - f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getCombinedArea(Spatial spatial, Spatial spatial2) {
        if (spatial.isNull()) {
            return getArea(spatial2);
        }
        if (spatial2.isNull()) {
            return getArea(spatial);
        }
        float f = 1.0f;
        for (int i = 0; i < this.dimensions; i++) {
            f *= Math.max(spatial.max(i), spatial2.max(i)) - Math.min(spatial.min(i), spatial2.min(i));
        }
        return f;
    }

    private float getArea(Spatial spatial) {
        if (spatial.isNull()) {
            return 0.0f;
        }
        float f = 1.0f;
        for (int i = 0; i < this.dimensions; i++) {
            f *= spatial.max(i) - spatial.min(i);
        }
        return f;
    }

    public boolean contains(Spatial spatial, Spatial spatial2) {
        if (spatial.isNull() || spatial2.isNull()) {
            return false;
        }
        for (int i = 0; i < this.dimensions; i++) {
            if (spatial.min(i) > spatial2.min(i) || spatial.max(i) < spatial2.max(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean isInside(Spatial spatial, Spatial spatial2) {
        if (spatial.isNull() || spatial2.isNull()) {
            return false;
        }
        for (int i = 0; i < this.dimensions; i++) {
            if (spatial.min(i) <= spatial2.min(i) || spatial.max(i) >= spatial2.max(i)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Spatial createBoundingBox(Spatial spatial) {
        if (spatial.isNull()) {
            return spatial;
        }
        return spatial.clone(0L);
    }

    public int[] getExtremes(ArrayList<Spatial> arrayList) {
        ArrayList<Spatial> notNull = getNotNull(arrayList);
        if (notNull.isEmpty()) {
            return null;
        }
        Spatial createBoundingBox = createBoundingBox(notNull.get(0));
        Spatial createBoundingBox2 = createBoundingBox(createBoundingBox);
        for (int i = 0; i < this.dimensions; i++) {
            float min = createBoundingBox2.min(i);
            createBoundingBox2.setMin(i, createBoundingBox2.max(i));
            createBoundingBox2.setMax(i, min);
        }
        Iterator<Spatial> it = notNull.iterator();
        while (it.hasNext()) {
            Spatial next = it.next();
            increaseBounds(createBoundingBox, next);
            increaseMaxInnerBounds(createBoundingBox2, next);
        }
        double d = 0.0d;
        int i2 = 0;
        for (int i3 = 0; i3 < this.dimensions; i3++) {
            float max = createBoundingBox2.max(i3) - createBoundingBox2.min(i3);
            if (max >= 0.0f) {
                float max2 = max / (createBoundingBox.max(i3) - createBoundingBox.min(i3));
                if (max2 > d) {
                    d = max2;
                    i2 = i3;
                }
            }
        }
        if (d <= 0.0d) {
            return null;
        }
        float min2 = createBoundingBox2.min(i2);
        float max3 = createBoundingBox2.max(i2);
        int i4 = -1;
        int i5 = -1;
        for (int i6 = 0; i6 < notNull.size() && (i4 < 0 || i5 < 0); i6++) {
            Spatial spatial = notNull.get(i6);
            if (i4 < 0 && spatial.max(i2) == min2) {
                i4 = i6;
            } else if (i5 < 0 && spatial.min(i2) == max3) {
                i5 = i6;
            }
        }
        return new int[]{i4, i5};
    }

    private static ArrayList<Spatial> getNotNull(ArrayList<Spatial> arrayList) {
        boolean z = false;
        Iterator<Spatial> it = arrayList.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            if (it.next().isNull()) {
                z = true;
                break;
            }
        }
        if (!z) {
            return arrayList;
        }
        ArrayList<Spatial> arrayList2 = new ArrayList<>();
        Iterator<Spatial> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            Spatial next = it2.next();
            if (!next.isNull()) {
                arrayList2.add(next);
            }
        }
        return arrayList2;
    }

    private void increaseMaxInnerBounds(Spatial spatial, Spatial spatial2) {
        for (int i = 0; i < this.dimensions; i++) {
            spatial.setMin(i, Math.min(spatial.min(i), spatial2.max(i)));
            spatial.setMax(i, Math.max(spatial.max(i), spatial2.min(i)));
        }
    }
}
