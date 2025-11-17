package org.h2.mvstore;

import ch.qos.logback.core.util.FileSize;
import cn.hutool.core.text.StrPool;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.h2.compress.CompressDeflate;
import org.h2.compress.CompressLZF;
import org.h2.compress.Compressor;
import org.h2.engine.Constants;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.StringDataType;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FileUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVStoreTool.class */
public class MVStoreTool {
    public static void main(String... strArr) {
        int i = 0;
        while (i < strArr.length) {
            if ("-dump".equals(strArr[i])) {
                i++;
                dump(strArr[i], new PrintWriter(System.out), true);
            } else if ("-info".equals(strArr[i])) {
                i++;
                info(strArr[i], new PrintWriter(System.out));
            } else if ("-compact".equals(strArr[i])) {
                i++;
                compact(strArr[i], false);
            } else if ("-compress".equals(strArr[i])) {
                i++;
                compact(strArr[i], true);
            } else if ("-rollback".equals(strArr[i])) {
                int i2 = i + 1;
                String str = strArr[i2];
                i = i2 + 1;
                rollback(str, Long.decode(strArr[i]).longValue(), new PrintWriter(System.out));
            } else if ("-repair".equals(strArr[i])) {
                i++;
                repair(strArr[i]);
            }
            i++;
        }
    }

    public static void dump(String str, boolean z) {
        dump(str, new PrintWriter(System.out), z);
    }

    public static void info(String str) {
        info(str, new PrintWriter(System.out));
    }

    public static void dump(String str, Writer writer, boolean z) {
        ByteBuffer byteBuffer;
        PrintWriter printWriter = new PrintWriter(writer, true);
        if (!FilePath.get(str).exists()) {
            printWriter.println("File not found: " + str);
            return;
        }
        long size = FileUtils.size(str);
        printWriter.printf("File %s, %d bytes, %d MB\n", str, Long.valueOf(size), Long.valueOf((size / FileSize.KB_COEFFICIENT) / FileSize.KB_COEFFICIENT));
        TreeMap treeMap = new TreeMap();
        long j = 0;
        try {
            FileChannel open = FilePath.get(str).open("r");
            Throwable th = null;
            try {
                try {
                    long size2 = open.size();
                    int length = Long.toHexString(size2).length();
                    ByteBuffer allocate = ByteBuffer.allocate(4096);
                    long j2 = 0;
                    long j3 = 0;
                    while (j3 < size2) {
                        allocate.rewind();
                        try {
                            DataUtils.readFully(open, j3, allocate);
                            allocate.rewind();
                            byte b = allocate.get();
                            if (b == 72) {
                                printWriter.printf("%0" + length + "x fileHeader %s%n", Long.valueOf(j3), new String(allocate.array(), StandardCharsets.ISO_8859_1).trim());
                                j3 += 4096;
                            } else if (b != 99) {
                                j3 += 4096;
                            } else {
                                allocate.position(0);
                                try {
                                    SFChunk sFChunk = new SFChunk(Chunk.readChunkHeader(allocate));
                                    if (sFChunk.len <= 0) {
                                        j3 += 4096;
                                    } else {
                                        int i = sFChunk.len * 4096;
                                        printWriter.printf("%n%0" + length + "x chunkHeader %s%n", Long.valueOf(j3), sFChunk);
                                        ByteBuffer allocate2 = ByteBuffer.allocate(i);
                                        DataUtils.readFully(open, j3, allocate2);
                                        int position = allocate.position();
                                        j3 += i;
                                        int i2 = sFChunk.pageCount;
                                        j2 += sFChunk.pageCount;
                                        TreeMap treeMap2 = new TreeMap();
                                        int i3 = 0;
                                        while (i2 > 0) {
                                            int i4 = position;
                                            try {
                                                allocate2.position(position);
                                                int i5 = allocate2.getInt();
                                                allocate2.getShort();
                                                DataUtils.readVarInt(allocate2);
                                                int readVarInt = DataUtils.readVarInt(allocate2);
                                                int readVarInt2 = DataUtils.readVarInt(allocate2);
                                                byte b2 = allocate2.get();
                                                boolean z2 = (b2 & 2) != 0;
                                                boolean z3 = (b2 & 1) != 0;
                                                if (z) {
                                                    String str2 = "+%0" + length + "x %s, map %x, %d entries, %d bytes, maxLen %x%n";
                                                    Object[] objArr = new Object[6];
                                                    objArr[0] = Integer.valueOf(position);
                                                    objArr[1] = (z3 ? "node" : "leaf") + (z2 ? " compressed" : "");
                                                    objArr[2] = Integer.valueOf(readVarInt);
                                                    objArr[3] = Integer.valueOf(z3 ? readVarInt2 + 1 : readVarInt2);
                                                    objArr[4] = Integer.valueOf(i5);
                                                    objArr[5] = Integer.valueOf(DataUtils.getPageMaxLength(DataUtils.composePagePos(0, 0, i5, 0)));
                                                    printWriter.printf(str2, objArr);
                                                }
                                                position += i5;
                                                Integer num = (Integer) treeMap2.get(Integer.valueOf(readVarInt));
                                                if (num == null) {
                                                    num = 0;
                                                }
                                                treeMap2.put(Integer.valueOf(readVarInt), Integer.valueOf(num.intValue() + i5));
                                                Long l = (Long) treeMap.get(Integer.valueOf(readVarInt));
                                                if (l == null) {
                                                    l = 0L;
                                                }
                                                treeMap.put(Integer.valueOf(readVarInt), Long.valueOf(l.longValue() + i5));
                                                i3 += i5;
                                                j += i5;
                                                i2--;
                                                long[] jArr = null;
                                                long[] jArr2 = null;
                                                if (z3) {
                                                    jArr = new long[readVarInt2 + 1];
                                                    for (int i6 = 0; i6 <= readVarInt2; i6++) {
                                                        jArr[i6] = allocate2.getLong();
                                                    }
                                                    jArr2 = new long[readVarInt2 + 1];
                                                    for (int i7 = 0; i7 <= readVarInt2; i7++) {
                                                        jArr2[i7] = DataUtils.readVarLong(allocate2);
                                                    }
                                                }
                                                String[] strArr = new String[readVarInt2];
                                                if (readVarInt == 0 && z) {
                                                    if (z2) {
                                                        Compressor compressor = getCompressor((b2 & 6) != 6);
                                                        int readVarInt3 = DataUtils.readVarInt(allocate2);
                                                        int position2 = (i5 + i4) - allocate2.position();
                                                        byte[] newBytes = Utils.newBytes(position2);
                                                        allocate2.get(newBytes);
                                                        int i8 = position2 + readVarInt3;
                                                        byteBuffer = ByteBuffer.allocate(i8);
                                                        compressor.expand(newBytes, 0, position2, byteBuffer.array(), 0, i8);
                                                    } else {
                                                        byteBuffer = allocate2;
                                                    }
                                                    for (int i9 = 0; i9 < readVarInt2; i9++) {
                                                        strArr[i9] = StringDataType.INSTANCE.read(byteBuffer);
                                                    }
                                                    if (z3) {
                                                        for (int i10 = 0; i10 < readVarInt2; i10++) {
                                                            long j4 = jArr[i10];
                                                            printWriter.printf("    %d children < %s @ chunk %x +%0" + length + "x%n", Long.valueOf(jArr2[i10]), strArr[i10], Integer.valueOf(DataUtils.getPageChunkId(j4)), Integer.valueOf(DataUtils.getPageOffset(j4)));
                                                        }
                                                        long j5 = jArr[readVarInt2];
                                                        String str3 = "    %d children >= %s @ chunk %x +%0" + length + "x%n";
                                                        Object[] objArr2 = new Object[4];
                                                        objArr2[0] = Long.valueOf(jArr2[readVarInt2]);
                                                        objArr2[1] = readVarInt2 <= strArr.length ? null : strArr[readVarInt2];
                                                        objArr2[2] = Integer.valueOf(DataUtils.getPageChunkId(j5));
                                                        objArr2[3] = Integer.valueOf(DataUtils.getPageOffset(j5));
                                                        printWriter.printf(str3, objArr2);
                                                    } else {
                                                        String[] strArr2 = new String[readVarInt2];
                                                        for (int i11 = 0; i11 < readVarInt2; i11++) {
                                                            strArr2[i11] = StringDataType.INSTANCE.read(byteBuffer);
                                                        }
                                                        for (int i12 = 0; i12 < readVarInt2; i12++) {
                                                            printWriter.println("    " + strArr[i12] + " = " + strArr2[i12]);
                                                        }
                                                    }
                                                } else if (z3 && z) {
                                                    for (int i13 = 0; i13 <= readVarInt2; i13++) {
                                                        long j6 = jArr[i13];
                                                        printWriter.printf("    %d children @ chunk %x +%0" + length + "x%n", Long.valueOf(jArr2[i13]), Integer.valueOf(DataUtils.getPageChunkId(j6)), Integer.valueOf(DataUtils.getPageOffset(j6)));
                                                    }
                                                }
                                            } catch (IllegalArgumentException e) {
                                                printWriter.printf("ERROR illegal position %d%n", Integer.valueOf(position));
                                            }
                                        }
                                        int max = Math.max(1, i3);
                                        for (Integer num2 : treeMap2.keySet()) {
                                            printWriter.printf("map %x: %d bytes, %d%%%n", num2, treeMap2.get(num2), Integer.valueOf((100 * ((Integer) treeMap2.get(num2)).intValue()) / max));
                                        }
                                        int limit = allocate2.limit() - 128;
                                        try {
                                            allocate2.position(limit);
                                            printWriter.printf("+%0" + length + "x chunkFooter %s%n", Integer.valueOf(limit), new String(allocate2.array(), allocate2.position(), 128, StandardCharsets.ISO_8859_1).trim());
                                        } catch (IllegalArgumentException e2) {
                                            printWriter.printf("ERROR illegal footer position %d%n", Integer.valueOf(limit));
                                        }
                                    }
                                } catch (MVStoreException e3) {
                                    j3 += 4096;
                                }
                            }
                        } catch (MVStoreException e4) {
                            j3 += 4096;
                            printWriter.printf("ERROR illegal position %d%n", Long.valueOf(j3));
                        }
                    }
                    printWriter.printf("%n%0" + length + "x eof%n", Long.valueOf(size2));
                    printWriter.printf(StrPool.LF, new Object[0]);
                    long max2 = Math.max(1L, j2);
                    printWriter.printf("page size total: %d bytes, page count: %d, average page size: %d bytes\n", Long.valueOf(j), Long.valueOf(max2), Long.valueOf(j / max2));
                    long max3 = Math.max(1L, j);
                    for (Integer num3 : treeMap.keySet()) {
                        printWriter.printf("map %x: %d bytes, %d%%%n", num3, treeMap.get(num3), Integer.valueOf((int) ((100 * ((Long) treeMap.get(num3)).longValue()) / max3)));
                    }
                    if (open != null) {
                        if (0 != 0) {
                            try {
                                open.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            open.close();
                        }
                    }
                } finally {
                }
            } finally {
            }
        } catch (IOException e5) {
            printWriter.println("ERROR: " + e5);
            e5.printStackTrace(printWriter);
        }
        printWriter.flush();
    }

    private static Compressor getCompressor(boolean z) {
        return z ? new CompressLZF() : new CompressDeflate();
    }

    /* JADX WARN: Type inference failed for: r0v99, types: [java.lang.Object, org.h2.mvstore.Chunk] */
    public static String info(String str, Writer writer) {
        PrintWriter printWriter = new PrintWriter(writer, true);
        if (!FilePath.get(str).exists()) {
            printWriter.println("File not found: " + str);
            return "File not found: " + str;
        }
        long size = FileUtils.size(str);
        try {
            MVStore open = new MVStore.Builder().fileName(str).recoveryMode().readOnly().open();
            Throwable th = null;
            try {
                try {
                    Map<String, String> layoutMap = open.getLayoutMap();
                    long readHexLong = DataUtils.readHexLong(open.getStoreHeader(), "created", 0L);
                    TreeMap treeMap = new TreeMap();
                    long j = 0;
                    long j2 = 0;
                    long j3 = 0;
                    long j4 = 0;
                    for (Map.Entry<String, String> entry : layoutMap.entrySet()) {
                        if (entry.getKey().startsWith(DataUtils.META_CHUNK)) {
                            ?? createChunk = open.getFileStore().createChunk(entry.getValue());
                            treeMap.put(Integer.valueOf(createChunk.id), createChunk);
                            j += createChunk.len * 4096;
                            j2 += createChunk.maxLen;
                            j3 += createChunk.maxLenLive;
                            if (createChunk.maxLenLive > 0) {
                                j4 += createChunk.maxLen;
                            }
                        }
                    }
                    printWriter.printf("Created: %s\n", formatTimestamp(readHexLong, readHexLong));
                    printWriter.printf("Last modified: %s\n", formatTimestamp(FileUtils.lastModified(str), readHexLong));
                    printWriter.printf("File length: %d\n", Long.valueOf(size));
                    printWriter.printf("The last chunk is not listed\n", new Object[0]);
                    printWriter.printf("Chunk length: %d\n", Long.valueOf(j));
                    printWriter.printf("Chunk count: %d\n", Integer.valueOf(treeMap.size()));
                    printWriter.printf("Used space: %d%%\n", Integer.valueOf(getPercent(j, size)));
                    Object[] objArr = new Object[1];
                    objArr[0] = Integer.valueOf(j2 == 0 ? 100 : getPercent(j3, j2));
                    printWriter.printf("Chunk fill rate: %d%%\n", objArr);
                    Object[] objArr2 = new Object[1];
                    objArr2[0] = Integer.valueOf(j4 == 0 ? 100 : getPercent(j3, j4));
                    printWriter.printf("Chunk fill rate excluding empty chunks: %d%%\n", objArr2);
                    Iterator it = treeMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Chunk chunk = (Chunk) ((Map.Entry) it.next()).getValue();
                        printWriter.printf("  Chunk %d: %s, %d%% used, %d blocks", Integer.valueOf(chunk.id), formatTimestamp(readHexLong + chunk.time, readHexLong), Integer.valueOf(getPercent(chunk.maxLenLive, chunk.maxLen)), Integer.valueOf(chunk.len));
                        if (chunk.maxLenLive == 0) {
                            printWriter.printf(", unused: %s", formatTimestamp(readHexLong + chunk.unused, readHexLong));
                        }
                        printWriter.printf(StrPool.LF, new Object[0]);
                    }
                    printWriter.printf(StrPool.LF, new Object[0]);
                    if (open != null) {
                        if (0 != 0) {
                            try {
                                open.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            open.close();
                        }
                    }
                    printWriter.flush();
                    return null;
                } finally {
                }
            } finally {
            }
        } catch (Exception e) {
            printWriter.println("ERROR: " + e);
            e.printStackTrace(printWriter);
            return e.getMessage();
        }
    }

    private static String formatTimestamp(long j, long j2) {
        return new Timestamp(j).toString().substring(0, 19) + " (+" + ((j - j2) / 1000) + " s)";
    }

    private static int getPercent(long j, long j2) {
        if (j == 0) {
            return 0;
        }
        if (j == j2) {
            return 100;
        }
        return (int) (1 + ((98 * j) / Math.max(1L, j2)));
    }

    public static void compact(String str, boolean z) {
        String str2 = str + Constants.SUFFIX_MV_STORE_TEMP_FILE;
        FileUtils.delete(str2);
        compact(str, str2, z);
        moveAtomicReplace(str2, str);
    }

    public static void moveAtomicReplace(String str, String str2) {
        try {
            FileUtils.moveAtomicReplace(str, str2);
        } catch (MVStoreException e) {
            String str3 = str2 + Constants.SUFFIX_MV_STORE_NEW_FILE;
            FileUtils.delete(str3);
            FileUtils.move(str, str3);
            FileUtils.delete(str2);
            FileUtils.move(str3, str2);
        }
    }

    public static void compactCleanUp(String str) {
        String str2 = str + Constants.SUFFIX_MV_STORE_TEMP_FILE;
        if (FileUtils.exists(str2)) {
            FileUtils.delete(str2);
        }
        String str3 = str + Constants.SUFFIX_MV_STORE_NEW_FILE;
        if (FileUtils.exists(str3)) {
            if (FileUtils.exists(str)) {
                FileUtils.delete(str3);
            } else {
                FileUtils.move(str3, str);
            }
        }
    }

    public static void compact(String str, String str2, boolean z) {
        MVStore open = new MVStore.Builder().fileName(str).readOnly().open();
        Throwable th = null;
        try {
            FileUtils.delete(str2);
            MVStore.Builder fileName = new MVStore.Builder().fileName(str2);
            if (z) {
                fileName.compress();
            }
            MVStore open2 = fileName.open();
            Throwable th2 = null;
            try {
                try {
                    compact(open, open2);
                    if (open2 != null) {
                        if (0 != 0) {
                            try {
                                open2.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            open2.close();
                        }
                    }
                    if (open != null) {
                        if (0 != 0) {
                            try {
                                open.close();
                                return;
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                                return;
                            }
                        }
                        open.close();
                    }
                } catch (Throwable th5) {
                    if (open2 != null) {
                        if (th2 != null) {
                            try {
                                open2.close();
                            } catch (Throwable th6) {
                                th2.addSuppressed(th6);
                            }
                        } else {
                            open2.close();
                        }
                    }
                    throw th5;
                }
            } catch (Throwable th7) {
                th2 = th7;
                throw th7;
            }
        } catch (Throwable th8) {
            if (open != null) {
                if (0 != 0) {
                    try {
                        open.close();
                    } catch (Throwable th9) {
                        th.addSuppressed(th9);
                    }
                } else {
                    open.close();
                }
            }
            throw th8;
        }
    }

    public static void compact(MVStore mVStore, MVStore mVStore2) {
        mVStore2.setCurrentVersion(mVStore.getCurrentVersion());
        mVStore2.adjustLastMapId(mVStore.getLastMapId());
        int autoCommitDelay = mVStore2.getAutoCommitDelay();
        boolean isSpaceReused = mVStore2.isSpaceReused();
        try {
            mVStore2.setReuseSpace(false);
            mVStore2.setAutoCommitDelay(0);
            MVMap<String, String> metaMap = mVStore.getMetaMap();
            MVMap<String, String> metaMap2 = mVStore2.getMetaMap();
            for (Map.Entry<String, String> entry : metaMap.entrySet()) {
                String key = entry.getKey();
                if (!key.startsWith(DataUtils.META_MAP) && !key.startsWith(DataUtils.META_NAME)) {
                    metaMap2.put(key, entry.getValue());
                }
            }
            for (String str : mVStore.getMapNames()) {
                MVMap.Builder<Object, Object> genericMapBuilder = getGenericMapBuilder();
                if (str.startsWith(TransactionStore.UNDO_LOG_NAME_PREFIX)) {
                    genericMapBuilder.singleWriter();
                }
                MVMap openMap = mVStore.openMap(str, genericMapBuilder);
                MVMap openMap2 = mVStore2.openMap(str, genericMapBuilder);
                openMap2.copyFrom(openMap);
                metaMap2.put(MVMap.getMapKey(openMap2.getId()), metaMap.get(MVMap.getMapKey(openMap.getId())));
            }
            mVStore2.commit();
            mVStore2.setAutoCommitDelay(autoCommitDelay);
            mVStore2.setReuseSpace(isSpaceReused);
        } catch (Throwable th) {
            mVStore2.setAutoCommitDelay(autoCommitDelay);
            mVStore2.setReuseSpace(isSpaceReused);
            throw th;
        }
    }

    public static void repair(String str) {
        String info;
        PrintWriter printWriter = new PrintWriter(System.out);
        long j = Long.MAX_VALUE;
        OutputStream outputStream = new OutputStream() { // from class: org.h2.mvstore.MVStoreTool.1
            @Override // java.io.OutputStream
            public void write(int i) {
            }
        };
        while (j >= 0) {
            printWriter.println(j == Long.MAX_VALUE ? "Trying latest version" : "Trying version " + j);
            printWriter.flush();
            long rollback = rollback(str, j, new PrintWriter(outputStream));
            try {
                info = info(str + ".temp", new PrintWriter(outputStream));
            } catch (Exception e) {
                printWriter.println("Fail: " + e.getMessage());
                printWriter.flush();
            }
            if (info == null) {
                FilePath.get(str).moveTo(FilePath.get(str + ".back"), true);
                FilePath.get(str + ".temp").moveTo(FilePath.get(str), true);
                printWriter.println("Success");
                break;
            }
            printWriter.println("    ... failed: " + info);
            j = rollback - 1;
        }
        printWriter.flush();
    }

    public static long rollback(String str, long j, Writer writer) {
        long j2 = -1;
        PrintWriter printWriter = new PrintWriter(writer, true);
        if (!FilePath.get(str).exists()) {
            printWriter.println("File not found: " + str);
            return -1L;
        }
        FileChannel fileChannel = null;
        FileChannel fileChannel2 = null;
        try {
            try {
                fileChannel = FilePath.get(str).open("r");
                FilePath.get(str + ".temp").delete();
                fileChannel2 = FilePath.get(str + ".temp").open("rw");
                long size = fileChannel.size();
                ByteBuffer allocate = ByteBuffer.allocate(4096);
                SFChunk sFChunk = null;
                long j3 = 0;
                while (j3 < size) {
                    allocate.rewind();
                    DataUtils.readFully(fileChannel, j3, allocate);
                    allocate.rewind();
                    byte b = allocate.get();
                    if (b == 72) {
                        allocate.rewind();
                        fileChannel2.write(allocate, j3);
                        j3 += 4096;
                    } else if (b != 99) {
                        j3 += 4096;
                    } else {
                        try {
                            SFChunk sFChunk2 = new SFChunk(Chunk.readChunkHeader(allocate));
                            if (sFChunk2.len <= 0) {
                                j3 += 4096;
                            } else {
                                int i = sFChunk2.len * 4096;
                                ByteBuffer allocate2 = ByteBuffer.allocate(i);
                                DataUtils.readFully(fileChannel, j3, allocate2);
                                if (sFChunk2.version > j) {
                                    j3 += i;
                                } else {
                                    allocate2.rewind();
                                    fileChannel2.write(allocate2, j3);
                                    if (sFChunk == null || sFChunk2.version > sFChunk.version) {
                                        sFChunk = sFChunk2;
                                        j2 = sFChunk2.version;
                                    }
                                    j3 += i;
                                }
                            }
                        } catch (MVStoreException e) {
                            j3 += 4096;
                        }
                    }
                }
                ByteBuffer allocate3 = ByteBuffer.allocate(sFChunk.len * 4096);
                DataUtils.readFully(fileChannel, sFChunk.block * 4096, allocate3);
                allocate3.rewind();
                fileChannel2.write(allocate3, size);
                if (fileChannel != null) {
                    try {
                        fileChannel.close();
                    } catch (IOException e2) {
                    }
                }
                if (fileChannel2 != null) {
                    try {
                        fileChannel2.close();
                    } catch (IOException e3) {
                    }
                }
            } catch (IOException e4) {
                printWriter.println("ERROR: " + e4);
                e4.printStackTrace(printWriter);
                if (fileChannel != null) {
                    try {
                        fileChannel.close();
                    } catch (IOException e5) {
                    }
                }
                if (fileChannel2 != null) {
                    try {
                        fileChannel2.close();
                    } catch (IOException e6) {
                    }
                }
            }
            printWriter.flush();
            return j2;
        } catch (Throwable th) {
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e7) {
                }
            }
            if (fileChannel2 != null) {
                try {
                    fileChannel2.close();
                } catch (IOException e8) {
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MVMap.Builder<Object, Object> getGenericMapBuilder() {
        return new MVMap.Builder().keyType((DataType) GenericDataType.INSTANCE).valueType((DataType) GenericDataType.INSTANCE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVStoreTool$GenericDataType.class */
    public static class GenericDataType extends BasicDataType<byte[]> {
        static GenericDataType INSTANCE = new GenericDataType();

        private GenericDataType() {
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public boolean isMemoryEstimationAllowed() {
            return false;
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public int getMemory(byte[] bArr) {
            if (bArr == null) {
                return 0;
            }
            return bArr.length * 8;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [byte[], byte[][]] */
        @Override // org.h2.mvstore.type.DataType
        public byte[][] createStorage(int i) {
            return new byte[i];
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public void write(WriteBuffer writeBuffer, byte[] bArr) {
            if (bArr != null) {
                writeBuffer.put(bArr);
            }
        }

        @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
        public byte[] read(ByteBuffer byteBuffer) {
            int remaining = byteBuffer.remaining();
            if (remaining == 0) {
                return null;
            }
            byte[] bArr = new byte[remaining];
            byteBuffer.get(bArr);
            return bArr;
        }
    }
}
