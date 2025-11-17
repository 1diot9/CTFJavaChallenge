package org.jooq.migrations.xml.jaxb;

import ch.qos.logback.classic.encoder.JsonEncoder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommitType", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/migrations/xml/jaxb/CommitType.class */
public class CommitType implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlElement(required = true)
    protected String id;
    protected String message;

    @XmlElementWrapper(name = "parents")
    @XmlElement(name = "parent")
    protected List<ParentType> parents;

    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tag")
    protected List<TagType> tags;

    @XmlElementWrapper(name = "files")
    @XmlElement(name = "file")
    protected List<FileType> files;

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public List<ParentType> getParents() {
        if (this.parents == null) {
            this.parents = new ArrayList();
        }
        return this.parents;
    }

    public void setParents(List<ParentType> parents) {
        this.parents = parents;
    }

    public List<TagType> getTags() {
        if (this.tags == null) {
            this.tags = new ArrayList();
        }
        return this.tags;
    }

    public void setTags(List<TagType> tags) {
        this.tags = tags;
    }

    public List<FileType> getFiles() {
        if (this.files == null) {
            this.files = new ArrayList();
        }
        return this.files;
    }

    public void setFiles(List<FileType> files) {
        this.files = files;
    }

    public CommitType withId(String value) {
        setId(value);
        return this;
    }

    public CommitType withMessage(String value) {
        setMessage(value);
        return this;
    }

    public CommitType withParents(ParentType... values) {
        if (values != null) {
            for (ParentType value : values) {
                getParents().add(value);
            }
        }
        return this;
    }

    public CommitType withParents(Collection<ParentType> values) {
        if (values != null) {
            getParents().addAll(values);
        }
        return this;
    }

    public CommitType withParents(List<ParentType> parents) {
        setParents(parents);
        return this;
    }

    public CommitType withTags(TagType... values) {
        if (values != null) {
            for (TagType value : values) {
                getTags().add(value);
            }
        }
        return this;
    }

    public CommitType withTags(Collection<TagType> values) {
        if (values != null) {
            getTags().addAll(values);
        }
        return this;
    }

    public CommitType withTags(List<TagType> tags) {
        setTags(tags);
        return this;
    }

    public CommitType withFiles(FileType... values) {
        if (values != null) {
            for (FileType value : values) {
                getFiles().add(value);
            }
        }
        return this;
    }

    public CommitType withFiles(Collection<FileType> values) {
        if (values != null) {
            getFiles().addAll(values);
        }
        return this;
    }

    public CommitType withFiles(List<FileType> files) {
        setFiles(files);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("id", this.id);
        builder.append(JsonEncoder.MESSAGE_ATTR_NAME, this.message);
        builder.append("parents", "parent", this.parents);
        builder.append("tags", "tag", this.tags);
        builder.append("files", "file", this.files);
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
        CommitType other = (CommitType) that;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!this.message.equals(other.message)) {
            return false;
        }
        if (this.parents == null) {
            if (other.parents != null) {
                return false;
            }
        } else if (!this.parents.equals(other.parents)) {
            return false;
        }
        if (this.tags == null) {
            if (other.tags != null) {
                return false;
            }
        } else if (!this.tags.equals(other.tags)) {
            return false;
        }
        if (this.files == null) {
            if (other.files != null) {
                return false;
            }
            return true;
        }
        if (!this.files.equals(other.files)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.id == null ? 0 : this.id.hashCode());
        return (31 * ((31 * ((31 * ((31 * result) + (this.message == null ? 0 : this.message.hashCode()))) + (this.parents == null ? 0 : this.parents.hashCode()))) + (this.tags == null ? 0 : this.tags.hashCode()))) + (this.files == null ? 0 : this.files.hashCode());
    }
}
