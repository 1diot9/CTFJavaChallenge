package cn.hutool.poi.excel.sax.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import java.lang.invoke.SerializedLambda;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/sax/handler/BeanRowHandler.class */
public abstract class BeanRowHandler<T> extends AbstractRowHandler<T> {
    private final int headerRowIndex;
    List<String> headerList;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 899171852:
                if (implMethodName.equals("lambda$new$4d50292$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 7 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/poi/excel/sax/handler/BeanRowHandler") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;Ljava/util/List;)Ljava/lang/Object;")) {
                    BeanRowHandler beanRowHandler = (BeanRowHandler) lambda.getCapturedArg(0);
                    Class cls = (Class) lambda.getCapturedArg(1);
                    return rowList -> {
                        return BeanUtil.toBean(IterUtil.toMap(this.headerList, rowList), cls);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public BeanRowHandler(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> clazz) {
        super(startRowIndex, endRowIndex);
        Assert.isTrue(headerRowIndex <= startRowIndex, "Header row must before the start row!", new Object[0]);
        this.headerRowIndex = headerRowIndex;
        this.convertFunc = rowList -> {
            return BeanUtil.toBean(IterUtil.toMap(this.headerList, rowList), clazz);
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
