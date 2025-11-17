package ch.qos.logback.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/Model.class */
public class Model implements Serializable {
    private static final long serialVersionUID = -797372668713068159L;
    String tag;
    String bodyText;
    int lineNumber;
    boolean handled = false;
    boolean skipped = false;
    List<Model> subModels = new ArrayList();

    public static Model duplicate(Model that) {
        Model copy = that.makeNewInstance();
        copy.mirror(that);
        for (Model m : that.subModels) {
            Model duplicate = duplicate(m);
            copy.subModels.add(duplicate);
        }
        return copy;
    }

    protected Model makeNewInstance() {
        return new Model();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void mirror(Model that) {
        this.tag = that.tag;
        this.bodyText = that.bodyText;
        this.lineNumber = that.lineNumber;
    }

    public void markAsSkipped() {
        this.skipped = true;
    }

    public void deepMarkAsSkipped() {
        markAsSkipped();
        for (Model m : getSubModels()) {
            m.deepMarkAsSkipped();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetForReuse() {
        this.handled = false;
        this.skipped = false;
        for (Model sub : this.subModels) {
            sub.resetForReuse();
        }
    }

    public boolean isSkipped() {
        return this.skipped;
    }

    public boolean isUnhandled() {
        return !this.handled;
    }

    public boolean isHandled() {
        return this.handled;
    }

    public void markAsHandled() {
        this.handled = true;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public List<Model> getSubModels() {
        return this.subModels;
    }

    public void addSubModel(Model m) {
        this.subModels.add(m);
    }

    public String getBodyText() {
        return this.bodyText;
    }

    public void addText(String bodytext) {
        if (this.bodyText == null) {
            this.bodyText = bodytext;
        } else {
            this.bodyText += bodytext;
        }
    }

    public String idString() {
        return "<" + this.tag + "> at line " + this.lineNumber;
    }

    public int hashCode() {
        return Objects.hash(this.bodyText, Integer.valueOf(this.lineNumber), this.subModels, this.tag);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Model other = (Model) obj;
        return Objects.equals(this.bodyText, other.bodyText) && this.lineNumber == other.lineNumber && Objects.equals(this.subModels, other.subModels) && Objects.equals(this.tag, other.tag);
    }

    public String toString() {
        return getClass().getSimpleName() + " [tag=" + this.tag + ", bodyText=" + this.bodyText + ", id=" + hashCode() + "]";
    }
}
