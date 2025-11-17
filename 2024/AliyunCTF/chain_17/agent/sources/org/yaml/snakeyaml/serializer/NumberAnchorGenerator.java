package org.yaml.snakeyaml.serializer;

import java.text.NumberFormat;
import org.yaml.snakeyaml.nodes.Node;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/serializer/NumberAnchorGenerator.class */
public class NumberAnchorGenerator implements AnchorGenerator {
    private int lastAnchorId;

    public NumberAnchorGenerator(int lastAnchorId) {
        this.lastAnchorId = 0;
        this.lastAnchorId = lastAnchorId;
    }

    @Override // org.yaml.snakeyaml.serializer.AnchorGenerator
    public String nextAnchor(Node node) {
        this.lastAnchorId++;
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumIntegerDigits(3);
        format.setMaximumFractionDigits(0);
        format.setGroupingUsed(false);
        String anchorId = format.format(this.lastAnchorId);
        return "id" + anchorId;
    }
}
