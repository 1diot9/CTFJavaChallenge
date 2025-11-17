package org.apache.tomcat.util.buf;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/StringCache.class */
public class StringCache {
    private static final Log log = LogFactory.getLog((Class<?>) StringCache.class);
    protected static boolean byteEnabled = Boolean.getBoolean("tomcat.util.buf.StringCache.byte.enabled");
    protected static boolean charEnabled = Boolean.getBoolean("tomcat.util.buf.StringCache.char.enabled");
    protected static int trainThreshold = Integer.getInteger("tomcat.util.buf.StringCache.trainThreshold", 20000).intValue();
    protected static int cacheSize = Integer.getInteger("tomcat.util.buf.StringCache.cacheSize", 200).intValue();
    protected static final int maxStringSize = Integer.getInteger("tomcat.util.buf.StringCache.maxStringSize", 128).intValue();
    protected static final HashMap<ByteEntry, int[]> bcStats = new HashMap<>(cacheSize);
    protected static int bcCount = 0;
    protected static volatile ByteEntry[] bcCache = null;
    protected static final HashMap<CharEntry, int[]> ccStats = new HashMap<>(cacheSize);
    protected static int ccCount = 0;
    protected static volatile CharEntry[] ccCache = null;
    protected static int accessCount = 0;
    protected static int hitCount = 0;

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize2) {
        cacheSize = cacheSize2;
    }

    public boolean getByteEnabled() {
        return byteEnabled;
    }

    public void setByteEnabled(boolean byteEnabled2) {
        byteEnabled = byteEnabled2;
    }

    public boolean getCharEnabled() {
        return charEnabled;
    }

    public void setCharEnabled(boolean charEnabled2) {
        charEnabled = charEnabled2;
    }

    public int getTrainThreshold() {
        return trainThreshold;
    }

    public void setTrainThreshold(int trainThreshold2) {
        trainThreshold = trainThreshold2;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void reset() {
        hitCount = 0;
        accessCount = 0;
        synchronized (bcStats) {
            bcCache = null;
            bcCount = 0;
        }
        synchronized (ccStats) {
            ccCache = null;
            ccCount = 0;
        }
    }

    public static String toString(ByteChunk bc) {
        try {
            return toString(bc, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String toString(ByteChunk bc, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) throws CharacterCodingException {
        if (bcCache == null) {
            String value = bc.toStringInternal(malformedInputAction, unmappableCharacterAction);
            if (byteEnabled && value.length() < maxStringSize) {
                synchronized (bcStats) {
                    if (bcCache != null) {
                        return value;
                    }
                    if (bcCount > trainThreshold) {
                        long t1 = System.currentTimeMillis();
                        TreeMap<Integer, ArrayList<ByteEntry>> tempMap = new TreeMap<>();
                        for (Map.Entry<ByteEntry, int[]> item : bcStats.entrySet()) {
                            ByteEntry entry = item.getKey();
                            int[] countA = item.getValue();
                            ((ArrayList) tempMap.computeIfAbsent(Integer.valueOf(countA[0]), k -> {
                                return new ArrayList();
                            })).add(entry);
                        }
                        int size = bcStats.size();
                        if (size > cacheSize) {
                            size = cacheSize;
                        }
                        ByteEntry[] tempbcCache = new ByteEntry[size];
                        ByteChunk tempChunk = new ByteChunk();
                        int n = 0;
                        while (n < size) {
                            Object key = tempMap.lastKey();
                            ArrayList<ByteEntry> list = tempMap.get(key);
                            for (int i = 0; i < list.size() && n < size; i++) {
                                ByteEntry entry2 = list.get(i);
                                tempChunk.setBytes(entry2.name, 0, entry2.name.length);
                                int insertPos = findClosest(tempChunk, tempbcCache, n);
                                if (insertPos == n) {
                                    tempbcCache[n + 1] = entry2;
                                } else {
                                    System.arraycopy(tempbcCache, insertPos + 1, tempbcCache, insertPos + 2, (n - insertPos) - 1);
                                    tempbcCache[insertPos + 1] = entry2;
                                }
                                n++;
                            }
                            tempMap.remove(key);
                        }
                        bcCount = 0;
                        bcStats.clear();
                        bcCache = tempbcCache;
                        if (log.isDebugEnabled()) {
                            long t2 = System.currentTimeMillis();
                            log.debug("ByteCache generation time: " + (t2 - t1) + "ms");
                        }
                    } else {
                        bcCount++;
                        ByteEntry entry3 = new ByteEntry();
                        entry3.value = value;
                        int[] count = bcStats.get(entry3);
                        if (count == null) {
                            int end = bc.getEnd();
                            int start = bc.getStart();
                            entry3.name = new byte[bc.getLength()];
                            System.arraycopy(bc.getBuffer(), start, entry3.name, 0, end - start);
                            entry3.charset = bc.getCharset();
                            entry3.malformedInputAction = malformedInputAction;
                            entry3.unmappableCharacterAction = unmappableCharacterAction;
                            bcStats.put(entry3, new int[]{1});
                        } else {
                            count[0] = count[0] + 1;
                        }
                    }
                }
            }
            return value;
        }
        accessCount++;
        String result = find(bc, malformedInputAction, unmappableCharacterAction);
        if (result == null) {
            return bc.toStringInternal(malformedInputAction, unmappableCharacterAction);
        }
        hitCount++;
        return result;
    }

    public static String toString(CharChunk cc) {
        if (ccCache == null) {
            String value = cc.toStringInternal();
            if (charEnabled && value.length() < maxStringSize) {
                synchronized (ccStats) {
                    if (ccCache != null) {
                        return value;
                    }
                    if (ccCount > trainThreshold) {
                        long t1 = System.currentTimeMillis();
                        TreeMap<Integer, ArrayList<CharEntry>> tempMap = new TreeMap<>();
                        for (Map.Entry<CharEntry, int[]> item : ccStats.entrySet()) {
                            CharEntry entry = item.getKey();
                            int[] countA = item.getValue();
                            ((ArrayList) tempMap.computeIfAbsent(Integer.valueOf(countA[0]), k -> {
                                return new ArrayList();
                            })).add(entry);
                        }
                        int size = ccStats.size();
                        if (size > cacheSize) {
                            size = cacheSize;
                        }
                        CharEntry[] tempccCache = new CharEntry[size];
                        CharChunk tempChunk = new CharChunk();
                        int n = 0;
                        while (n < size) {
                            Object key = tempMap.lastKey();
                            ArrayList<CharEntry> list = tempMap.get(key);
                            for (int i = 0; i < list.size() && n < size; i++) {
                                CharEntry entry2 = list.get(i);
                                tempChunk.setChars(entry2.name, 0, entry2.name.length);
                                int insertPos = findClosest(tempChunk, tempccCache, n);
                                if (insertPos == n) {
                                    tempccCache[n + 1] = entry2;
                                } else {
                                    System.arraycopy(tempccCache, insertPos + 1, tempccCache, insertPos + 2, (n - insertPos) - 1);
                                    tempccCache[insertPos + 1] = entry2;
                                }
                                n++;
                            }
                            tempMap.remove(key);
                        }
                        ccCount = 0;
                        ccStats.clear();
                        ccCache = tempccCache;
                        if (log.isDebugEnabled()) {
                            long t2 = System.currentTimeMillis();
                            log.debug("CharCache generation time: " + (t2 - t1) + "ms");
                        }
                    } else {
                        ccCount++;
                        CharEntry entry3 = new CharEntry();
                        entry3.value = value;
                        int[] count = ccStats.get(entry3);
                        if (count == null) {
                            int end = cc.getEnd();
                            int start = cc.getStart();
                            entry3.name = new char[cc.getLength()];
                            System.arraycopy(cc.getBuffer(), start, entry3.name, 0, end - start);
                            ccStats.put(entry3, new int[]{1});
                        } else {
                            count[0] = count[0] + 1;
                        }
                    }
                }
            }
            return value;
        }
        accessCount++;
        String result = find(cc);
        if (result == null) {
            return cc.toStringInternal();
        }
        hitCount++;
        return result;
    }

    protected static final int compare(ByteChunk name, byte[] compareTo) {
        int result = 0;
        byte[] b = name.getBuffer();
        int start = name.getStart();
        int end = name.getEnd();
        int len = compareTo.length;
        if (end - start < len) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; i++) {
            if (b[i + start] > compareTo[i]) {
                result = 1;
            } else if (b[i + start] < compareTo[i]) {
                result = -1;
            }
        }
        if (result == 0) {
            if (compareTo.length > end - start) {
                result = -1;
            } else if (compareTo.length < end - start) {
                result = 1;
            }
        }
        return result;
    }

    @Deprecated
    protected static final String find(ByteChunk name) {
        return find(name, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
    }

    protected static final String find(ByteChunk name, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) {
        int pos = findClosest(name, bcCache, bcCache.length);
        if (pos < 0 || compare(name, bcCache[pos].name) != 0 || !name.getCharset().equals(bcCache[pos].charset) || !malformedInputAction.equals(bcCache[pos].malformedInputAction) || !unmappableCharacterAction.equals(bcCache[pos].unmappableCharacterAction)) {
            return null;
        }
        return bcCache[pos].value;
    }

    protected static final int findClosest(ByteChunk name, ByteEntry[] array, int len) {
        int a = 0;
        int b = len - 1;
        if (b == -1 || compare(name, array[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        do {
            int i = (b + a) >>> 1;
            int result = compare(name, array[i].name);
            if (result == 1) {
                a = i;
            } else {
                if (result == 0) {
                    return i;
                }
                b = i;
            }
        } while (b - a != 1);
        int result2 = compare(name, array[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    protected static final int compare(CharChunk name, char[] compareTo) {
        int result = 0;
        char[] c = name.getBuffer();
        int start = name.getStart();
        int end = name.getEnd();
        int len = compareTo.length;
        if (end - start < len) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; i++) {
            if (c[i + start] > compareTo[i]) {
                result = 1;
            } else if (c[i + start] < compareTo[i]) {
                result = -1;
            }
        }
        if (result == 0) {
            if (compareTo.length > end - start) {
                result = -1;
            } else if (compareTo.length < end - start) {
                result = 1;
            }
        }
        return result;
    }

    protected static final String find(CharChunk name) {
        int pos = findClosest(name, ccCache, ccCache.length);
        if (pos < 0 || compare(name, ccCache[pos].name) != 0) {
            return null;
        }
        return ccCache[pos].value;
    }

    protected static final int findClosest(CharChunk name, CharEntry[] array, int len) {
        int a = 0;
        int b = len - 1;
        if (b == -1 || compare(name, array[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        do {
            int i = (b + a) >>> 1;
            int result = compare(name, array[i].name);
            if (result == 1) {
                a = i;
            } else {
                if (result == 0) {
                    return i;
                }
                b = i;
            }
        } while (b - a != 1);
        int result2 = compare(name, array[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/StringCache$ByteEntry.class */
    public static class ByteEntry {
        private byte[] name = null;
        private Charset charset = null;
        private CodingErrorAction malformedInputAction = null;
        private CodingErrorAction unmappableCharacterAction = null;
        private String value = null;

        private ByteEntry() {
        }

        public String toString() {
            return this.value;
        }

        public int hashCode() {
            return Objects.hash(this.malformedInputAction, this.unmappableCharacterAction, this.value);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ByteEntry other = (ByteEntry) obj;
            return Objects.equals(this.malformedInputAction, other.malformedInputAction) && Objects.equals(this.unmappableCharacterAction, other.unmappableCharacterAction) && Objects.equals(this.value, other.value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/StringCache$CharEntry.class */
    public static class CharEntry {
        private char[] name = null;
        private String value = null;

        private CharEntry() {
        }

        public String toString() {
            return this.value;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof CharEntry) {
                return this.value.equals(((CharEntry) obj).value);
            }
            return false;
        }
    }
}
