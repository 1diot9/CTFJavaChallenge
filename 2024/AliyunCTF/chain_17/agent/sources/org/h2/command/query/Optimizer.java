package org.h2.command.query;

import java.util.BitSet;
import java.util.Random;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.table.Plan;
import org.h2.table.TableFilter;
import org.h2.util.Permutations;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/Optimizer.class */
public class Optimizer {
    private static final int MAX_BRUTE_FORCE_FILTERS = 7;
    private static final int MAX_BRUTE_FORCE = 2000;
    private static final int MAX_GENETIC = 500;
    private long startNs;
    private BitSet switched;
    private final TableFilter[] filters;
    private final Expression condition;
    private final SessionLocal session;
    private Plan bestPlan;
    private TableFilter topFilter;
    private double cost;
    private Random random;
    private final AllColumnsForPlan allColumnsSet;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Optimizer(TableFilter[] tableFilterArr, Expression expression, SessionLocal sessionLocal) {
        this.filters = tableFilterArr;
        this.condition = expression;
        this.session = sessionLocal;
        this.allColumnsSet = new AllColumnsForPlan(tableFilterArr);
    }

    private static int getMaxBruteForceFilters(int i) {
        int i2 = 0;
        int i3 = i;
        int i4 = i;
        while (i3 > 0 && i4 * ((i3 * (i3 - 1)) / 2) < 2000) {
            i3--;
            i4 *= i3;
            i2++;
        }
        return i2;
    }

    private void calculateBestPlan() {
        this.cost = -1.0d;
        if (this.filters.length == 1) {
            testPlan(this.filters);
            return;
        }
        this.startNs = System.nanoTime();
        if (this.filters.length <= 7) {
            calculateBruteForceAll();
            return;
        }
        calculateBruteForceSome();
        this.random = new Random(0L);
        calculateGenetic();
    }

    private void calculateFakePlan() {
        this.cost = -1.0d;
        this.bestPlan = new Plan(this.filters, this.filters.length, this.condition);
    }

    private boolean canStop(int i) {
        return (i & 127) == 0 && this.cost >= 0.0d && ((double) (System.nanoTime() - this.startNs)) > this.cost * 100000.0d;
    }

    private void calculateBruteForceAll() {
        TableFilter[] tableFilterArr = new TableFilter[this.filters.length];
        Permutations create = Permutations.create(this.filters, tableFilterArr);
        for (int i = 0; !canStop(i) && create.next(); i++) {
            testPlan(tableFilterArr);
        }
    }

    private void calculateBruteForceSome() {
        int maxBruteForceFilters = getMaxBruteForceFilters(this.filters.length);
        TableFilter[] tableFilterArr = new TableFilter[this.filters.length];
        Permutations create = Permutations.create(this.filters, tableFilterArr, maxBruteForceFilters);
        for (int i = 0; !canStop(i) && create.next(); i++) {
            for (TableFilter tableFilter : this.filters) {
                tableFilter.setUsed(false);
            }
            for (int i2 = 0; i2 < maxBruteForceFilters; i2++) {
                tableFilterArr[i2].setUsed(true);
            }
            for (int i3 = maxBruteForceFilters; i3 < this.filters.length; i3++) {
                double d = -1.0d;
                int i4 = -1;
                int i5 = 0;
                while (true) {
                    if (i5 >= this.filters.length) {
                        break;
                    }
                    if (!this.filters[i5].isUsed()) {
                        if (i3 == this.filters.length - 1) {
                            i4 = i5;
                            break;
                        }
                        tableFilterArr[i3] = this.filters[i5];
                        double calculateCost = new Plan(tableFilterArr, i3 + 1, this.condition).calculateCost(this.session, this.allColumnsSet);
                        if (d < 0.0d || calculateCost < d) {
                            d = calculateCost;
                            i4 = i5;
                        }
                    }
                    i5++;
                }
                this.filters[i4].setUsed(true);
                tableFilterArr[i3] = this.filters[i4];
            }
            testPlan(tableFilterArr);
        }
    }

    private void calculateGenetic() {
        TableFilter[] tableFilterArr = new TableFilter[this.filters.length];
        TableFilter[] tableFilterArr2 = new TableFilter[this.filters.length];
        for (int i = 0; i < 500 && !canStop(i); i++) {
            boolean z = (i & 127) == 0;
            if (!z) {
                System.arraycopy(tableFilterArr, 0, tableFilterArr2, 0, this.filters.length);
                if (!shuffleTwo(tableFilterArr2)) {
                    z = true;
                }
            }
            if (z) {
                this.switched = new BitSet();
                System.arraycopy(this.filters, 0, tableFilterArr, 0, this.filters.length);
                shuffleAll(tableFilterArr);
                System.arraycopy(tableFilterArr, 0, tableFilterArr2, 0, this.filters.length);
            }
            if (testPlan(tableFilterArr2)) {
                this.switched = new BitSet();
                System.arraycopy(tableFilterArr2, 0, tableFilterArr, 0, this.filters.length);
            }
        }
    }

    private boolean testPlan(TableFilter[] tableFilterArr) {
        Plan plan = new Plan(tableFilterArr, tableFilterArr.length, this.condition);
        double calculateCost = plan.calculateCost(this.session, this.allColumnsSet);
        if (this.cost < 0.0d || calculateCost < this.cost) {
            this.cost = calculateCost;
            this.bestPlan = plan;
            return true;
        }
        return false;
    }

    private void shuffleAll(TableFilter[] tableFilterArr) {
        for (int i = 0; i < tableFilterArr.length - 1; i++) {
            int nextInt = i + this.random.nextInt(tableFilterArr.length - i);
            if (nextInt != i) {
                TableFilter tableFilter = tableFilterArr[i];
                tableFilterArr[i] = tableFilterArr[nextInt];
                tableFilterArr[nextInt] = tableFilter;
            }
        }
    }

    private boolean shuffleTwo(TableFilter[] tableFilterArr) {
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i3 >= 20) {
                break;
            }
            i = this.random.nextInt(tableFilterArr.length);
            i2 = this.random.nextInt(tableFilterArr.length);
            if (i != i2) {
                if (i < i2) {
                    i = i2;
                    i2 = i;
                }
                int length = (i * tableFilterArr.length) + i2;
                if (!this.switched.get(length)) {
                    this.switched.set(length);
                    break;
                }
            }
            i3++;
        }
        if (i3 == 20) {
            return false;
        }
        TableFilter tableFilter = tableFilterArr[i];
        tableFilterArr[i] = tableFilterArr[i2];
        tableFilterArr[i2] = tableFilter;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void optimize(boolean z) {
        if (z) {
            calculateFakePlan();
        } else {
            calculateBestPlan();
            this.bestPlan.removeUnusableIndexConditions();
        }
        TableFilter[] filters = this.bestPlan.getFilters();
        this.topFilter = filters[0];
        for (int i = 0; i < filters.length - 1; i++) {
            filters[i].addJoin(filters[i + 1], false, null);
        }
        if (z) {
            return;
        }
        for (TableFilter tableFilter : filters) {
            tableFilter.setPlanItem(this.bestPlan.getItem(tableFilter));
        }
    }

    public TableFilter getTopFilter() {
        return this.topFilter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public double getCost() {
        return this.cost;
    }
}
