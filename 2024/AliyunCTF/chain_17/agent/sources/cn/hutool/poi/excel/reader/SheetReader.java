package cn.hutool.poi.excel.reader;

import org.apache.poi.ss.usermodel.Sheet;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/reader/SheetReader.class */
public interface SheetReader<T> {
    T read(Sheet sheet);
}
