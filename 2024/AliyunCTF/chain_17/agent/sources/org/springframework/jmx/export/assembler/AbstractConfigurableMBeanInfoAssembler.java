package org.springframework.jmx.export.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import org.springframework.jmx.export.metadata.JmxMetadataUtils;
import org.springframework.jmx.export.metadata.ManagedNotification;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/assembler/AbstractConfigurableMBeanInfoAssembler.class */
public abstract class AbstractConfigurableMBeanInfoAssembler extends AbstractReflectiveMBeanInfoAssembler {

    @Nullable
    private ModelMBeanNotificationInfo[] notificationInfos;
    private final Map<String, ModelMBeanNotificationInfo[]> notificationInfoMappings = new HashMap();

    public void setNotificationInfos(ManagedNotification[] notificationInfos) {
        ModelMBeanNotificationInfo[] infos = new ModelMBeanNotificationInfo[notificationInfos.length];
        for (int i = 0; i < notificationInfos.length; i++) {
            ManagedNotification notificationInfo = notificationInfos[i];
            infos[i] = JmxMetadataUtils.convertToModelMBeanNotificationInfo(notificationInfo);
        }
        this.notificationInfos = infos;
    }

    public void setNotificationInfoMappings(Map<String, Object> notificationInfoMappings) {
        notificationInfoMappings.forEach((beanKey, result) -> {
            this.notificationInfoMappings.put(beanKey, extractNotificationMetadata(result));
        });
    }

    @Override // org.springframework.jmx.export.assembler.AbstractMBeanInfoAssembler
    protected ModelMBeanNotificationInfo[] getNotificationInfo(Object managedBean, String beanKey) {
        ModelMBeanNotificationInfo[] result = null;
        if (StringUtils.hasText(beanKey)) {
            result = this.notificationInfoMappings.get(beanKey);
        }
        if (result == null) {
            result = this.notificationInfos;
        }
        return result != null ? result : new ModelMBeanNotificationInfo[0];
    }

    private ModelMBeanNotificationInfo[] extractNotificationMetadata(Object mapValue) {
        if (mapValue instanceof ManagedNotification) {
            ManagedNotification mn = (ManagedNotification) mapValue;
            return new ModelMBeanNotificationInfo[]{JmxMetadataUtils.convertToModelMBeanNotificationInfo(mn)};
        }
        if (mapValue instanceof Collection) {
            Collection<?> col = (Collection) mapValue;
            List<ModelMBeanNotificationInfo> result = new ArrayList<>();
            for (Object colValue : col) {
                if (!(colValue instanceof ManagedNotification)) {
                    throw new IllegalArgumentException("Property 'notificationInfoMappings' only accepts ManagedNotifications for Map values");
                }
                ManagedNotification mn2 = (ManagedNotification) colValue;
                result.add(JmxMetadataUtils.convertToModelMBeanNotificationInfo(mn2));
            }
            return (ModelMBeanNotificationInfo[]) result.toArray(new ModelMBeanNotificationInfo[0]);
        }
        throw new IllegalArgumentException("Property 'notificationInfoMappings' only accepts ManagedNotifications for Map values");
    }
}
