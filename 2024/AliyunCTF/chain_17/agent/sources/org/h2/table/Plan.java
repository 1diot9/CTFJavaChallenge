package org.h2.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.Trace;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/Plan.class */
public class Plan {
    private final TableFilter[] filters;
    private final HashMap<TableFilter, PlanItem> planItems = new HashMap<>();
    private final Expression[] allConditions;
    private final TableFilter[] allFilters;

    public Plan(TableFilter[] tableFilterArr, int i, Expression expression) {
        this.filters = new TableFilter[i];
        System.arraycopy(tableFilterArr, 0, this.filters, 0, i);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (expression != null) {
            arrayList.add(expression);
        }
        for (int i2 = 0; i2 < i; i2++) {
            tableFilterArr[i2].visit(tableFilter -> {
                arrayList2.add(tableFilter);
                if (tableFilter.getJoinCondition() != null) {
                    arrayList.add(tableFilter.getJoinCondition());
                }
            });
        }
        this.allConditions = (Expression[]) arrayList.toArray(new Expression[0]);
        this.allFilters = (TableFilter[]) arrayList2.toArray(new TableFilter[0]);
    }

    public PlanItem getItem(TableFilter tableFilter) {
        return this.planItems.get(tableFilter);
    }

    public TableFilter[] getFilters() {
        return this.filters;
    }

    public void removeUnusableIndexConditions() {
        for (int i = 0; i < this.allFilters.length; i++) {
            TableFilter tableFilter = this.allFilters[i];
            setEvaluatable(tableFilter, true);
            if (i < this.allFilters.length - 1) {
                tableFilter.optimizeFullCondition();
            }
            tableFilter.removeUnusableIndexConditions();
        }
        for (TableFilter tableFilter2 : this.allFilters) {
            setEvaluatable(tableFilter2, false);
        }
    }

    public double calculateCost(SessionLocal sessionLocal, AllColumnsForPlan allColumnsForPlan) {
        Trace trace = sessionLocal.getTrace();
        if (trace.isDebugEnabled()) {
            trace.debug("Plan       : calculate cost for plan {0}", Arrays.toString(this.allFilters));
        }
        double d = 1.0d;
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= this.allFilters.length) {
                break;
            }
            TableFilter tableFilter = this.allFilters[i];
            if (trace.isDebugEnabled()) {
                trace.debug("Plan       :   for table filter {0}", tableFilter);
            }
            PlanItem bestPlanItem = tableFilter.getBestPlanItem(sessionLocal, this.allFilters, i, allColumnsForPlan);
            this.planItems.put(tableFilter, bestPlanItem);
            if (trace.isDebugEnabled()) {
                trace.debug("Plan       :   best plan item cost {0} index {1}", Double.valueOf(bestPlanItem.cost), bestPlanItem.getIndex().getPlanSQL());
            }
            d += d * bestPlanItem.cost;
            setEvaluatable(tableFilter, true);
            Expression joinCondition = tableFilter.getJoinCondition();
            if (joinCondition == null || joinCondition.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
                i++;
            } else {
                z = true;
                break;
            }
        }
        if (z) {
            d = Double.POSITIVE_INFINITY;
        }
        if (trace.isDebugEnabled()) {
            sessionLocal.getTrace().debug("Plan       : plan cost {0}", Double.valueOf(d));
        }
        for (TableFilter tableFilter2 : this.allFilters) {
            setEvaluatable(tableFilter2, false);
        }
        return d;
    }

    private void setEvaluatable(TableFilter tableFilter, boolean z) {
        tableFilter.setEvaluatable(tableFilter, z);
        for (Expression expression : this.allConditions) {
            expression.setEvaluatable(tableFilter, z);
        }
    }
}
