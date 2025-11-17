package cn.hutool.poi.excel.cell;

import java.io.Serializable;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/excel/cell/CellLocation.class */
public class CellLocation implements Serializable {
    private static final long serialVersionUID = 1;
    private int x;
    private int y;

    public CellLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CellLocation that = (CellLocation) o;
        return this.x == that.x && this.y == that.y;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.x), Integer.valueOf(this.y));
    }

    public String toString() {
        return "CellLocation{x=" + this.x + ", y=" + this.y + '}';
    }
}
