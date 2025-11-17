package cn.hutool.poi.excel.sax.handler;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/handler/MapRowHandler.class */
public abstract class MapRowHandler extends AbstractRowHandler<Map<String, Object>> {
    private final int headerRowIndex;
    List<String> headerList;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -574859861:
                if (implMethodName.equals("lambda$new$533315fa$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 7 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/poi/excel/sax/handler/MapRowHandler") && lambda.getImplMethodSignature().equals("(Ljava/util/List;)Ljava/util/Map;")) {
                    MapRowHandler mapRowHandler = (MapRowHandler) lambda.getCapturedArg(0);
                    return rowList -> {
                        return IterUtil.toMap(this.headerList, rowList);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public MapRowHandler(int headerRowIndex, int startRowIndex, int endRowIndex) {
        super(startRowIndex, endRowIndex);
        this.headerRowIndex = headerRowIndex;
        this.convertFunc = rowList -> {
            return IterUtil.toMap(this.headerList, rowList);
        };
    }

    @Override // cn.hutool.poi.excel.sax.handler.AbstractRowHandler, cn.hutool.poi.excel.sax.handler.RowHandler
    public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
        if (rowIndex == this.headerRowIndex) {
            this.headerList = ListUtil.unmodifiable(Convert.toList(String.class, rowCells));
        } else {
            super.handle(sheetIndex, rowIndex, rowCells);
        }
    }
}
