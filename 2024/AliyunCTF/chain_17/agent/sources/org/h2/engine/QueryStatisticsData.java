package org.h2.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/QueryStatisticsData.class */
public class QueryStatisticsData {
    private static final Comparator<QueryEntry> QUERY_ENTRY_COMPARATOR = Comparator.comparingLong(queryEntry -> {
        return queryEntry.lastUpdateTime;
    });
    private final HashMap<String, QueryEntry> map = new HashMap<>();
    private int maxQueryEntries;

    public QueryStatisticsData(int i) {
        this.maxQueryEntries = i;
    }

    public synchronized void setMaxQueryEntries(int i) {
        this.maxQueryEntries = i;
    }

    public synchronized List<QueryEntry> getQueries() {
        ArrayList arrayList = new ArrayList(this.map.values());
        arrayList.sort(QUERY_ENTRY_COMPARATOR);
        return arrayList.subList(0, Math.min(arrayList.size(), this.maxQueryEntries));
    }

    public synchronized void update(String str, long j, long j2) {
        QueryEntry queryEntry = this.map.get(str);
        if (queryEntry == null) {
            queryEntry = new QueryEntry(str);
            this.map.put(str, queryEntry);
        }
        queryEntry.update(j, j2);
        if (this.map.size() > this.maxQueryEntries * 1.5f) {
            ArrayList arrayList = new ArrayList(this.map.values());
            arrayList.sort(QUERY_ENTRY_COMPARATOR);
            HashSet hashSet = new HashSet(arrayList.subList(0, arrayList.size() / 3));
            Iterator<Map.Entry<String, QueryEntry>> it = this.map.entrySet().iterator();
            while (it.hasNext()) {
                if (hashSet.contains(it.next().getValue())) {
                    it.remove();
                }
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/QueryStatisticsData$QueryEntry.class */
    public static final class QueryEntry {
        public final String sqlStatement;
        public int count;
        public long lastUpdateTime;
        public long executionTimeMinNanos;
        public long executionTimeMaxNanos;
        public long executionTimeCumulativeNanos;
        public long rowCountMin;
        public long rowCountMax;
        public long rowCountCumulative;
        public double executionTimeMeanNanos;
        public double rowCountMean;
        private double executionTimeM2Nanos;
        private double rowCountM2;

        public QueryEntry(String str) {
            this.sqlStatement = str;
        }

        void update(long j, long j2) {
            this.count++;
            this.executionTimeMinNanos = Math.min(j, this.executionTimeMinNanos);
            this.executionTimeMaxNanos = Math.max(j, this.executionTimeMaxNanos);
            this.rowCountMin = Math.min(j2, this.rowCountMin);
            this.rowCountMax = Math.max(j2, this.rowCountMax);
            double d = j2 - this.rowCountMean;
            this.rowCountMean += d / this.count;
            this.rowCountM2 += d * (j2 - this.rowCountMean);
            double d2 = j - this.executionTimeMeanNanos;
            this.executionTimeMeanNanos += d2 / this.count;
            this.executionTimeM2Nanos += d2 * (j - this.executionTimeMeanNanos);
            this.executionTimeCumulativeNanos += j;
            this.rowCountCumulative += j2;
            this.lastUpdateTime = System.currentTimeMillis();
        }

        public double getExecutionTimeStandardDeviation() {
            return Math.sqrt(this.executionTimeM2Nanos / this.count);
        }

        public double getRowCountStandardDeviation() {
            return Math.sqrt(this.rowCountM2 / this.count);
        }
    }
}
