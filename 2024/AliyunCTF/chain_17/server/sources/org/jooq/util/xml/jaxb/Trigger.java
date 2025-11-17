package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import org.jooq.util.jaxb.tools.StringAdapter;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Trigger", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Trigger.class */
public class Trigger implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "trigger_catalog")
    protected String triggerCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "trigger_schema")
    protected String triggerSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "trigger_name", required = true)
    protected String triggerName;

    @XmlSchemaType(name = "string")
    @XmlElement(name = "event_manipulation", required = true)
    protected TriggerEventManipulation eventManipulation;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "event_object_catalog")
    protected String eventObjectCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "event_object_schema")
    protected String eventObjectSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "event_object_table", required = true)
    protected String eventObjectTable;

    @XmlElement(name = "action_order")
    protected Integer actionOrder;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "action_condition")
    protected String actionCondition;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "action_statement", required = true)
    protected String actionStatement;

    @XmlSchemaType(name = "string")
    @XmlElement(name = "action_orientation", required = true)
    protected TriggerActionOrientation actionOrientation;

    @XmlSchemaType(name = "string")
    @XmlElement(name = "action_timing", required = true)
    protected TriggerActionTiming actionTiming;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "action_reference_old_table")
    protected String actionReferenceOldTable;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "action_reference_new_table")
    protected String actionReferenceNewTable;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "action_reference_old_row")
    protected String actionReferenceOldRow;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "action_reference_new_row")
    protected String actionReferenceNewRow;

    public String getTriggerCatalog() {
        return this.triggerCatalog;
    }

    public void setTriggerCatalog(String value) {
        this.triggerCatalog = value;
    }

    public String getTriggerSchema() {
        return this.triggerSchema;
    }

    public void setTriggerSchema(String value) {
        this.triggerSchema = value;
    }

    public String getTriggerName() {
        return this.triggerName;
    }

    public void setTriggerName(String value) {
        this.triggerName = value;
    }

    public TriggerEventManipulation getEventManipulation() {
        return this.eventManipulation;
    }

    public void setEventManipulation(TriggerEventManipulation value) {
        this.eventManipulation = value;
    }

    public String getEventObjectCatalog() {
        return this.eventObjectCatalog;
    }

    public void setEventObjectCatalog(String value) {
        this.eventObjectCatalog = value;
    }

    public String getEventObjectSchema() {
        return this.eventObjectSchema;
    }

    public void setEventObjectSchema(String value) {
        this.eventObjectSchema = value;
    }

    public String getEventObjectTable() {
        return this.eventObjectTable;
    }

    public void setEventObjectTable(String value) {
        this.eventObjectTable = value;
    }

    public Integer getActionOrder() {
        return this.actionOrder;
    }

    public void setActionOrder(Integer value) {
        this.actionOrder = value;
    }

    public String getActionCondition() {
        return this.actionCondition;
    }

    public void setActionCondition(String value) {
        this.actionCondition = value;
    }

    public String getActionStatement() {
        return this.actionStatement;
    }

    public void setActionStatement(String value) {
        this.actionStatement = value;
    }

    public TriggerActionOrientation getActionOrientation() {
        return this.actionOrientation;
    }

    public void setActionOrientation(TriggerActionOrientation value) {
        this.actionOrientation = value;
    }

    public TriggerActionTiming getActionTiming() {
        return this.actionTiming;
    }

    public void setActionTiming(TriggerActionTiming value) {
        this.actionTiming = value;
    }

    public String getActionReferenceOldTable() {
        return this.actionReferenceOldTable;
    }

    public void setActionReferenceOldTable(String value) {
        this.actionReferenceOldTable = value;
    }

    public String getActionReferenceNewTable() {
        return this.actionReferenceNewTable;
    }

    public void setActionReferenceNewTable(String value) {
        this.actionReferenceNewTable = value;
    }

    public String getActionReferenceOldRow() {
        return this.actionReferenceOldRow;
    }

    public void setActionReferenceOldRow(String value) {
        this.actionReferenceOldRow = value;
    }

    public String getActionReferenceNewRow() {
        return this.actionReferenceNewRow;
    }

    public void setActionReferenceNewRow(String value) {
        this.actionReferenceNewRow = value;
    }

    public Trigger withTriggerCatalog(String value) {
        setTriggerCatalog(value);
        return this;
    }

    public Trigger withTriggerSchema(String value) {
        setTriggerSchema(value);
        return this;
    }

    public Trigger withTriggerName(String value) {
        setTriggerName(value);
        return this;
    }

    public Trigger withEventManipulation(TriggerEventManipulation value) {
        setEventManipulation(value);
        return this;
    }

    public Trigger withEventObjectCatalog(String value) {
        setEventObjectCatalog(value);
        return this;
    }

    public Trigger withEventObjectSchema(String value) {
        setEventObjectSchema(value);
        return this;
    }

    public Trigger withEventObjectTable(String value) {
        setEventObjectTable(value);
        return this;
    }

    public Trigger withActionOrder(Integer value) {
        setActionOrder(value);
        return this;
    }

    public Trigger withActionCondition(String value) {
        setActionCondition(value);
        return this;
    }

    public Trigger withActionStatement(String value) {
        setActionStatement(value);
        return this;
    }

    public Trigger withActionOrientation(TriggerActionOrientation value) {
        setActionOrientation(value);
        return this;
    }

    public Trigger withActionTiming(TriggerActionTiming value) {
        setActionTiming(value);
        return this;
    }

    public Trigger withActionReferenceOldTable(String value) {
        setActionReferenceOldTable(value);
        return this;
    }

    public Trigger withActionReferenceNewTable(String value) {
        setActionReferenceNewTable(value);
        return this;
    }

    public Trigger withActionReferenceOldRow(String value) {
        setActionReferenceOldRow(value);
        return this;
    }

    public Trigger withActionReferenceNewRow(String value) {
        setActionReferenceNewRow(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("trigger_catalog", this.triggerCatalog);
        builder.append("trigger_schema", this.triggerSchema);
        builder.append("trigger_name", this.triggerName);
        builder.append("event_manipulation", this.eventManipulation);
        builder.append("event_object_catalog", this.eventObjectCatalog);
        builder.append("event_object_schema", this.eventObjectSchema);
        builder.append("event_object_table", this.eventObjectTable);
        builder.append("action_order", this.actionOrder);
        builder.append("action_condition", this.actionCondition);
        builder.append("action_statement", this.actionStatement);
        builder.append("action_orientation", this.actionOrientation);
        builder.append("action_timing", this.actionTiming);
        builder.append("action_reference_old_table", this.actionReferenceOldTable);
        builder.append("action_reference_new_table", this.actionReferenceNewTable);
        builder.append("action_reference_old_row", this.actionReferenceOldRow);
        builder.append("action_reference_new_row", this.actionReferenceNewRow);
    }

    public String toString() {
        XMLBuilder builder = XMLBuilder.nonFormatting();
        appendTo(builder);
        return builder.toString();
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }
        Trigger other = (Trigger) that;
        if (this.triggerCatalog == null) {
            if (other.triggerCatalog != null) {
                return false;
            }
        } else if (!this.triggerCatalog.equals(other.triggerCatalog)) {
            return false;
        }
        if (this.triggerSchema == null) {
            if (other.triggerSchema != null) {
                return false;
            }
        } else if (!this.triggerSchema.equals(other.triggerSchema)) {
            return false;
        }
        if (this.triggerName == null) {
            if (other.triggerName != null) {
                return false;
            }
        } else if (!this.triggerName.equals(other.triggerName)) {
            return false;
        }
        if (this.eventManipulation == null) {
            if (other.eventManipulation != null) {
                return false;
            }
        } else if (!this.eventManipulation.equals(other.eventManipulation)) {
            return false;
        }
        if (this.eventObjectCatalog == null) {
            if (other.eventObjectCatalog != null) {
                return false;
            }
        } else if (!this.eventObjectCatalog.equals(other.eventObjectCatalog)) {
            return false;
        }
        if (this.eventObjectSchema == null) {
            if (other.eventObjectSchema != null) {
                return false;
            }
        } else if (!this.eventObjectSchema.equals(other.eventObjectSchema)) {
            return false;
        }
        if (this.eventObjectTable == null) {
            if (other.eventObjectTable != null) {
                return false;
            }
        } else if (!this.eventObjectTable.equals(other.eventObjectTable)) {
            return false;
        }
        if (this.actionOrder == null) {
            if (other.actionOrder != null) {
                return false;
            }
        } else if (!this.actionOrder.equals(other.actionOrder)) {
            return false;
        }
        if (this.actionCondition == null) {
            if (other.actionCondition != null) {
                return false;
            }
        } else if (!this.actionCondition.equals(other.actionCondition)) {
            return false;
        }
        if (this.actionStatement == null) {
            if (other.actionStatement != null) {
                return false;
            }
        } else if (!this.actionStatement.equals(other.actionStatement)) {
            return false;
        }
        if (this.actionOrientation == null) {
            if (other.actionOrientation != null) {
                return false;
            }
        } else if (!this.actionOrientation.equals(other.actionOrientation)) {
            return false;
        }
        if (this.actionTiming == null) {
            if (other.actionTiming != null) {
                return false;
            }
        } else if (!this.actionTiming.equals(other.actionTiming)) {
            return false;
        }
        if (this.actionReferenceOldTable == null) {
            if (other.actionReferenceOldTable != null) {
                return false;
            }
        } else if (!this.actionReferenceOldTable.equals(other.actionReferenceOldTable)) {
            return false;
        }
        if (this.actionReferenceNewTable == null) {
            if (other.actionReferenceNewTable != null) {
                return false;
            }
        } else if (!this.actionReferenceNewTable.equals(other.actionReferenceNewTable)) {
            return false;
        }
        if (this.actionReferenceOldRow == null) {
            if (other.actionReferenceOldRow != null) {
                return false;
            }
        } else if (!this.actionReferenceOldRow.equals(other.actionReferenceOldRow)) {
            return false;
        }
        if (this.actionReferenceNewRow == null) {
            if (other.actionReferenceNewRow != null) {
                return false;
            }
            return true;
        }
        if (!this.actionReferenceNewRow.equals(other.actionReferenceNewRow)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.triggerCatalog == null ? 0 : this.triggerCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.triggerSchema == null ? 0 : this.triggerSchema.hashCode()))) + (this.triggerName == null ? 0 : this.triggerName.hashCode()))) + (this.eventManipulation == null ? 0 : this.eventManipulation.hashCode()))) + (this.eventObjectCatalog == null ? 0 : this.eventObjectCatalog.hashCode()))) + (this.eventObjectSchema == null ? 0 : this.eventObjectSchema.hashCode()))) + (this.eventObjectTable == null ? 0 : this.eventObjectTable.hashCode()))) + (this.actionOrder == null ? 0 : this.actionOrder.hashCode()))) + (this.actionCondition == null ? 0 : this.actionCondition.hashCode()))) + (this.actionStatement == null ? 0 : this.actionStatement.hashCode()))) + (this.actionOrientation == null ? 0 : this.actionOrientation.hashCode()))) + (this.actionTiming == null ? 0 : this.actionTiming.hashCode()))) + (this.actionReferenceOldTable == null ? 0 : this.actionReferenceOldTable.hashCode()))) + (this.actionReferenceNewTable == null ? 0 : this.actionReferenceNewTable.hashCode()))) + (this.actionReferenceOldRow == null ? 0 : this.actionReferenceOldRow.hashCode()))) + (this.actionReferenceNewRow == null ? 0 : this.actionReferenceNewRow.hashCode());
    }
}
