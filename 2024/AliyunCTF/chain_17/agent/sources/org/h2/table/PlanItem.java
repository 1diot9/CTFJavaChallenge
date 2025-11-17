package org.h2.table;

import org.h2.index.Index;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/PlanItem.class */
public class PlanItem {
    double cost;
    private int[] masks;
    private Index index;
    private PlanItem joinPlan;
    private PlanItem nestedJoinPlan;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMasks(int[] iArr) {
        this.masks = iArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getMasks() {
        return this.masks;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIndex(Index index) {
        this.index = index;
    }

    public Index getIndex() {
        return this.index;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlanItem getJoinPlan() {
        return this.joinPlan;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlanItem getNestedJoinPlan() {
        return this.nestedJoinPlan;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setJoinPlan(PlanItem planItem) {
        this.joinPlan = planItem;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNestedJoinPlan(PlanItem planItem) {
        this.nestedJoinPlan = planItem;
    }
}
