package cn.hutool.poi.excel.sax;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.tomcat.jni.SSL;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/Excel03SaxReader.class */
public class Excel03SaxReader implements HSSFListener, ExcelSaxReader<Excel03SaxReader> {
    private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
    private HSSFWorkbook stubWorkbook;
    private SSTRecord sstRecord;
    private FormatTrackingHSSFListener formatListener;
    private boolean isOutputNextStringRecord;
    private String sheetName;
    private final RowHandler rowHandler;
    private final boolean isOutputFormulaValues = true;
    private final List<BoundSheetRecord> boundSheetRecords = new ArrayList();
    private List<Object> rowCellList = new ArrayList();
    private int rid = -1;
    private int curRid = -1;

    public Excel03SaxReader(RowHandler rowHandler) {
        this.rowHandler = rowHandler;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.sax.ExcelSaxReader
    public Excel03SaxReader read(File file, String idOrRidOrSheetName) throws POIException {
        try {
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(file, true);
            Throwable th = null;
            try {
                Excel03SaxReader read = read(poifsFileSystem, idOrRidOrSheetName);
                if (poifsFileSystem != null) {
                    if (0 != 0) {
                        try {
                            poifsFileSystem.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        poifsFileSystem.close();
                    }
                }
                return read;
            } finally {
            }
        } catch (IOException e) {
            throw new POIException(e);
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.poi.excel.sax.ExcelSaxReader
    public Excel03SaxReader read(InputStream excelStream, String idOrRidOrSheetName) throws POIException {
        try {
            return read(new POIFSFileSystem(excelStream), idOrRidOrSheetName);
        } catch (IOException e) {
            throw new POIException(e);
        }
    }

    public Excel03SaxReader read(POIFSFileSystem fs, String idOrRidOrSheetName) throws POIException {
        this.rid = getSheetIndex(idOrRidOrSheetName);
        this.formatListener = new FormatTrackingHSSFListener(new MissingRecordAwareHSSFListener(this));
        HSSFRequest request = new HSSFRequest();
        request.addListenerForAllRecords(this.formatListener);
        HSSFEventFactory factory = new HSSFEventFactory();
        try {
            try {
                factory.processWorkbookEvents(request, fs);
                IoUtil.close((Closeable) fs);
                return this;
            } catch (IOException e) {
                throw new POIException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) fs);
            throw th;
        }
    }

    public int getSheetIndex() {
        return this.rid;
    }

    public String getSheetName() {
        if (null != this.sheetName) {
            return this.sheetName;
        }
        if (this.boundSheetRecords.size() > this.rid) {
            return this.boundSheetRecords.get(this.rid > -1 ? this.rid : this.curRid).getSheetname();
        }
        return null;
    }

    public void processRecord(Record record) {
        if (this.rid > -1 && this.curRid > this.rid) {
            return;
        }
        if (record instanceof BoundSheetRecord) {
            BoundSheetRecord boundSheetRecord = (BoundSheetRecord) record;
            this.boundSheetRecords.add(boundSheetRecord);
            String currentSheetName = boundSheetRecord.getSheetname();
            if (null != this.sheetName && StrUtil.equals(this.sheetName, currentSheetName)) {
                this.rid = this.boundSheetRecords.size() - 1;
                return;
            }
            return;
        }
        if (record instanceof SSTRecord) {
            this.sstRecord = (SSTRecord) record;
            return;
        }
        if (record instanceof BOFRecord) {
            BOFRecord bofRecord = (BOFRecord) record;
            if (bofRecord.getType() == 16) {
                if (this.workbookBuildingListener != null && this.stubWorkbook == null) {
                    this.stubWorkbook = this.workbookBuildingListener.getStubHSSFWorkbook();
                }
                this.curRid++;
                return;
            }
            return;
        }
        if (record instanceof EOFRecord) {
            if (this.rid < 0 && null != this.sheetName) {
                throw new POIException("Sheet [{}] not exist!", this.sheetName);
            }
            if (this.curRid != -1 && isProcessCurrentSheet()) {
                processLastCellSheet();
                return;
            }
            return;
        }
        if (isProcessCurrentSheet()) {
            if (record instanceof MissingCellDummyRecord) {
                MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
                addToRowCellList(mc);
            } else if (record instanceof LastCellOfRowDummyRecord) {
                processLastCell((LastCellOfRowDummyRecord) record);
            } else {
                processCellValue(record);
            }
        }
    }

    private void addToRowCellList(MissingCellDummyRecord record) {
        addToRowCellList(record.getRow(), record.getColumn(), "");
    }

    private void addToRowCellList(CellValueRecordInterface record, Object value) {
        addToRowCellList(record.getRow(), record.getColumn(), value);
    }

    private void addToRowCellList(int row, int column, Object value) {
        while (column > this.rowCellList.size()) {
            this.rowCellList.add("");
            this.rowHandler.handleCell(this.curRid, row, this.rowCellList.size() - 1, value, null);
        }
        this.rowCellList.add(column, value);
        this.rowHandler.handleCell(this.curRid, row, column, value, null);
    }

    private void processCellValue(Record record) {
        Object value = null;
        switch (record.getSid()) {
            case 6:
                FormulaRecord formulaRec = (FormulaRecord) record;
                if (Double.isNaN(formulaRec.getValue())) {
                    this.isOutputNextStringRecord = true;
                } else {
                    value = ExcelSaxUtil.getNumberOrDateValue(formulaRec, formulaRec.getValue(), this.formatListener);
                }
                addToRowCellList(formulaRec, value);
                return;
            case 253:
                LabelSSTRecord lsrec = (LabelSSTRecord) record;
                if (null != this.sstRecord) {
                    value = this.sstRecord.getString(lsrec.getSSTIndex()).toString();
                }
                addToRowCellList(lsrec, ObjectUtil.defaultIfNull((String) value, ""));
                return;
            case SSL.SSL_INFO_SERVER_M_VERSION /* 513 */:
                addToRowCellList((BlankRecord) record, "");
                return;
            case SSL.SSL_INFO_SERVER_V_START /* 515 */:
                NumberRecord numrec = (NumberRecord) record;
                addToRowCellList(numrec, ExcelSaxUtil.getNumberOrDateValue(numrec, numrec.getValue(), this.formatListener));
                return;
            case SSL.SSL_INFO_SERVER_V_END /* 516 */:
                LabelRecord lrec = (LabelRecord) record;
                addToRowCellList(lrec, lrec.getValue());
                return;
            case SSL.SSL_INFO_SERVER_A_SIG /* 517 */:
                BoolErrRecord berec = (BoolErrRecord) record;
                addToRowCellList(berec, Boolean.valueOf(berec.getBooleanValue()));
                return;
            case SSL.SSL_INFO_SERVER_CERT /* 519 */:
                if (this.isOutputNextStringRecord) {
                    this.isOutputNextStringRecord = false;
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void processLastCell(LastCellOfRowDummyRecord lastCell) {
        this.rowHandler.handle(this.curRid, lastCell.getRow(), this.rowCellList);
        this.rowCellList = new ArrayList(this.rowCellList.size());
    }

    private void processLastCellSheet() {
        this.rowHandler.doAfterAllAnalysed();
    }

    private boolean isProcessCurrentSheet() {
        return (this.rid < 0 && null == this.sheetName) || this.rid == this.curRid;
    }

    private int getSheetIndex(String idOrRidOrSheetName) {
        Assert.notBlank(idOrRidOrSheetName, "id or rid or sheetName must be not blank!", new Object[0]);
        if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, ExcelSaxReader.RID_PREFIX)) {
            return Integer.parseInt(StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, ExcelSaxReader.RID_PREFIX));
        }
        if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, ExcelSaxReader.SHEET_NAME_PREFIX)) {
            this.sheetName = StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, ExcelSaxReader.SHEET_NAME_PREFIX);
            return -1;
        }
        try {
            return Integer.parseInt(idOrRidOrSheetName);
        } catch (NumberFormatException e) {
            this.sheetName = idOrRidOrSheetName;
            return -1;
        }
    }
}
