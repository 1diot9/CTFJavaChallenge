package org.apache.coyote.http2;

import java.nio.ByteBuffer;
import org.apache.coyote.http2.Hpack;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/HpackDecoder.class */
public class HpackDecoder {
    private static final Log log = LogFactory.getLog((Class<?>) HpackDecoder.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) HpackDecoder.class);
    private static final int DEFAULT_RING_BUFFER_SIZE = 10;
    private HeaderEmitter headerEmitter;
    private Hpack.HeaderField[] headerTable;
    private int firstSlotPosition;
    private int filledTableSlots;
    private int currentMemorySize;
    private int maxMemorySizeHard;
    private int maxMemorySizeSoft;
    private int maxHeaderCount;
    private int maxHeaderSize;
    private volatile int headerCount;
    private volatile boolean countedCookie;
    private volatile int headerSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/HpackDecoder$HeaderEmitter.class */
    public interface HeaderEmitter {
        void emitHeader(String str, String str2) throws HpackException;

        void setHeaderException(StreamException streamException);

        void validateHeaders() throws StreamException;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HpackDecoder(int maxMemorySize) {
        this.firstSlotPosition = 0;
        this.filledTableSlots = 0;
        this.currentMemorySize = 0;
        this.maxHeaderCount = 100;
        this.maxHeaderSize = 8192;
        this.headerCount = 0;
        this.headerSize = 0;
        this.maxMemorySizeHard = maxMemorySize;
        this.maxMemorySizeSoft = maxMemorySize;
        this.headerTable = new Hpack.HeaderField[10];
    }

    HpackDecoder() {
        this(4096);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void decode(ByteBuffer buffer) throws HpackException {
        while (buffer.hasRemaining()) {
            int originalPos = buffer.position();
            byte b = buffer.get();
            if ((b & 128) != 0) {
                buffer.position(buffer.position() - 1);
                int index = Hpack.decodeInteger(buffer, 7);
                if (index == -1) {
                    buffer.position(originalPos);
                    return;
                } else {
                    if (index == 0) {
                        throw new HpackException(sm.getString("hpackdecoder.zeroNotValidHeaderTableIndex"));
                    }
                    handleIndex(index);
                }
            } else if ((b & 64) != 0) {
                String headerName = readHeaderName(buffer, 6);
                if (headerName == null) {
                    buffer.position(originalPos);
                    return;
                }
                String headerValue = readHpackString(buffer);
                if (headerValue == null) {
                    buffer.position(originalPos);
                    return;
                } else {
                    emitHeader(headerName, headerValue);
                    addEntryToHeaderTable(new Hpack.HeaderField(headerName, headerValue));
                }
            } else if ((b & 240) == 0) {
                String headerName2 = readHeaderName(buffer, 4);
                if (headerName2 == null) {
                    buffer.position(originalPos);
                    return;
                }
                String headerValue2 = readHpackString(buffer);
                if (headerValue2 == null) {
                    buffer.position(originalPos);
                    return;
                }
                emitHeader(headerName2, headerValue2);
            } else if ((b & 240) == 16) {
                String headerName3 = readHeaderName(buffer, 4);
                if (headerName3 == null) {
                    buffer.position(originalPos);
                    return;
                }
                String headerValue3 = readHpackString(buffer);
                if (headerValue3 == null) {
                    buffer.position(originalPos);
                    return;
                }
                emitHeader(headerName3, headerValue3);
            } else if ((b & 224) == 32) {
                if (!handleMaxMemorySizeChange(buffer, originalPos)) {
                    return;
                }
            } else {
                throw new RuntimeException(sm.getString("hpackdecoder.notImplemented"));
            }
        }
    }

    private boolean handleMaxMemorySizeChange(ByteBuffer buffer, int originalPos) throws HpackException {
        if (this.headerCount != 0) {
            throw new HpackException(sm.getString("hpackdecoder.tableSizeUpdateNotAtStart"));
        }
        buffer.position(buffer.position() - 1);
        int size = Hpack.decodeInteger(buffer, 5);
        if (size == -1) {
            buffer.position(originalPos);
            return false;
        }
        if (size > this.maxMemorySizeHard) {
            throw new HpackException(sm.getString("hpackdecoder.maxMemorySizeExceeded", Integer.valueOf(size), Integer.valueOf(this.maxMemorySizeHard)));
        }
        this.maxMemorySizeSoft = size;
        if (this.currentMemorySize > this.maxMemorySizeSoft) {
            int newTableSlots = this.filledTableSlots;
            int tableLength = this.headerTable.length;
            int newSize = this.currentMemorySize;
            while (newSize > this.maxMemorySizeSoft) {
                int clearIndex = this.firstSlotPosition;
                this.firstSlotPosition++;
                if (this.firstSlotPosition == tableLength) {
                    this.firstSlotPosition = 0;
                }
                Hpack.HeaderField oldData = this.headerTable[clearIndex];
                this.headerTable[clearIndex] = null;
                newSize -= oldData.size;
                newTableSlots--;
            }
            this.filledTableSlots = newTableSlots;
            this.currentMemorySize = newSize;
            return true;
        }
        return true;
    }

    private String readHeaderName(ByteBuffer buffer, int prefixLength) throws HpackException {
        buffer.position(buffer.position() - 1);
        int index = Hpack.decodeInteger(buffer, prefixLength);
        if (index == -1) {
            return null;
        }
        if (index != 0) {
            return handleIndexedHeaderName(index);
        }
        return readHpackString(buffer);
    }

    private String readHpackString(ByteBuffer buffer) throws HpackException {
        if (!buffer.hasRemaining()) {
            return null;
        }
        byte data = buffer.get(buffer.position());
        int length = Hpack.decodeInteger(buffer, 7);
        if (buffer.remaining() < length || length == -1) {
            return null;
        }
        boolean huffman = (data & 128) != 0;
        if (huffman) {
            return readHuffmanString(length, buffer);
        }
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append((char) buffer.get());
        }
        return stringBuilder.toString();
    }

    private String readHuffmanString(int length, ByteBuffer buffer) throws HpackException {
        StringBuilder stringBuilder = new StringBuilder(length);
        HPackHuffman.decode(buffer, length, stringBuilder);
        return stringBuilder.toString();
    }

    private String handleIndexedHeaderName(int index) throws HpackException {
        if (index <= Hpack.STATIC_TABLE_LENGTH) {
            return Hpack.STATIC_TABLE[index].name;
        }
        if (index > Hpack.STATIC_TABLE_LENGTH + this.filledTableSlots) {
            throw new HpackException(sm.getString("hpackdecoder.headerTableIndexInvalid", Integer.valueOf(index), Integer.valueOf(Hpack.STATIC_TABLE_LENGTH), Integer.valueOf(this.filledTableSlots)));
        }
        int adjustedIndex = getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
        Hpack.HeaderField res = this.headerTable[adjustedIndex];
        if (res == null) {
            throw new HpackException(sm.getString("hpackdecoder.nullHeader", Integer.valueOf(index)));
        }
        return res.name;
    }

    private void handleIndex(int index) throws HpackException {
        if (index <= Hpack.STATIC_TABLE_LENGTH) {
            addStaticTableEntry(index);
            return;
        }
        int adjustedIndex = getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("hpackdecoder.useDynamic", Integer.valueOf(adjustedIndex)));
        }
        Hpack.HeaderField headerField = this.headerTable[adjustedIndex];
        emitHeader(headerField.name, headerField.value);
    }

    int getRealIndex(int index) throws HpackException {
        int realIndex = (this.firstSlotPosition + (this.filledTableSlots - index)) % this.headerTable.length;
        if (realIndex < 0) {
            throw new HpackException(sm.getString("hpackdecoder.headerTableIndexInvalid", Integer.valueOf(index), Integer.valueOf(Hpack.STATIC_TABLE_LENGTH), Integer.valueOf(this.filledTableSlots)));
        }
        return realIndex;
    }

    private void addStaticTableEntry(int index) throws HpackException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("hpackdecoder.useStatic", Integer.valueOf(index)));
        }
        Hpack.HeaderField entry = Hpack.STATIC_TABLE[index];
        emitHeader(entry.name, entry.value == null ? "" : entry.value);
    }

    private void addEntryToHeaderTable(Hpack.HeaderField entry) {
        if (entry.size > this.maxMemorySizeSoft) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("hpackdecoder.clearDynamic"));
            }
            while (this.filledTableSlots > 0) {
                this.headerTable[this.firstSlotPosition] = null;
                this.firstSlotPosition++;
                if (this.firstSlotPosition == this.headerTable.length) {
                    this.firstSlotPosition = 0;
                }
                this.filledTableSlots--;
            }
            this.currentMemorySize = 0;
            return;
        }
        resizeIfRequired();
        int newTableSlots = this.filledTableSlots + 1;
        int tableLength = this.headerTable.length;
        int index = (this.firstSlotPosition + this.filledTableSlots) % tableLength;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("hpackdecoder.addDynamic", Integer.valueOf(index), entry.name, entry.value));
        }
        this.headerTable[index] = entry;
        int newSize = this.currentMemorySize + entry.size;
        while (newSize > this.maxMemorySizeSoft) {
            int clearIndex = this.firstSlotPosition;
            this.firstSlotPosition++;
            if (this.firstSlotPosition == tableLength) {
                this.firstSlotPosition = 0;
            }
            Hpack.HeaderField oldData = this.headerTable[clearIndex];
            this.headerTable[clearIndex] = null;
            newSize -= oldData.size;
            newTableSlots--;
        }
        this.filledTableSlots = newTableSlots;
        this.currentMemorySize = newSize;
    }

    private void resizeIfRequired() {
        if (this.filledTableSlots == this.headerTable.length) {
            Hpack.HeaderField[] newArray = new Hpack.HeaderField[this.headerTable.length + 10];
            for (int i = 0; i < this.headerTable.length; i++) {
                newArray[i] = this.headerTable[(this.firstSlotPosition + i) % this.headerTable.length];
            }
            this.firstSlotPosition = 0;
            this.headerTable = newArray;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HeaderEmitter getHeaderEmitter() {
        return this.headerEmitter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHeaderEmitter(HeaderEmitter headerEmitter) {
        this.headerEmitter = headerEmitter;
        this.headerCount = 0;
        this.countedCookie = false;
        this.headerSize = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMaxHeaderCount(int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    private void emitHeader(String name, String value) throws HpackException {
        if ("cookie".equals(name)) {
            if (!this.countedCookie) {
                this.headerCount++;
                this.countedCookie = true;
            }
        } else {
            this.headerCount++;
        }
        int inc = 3 + name.length() + value.length();
        this.headerSize += inc;
        if (!isHeaderCountExceeded() && !isHeaderSizeExceeded(0)) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("hpackdecoder.emitHeader", name, value));
            }
            this.headerEmitter.emitHeader(name, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHeaderCountExceeded() {
        return this.maxHeaderCount >= 0 && this.headerCount > this.maxHeaderCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHeaderSizeExceeded(int unreadSize) {
        return this.maxHeaderSize >= 0 && this.headerSize + unreadSize > this.maxHeaderSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHeaderSwallowSizeExceeded(int unreadSize) {
        return this.maxHeaderSize >= 0 && this.headerSize + unreadSize > 2 * this.maxHeaderSize;
    }

    int getFirstSlotPosition() {
        return this.firstSlotPosition;
    }

    Hpack.HeaderField[] getHeaderTable() {
        return this.headerTable;
    }

    int getFilledTableSlots() {
        return this.filledTableSlots;
    }

    int getCurrentMemorySize() {
        return this.currentMemorySize;
    }

    int getMaxMemorySizeSoft() {
        return this.maxMemorySizeSoft;
    }
}
