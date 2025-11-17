package org.springframework.boot.context.config;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/Profiles.class */
public class Profiles implements Iterable<String> {
    public static final String INCLUDE_PROFILES_PROPERTY_NAME = "spring.profiles.include";
    static final ConfigurationPropertyName INCLUDE_PROFILES = ConfigurationPropertyName.of(INCLUDE_PROFILES_PROPERTY_NAME);
    private static final Bindable<MultiValueMap<String, String>> STRING_STRINGS_MAP = Bindable.of(ResolvableType.forClassWithGenerics((Class<?>) MultiValueMap.class, (Class<?>[]) new Class[]{String.class, String.class}));
    private static final Bindable<Set<String>> STRING_SET = Bindable.setOf(String.class);
    private final MultiValueMap<String, String> groups;
    private final List<String> activeProfiles;
    private final List<String> defaultProfiles;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Profiles(Environment environment, Binder binder, Collection<String> additionalProfiles) {
        this.groups = (MultiValueMap) binder.bind("spring.profiles.group", STRING_STRINGS_MAP).orElseGet(LinkedMultiValueMap::new);
        this.activeProfiles = expandProfiles(getActivatedProfiles(environment, binder, additionalProfiles));
        this.defaultProfiles = expandProfiles(getDefaultProfiles(environment, binder));
    }

    private List<String> getActivatedProfiles(Environment environment, Binder binder, Collection<String> additionalProfiles) {
        return asUniqueItemList(getProfiles(environment, binder, Type.ACTIVE), additionalProfiles);
    }

    private List<String> getDefaultProfiles(Environment environment, Binder binder) {
        return asUniqueItemList(getProfiles(environment, binder, Type.DEFAULT));
    }

    private Collection<String> getProfiles(Environment environment, Binder binder, Type type) {
        Set<String> commaDelimitedListToSet;
        String environmentPropertyValue = environment.getProperty(type.getName());
        if (!StringUtils.hasLength(environmentPropertyValue)) {
            commaDelimitedListToSet = Collections.emptySet();
        } else {
            commaDelimitedListToSet = StringUtils.commaDelimitedListToSet(StringUtils.trimAllWhitespace(environmentPropertyValue));
        }
        Set<String> environmentPropertyProfiles = commaDelimitedListToSet;
        Set<String> environmentProfiles = new LinkedHashSet<>(Arrays.asList(type.get(environment)));
        BindResult<Set<String>> boundProfiles = binder.bind(type.getName(), STRING_SET);
        if (hasProgrammaticallySetProfiles(type, environmentPropertyValue, environmentPropertyProfiles, environmentProfiles)) {
            if (!type.isMergeWithEnvironmentProfiles() || !boundProfiles.isBound()) {
                return environmentProfiles;
            }
            return (Collection) boundProfiles.map(bound -> {
                return merge(environmentProfiles, bound);
            }).get();
        }
        return boundProfiles.orElse(type.getDefaultValue());
    }

    private boolean hasProgrammaticallySetProfiles(Type type, String environmentPropertyValue, Set<String> environmentPropertyProfiles, Set<String> environmentProfiles) {
        return !StringUtils.hasLength(environmentPropertyValue) ? !type.getDefaultValue().equals(environmentProfiles) : (type.getDefaultValue().equals(environmentProfiles) || environmentPropertyProfiles.equals(environmentProfiles)) ? false : true;
    }

    private Set<String> merge(Set<String> environmentProfiles, Set<String> bound) {
        Set<String> result = new LinkedHashSet<>(environmentProfiles);
        result.addAll(bound);
        return result;
    }

    private List<String> expandProfiles(List<String> profiles) {
        Deque<String> stack = new ArrayDeque<>();
        List<String> asReversedList = asReversedList(profiles);
        Objects.requireNonNull(stack);
        asReversedList.forEach((v1) -> {
            r1.push(v1);
        });
        Set<String> expandedProfiles = new LinkedHashSet<>();
        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (expandedProfiles.add(current)) {
                List<String> asReversedList2 = asReversedList((List) this.groups.get(current));
                Objects.requireNonNull(stack);
                asReversedList2.forEach((v1) -> {
                    r1.push(v1);
                });
            }
        }
        return asUniqueItemList(expandedProfiles);
    }

    private List<String> asReversedList(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<String> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }

    private List<String> asUniqueItemList(Collection<String> profiles) {
        return asUniqueItemList(profiles, null);
    }

    private List<String> asUniqueItemList(Collection<String> profiles, Collection<String> additional) {
        LinkedHashSet<String> uniqueItems = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(additional)) {
            uniqueItems.addAll(additional);
        }
        uniqueItems.addAll(profiles);
        return Collections.unmodifiableList(new ArrayList(uniqueItems));
    }

    @Override // java.lang.Iterable
    public Iterator<String> iterator() {
        return getAccepted().iterator();
    }

    public List<String> getActive() {
        return this.activeProfiles;
    }

    public List<String> getDefault() {
        return this.defaultProfiles;
    }

    public List<String> getAccepted() {
        return !this.activeProfiles.isEmpty() ? this.activeProfiles : this.defaultProfiles;
    }

    public boolean isAccepted(String profile) {
        return getAccepted().contains(profile);
    }

    public String toString() {
        ToStringCreator creator = new ToStringCreator(this);
        creator.append("active", getActive().toString());
        creator.append("default", getDefault().toString());
        creator.append("accepted", getAccepted().toString());
        return creator.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/Profiles$Type.class */
    public enum Type {
        ACTIVE(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, (v0) -> {
            return v0.getActiveProfiles();
        }, true, Collections.emptySet()),
        DEFAULT(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, (v0) -> {
            return v0.getDefaultProfiles();
        }, false, Collections.singleton("default"));

        private final Function<Environment, String[]> getter;
        private final boolean mergeWithEnvironmentProfiles;
        private final String name;
        private final Set<String> defaultValue;

        Type(String name, Function getter, boolean mergeWithEnvironmentProfiles, Set defaultValue) {
            this.name = name;
            this.getter = getter;
            this.mergeWithEnvironmentProfiles = mergeWithEnvironmentProfiles;
            this.defaultValue = defaultValue;
        }

        String getName() {
            return this.name;
        }

        String[] get(Environment environment) {
            return this.getter.apply(environment);
        }

        Set<String> getDefaultValue() {
            return this.defaultValue;
        }

        boolean isMergeWithEnvironmentProfiles() {
            return this.mergeWithEnvironmentProfiles;
        }
    }
}
