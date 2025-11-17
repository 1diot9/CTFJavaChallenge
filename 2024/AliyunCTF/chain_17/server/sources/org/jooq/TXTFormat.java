package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TXTFormat.class */
public final class TXTFormat {
    public static final TXTFormat DEFAULT = new TXTFormat();
    final int maxRows;
    final int minColWidth;
    final int maxColWidth;
    final boolean horizontalTableBorder;
    final boolean horizontalHeaderBorder;
    final boolean horizontalCellBorder;
    final boolean verticalTableBorder;
    final boolean verticalCellBorder;
    final boolean intersectLines;

    public TXTFormat() {
        this(Integer.MAX_VALUE, 4, Integer.MAX_VALUE, true, true, false, true, true, true);
    }

    private TXTFormat(int maxRows, int minColWidth, int maxColWidth, boolean horizontalTableBorder, boolean horizontalHeaderBorder, boolean horizontalCellBorder, boolean verticalTableBorder, boolean verticalCellBorder, boolean intersectLines) {
        this.maxRows = maxRows;
        this.minColWidth = minColWidth;
        this.maxColWidth = maxColWidth;
        this.horizontalTableBorder = horizontalTableBorder;
        this.horizontalHeaderBorder = horizontalHeaderBorder;
        this.horizontalCellBorder = horizontalCellBorder;
        this.verticalTableBorder = verticalTableBorder;
        this.verticalCellBorder = verticalCellBorder;
        this.intersectLines = intersectLines;
    }

    @NotNull
    public TXTFormat maxRows(int newMaxRows) {
        return new TXTFormat(newMaxRows, this.minColWidth, this.maxColWidth, this.horizontalTableBorder, this.horizontalHeaderBorder, this.horizontalCellBorder, this.verticalTableBorder, this.verticalCellBorder, this.intersectLines);
    }

    public int maxRows() {
        return this.maxRows;
    }

    @NotNull
    public TXTFormat minColWidth(int newMinColWidth) {
        return new TXTFormat(this.maxRows, newMinColWidth, this.maxColWidth, this.horizontalTableBorder, this.horizontalHeaderBorder, this.horizontalCellBorder, this.verticalTableBorder, this.verticalCellBorder, this.intersectLines);
    }

    public int minColWidth() {
        return this.minColWidth;
    }

    @NotNull
    public TXTFormat maxColWidth(int newMaxColWidth) {
        return new TXTFormat(this.maxRows, this.minColWidth, newMaxColWidth, this.horizontalTableBorder, this.horizontalHeaderBorder, this.horizontalCellBorder, this.verticalTableBorder, this.verticalCellBorder, this.intersectLines);
    }

    public int maxColWidth() {
        return this.maxColWidth;
    }

    @NotNull
    public TXTFormat horizontalTableBorder(boolean newHorizontalTableBorder) {
        return new TXTFormat(this.maxRows, this.minColWidth, this.maxColWidth, newHorizontalTableBorder, this.horizontalHeaderBorder, this.horizontalCellBorder, this.verticalTableBorder, this.verticalCellBorder, this.intersectLines);
    }

    public boolean horizontalTableBorder() {
        return this.horizontalTableBorder;
    }

    @NotNull
    public TXTFormat horizontalHeaderBorder(boolean newHorizontalHeaderBorder) {
        return new TXTFormat(this.maxRows, this.minColWidth, this.maxColWidth, this.horizontalTableBorder, newHorizontalHeaderBorder, this.horizontalCellBorder, this.verticalTableBorder, this.verticalCellBorder, this.intersectLines);
    }

    public boolean horizontalHeaderBorder() {
        return this.horizontalHeaderBorder;
    }

    @NotNull
    public TXTFormat horizontalCellBorder(boolean newHorizontalCellBorder) {
        return new TXTFormat(this.maxRows, this.minColWidth, this.maxColWidth, this.horizontalTableBorder, this.horizontalHeaderBorder, newHorizontalCellBorder, this.verticalTableBorder, this.verticalCellBorder, this.intersectLines);
    }

    public boolean horizontalCellBorder() {
        return this.horizontalCellBorder;
    }

    @NotNull
    public TXTFormat verticalTableBorder(boolean newVerticalTableBorder) {
        return new TXTFormat(this.maxRows, this.minColWidth, this.maxColWidth, this.horizontalTableBorder, this.horizontalHeaderBorder, this.horizontalCellBorder, newVerticalTableBorder, this.verticalCellBorder, this.intersectLines);
    }

    public boolean verticalTableBorder() {
        return this.verticalTableBorder;
    }

    @NotNull
    public TXTFormat verticalCellBorder(boolean newVerticalCellBorder) {
        return new TXTFormat(this.maxRows, this.minColWidth, this.maxColWidth, this.horizontalTableBorder, this.horizontalHeaderBorder, this.horizontalCellBorder, this.verticalTableBorder, newVerticalCellBorder, this.intersectLines);
    }

    public boolean verticalCellBorder() {
        return this.verticalCellBorder;
    }

    @NotNull
    public TXTFormat intersectLines(boolean newIntersectLines) {
        return new TXTFormat(this.maxRows, this.minColWidth, this.maxColWidth, this.horizontalTableBorder, this.horizontalHeaderBorder, this.horizontalCellBorder, this.verticalTableBorder, this.verticalCellBorder, newIntersectLines);
    }

    public boolean intersectLines() {
        return this.intersectLines;
    }
}
