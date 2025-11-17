package org.jooq;

import java.text.DecimalFormat;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ChartFormat.class */
public final class ChartFormat {
    public static final ChartFormat DEFAULT = new ChartFormat();
    final Output output;
    final Type type;
    final Display display;
    final int width;
    final int height;
    final int category;
    final boolean categoryAsText;
    final int[] values;
    final char[] shades;
    final boolean showHorizontalLegend;
    final boolean showVerticalLegend;
    final String newline;
    final DecimalFormat numericFormat;
    final DecimalFormat percentFormat;

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ChartFormat$Display.class */
    public enum Display {
        DEFAULT,
        STACKED,
        HUNDRED_PERCENT_STACKED
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ChartFormat$Output.class */
    public enum Output {
        ASCII
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ChartFormat$Type.class */
    public enum Type {
        AREA
    }

    public ChartFormat() {
        this(Output.ASCII, Type.AREA, Display.STACKED, 80, 25, 0, true, new int[]{1}, new char[]{9608, 9619, 9618, 9617}, true, true, "\n", new DecimalFormat("###,###.00"), new DecimalFormat("##0.00'%'"));
    }

    private ChartFormat(Output output, Type type, Display display, int width, int height, int category, boolean categoryAsText, int[] values, char[] shades, boolean showHorizontalLegend, boolean showVerticalLegend, String newline, DecimalFormat numericFormat, DecimalFormat percentFormat) {
        this.output = output;
        this.type = type;
        this.display = display;
        this.width = width;
        this.height = height;
        this.category = category;
        this.categoryAsText = categoryAsText;
        this.values = values;
        this.shades = shades;
        this.showHorizontalLegend = showHorizontalLegend;
        this.showVerticalLegend = showVerticalLegend;
        this.newline = newline;
        this.numericFormat = numericFormat;
        this.percentFormat = percentFormat;
    }

    @NotNull
    public ChartFormat output(Output newOutput) {
        return new ChartFormat(newOutput, this.type, this.display, this.width, this.height, this.category, this.categoryAsText, this.values, this.shades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, this.numericFormat, this.percentFormat);
    }

    @NotNull
    public Output output() {
        return this.output;
    }

    @NotNull
    public ChartFormat type(Type newType) {
        return new ChartFormat(this.output, newType, this.display, this.width, this.height, this.category, this.categoryAsText, this.values, this.shades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, this.numericFormat, this.percentFormat);
    }

    @NotNull
    public Type type() {
        return this.type;
    }

    @NotNull
    public ChartFormat display(Display newDisplay) {
        return new ChartFormat(this.output, this.type, newDisplay, this.width, this.height, this.category, this.categoryAsText, this.values, this.shades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, this.numericFormat, this.percentFormat);
    }

    @NotNull
    public Display display() {
        return this.display;
    }

    @NotNull
    public ChartFormat dimensions(int newWidth, int newHeight) {
        return new ChartFormat(this.output, this.type, this.display, newWidth, newHeight, this.category, this.categoryAsText, this.values, this.shades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, this.numericFormat, this.percentFormat);
    }

    @NotNull
    public ChartFormat width(int newWidth) {
        return dimensions(newWidth, this.height);
    }

    public int width() {
        return this.width;
    }

    @NotNull
    public ChartFormat height(int newHeight) {
        return dimensions(this.width, newHeight);
    }

    public int height() {
        return this.height;
    }

    @NotNull
    public ChartFormat category(int newCategory) {
        return new ChartFormat(this.output, this.type, this.display, this.width, this.height, newCategory, this.categoryAsText, this.values, this.shades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, this.numericFormat, this.percentFormat);
    }

    public int category() {
        return this.category;
    }

    @NotNull
    public ChartFormat categoryAsText(boolean newCategoryAsText) {
        return new ChartFormat(this.output, this.type, this.display, this.width, this.height, this.category, newCategoryAsText, this.values, this.shades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, this.numericFormat, this.percentFormat);
    }

    public boolean categoryAsText() {
        return this.categoryAsText;
    }

    @NotNull
    public ChartFormat values(int... newValues) {
        return new ChartFormat(this.output, this.type, this.display, this.width, this.height, this.category, this.categoryAsText, newValues, this.shades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, this.numericFormat, this.percentFormat);
    }

    public int[] values() {
        return this.values;
    }

    @NotNull
    public ChartFormat shades(char... newShades) {
        return new ChartFormat(this.output, this.type, this.display, this.width, this.height, this.category, this.categoryAsText, this.values, newShades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, this.numericFormat, this.percentFormat);
    }

    public char[] shades() {
        return this.shades;
    }

    @NotNull
    public ChartFormat showLegends(boolean newShowHorizontalLegend, boolean newShowVerticalLegend) {
        return new ChartFormat(this.output, this.type, this.display, this.width, this.height, this.category, this.categoryAsText, this.values, this.shades, newShowHorizontalLegend, newShowVerticalLegend, this.newline, this.numericFormat, this.percentFormat);
    }

    @NotNull
    public ChartFormat showHorizontalLegend(boolean newShowHorizontalLegend) {
        return showLegends(newShowHorizontalLegend, this.showVerticalLegend);
    }

    public boolean showHorizontalLegend() {
        return this.showHorizontalLegend;
    }

    @NotNull
    public ChartFormat showVerticalLegend(boolean newShowVerticalLegend) {
        return showLegends(this.showHorizontalLegend, newShowVerticalLegend);
    }

    public boolean showVerticalLegend() {
        return this.showVerticalLegend;
    }

    @NotNull
    public ChartFormat newline(String newNewline) {
        return new ChartFormat(this.output, this.type, this.display, this.width, this.height, this.category, this.categoryAsText, this.values, this.shades, this.showHorizontalLegend, this.showVerticalLegend, newNewline, this.numericFormat, this.percentFormat);
    }

    @NotNull
    public String newline() {
        return this.newline;
    }

    @NotNull
    public ChartFormat numericFormat(DecimalFormat newNumericFormat) {
        return new ChartFormat(this.output, this.type, this.display, this.width, this.height, this.category, this.categoryAsText, this.values, this.shades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, newNumericFormat, this.percentFormat);
    }

    @NotNull
    public DecimalFormat numericFormat() {
        return this.numericFormat;
    }

    @NotNull
    public ChartFormat percentFormat(DecimalFormat newPercentFormat) {
        return new ChartFormat(this.output, this.type, this.display, this.width, this.height, this.category, this.categoryAsText, this.values, this.shades, this.showHorizontalLegend, this.showVerticalLegend, this.newline, this.numericFormat, newPercentFormat);
    }

    @NotNull
    public DecimalFormat percentFormat() {
        return this.percentFormat;
    }
}
