package org.h2.index;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.table.TableLink;
import org.h2.value.ValueToObjectConverter2;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/LinkedCursor.class */
public class LinkedCursor implements Cursor {
    private final TableLink tableLink;
    private final PreparedStatement prep;
    private final String sql;
    private final SessionLocal session;
    private final ResultSet rs;
    private Row current;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LinkedCursor(TableLink tableLink, ResultSet resultSet, SessionLocal sessionLocal, String str, PreparedStatement preparedStatement) {
        this.session = sessionLocal;
        this.tableLink = tableLink;
        this.rs = resultSet;
        this.sql = str;
        this.prep = preparedStatement;
    }

    @Override // org.h2.index.Cursor
    public Row get() {
        return this.current;
    }

    @Override // org.h2.index.Cursor
    public SearchRow getSearchRow() {
        return this.current;
    }

    @Override // org.h2.index.Cursor
    public boolean next() {
        try {
            if (!this.rs.next()) {
                this.rs.close();
                this.tableLink.reusePreparedStatement(this.prep, this.sql);
                this.current = null;
                return false;
            }
            this.current = this.tableLink.getTemplateRow();
            for (int i = 0; i < this.current.getColumnCount(); i++) {
                this.current.setValue(i, ValueToObjectConverter2.readValue(this.session, this.rs, i + 1, this.tableLink.getColumn(i).getType().getValueType()));
            }
            return true;
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    @Override // org.h2.index.Cursor
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }
}
