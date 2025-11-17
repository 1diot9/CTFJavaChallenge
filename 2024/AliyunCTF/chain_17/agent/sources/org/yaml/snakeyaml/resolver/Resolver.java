package org.yaml.snakeyaml.resolver;

import cn.hutool.core.util.RandomUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/resolver/Resolver.class */
public class Resolver {
    public static final Pattern BOOL = Pattern.compile("^(?:yes|Yes|YES|no|No|NO|true|True|TRUE|false|False|FALSE|on|On|ON|off|Off|OFF)$");
    public static final Pattern FLOAT = Pattern.compile("^([-+]?(?:[0-9][0-9_]*)\\.[0-9_]*(?:[eE][-+]?[0-9]+)?|[-+]?(?:[0-9][0-9_]*)(?:[eE][-+]?[0-9]+)|[-+]?\\.[0-9_]+(?:[eE][-+]?[0-9]+)?|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
    public static final Pattern INT = Pattern.compile("^(?:[-+]?0b_*[0-1][0-1_]*|[-+]?0_*[0-7][0-7_]*|[-+]?(?:0|[1-9][0-9_]*)|[-+]?0x_*[0-9a-fA-F][0-9a-fA-F_]*|[-+]?[1-9][0-9_]*(?::[0-5]?[0-9])+)$");
    public static final Pattern MERGE = Pattern.compile("^(?:<<)$");
    public static final Pattern NULL = Pattern.compile("^(?:~|null|Null|NULL| )$");
    public static final Pattern EMPTY = Pattern.compile("^$");
    public static final Pattern TIMESTAMP = Pattern.compile("^(?:[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]|[0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]?(?:[Tt]|[ \t]+)[0-9][0-9]?:[0-9][0-9]:[0-9][0-9](?:\\.[0-9]*)?(?:[ \t]*(?:Z|[-+][0-9][0-9]?(?::[0-9][0-9])?))?)$");
    public static final Pattern VALUE = Pattern.compile("^(?:=)$");
    public static final Pattern YAML = Pattern.compile("^(?:!|&|\\*)$");
    protected Map<Character, List<ResolverTuple>> yamlImplicitResolvers = new HashMap();

    protected void addImplicitResolvers() {
        addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO", 10);
        addImplicitResolver(Tag.INT, INT, "-+0123456789");
        addImplicitResolver(Tag.FLOAT, FLOAT, "-+0123456789.");
        addImplicitResolver(Tag.MERGE, MERGE, "<", 10);
        addImplicitResolver(Tag.NULL, NULL, "~nN��", 10);
        addImplicitResolver(Tag.NULL, EMPTY, null, 10);
        addImplicitResolver(Tag.TIMESTAMP, TIMESTAMP, RandomUtil.BASE_NUMBER, 50);
        addImplicitResolver(Tag.YAML, YAML, "!&*", 10);
    }

    public Resolver() {
        addImplicitResolvers();
    }

    public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
        addImplicitResolver(tag, regexp, first, 1024);
    }

    public void addImplicitResolver(Tag tag, Pattern regexp, String first, int limit) {
        if (first == null) {
            List<ResolverTuple> curr = this.yamlImplicitResolvers.get(null);
            if (curr == null) {
                curr = new ArrayList();
                this.yamlImplicitResolvers.put(null, curr);
            }
            curr.add(new ResolverTuple(tag, regexp, limit));
            return;
        }
        char[] chrs = first.toCharArray();
        for (char c : chrs) {
            Character theC = Character.valueOf(c);
            if (theC.charValue() == 0) {
                theC = null;
            }
            List<ResolverTuple> curr2 = this.yamlImplicitResolvers.get(theC);
            if (curr2 == null) {
                curr2 = new ArrayList();
                this.yamlImplicitResolvers.put(theC, curr2);
            }
            curr2.add(new ResolverTuple(tag, regexp, limit));
        }
    }

    public Tag resolve(NodeId kind, String value, boolean implicit) {
        List<ResolverTuple> resolvers;
        if (kind == NodeId.scalar && implicit) {
            if (value.length() == 0) {
                resolvers = this.yamlImplicitResolvers.get((char) 0);
            } else {
                resolvers = this.yamlImplicitResolvers.get(Character.valueOf(value.charAt(0)));
            }
            if (resolvers != null) {
                for (ResolverTuple v : resolvers) {
                    Tag tag = v.getTag();
                    Pattern regexp = v.getRegexp();
                    if (value.length() <= v.getLimit() && regexp.matcher(value).matches()) {
                        return tag;
                    }
                }
            }
            if (this.yamlImplicitResolvers.containsKey(null)) {
                for (ResolverTuple v2 : this.yamlImplicitResolvers.get(null)) {
                    Tag tag2 = v2.getTag();
                    Pattern regexp2 = v2.getRegexp();
                    if (value.length() <= v2.getLimit() && regexp2.matcher(value).matches()) {
                        return tag2;
                    }
                }
            }
        }
        switch (kind) {
            case scalar:
                return Tag.STR;
            case sequence:
                return Tag.SEQ;
            default:
                return Tag.MAP;
        }
    }
}
