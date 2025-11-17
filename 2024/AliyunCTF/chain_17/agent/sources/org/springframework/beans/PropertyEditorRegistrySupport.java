package org.springframework.beans;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.beans.propertyeditors.PathEditor;
import org.springframework.beans.propertyeditors.PatternEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.beans.propertyeditors.ReaderEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.TimeZoneEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.beans.propertyeditors.ZoneIdEditor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.xml.sax.InputSource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyEditorRegistrySupport.class */
public class PropertyEditorRegistrySupport implements PropertyEditorRegistry {

    @Nullable
    private ConversionService conversionService;
    private boolean defaultEditorsActive = false;
    private boolean configValueEditorsActive = false;

    @Nullable
    private Map<Class<?>, PropertyEditor> defaultEditors;

    @Nullable
    private Map<Class<?>, PropertyEditor> overriddenDefaultEditors;

    @Nullable
    private Map<Class<?>, PropertyEditor> customEditors;

    @Nullable
    private Map<String, CustomEditorHolder> customEditorsForPath;

    @Nullable
    private Map<Class<?>, PropertyEditor> customEditorCache;

    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerDefaultEditors() {
        this.defaultEditorsActive = true;
    }

    public void useConfigValueEditors() {
        this.configValueEditorsActive = true;
    }

    public void overrideDefaultEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        if (this.overriddenDefaultEditors == null) {
            this.overriddenDefaultEditors = new HashMap();
        }
        this.overriddenDefaultEditors.put(requiredType, propertyEditor);
    }

    @Nullable
    public PropertyEditor getDefaultEditor(Class<?> requiredType) {
        PropertyEditor editor;
        if (!this.defaultEditorsActive) {
            return null;
        }
        if (this.overriddenDefaultEditors != null && (editor = this.overriddenDefaultEditors.get(requiredType)) != null) {
            return editor;
        }
        if (this.defaultEditors == null) {
            createDefaultEditors();
        }
        return this.defaultEditors.get(requiredType);
    }

    private void createDefaultEditors() {
        this.defaultEditors = new HashMap(64);
        this.defaultEditors.put(Charset.class, new CharsetEditor());
        this.defaultEditors.put(Class.class, new ClassEditor());
        this.defaultEditors.put(Class[].class, new ClassArrayEditor());
        this.defaultEditors.put(Currency.class, new CurrencyEditor());
        this.defaultEditors.put(File.class, new FileEditor());
        this.defaultEditors.put(InputStream.class, new InputStreamEditor());
        this.defaultEditors.put(InputSource.class, new InputSourceEditor());
        this.defaultEditors.put(Locale.class, new LocaleEditor());
        this.defaultEditors.put(Path.class, new PathEditor());
        this.defaultEditors.put(Pattern.class, new PatternEditor());
        this.defaultEditors.put(Properties.class, new PropertiesEditor());
        this.defaultEditors.put(Reader.class, new ReaderEditor());
        this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
        this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
        this.defaultEditors.put(URI.class, new URIEditor());
        this.defaultEditors.put(URL.class, new URLEditor());
        this.defaultEditors.put(UUID.class, new UUIDEditor());
        this.defaultEditors.put(ZoneId.class, new ZoneIdEditor());
        this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
        this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
        this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
        this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
        this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));
        this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
        this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());
        this.defaultEditors.put(Character.TYPE, new CharacterEditor(false));
        this.defaultEditors.put(Character.class, new CharacterEditor(true));
        this.defaultEditors.put(Boolean.TYPE, new CustomBooleanEditor(false));
        this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));
        this.defaultEditors.put(Byte.TYPE, new CustomNumberEditor(Byte.class, false));
        this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
        this.defaultEditors.put(Short.TYPE, new CustomNumberEditor(Short.class, false));
        this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
        this.defaultEditors.put(Integer.TYPE, new CustomNumberEditor(Integer.class, false));
        this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
        this.defaultEditors.put(Long.TYPE, new CustomNumberEditor(Long.class, false));
        this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
        this.defaultEditors.put(Float.TYPE, new CustomNumberEditor(Float.class, false));
        this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
        this.defaultEditors.put(Double.TYPE, new CustomNumberEditor(Double.class, false));
        this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
        this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
        this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));
        if (this.configValueEditorsActive) {
            PropertyEditor stringArrayPropertyEditor = new StringArrayPropertyEditor();
            this.defaultEditors.put(String[].class, stringArrayPropertyEditor);
            this.defaultEditors.put(short[].class, stringArrayPropertyEditor);
            this.defaultEditors.put(int[].class, stringArrayPropertyEditor);
            this.defaultEditors.put(long[].class, stringArrayPropertyEditor);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void copyDefaultEditorsTo(PropertyEditorRegistrySupport target) {
        target.defaultEditorsActive = this.defaultEditorsActive;
        target.configValueEditorsActive = this.configValueEditorsActive;
        target.defaultEditors = this.defaultEditors;
        target.overriddenDefaultEditors = this.overriddenDefaultEditors;
    }

    @Override // org.springframework.beans.PropertyEditorRegistry
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        registerCustomEditor(requiredType, null, propertyEditor);
    }

    @Override // org.springframework.beans.PropertyEditorRegistry
    public void registerCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath, PropertyEditor propertyEditor) {
        if (requiredType == null && propertyPath == null) {
            throw new IllegalArgumentException("Either requiredType or propertyPath is required");
        }
        if (propertyPath != null) {
            if (this.customEditorsForPath == null) {
                this.customEditorsForPath = new LinkedHashMap(16);
            }
            this.customEditorsForPath.put(propertyPath, new CustomEditorHolder(propertyEditor, requiredType));
        } else {
            if (this.customEditors == null) {
                this.customEditors = new LinkedHashMap(16);
            }
            this.customEditors.put(requiredType, propertyEditor);
            this.customEditorCache = null;
        }
    }

    @Override // org.springframework.beans.PropertyEditorRegistry
    @Nullable
    public PropertyEditor findCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath) {
        Class<?> requiredTypeToUse = requiredType;
        if (propertyPath != null) {
            if (this.customEditorsForPath != null) {
                PropertyEditor editor = getCustomEditor(propertyPath, requiredType);
                if (editor == null) {
                    List<String> strippedPaths = new ArrayList<>();
                    addStrippedPropertyPaths(strippedPaths, "", propertyPath);
                    Iterator<String> it = strippedPaths.iterator();
                    while (it.hasNext() && editor == null) {
                        String strippedPath = it.next();
                        editor = getCustomEditor(strippedPath, requiredType);
                    }
                }
                if (editor != null) {
                    return editor;
                }
            }
            if (requiredType == null) {
                requiredTypeToUse = getPropertyType(propertyPath);
            }
        }
        return getCustomEditor(requiredTypeToUse);
    }

    public boolean hasCustomEditorForElement(@Nullable Class<?> elementType, @Nullable String propertyPath) {
        if (propertyPath != null && this.customEditorsForPath != null) {
            for (Map.Entry<String, CustomEditorHolder> entry : this.customEditorsForPath.entrySet()) {
                if (PropertyAccessorUtils.matchesProperty(entry.getKey(), propertyPath) && entry.getValue().getPropertyEditor(elementType) != null) {
                    return true;
                }
            }
        }
        return (elementType == null || this.customEditors == null || !this.customEditors.containsKey(elementType)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Class<?> getPropertyType(String propertyPath) {
        return null;
    }

    @Nullable
    private PropertyEditor getCustomEditor(String propertyName, @Nullable Class<?> requiredType) {
        CustomEditorHolder holder = this.customEditorsForPath != null ? this.customEditorsForPath.get(propertyName) : null;
        if (holder != null) {
            return holder.getPropertyEditor(requiredType);
        }
        return null;
    }

    @Nullable
    private PropertyEditor getCustomEditor(@Nullable Class<?> requiredType) {
        if (requiredType == null || this.customEditors == null) {
            return null;
        }
        PropertyEditor editor = this.customEditors.get(requiredType);
        if (editor == null) {
            if (this.customEditorCache != null) {
                editor = this.customEditorCache.get(requiredType);
            }
            if (editor == null) {
                for (Map.Entry<Class<?>, PropertyEditor> entry : this.customEditors.entrySet()) {
                    Class<?> key = entry.getKey();
                    if (key.isAssignableFrom(requiredType)) {
                        editor = entry.getValue();
                        if (this.customEditorCache == null) {
                            this.customEditorCache = new HashMap();
                        }
                        this.customEditorCache.put(requiredType, editor);
                        if (editor != null) {
                            break;
                        }
                    }
                }
            }
        }
        return editor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Class<?> guessPropertyTypeFromEditors(String propertyName) {
        if (this.customEditorsForPath != null) {
            CustomEditorHolder editorHolder = this.customEditorsForPath.get(propertyName);
            if (editorHolder == null) {
                List<String> strippedPaths = new ArrayList<>();
                addStrippedPropertyPaths(strippedPaths, "", propertyName);
                Iterator<String> it = strippedPaths.iterator();
                while (it.hasNext() && editorHolder == null) {
                    String strippedName = it.next();
                    editorHolder = this.customEditorsForPath.get(strippedName);
                }
            }
            if (editorHolder != null) {
                return editorHolder.getRegisteredType();
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void copyCustomEditorsTo(PropertyEditorRegistry target, @Nullable String nestedProperty) {
        String actualPropertyName = nestedProperty != null ? PropertyAccessorUtils.getPropertyName(nestedProperty) : null;
        if (this.customEditors != null) {
            Map<Class<?>, PropertyEditor> map = this.customEditors;
            Objects.requireNonNull(target);
            map.forEach(target::registerCustomEditor);
        }
        if (this.customEditorsForPath != null) {
            this.customEditorsForPath.forEach((editorPath, editorHolder) -> {
                if (nestedProperty != null) {
                    int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(editorPath);
                    if (pos != -1) {
                        String editorNestedProperty = editorPath.substring(0, pos);
                        String editorNestedPath = editorPath.substring(pos + 1);
                        if (editorNestedProperty.equals(nestedProperty) || editorNestedProperty.equals(actualPropertyName)) {
                            target.registerCustomEditor(editorHolder.getRegisteredType(), editorNestedPath, editorHolder.getPropertyEditor());
                            return;
                        }
                        return;
                    }
                    return;
                }
                target.registerCustomEditor(editorHolder.getRegisteredType(), editorPath, editorHolder.getPropertyEditor());
            });
        }
    }

    private void addStrippedPropertyPaths(List<String> strippedPaths, String nestedPath, String propertyPath) {
        int endIndex;
        int startIndex = propertyPath.indexOf(91);
        if (startIndex != -1 && (endIndex = propertyPath.indexOf(93)) != -1) {
            String prefix = propertyPath.substring(0, startIndex);
            String key = propertyPath.substring(startIndex, endIndex + 1);
            String suffix = propertyPath.substring(endIndex + 1);
            strippedPaths.add(nestedPath + prefix + suffix);
            addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);
            addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyEditorRegistrySupport$CustomEditorHolder.class */
    public static final class CustomEditorHolder {
        private final PropertyEditor propertyEditor;

        @Nullable
        private final Class<?> registeredType;

        private CustomEditorHolder(PropertyEditor propertyEditor, @Nullable Class<?> registeredType) {
            this.propertyEditor = propertyEditor;
            this.registeredType = registeredType;
        }

        private PropertyEditor getPropertyEditor() {
            return this.propertyEditor;
        }

        @Nullable
        private Class<?> getRegisteredType() {
            return this.registeredType;
        }

        @Nullable
        private PropertyEditor getPropertyEditor(@Nullable Class<?> requiredType) {
            if (this.registeredType == null || ((requiredType != null && (ClassUtils.isAssignable(this.registeredType, requiredType) || ClassUtils.isAssignable(requiredType, this.registeredType))) || (requiredType == null && !Collection.class.isAssignableFrom(this.registeredType) && !this.registeredType.isArray()))) {
                return this.propertyEditor;
            }
            return null;
        }
    }
}
