package org.h2.expression.analysis;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFunctionType.class */
public enum WindowFunctionType {
    ROW_NUMBER,
    RANK,
    DENSE_RANK,
    PERCENT_RANK,
    CUME_DIST,
    NTILE,
    LEAD,
    LAG,
    FIRST_VALUE,
    LAST_VALUE,
    NTH_VALUE,
    RATIO_TO_REPORT;

    public static WindowFunctionType get(String str) {
        boolean z = -1;
        switch (str.hashCode()) {
            case -1914066206:
                if (str.equals("FIRST_VALUE")) {
                    z = 8;
                    break;
                }
                break;
            case -1341790405:
                if (str.equals("CUME_DIST")) {
                    z = 4;
                    break;
                }
                break;
            case -898354668:
                if (str.equals("NTH_VALUE")) {
                    z = 10;
                    break;
                }
                break;
            case -609108376:
                if (str.equals("LAST_VALUE")) {
                    z = 9;
                    break;
                }
                break;
            case 75122:
                if (str.equals("LAG")) {
                    z = 7;
                    break;
                }
                break;
            case 2332508:
                if (str.equals("LEAD")) {
                    z = 6;
                    break;
                }
                break;
            case 2507820:
                if (str.equals("RANK")) {
                    z = true;
                    break;
                }
                break;
            case 74609660:
                if (str.equals("NTILE")) {
                    z = 5;
                    break;
                }
                break;
            case 374551588:
                if (str.equals("RATIO_TO_REPORT")) {
                    z = 11;
                    break;
                }
                break;
            case 575945068:
                if (str.equals("DENSE_RANK")) {
                    z = 2;
                    break;
                }
                break;
            case 1227250694:
                if (str.equals("PERCENT_RANK")) {
                    z = 3;
                    break;
                }
                break;
            case 2038860142:
                if (str.equals("ROW_NUMBER")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return ROW_NUMBER;
            case true:
                return RANK;
            case true:
                return DENSE_RANK;
            case true:
                return PERCENT_RANK;
            case true:
                return CUME_DIST;
            case true:
                return NTILE;
            case true:
                return LEAD;
            case true:
                return LAG;
            case true:
                return FIRST_VALUE;
            case true:
                return LAST_VALUE;
            case true:
                return NTH_VALUE;
            case true:
                return RATIO_TO_REPORT;
            default:
                return null;
        }
    }

    public String getSQL() {
        return name();
    }

    public boolean requiresWindowOrdering() {
        switch (this) {
            case RANK:
            case DENSE_RANK:
            case NTILE:
            case LEAD:
            case LAG:
                return true;
            default:
                return false;
        }
    }
}
