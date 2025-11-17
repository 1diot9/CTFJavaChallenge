package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.OptionHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/SimpleRuleStore.class */
public class SimpleRuleStore extends ContextAwareBase implements RuleStore {
    static String KLEENE_STAR = "*";
    HashMap<ElementSelector, Supplier<Action>> rules = new HashMap<>();
    List<String> transparentPathParts = new ArrayList(2);

    public SimpleRuleStore(Context context) {
        setContext(context);
    }

    @Override // ch.qos.logback.core.joran.spi.RuleStore
    public void addTransparentPathPart(String pathPart) {
        if (pathPart == null) {
            throw new IllegalArgumentException("pathPart cannot be null");
        }
        String pathPart2 = pathPart.trim();
        if (pathPart2.isEmpty()) {
            throw new IllegalArgumentException("pathPart cannot be empty or to consist of only spaces");
        }
        if (pathPart2.contains("/")) {
            throw new IllegalArgumentException("pathPart cannot contain '/', i.e. the forward slash character");
        }
        this.transparentPathParts.add(pathPart2);
    }

    @Override // ch.qos.logback.core.joran.spi.RuleStore
    public void addRule(ElementSelector elementSelector, Supplier<Action> actionSupplier) {
        Supplier<Action> existing = this.rules.get(elementSelector);
        if (existing == null) {
            this.rules.put(elementSelector, actionSupplier);
            return;
        }
        throw new IllegalStateException(elementSelector.toString() + " already has an associated action supplier");
    }

    @Override // ch.qos.logback.core.joran.spi.RuleStore
    public void addRule(ElementSelector elementSelector, String actionClassName) {
        Action action = null;
        try {
            action = (Action) OptionHelper.instantiateByClassName(actionClassName, (Class<?>) Action.class, this.context);
        } catch (Exception e) {
            addError("Could not instantiate class [" + actionClassName + "]", e);
        }
        if (action != null) {
        }
    }

    @Override // ch.qos.logback.core.joran.spi.RuleStore
    public Supplier<Action> matchActions(ElementPath elementPath) {
        Supplier<Action> actionSupplier = internalMatchAction(elementPath);
        if (actionSupplier != null) {
            return actionSupplier;
        }
        ElementPath cleanedElementPath = removeTransparentPathParts(elementPath);
        return internalMatchAction(cleanedElementPath);
    }

    private Supplier<Action> internalMatchAction(ElementPath elementPath) {
        Supplier<Action> actionSupplier = fullPathMatch(elementPath);
        if (actionSupplier != null) {
            return actionSupplier;
        }
        Supplier<Action> actionSupplier2 = suffixMatch(elementPath);
        if (actionSupplier2 != null) {
            return actionSupplier2;
        }
        Supplier<Action> actionSupplier3 = prefixMatch(elementPath);
        if (actionSupplier3 != null) {
            return actionSupplier3;
        }
        Supplier<Action> actionSupplier4 = middleMatch(elementPath);
        if (actionSupplier4 != null) {
            return actionSupplier4;
        }
        return null;
    }

    ElementPath removeTransparentPathParts(ElementPath originalElementPath) {
        List<String> preservedElementList = new ArrayList<>(originalElementPath.partList.size());
        Iterator<String> it = originalElementPath.partList.iterator();
        while (it.hasNext()) {
            String part = it.next();
            boolean shouldKeep = this.transparentPathParts.stream().noneMatch(p -> {
                return p.equalsIgnoreCase(part);
            });
            if (shouldKeep) {
                preservedElementList.add(part);
            }
        }
        return new ElementPath(preservedElementList);
    }

    Supplier<Action> fullPathMatch(ElementPath elementPath) {
        for (ElementSelector selector : this.rules.keySet()) {
            if (selector.fullPathMatch(elementPath)) {
                return this.rules.get(selector);
            }
        }
        return null;
    }

    Supplier<Action> suffixMatch(ElementPath elementPath) {
        int r;
        int max = 0;
        ElementSelector longestMatchingElementSelector = null;
        for (ElementSelector selector : this.rules.keySet()) {
            if (isSuffixPattern(selector) && (r = selector.getTailMatchLength(elementPath)) > max) {
                max = r;
                longestMatchingElementSelector = selector;
            }
        }
        if (longestMatchingElementSelector != null) {
            return this.rules.get(longestMatchingElementSelector);
        }
        return null;
    }

    private boolean isSuffixPattern(ElementSelector p) {
        return p.size() > 1 && p.get(0).equals(KLEENE_STAR);
    }

    Supplier<Action> prefixMatch(ElementPath elementPath) {
        int r;
        int max = 0;
        ElementSelector longestMatchingElementSelector = null;
        for (ElementSelector selector : this.rules.keySet()) {
            String last = selector.peekLast();
            if (isKleeneStar(last) && (r = selector.getPrefixMatchLength(elementPath)) == selector.size() - 1 && r > max) {
                max = r;
                longestMatchingElementSelector = selector;
            }
        }
        if (longestMatchingElementSelector != null) {
            return this.rules.get(longestMatchingElementSelector);
        }
        return null;
    }

    private boolean isKleeneStar(String last) {
        return KLEENE_STAR.equals(last);
    }

    Supplier<Action> middleMatch(ElementPath path) {
        int max = 0;
        ElementSelector longestMatchingElementSelector = null;
        for (ElementSelector selector : this.rules.keySet()) {
            String last = selector.peekLast();
            String first = null;
            if (selector.size() > 1) {
                first = selector.get(0);
            }
            if (isKleeneStar(last) && isKleeneStar(first)) {
                List<String> copyOfPartList = selector.getCopyOfPartList();
                if (copyOfPartList.size() > 2) {
                    copyOfPartList.remove(0);
                    copyOfPartList.remove(copyOfPartList.size() - 1);
                }
                int r = 0;
                ElementSelector clone = new ElementSelector(copyOfPartList);
                if (clone.isContainedIn(path)) {
                    r = clone.size();
                }
                if (r > max) {
                    max = r;
                    longestMatchingElementSelector = selector;
                }
            }
        }
        if (longestMatchingElementSelector != null) {
            return this.rules.get(longestMatchingElementSelector);
        }
        return null;
    }

    public String toString() {
        StringBuilder retValue = new StringBuilder();
        retValue.append("SimpleRuleStore ( ").append("rules = ").append(this.rules).append("  ").append(" )");
        return retValue.toString();
    }
}
