package org.apache.coyote.http2;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.coyote.http2.Hpack;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/HpackEncoder.class */
class HpackEncoder {
    private static final Log log = LogFactory.getLog((Class<?>) HpackEncoder.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) HpackEncoder.class);
    private static final HpackHeaderFunction DEFAULT_HEADER_FUNCTION = new HpackHeaderFunction() { // from class: org.apache.coyote.http2.HpackEncoder.1
        @Override // org.apache.coyote.http2.HpackEncoder.HpackHeaderFunction
        public boolean shouldUseIndexing(String headerName, String value) {
            boolean z = -1;
            switch (headerName.hashCode()) {
                case -1132779846:
                    if (headerName.equals("content-length")) {
                        z = false;
                        break;
                    }
                    break;
                case 3076014:
                    if (headerName.equals("date")) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                case true:
                    return false;
                default:
                    return true;
            }
        }

        @Override // org.apache.coyote.http2.HpackEncoder.HpackHeaderFunction
        public boolean shouldUseHuffman(String header, String value) {
            return value.length() > 5;
        }

        @Override // org.apache.coyote.http2.HpackEncoder.HpackHeaderFunction
        public boolean shouldUseHuffman(String header) {
            return header.length() > 5;
        }
    };
    private MimeHeaders currentHeaders;
    private int entryPositionCounter;
    private static final Map<String, TableEntry[]> ENCODING_STATIC_TABLE;
    private int currentTableSize;
    private int headersIterator = -1;
    private boolean firstPass = true;
    private int newMaxHeaderSize = -1;
    private int minNewMaxHeaderSize = -1;
    private final Deque<TableEntry> evictionQueue = new ArrayDeque();
    private final Map<String, List<TableEntry>> dynamicTable = new HashMap();
    private int maxTableSize = Opcodes.ACC_SYNTHETIC;
    private final HpackHeaderFunction hpackHeaderFunction = DEFAULT_HEADER_FUNCTION;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/HpackEncoder$HpackHeaderFunction.class */
    public interface HpackHeaderFunction {
        boolean shouldUseIndexing(String str, String str2);

        boolean shouldUseHuffman(String str, String str2);

        boolean shouldUseHuffman(String str);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/HpackEncoder$State.class */
    enum State {
        COMPLETE,
        UNDERFLOW
    }

    static {
        Map<String, TableEntry[]> map = new HashMap<>();
        for (int i = 1; i < Hpack.STATIC_TABLE.length; i++) {
            Hpack.HeaderField m = Hpack.STATIC_TABLE[i];
            TableEntry[] existing = map.get(m.name);
            if (existing == null) {
                map.put(m.name, new TableEntry[]{new TableEntry(m.name, m.value, i)});
            } else {
                TableEntry[] newEntry = new TableEntry[existing.length + 1];
                System.arraycopy(existing, 0, newEntry, 0, existing.length);
                newEntry[existing.length] = new TableEntry(m.name, m.value, i);
                map.put(m.name, newEntry);
            }
        }
        ENCODING_STATIC_TABLE = Collections.unmodifiableMap(map);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public State encode(MimeHeaders headers, ByteBuffer target) {
        int it = this.headersIterator;
        if (this.headersIterator == -1) {
            handleTableSizeChange(target);
            it = 0;
            this.currentHeaders = headers;
        } else if (headers != this.currentHeaders) {
            throw new IllegalStateException();
        }
        while (it < this.currentHeaders.size()) {
            String headerName = headers.getName(it).toString().toLowerCase(Locale.US);
            boolean skip = false;
            if (this.firstPass) {
                if (headerName.charAt(0) != ':') {
                    skip = true;
                }
            } else if (headerName.charAt(0) == ':') {
                skip = true;
            }
            if (!skip) {
                String val = headers.getValue(it).toString();
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("hpackEncoder.encodeHeader", headerName, val));
                }
                TableEntry tableEntry = findInTable(headerName, val);
                int required = 11 + headerName.length() + 1 + val.length();
                if (target.remaining() < required) {
                    this.headersIterator = it;
                    return State.UNDERFLOW;
                }
                boolean canIndex = this.hpackHeaderFunction.shouldUseIndexing(headerName, val) && (headerName.length() + val.length()) + 32 < this.maxTableSize;
                if (tableEntry == null && canIndex) {
                    target.put((byte) 64);
                    writeHuffmanEncodableName(target, headerName);
                    writeHuffmanEncodableValue(target, headerName, val);
                    addToDynamicTable(headerName, val);
                } else if (tableEntry == null) {
                    target.put((byte) 16);
                    writeHuffmanEncodableName(target, headerName);
                    writeHuffmanEncodableValue(target, headerName, val);
                } else if (val.equals(tableEntry.value)) {
                    target.put(Byte.MIN_VALUE);
                    Hpack.encodeInteger(target, tableEntry.getPosition(), 7);
                } else if (canIndex) {
                    target.put((byte) 64);
                    Hpack.encodeInteger(target, tableEntry.getPosition(), 6);
                    writeHuffmanEncodableValue(target, headerName, val);
                    addToDynamicTable(headerName, val);
                } else {
                    target.put((byte) 16);
                    Hpack.encodeInteger(target, tableEntry.getPosition(), 4);
                    writeHuffmanEncodableValue(target, headerName, val);
                }
            }
            it++;
            if (it == this.currentHeaders.size() && this.firstPass) {
                this.firstPass = false;
                it = 0;
            }
        }
        this.headersIterator = -1;
        this.firstPass = true;
        return State.COMPLETE;
    }

    private void writeHuffmanEncodableName(ByteBuffer target, String headerName) {
        if (this.hpackHeaderFunction.shouldUseHuffman(headerName) && HPackHuffman.encode(target, headerName, true)) {
            return;
        }
        target.put((byte) 0);
        Hpack.encodeInteger(target, headerName.length(), 7);
        for (int j = 0; j < headerName.length(); j++) {
            target.put((byte) Hpack.toLower(headerName.charAt(j)));
        }
    }

    private void writeHuffmanEncodableValue(ByteBuffer target, String headerName, String val) {
        if (this.hpackHeaderFunction.shouldUseHuffman(headerName, val)) {
            if (!HPackHuffman.encode(target, val, false)) {
                writeValueString(target, val);
                return;
            }
            return;
        }
        writeValueString(target, val);
    }

    private void writeValueString(ByteBuffer target, String val) {
        target.put((byte) 0);
        Hpack.encodeInteger(target, val.length(), 7);
        for (int j = 0; j < val.length(); j++) {
            target.put((byte) val.charAt(j));
        }
    }

    private void addToDynamicTable(String headerName, String val) {
        int pos = this.entryPositionCounter;
        this.entryPositionCounter = pos + 1;
        DynamicTableEntry d = new DynamicTableEntry(headerName, val, -pos);
        this.dynamicTable.computeIfAbsent(headerName, k -> {
            return new ArrayList(1);
        }).add(d);
        this.evictionQueue.add(d);
        this.currentTableSize += d.getSize();
        runEvictionIfRequired();
        if (this.entryPositionCounter == Integer.MAX_VALUE) {
            preventPositionRollover();
        }
    }

    private void preventPositionRollover() {
        for (List<TableEntry> tableEntries : this.dynamicTable.values()) {
            for (TableEntry t : tableEntries) {
                t.position = t.getPosition();
            }
        }
        this.entryPositionCounter = 0;
    }

    private void runEvictionIfRequired() {
        TableEntry next;
        while (this.currentTableSize > this.maxTableSize && (next = this.evictionQueue.poll()) != null) {
            this.currentTableSize -= next.size;
            List<TableEntry> list = this.dynamicTable.get(next.name);
            list.remove(next);
            if (list.isEmpty()) {
                this.dynamicTable.remove(next.name);
            }
        }
    }

    private TableEntry findInTable(String headerName, String value) {
        TableEntry[] staticTable = ENCODING_STATIC_TABLE.get(headerName);
        if (staticTable != null) {
            for (TableEntry st : staticTable) {
                if (st.value != null && st.value.equals(value)) {
                    return st;
                }
            }
        }
        List<TableEntry> dynamic = this.dynamicTable.get(headerName);
        if (dynamic != null) {
            for (TableEntry st2 : dynamic) {
                if (st2.value.equals(value)) {
                    return st2;
                }
            }
        }
        if (staticTable != null) {
            return staticTable[0];
        }
        return null;
    }

    public void setMaxTableSize(int newSize) {
        this.newMaxHeaderSize = newSize;
        if (this.minNewMaxHeaderSize == -1) {
            this.minNewMaxHeaderSize = newSize;
        } else {
            this.minNewMaxHeaderSize = Math.min(newSize, this.minNewMaxHeaderSize);
        }
    }

    private void handleTableSizeChange(ByteBuffer target) {
        if (this.newMaxHeaderSize == -1) {
            return;
        }
        if (this.minNewMaxHeaderSize != this.newMaxHeaderSize) {
            target.put((byte) 32);
            Hpack.encodeInteger(target, this.minNewMaxHeaderSize, 5);
        }
        target.put((byte) 32);
        Hpack.encodeInteger(target, this.newMaxHeaderSize, 5);
        this.maxTableSize = this.newMaxHeaderSize;
        runEvictionIfRequired();
        this.newMaxHeaderSize = -1;
        this.minNewMaxHeaderSize = -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/HpackEncoder$TableEntry.class */
    public static class TableEntry {
        private final String name;
        private final String value;
        private final int size;
        private int position;

        private TableEntry(String name, String value, int position) {
            this.name = name;
            this.value = value;
            this.position = position;
            if (value != null) {
                this.size = 32 + name.length() + value.length();
            } else {
                this.size = -1;
            }
        }

        int getPosition() {
            return this.position;
        }

        int getSize() {
            return this.size;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/HpackEncoder$DynamicTableEntry.class */
    public class DynamicTableEntry extends TableEntry {
        private DynamicTableEntry(String name, String value, int position) {
            super(name, value, position);
        }

        @Override // org.apache.coyote.http2.HpackEncoder.TableEntry
        int getPosition() {
            return super.getPosition() + HpackEncoder.this.entryPositionCounter + Hpack.STATIC_TABLE_LENGTH;
        }
    }
}
