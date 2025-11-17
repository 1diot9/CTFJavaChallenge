package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.support.BindStatus;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/SelectedValueComparator.class */
public abstract class SelectedValueComparator {
    SelectedValueComparator() {
    }

    public static boolean isSelected(BindStatus bindStatus, @Nullable Object candidateValue) {
        Object boundValue = bindStatus.getValue();
        if (ObjectUtils.nullSafeEquals(boundValue, candidateValue)) {
            return true;
        }
        Object actualValue = bindStatus.getActualValue();
        if (actualValue != null && actualValue != boundValue && ObjectUtils.nullSafeEquals(actualValue, candidateValue)) {
            return true;
        }
        if (actualValue != null) {
            boundValue = actualValue;
        } else if (boundValue == null) {
            return false;
        }
        boolean selected = false;
        if (candidateValue != null) {
            if (boundValue.getClass().isArray()) {
                selected = collectionCompare(CollectionUtils.arrayToList(boundValue), candidateValue, bindStatus);
            } else if (boundValue instanceof Collection) {
                Collection<?> collection = (Collection) boundValue;
                selected = collectionCompare(collection, candidateValue, bindStatus);
            } else if (boundValue instanceof Map) {
                Map<?, ?> map = (Map) boundValue;
                selected = mapCompare(map, candidateValue, bindStatus);
            }
        }
        if (!selected) {
            selected = exhaustiveCompare(boundValue, candidateValue, bindStatus.getEditor(), null);
        }
        return selected;
    }

    private static boolean collectionCompare(Collection<?> boundCollection, Object candidateValue, BindStatus bindStatus) {
        try {
            if (boundCollection.contains(candidateValue)) {
                return true;
            }
        } catch (ClassCastException e) {
        }
        return exhaustiveCollectionCompare(boundCollection, candidateValue, bindStatus);
    }

    private static boolean mapCompare(Map<?, ?> boundMap, Object candidateValue, BindStatus bindStatus) {
        try {
            if (boundMap.containsKey(candidateValue)) {
                return true;
            }
        } catch (ClassCastException e) {
        }
        return exhaustiveCollectionCompare(boundMap.keySet(), candidateValue, bindStatus);
    }

    private static boolean exhaustiveCollectionCompare(Collection<?> collection, Object candidateValue, BindStatus bindStatus) {
        Map<PropertyEditor, Object> convertedValueCache = new HashMap<>();
        PropertyEditor editor = null;
        boolean candidateIsString = candidateValue instanceof String;
        if (!candidateIsString) {
            editor = bindStatus.findEditor(candidateValue.getClass());
        }
        for (Object element : collection) {
            if (editor == null && element != null && candidateIsString) {
                editor = bindStatus.findEditor(element.getClass());
            }
            if (exhaustiveCompare(element, candidateValue, editor, convertedValueCache)) {
                return true;
            }
        }
        return false;
    }

    private static boolean exhaustiveCompare(@Nullable Object boundValue, @Nullable Object candidate, @Nullable PropertyEditor editor, @Nullable Map<PropertyEditor, Object> convertedValueCache) {
        Object candidateAsValue;
        String candidateDisplayString = ValueFormatter.getDisplayString(candidate, editor, false);
        if (boundValue != null && boundValue.getClass().isEnum()) {
            Enum<?> boundEnum = (Enum) boundValue;
            String enumCodeAsString = ObjectUtils.getDisplayString(boundEnum.name());
            if (enumCodeAsString.equals(candidateDisplayString)) {
                return true;
            }
            String enumLabelAsString = ObjectUtils.getDisplayString(boundEnum.toString());
            if (enumLabelAsString.equals(candidateDisplayString)) {
                return true;
            }
        } else if (ObjectUtils.getDisplayString(boundValue).equals(candidateDisplayString)) {
            return true;
        }
        if (editor != null && (candidate instanceof String)) {
            String candidateAsString = (String) candidate;
            if (convertedValueCache != null && convertedValueCache.containsKey(editor)) {
                candidateAsValue = convertedValueCache.get(editor);
            } else {
                editor.setAsText(candidateAsString);
                candidateAsValue = editor.getValue();
                if (convertedValueCache != null) {
                    convertedValueCache.put(editor, candidateAsValue);
                }
            }
            if (ObjectUtils.nullSafeEquals(boundValue, candidateAsValue)) {
                return true;
            }
            return false;
        }
        return false;
    }
}
