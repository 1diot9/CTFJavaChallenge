package org.h2.tools;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/MultiDimension.class */
public class MultiDimension implements Comparator<long[]> {
    private static final MultiDimension INSTANCE = new MultiDimension();

    protected MultiDimension() {
    }

    public static MultiDimension getInstance() {
        return INSTANCE;
    }

    public int normalize(int i, double d, double d2, double d3) {
        if (d < d2 || d > d3) {
            throw new IllegalArgumentException(d2 + "<" + d + "<" + d3);
        }
        return (int) (((d - d2) / (d3 - d2)) * getMaxValue(i));
    }

    public int getMaxValue(int i) {
        if (i < 2 || i > 32) {
            throw new IllegalArgumentException(Integer.toString(i));
        }
        return (int) ((1 << getBitsPerValue(i)) - 1);
    }

    private static int getBitsPerValue(int i) {
        return Math.min(31, 64 / i);
    }

    public long interleave(int... iArr) {
        int length = iArr.length;
        long maxValue = getMaxValue(length);
        int bitsPerValue = getBitsPerValue(length);
        long j = 0;
        for (int i = 0; i < length; i++) {
            long j2 = iArr[i];
            if (j2 < 0 || j2 > maxValue) {
                throw new IllegalArgumentException("0<" + j2 + "<" + maxValue);
            }
            for (int i2 = 0; i2 < bitsPerValue; i2++) {
                j |= (j2 & (1 << i2)) << (i + ((length - 1) * i2));
            }
        }
        return j;
    }

    public long interleave(int i, int i2) {
        if (i < 0) {
            throw new IllegalArgumentException("0<" + i);
        }
        if (i2 < 0) {
            throw new IllegalArgumentException("0<" + i2);
        }
        long j = 0;
        for (int i3 = 0; i3 < 32; i3++) {
            j = j | ((i & (1 << i3)) << i3) | ((i2 & (1 << i3)) << (i3 + 1));
        }
        return j;
    }

    public int deinterleave(int i, long j, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < getBitsPerValue(i); i4++) {
            i3 = (int) (i3 | ((j >> (i2 + ((i - 1) * i4))) & (1 << i4)));
        }
        return i3;
    }

    public String generatePreparedQuery(String str, String str2, String[] strArr) {
        StringBuilder sb = new StringBuilder("SELECT D.* FROM ");
        StringUtils.quoteIdentifier(sb, str).append(" D, TABLE(_FROM_ BIGINT=?, _TO_ BIGINT=?) WHERE ");
        StringUtils.quoteIdentifier(sb, str2).append(" BETWEEN _FROM_ AND _TO_");
        for (String str3 : strArr) {
            sb.append(" AND ");
            StringUtils.quoteIdentifier(sb, str3).append("+1 BETWEEN ?+1 AND ?+1");
        }
        return sb.toString();
    }

    public ResultSet getResult(PreparedStatement preparedStatement, int[] iArr, int[] iArr2) throws SQLException {
        long[][] mortonRanges = getMortonRanges(iArr, iArr2);
        int length = mortonRanges.length;
        Long[] lArr = new Long[length];
        Long[] lArr2 = new Long[length];
        for (int i = 0; i < length; i++) {
            lArr[i] = Long.valueOf(mortonRanges[i][0]);
            lArr2[i] = Long.valueOf(mortonRanges[i][1]);
        }
        preparedStatement.setObject(1, lArr);
        preparedStatement.setObject(2, lArr2);
        int length2 = iArr.length;
        int i2 = 3;
        for (int i3 = 0; i3 < length2; i3++) {
            int i4 = i2;
            int i5 = i2 + 1;
            preparedStatement.setInt(i4, iArr[i3]);
            i2 = i5 + 1;
            preparedStatement.setInt(i5, iArr2[i3]);
        }
        return preparedStatement.executeQuery();
    }

    private long[][] getMortonRanges(int[] iArr, int[] iArr2) {
        int length = iArr.length;
        if (iArr2.length != length) {
            throw new IllegalArgumentException(length + "=" + iArr2.length);
        }
        for (int i = 0; i < length; i++) {
            if (iArr[i] > iArr2[i]) {
                int i2 = iArr[i];
                iArr[i] = iArr2[i];
                iArr2[i] = i2;
            }
        }
        int size = getSize(iArr, iArr2, length);
        ArrayList<long[]> arrayList = new ArrayList<>();
        addMortonRanges(arrayList, iArr, iArr2, length, 0);
        combineEntries(arrayList, size);
        return (long[][]) arrayList.toArray((Object[]) new long[0]);
    }

    private static int getSize(int[] iArr, int[] iArr2, int i) {
        int i2 = 1;
        for (int i3 = 0; i3 < i; i3++) {
            i2 *= (iArr2[i3] - iArr[i3]) + 1;
        }
        return i2;
    }

    private void combineEntries(ArrayList<long[]> arrayList, int i) {
        arrayList.sort(this);
        int i2 = 10;
        while (true) {
            int i3 = i2;
            if (i3 < i) {
                int i4 = 0;
                while (i4 < arrayList.size() - 1) {
                    long[] jArr = arrayList.get(i4);
                    long[] jArr2 = arrayList.get(i4 + 1);
                    if (jArr[1] + i3 >= jArr2[0]) {
                        jArr[1] = jArr2[1];
                        arrayList.remove(i4 + 1);
                        i4--;
                    }
                    i4++;
                }
                int i5 = 0;
                Iterator<long[]> it = arrayList.iterator();
                while (it.hasNext()) {
                    long[] next = it.next();
                    i5 = (int) (i5 + (next[1] - next[0]) + 1);
                }
                if (i5 <= 2 * i && arrayList.size() >= 100) {
                    i2 = i3 + (i3 / 2);
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    @Override // java.util.Comparator
    public int compare(long[] jArr, long[] jArr2) {
        return jArr[0] > jArr2[0] ? 1 : -1;
    }

    private void addMortonRanges(ArrayList<long[]> arrayList, int[] iArr, int[] iArr2, int i, int i2) {
        if (i2 > 100) {
            throw new IllegalArgumentException(Integer.toString(i2));
        }
        int i3 = 0;
        int i4 = 0;
        long j = 1;
        for (int i5 = 0; i5 < i; i5++) {
            int i6 = iArr2[i5] - iArr[i5];
            if (i6 < 0) {
                throw new IllegalArgumentException(Integer.toString(i6));
            }
            j *= i6 + 1;
            if (j < 0) {
                throw new IllegalArgumentException(Long.toString(j));
            }
            if (i6 > i4) {
                i4 = i6;
                i3 = i5;
            }
        }
        long interleave = interleave(iArr);
        long interleave2 = interleave(iArr2);
        if (interleave2 < interleave) {
            throw new IllegalArgumentException(interleave2 + "<" + interleave);
        }
        if ((interleave2 - interleave) + 1 == j) {
            arrayList.add(new long[]{interleave, interleave2});
            return;
        }
        int findMiddle = findMiddle(iArr[i3], iArr2[i3]);
        int i7 = iArr2[i3];
        iArr2[i3] = findMiddle;
        addMortonRanges(arrayList, iArr, iArr2, i, i2 + 1);
        iArr2[i3] = i7;
        int i8 = iArr[i3];
        iArr[i3] = findMiddle + 1;
        addMortonRanges(arrayList, iArr, iArr2, i, i2 + 1);
        iArr[i3] = i8;
    }

    private static int roundUp(int i, int i2) {
        return ((i + i2) - 1) & (-i2);
    }

    private static int findMiddle(int i, int i2) {
        int i3 = (i2 - i) - 1;
        if (i3 == 0) {
            return i;
        }
        if (i3 == 1) {
            return i + 1;
        }
        int i4 = 0;
        while ((1 << i4) < i3) {
            i4++;
        }
        int roundUp = roundUp(i + 2, 1 << (i4 - 1)) - 1;
        if (roundUp <= i || roundUp >= i2) {
            throw new IllegalArgumentException(i + "<" + roundUp + "<" + i2);
        }
        return roundUp;
    }
}
