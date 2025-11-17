package org.jooq.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jooq.Configuration;
import org.jooq.ContentType;
import org.jooq.DSLContext;
import org.jooq.File;
import org.jooq.Files;
import org.jooq.Meta;
import org.jooq.Source;
import org.jooq.Tag;
import org.jooq.Version;
import org.jooq.exception.DataMigrationException;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CommitImpl.class */
final class CommitImpl extends AbstractNode<org.jooq.Commit> implements org.jooq.Commit {
    final DSLContext ctx;
    final List<org.jooq.Commit> parents;
    final List<Tag> tags;
    final Map<String, File> delta;
    final Map<String, File> files;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommitImpl(Configuration configuration, String id, String message, org.jooq.Commit root, List<org.jooq.Commit> parents, Collection<? extends File> delta) {
        super(configuration, id, message, root);
        this.ctx = configuration.dsl();
        this.parents = parents;
        this.tags = new ArrayList();
        this.delta = map(delta, false);
        this.files = initFiles();
    }

    private CommitImpl(CommitImpl copy) {
        super(copy.configuration(), copy.id(), copy.message(), (org.jooq.Commit) copy.root);
        this.ctx = copy.ctx;
        this.parents = copy.parents;
        this.tags = new ArrayList(copy.tags);
        this.delta = copy.delta;
        this.files = copy.files;
    }

    private static final Map<String, File> map(Collection<? extends File> list, boolean applyDeletions) {
        return apply(new LinkedHashMap(), list, applyDeletions);
    }

    private static final Map<String, File> apply(Map<String, File> result, Collection<? extends File> list, boolean applyDeletions) {
        for (File file : list) {
            apply(result, file, applyDeletions);
        }
        return result;
    }

    private static final Map<String, File> apply(Map<String, File> result, File file, boolean applyDeletions) {
        if (applyDeletions && file.content() == null) {
            result.remove(file.path());
        } else {
            result.put(file.path(), file);
        }
        return result;
    }

    private final Map<String, File> initFiles() {
        if (this.parents.isEmpty()) {
            return this.delta;
        }
        org.jooq.Commit parent = this.parents.get(0);
        return apply(map(parent.files(), true), (Collection<? extends File>) delta(), true);
    }

    @Override // org.jooq.Node
    public final List<org.jooq.Commit> parents() {
        return Collections.unmodifiableList(this.parents);
    }

    @Override // org.jooq.Commit
    public final List<Tag> tags() {
        return Collections.unmodifiableList(this.tags);
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit tag(String id) {
        return tag(id, null);
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit tag(String id, String message) {
        CommitImpl result = new CommitImpl(this);
        result.tags.add(new TagImpl(id, message));
        return result;
    }

    @Override // org.jooq.Commit
    public final Collection<File> delta() {
        return this.delta.values();
    }

    @Override // org.jooq.Commit
    public final Collection<File> files() {
        return this.files.values();
    }

    private static final Collection<Source> sources(Collection<File> files) {
        return Tools.map(files, f -> {
            return Source.of(f.content());
        });
    }

    @Override // org.jooq.Commit
    public final Collection<Source> sources() {
        return sources(files());
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit commit(String newId, File... newFiles) {
        return commit(newId, "", newFiles);
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit commit(String newId, Collection<? extends File> newFiles) {
        return commit(newId, "", newFiles);
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit commit(String newId, String newMessage, File... newFiles) {
        return commit(newId, newMessage, Arrays.asList(newFiles));
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit commit(String newId, String newMessage, Collection<? extends File> newFiles) {
        return new CommitImpl(configuration(), newId, newMessage, (org.jooq.Commit) this.root, Arrays.asList(this), newFiles);
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit merge(String newId, org.jooq.Commit with, File... newFiles) {
        return merge(newId, (String) null, with, Arrays.asList(newFiles));
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit merge(String newId, org.jooq.Commit with, Collection<? extends File> newFiles) {
        return merge(newId, (String) null, with, newFiles);
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit merge(String newId, String newMessage, org.jooq.Commit with, File... newFiles) {
        return merge(newId, newMessage, with, Arrays.asList(newFiles));
    }

    @Override // org.jooq.Commit
    public final org.jooq.Commit merge(String newId, String newMessage, org.jooq.Commit with, Collection<? extends File> newFiles) {
        return new CommitImpl(configuration(), newId, newMessage, (org.jooq.Commit) this.root, Arrays.asList(this, with), newFiles);
    }

    @Override // org.jooq.Commit
    public final Version version() {
        return root().migrateTo(this).to();
    }

    @Override // org.jooq.Commit
    public final Meta meta() {
        return version().meta();
    }

    @Override // org.jooq.Commit
    public final Files migrateTo(org.jooq.Commit resultCommit) {
        commonAncestor(resultCommit);
        return migrateTo0(resultCommit);
    }

    private final Files migrateTo0(org.jooq.Commit resultCommit) {
        File historicFile;
        Map<String, File> history = new LinkedHashMap<>();
        Map<String, String> historyKeys = new HashMap<>();
        Map<String, File> result = new LinkedHashMap<>();
        Map<String, File> tempHistory = new LinkedHashMap<>();
        Map<String, String> tempHistoryKeys = new HashMap<>();
        Deque<org.jooq.Commit> commitHistory = new ArrayDeque<>();
        history(commitHistory, new HashSet(), Arrays.asList(resultCommit));
        boolean recordingResult = false;
        boolean hasDeletions = false;
        for (org.jooq.Commit commit : commitHistory) {
            List<File> commitFiles = new ArrayList<>(commit.delta());
            Iterator<File> deletions = commitFiles.iterator();
            while (deletions.hasNext()) {
                File file = deletions.next();
                if (file.content() == null) {
                    hasDeletions |= true;
                    String path = file.path();
                    String tempKey = tempHistoryKeys.remove(path);
                    String tempRemove = tempKey != null ? tempKey : path;
                    String key = historyKeys.remove(path);
                    String remove = key != null ? key : path;
                    if (recordingResult && result.remove(tempRemove) == null && file.type() == ContentType.INCREMENT && history.containsKey(tempRemove)) {
                        result.put(tempRemove, file);
                    } else if (recordingResult && result.remove(remove) == null && file.type() == ContentType.SCHEMA && history.containsKey(remove)) {
                        result.put(remove, file);
                    } else {
                        history.remove(tempRemove);
                    }
                    tempHistory.remove(path);
                    deletions.remove();
                }
            }
            Iterator<File> increments = commitFiles.iterator();
            while (increments.hasNext()) {
                File file2 = increments.next();
                if (file2.type() == ContentType.INCREMENT) {
                    String path2 = file2.path();
                    File oldFile = recordingResult ? history.get(path2) : history.put(path2, file2);
                    if (oldFile == null && !tempHistory.isEmpty() && !result.containsKey(path2)) {
                        move(tempHistory, result, tempHistoryKeys);
                    }
                    if (recordingResult) {
                        result.put(path2, file2);
                    }
                    increments.remove();
                }
            }
            Iterator<File> schemas = commitFiles.iterator();
            while (schemas.hasNext()) {
                File file3 = schemas.next();
                if (file3.type() == ContentType.SCHEMA) {
                    String path3 = file3.path();
                    String key2 = commit.id() + "-" + path3;
                    if (recordingResult) {
                        tempHistory.put(path3, file3);
                        tempHistoryKeys.put(path3, key2);
                    } else {
                        history.put(key2, file3);
                        historyKeys.put(path3, key2);
                    }
                    schemas.remove();
                }
            }
            recordingResult |= id().equals(commit.id());
        }
        move(tempHistory, result, tempHistoryKeys);
        Iterator<Map.Entry<String, File>> it = result.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, File> entry = it.next();
            String path4 = entry.getKey();
            File file4 = entry.getValue();
            if (file4.type() == ContentType.INCREMENT && (historicFile = history.get(path4)) != null) {
                if (!StringUtils.equals(historicFile.content(), file4.content())) {
                    throw new DataMigrationException("Cannot edit increment file that has already been applied: " + String.valueOf(file4));
                }
                it.remove();
            }
        }
        if (hasDeletions) {
            Map<String, List<String>> keys = new LinkedHashMap<>();
            Set<String> remove2 = new LinkedHashSet<>();
            result.forEach((key3, file5) -> {
                if (file5.type() == ContentType.SCHEMA) {
                    ((List) keys.computeIfAbsent(file5.path(), p -> {
                        return new ArrayList();
                    })).add(key3);
                } else {
                    moveAllButLast(keys, remove2);
                }
            });
            moveAllButLast(keys, remove2);
            for (String r : remove2) {
                result.remove(r);
            }
        }
        Map<String, File> versionFiles = new HashMap<>();
        Version from = version(this.ctx.migrations().version("init"), id(), versionFiles, history.values());
        Version to = version(from, resultCommit.id(), versionFiles, result.values());
        return new FilesImpl(from, to, result.values());
    }

    private static final void history(Deque<org.jooq.Commit> commitHistory, Set<org.jooq.Commit> set, List<org.jooq.Commit> commits) {
        for (org.jooq.Commit commit : commits) {
            if (set.add(commit)) {
                commitHistory.push(commit);
            }
        }
        Collection<org.jooq.Commit> p = new LinkedHashSet<>();
        Iterator<org.jooq.Commit> it = commits.iterator();
        while (it.hasNext()) {
            p.addAll(it.next().parents());
        }
        if (!p.isEmpty()) {
            List<org.jooq.Commit> l = new ArrayList<>(p);
            Collections.reverse(l);
            history(commitHistory, set, l);
        }
    }

    private static final Version version(Version from, String newId, Map<String, File> files, Collection<File> result) {
        Version apply;
        Version to = from;
        List<File> list = new ArrayList<>(result);
        for (int j = 0; j < list.size(); j++) {
            File file = list.get(j);
            String commitId = newId + "-" + file.path();
            if (file.type() == ContentType.SCHEMA) {
                apply = to.commit(commitId, (Source[]) sources(apply(files, file, true).values()).toArray(Tools.EMPTY_SOURCE));
            } else {
                apply = to.apply(commitId, file.content());
            }
            to = apply;
        }
        return to;
    }

    private static final void moveAllButLast(Map<String, List<String>> keys, Set<String> remove) {
        for (List<String> k : keys.values()) {
            if (k.size() > 1) {
                remove.addAll(k.subList(0, k.size() - 1));
            }
        }
        keys.clear();
    }

    private static final void move(Map<String, File> files, Map<String, File> result, Map<String, String> keys) {
        for (File file : files.values()) {
            result.put(keys.get(file.path()), file);
        }
        files.clear();
    }

    public int hashCode() {
        return id().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof org.jooq.Commit) {
            return id().equals(((org.jooq.Commit) obj).id());
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id());
        if (!StringUtils.isBlank(message())) {
            sb.append(" - ").append(message());
        }
        if (!this.tags.isEmpty()) {
            sb.append(' ').append(this.tags);
        }
        return sb.toString();
    }
}
