package org.apache.catalina.startup;

import ch.qos.logback.classic.encoder.JsonEncoder;
import org.apache.catalina.Container;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/EngineRuleSet.class */
public class EngineRuleSet implements RuleSet {
    protected final String prefix;

    public EngineRuleSet() {
        this("");
    }

    public EngineRuleSet(String prefix) {
        this.prefix = prefix;
    }

    @Override // org.apache.tomcat.util.digester.RuleSet
    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate(this.prefix + "Engine", "org.apache.catalina.core.StandardEngine", JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties(this.prefix + "Engine");
        digester.addRule(this.prefix + "Engine", new LifecycleListenerRule("org.apache.catalina.startup.EngineConfig", "engineConfigClass"));
        digester.addSetNext(this.prefix + "Engine", "setContainer", "org.apache.catalina.Engine");
        digester.addObjectCreate(this.prefix + "Engine/Cluster", null, JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties(this.prefix + "Engine/Cluster");
        digester.addSetNext(this.prefix + "Engine/Cluster", "setCluster", "org.apache.catalina.Cluster");
        digester.addObjectCreate(this.prefix + "Engine/Listener", null, JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties(this.prefix + "Engine/Listener");
        digester.addSetNext(this.prefix + "Engine/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addRuleSet(new RealmRuleSet(this.prefix + "Engine/"));
        digester.addObjectCreate(this.prefix + "Engine/Valve", null, JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties(this.prefix + "Engine/Valve");
        digester.addSetNext(this.prefix + "Engine/Valve", Container.ADD_VALVE_EVENT, "org.apache.catalina.Valve");
    }
}
