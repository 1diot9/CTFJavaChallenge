package org.springframework.cglib.util;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/util/SorterTemplate.class */
abstract class SorterTemplate {
    private static final int MERGESORT_THRESHOLD = 12;
    private static final int QUICKSORT_THRESHOLD = 7;

    protected abstract void swap(int i, int j);

    protected abstract int compare(int i, int j);

    /* JADX INFO: Access modifiers changed from: protected */
    public void quickSort(int lo, int hi) {
        quickSortHelper(lo, hi);
        insertionSort(lo, hi);
    }

    /* JADX WARN: Incorrect condition in loop: B:2:0x0007 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void quickSortHelper(int r6, int r7) {
        /*
            Method dump skipped, instructions count: 197
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.cglib.util.SorterTemplate.quickSortHelper(int, int):void");
    }

    private void insertionSort(int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            for (int j = i; j > lo && compare(j - 1, j) > 0; j--) {
                swap(j - 1, j);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void mergeSort(int lo, int hi) {
        int diff = hi - lo;
        if (diff <= 12) {
            insertionSort(lo, hi);
            return;
        }
        int mid = lo + (diff / 2);
        mergeSort(lo, mid);
        mergeSort(mid, hi);
        merge(lo, mid, hi, mid - lo, hi - mid);
    }

    private void merge(int lo, int pivot, int hi, int len1, int len2) {
        int len22;
        int second_cut;
        int first_cut;
        int len11;
        if (len1 == 0 || len2 == 0) {
            return;
        }
        if (len1 + len2 == 2) {
            if (compare(pivot, lo) < 0) {
                swap(pivot, lo);
                return;
            }
            return;
        }
        if (len1 > len2) {
            len11 = len1 / 2;
            first_cut = lo + len11;
            second_cut = lower(pivot, hi, first_cut);
            len22 = second_cut - pivot;
        } else {
            len22 = len2 / 2;
            second_cut = pivot + len22;
            first_cut = upper(lo, pivot, second_cut);
            len11 = first_cut - lo;
        }
        rotate(first_cut, pivot, second_cut);
        int new_mid = first_cut + len22;
        merge(lo, first_cut, new_mid, len11, len22);
        merge(new_mid, second_cut, hi, len1 - len11, len2 - len22);
    }

    private void rotate(int lo, int mid, int hi) {
        int lot = lo;
        int hit = mid - 1;
        while (lot < hit) {
            int i = lot;
            lot++;
            int i2 = hit;
            hit--;
            swap(i, i2);
        }
        int lot2 = mid;
        int hit2 = hi - 1;
        while (lot2 < hit2) {
            int i3 = lot2;
            lot2++;
            int i4 = hit2;
            hit2--;
            swap(i3, i4);
        }
        int lot3 = lo;
        int hit3 = hi - 1;
        while (lot3 < hit3) {
            int i5 = lot3;
            lot3++;
            int i6 = hit3;
            hit3--;
            swap(i5, i6);
        }
    }

    private int lower(int lo, int hi, int val) {
        int i = hi - lo;
        while (true) {
            int len = i;
            if (len > 0) {
                int half = len / 2;
                int mid = lo + half;
                if (compare(mid, val) < 0) {
                    lo = mid + 1;
                    i = (len - half) - 1;
                } else {
                    i = half;
                }
            } else {
                return lo;
            }
        }
    }

    private int upper(int lo, int hi, int val) {
        int i = hi - lo;
        while (true) {
            int len = i;
            if (len > 0) {
                int half = len / 2;
                int mid = lo + half;
                if (compare(val, mid) < 0) {
                    i = half;
                } else {
                    lo = mid + 1;
                    i = (len - half) - 1;
                }
            } else {
                return lo;
            }
        }
    }
}
