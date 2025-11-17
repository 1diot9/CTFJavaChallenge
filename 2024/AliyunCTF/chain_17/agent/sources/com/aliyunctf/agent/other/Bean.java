package com.aliyunctf.agent.other;

import cn.hutool.core.io.resource.ResourceUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/classes/com/aliyunctf/agent/other/Bean.class */
public class Bean implements Serializable {
    byte[] data;

    public void setData(byte[] data) {
        this.data = data;
    }

    public Object getObject() throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.data);
        BeanInputStream beanInputStream = new BeanInputStream(byteArrayInputStream);
        Object object = beanInputStream.readObject();
        return object;
    }

    /* loaded from: agent.jar:BOOT-INF/classes/com/aliyunctf/agent/other/Bean$BeanInputStream.class */
    static class BeanInputStream extends ObjectInputStream {
        static List<String> blackList;

        static {
            blackList = new ArrayList();
            try {
                blackList = new ArrayList(List.of("org.h2.", "com.fasterxml."));
                List<String> defaultBlackList = Arrays.asList(ResourceUtil.readUtf8Str("blacklist.txt").split("[\\n\\r]+"));
                blackList.addAll(defaultBlackList.stream().filter(l -> {
                    return !l.startsWith("#");
                }).map(l2 -> {
                    return l2.strip();
                }).toList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public BeanInputStream(InputStream s) throws IOException {
            super(s);
        }

        @Override // java.io.ObjectInputStream
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            List<String> rules = blackList.stream().filter(l -> {
                return desc.getName().startsWith(l);
            }).toList();
            if (!rules.isEmpty()) {
                throw new RuntimeException("%s matches blacklist rules: %s".formatted(desc.getName(), String.join(",", rules)));
            }
            return super.resolveClass(desc);
        }
    }
}
