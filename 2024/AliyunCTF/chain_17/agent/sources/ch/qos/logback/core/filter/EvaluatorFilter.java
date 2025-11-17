package ch.qos.logback.core.filter;

import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.spi.FilterReply;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/filter/EvaluatorFilter.class */
public class EvaluatorFilter<E> extends AbstractMatcherFilter<E> {
    EventEvaluator<E> evaluator;

    @Override // ch.qos.logback.core.filter.Filter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.evaluator != null) {
            super.start();
        } else {
            addError("No evaluator set for filter " + getName());
        }
    }

    public EventEvaluator<E> getEvaluator() {
        return this.evaluator;
    }

    public void setEvaluator(EventEvaluator<E> evaluator) {
        this.evaluator = evaluator;
    }

    @Override // ch.qos.logback.core.filter.Filter
    public FilterReply decide(E event) {
        if (!isStarted() || !this.evaluator.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        try {
            if (this.evaluator.evaluate(event)) {
                return this.onMatch;
            }
            return this.onMismatch;
        } catch (EvaluationException e) {
            addError("Evaluator " + this.evaluator.getName() + " threw an exception", e);
            return FilterReply.NEUTRAL;
        }
    }
}
