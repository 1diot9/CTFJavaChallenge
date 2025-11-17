package org.jooq.migrations.xml.jaxb;

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
@XmlType(name = "MigrationsType", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/migrations/xml/jaxb/MigrationsType.class */
public class MigrationsType implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlElementWrapper(name = "commits")
    @XmlElement(name = "commit")
    protected List<CommitType> commits;

    public List<CommitType> getCommits() {
        if (this.commits == null) {
            this.commits = new ArrayList();
        }
        return this.commits;
    }

    public void setCommits(List<CommitType> commits) {
        this.commits = commits;
    }

    public MigrationsType withCommits(CommitType... values) {
        if (values != null) {
            for (CommitType value : values) {
                getCommits().add(value);
            }
        }
        return this;
    }

    public MigrationsType withCommits(Collection<CommitType> values) {
        if (values != null) {
            getCommits().addAll(values);
        }
        return this;
    }

    public MigrationsType withCommits(List<CommitType> commits) {
        setCommits(commits);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("commits", "commit", this.commits);
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
        MigrationsType other = (MigrationsType) that;
        if (this.commits == null) {
            if (other.commits != null) {
                return false;
            }
            return true;
        }
        if (!this.commits.equals(other.commits)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.commits == null ? 0 : this.commits.hashCode());
        return result;
    }
}
