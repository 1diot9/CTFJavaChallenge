package org.jooq.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jooq.Commits;
import org.jooq.Configuration;
import org.jooq.ContentType;
import org.jooq.Migrations;
import org.jooq.Tag;
import org.jooq.exception.DataMigrationVerificationException;
import org.jooq.migrations.xml.jaxb.ChangeType;
import org.jooq.migrations.xml.jaxb.CommitType;
import org.jooq.migrations.xml.jaxb.FileType;
import org.jooq.migrations.xml.jaxb.MigrationsType;
import org.jooq.migrations.xml.jaxb.ParentType;
import org.jooq.migrations.xml.jaxb.TagType;
import org.jooq.tools.JooqLogger;
import org.jooq.util.jaxb.tools.MiniJAXB;
import org.springframework.web.context.support.XmlWebApplicationContext;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CommitsImpl.class */
final class CommitsImpl implements Commits {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) CommitsImpl.class);
    final Configuration configuration;
    final Migrations migrations;
    final org.jooq.Commit root;
    final Map<String, org.jooq.Commit> commitsById = new LinkedHashMap();
    final Map<String, org.jooq.Commit> commitsByTag = new LinkedHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommitsImpl(Configuration configuration, org.jooq.Commit root) {
        this.configuration = configuration;
        this.migrations = configuration.dsl().migrations();
        this.root = root;
        add(root);
    }

    @Override // org.jooq.Commits
    public final Commits add(org.jooq.Commit commit) {
        if (this.root != commit.root()) {
            throw new DataMigrationVerificationException("A Commits graph must contain a single graph whose commits all share the same root.");
        }
        org.jooq.Commit duplicate = this.commitsById.get(commit.id());
        if (duplicate != null) {
            throw new DataMigrationVerificationException("Duplicate commit ID already present on commit: " + String.valueOf(duplicate));
        }
        for (Tag tag : commit.tags()) {
            org.jooq.Commit duplicate2 = this.commitsByTag.get(tag.id());
            if (duplicate2 != null) {
                throw new DataMigrationVerificationException("Duplicate tag " + String.valueOf(tag) + " already present on commit: " + String.valueOf(duplicate2));
            }
        }
        this.commitsById.put(commit.id(), commit);
        Iterator<Tag> it = commit.tags().iterator();
        while (it.hasNext()) {
            this.commitsByTag.put(it.next().id(), commit);
        }
        if (log.isDebugEnabled()) {
            log.debug("Commit added", commit);
        }
        return this;
    }

    @Override // org.jooq.Commits
    public final Commits addAll(org.jooq.Commit... c) {
        return addAll(Arrays.asList(c));
    }

    @Override // org.jooq.Commits
    public final Commits addAll(Collection<? extends org.jooq.Commit> c) {
        for (org.jooq.Commit commit : c) {
            add(commit);
        }
        return this;
    }

    @Override // org.jooq.Commits
    public final org.jooq.Commit root() {
        return this.root;
    }

    @Override // org.jooq.Commits
    public final org.jooq.Commit current() {
        return new MigrationImpl(this.configuration, this.root).currentCommit();
    }

    @Override // org.jooq.Commits
    public final org.jooq.Commit latest() {
        Map<String, org.jooq.Commit> commits = new HashMap<>(this.commitsById);
        for (Map.Entry<String, org.jooq.Commit> e : this.commitsById.entrySet()) {
            for (org.jooq.Commit parent : e.getValue().parents()) {
                commits.remove(parent.id());
            }
        }
        if (commits.size() == 1) {
            return commits.values().iterator().next();
        }
        throw new DataMigrationVerificationException("No latest commit available. There are " + commits.size() + " unmerged branches.");
    }

    @Override // org.jooq.Commits
    public final org.jooq.Commit get(String id) {
        org.jooq.Commit result = this.commitsById.get(id);
        return result != null ? result : this.commitsByTag.get(id);
    }

    @Override // java.lang.Iterable
    public final Iterator<org.jooq.Commit> iterator() {
        return Collections.unmodifiableCollection(this.commitsById.values()).iterator();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CommitsImpl$FileData.class */
    public static final class FileData {
        final File file;
        final String basename;
        final String version;
        final String message;
        final List<TagType> tags;
        final String id;
        final List<String> parentIds;

        FileData(File file) {
            this.file = file;
            this.basename = file.getName().replace(".sql", "");
            String[] idAndParentsArray = this.basename.split("\\.");
            String[] idAndTagsArray = idAndParentsArray[0].split(",");
            this.id = idAndTagsArray[0];
            this.parentIds = idAndParentsArray.length > 1 ? Arrays.asList(idAndParentsArray[1].split(",")) : Arrays.asList(new String[0]);
            String[] idArray = this.id.split("-");
            this.version = idArray[0];
            this.message = idArray.length > 1 ? idArray[1] : null;
            this.tags = new ArrayList();
            for (int i = 1; i < idAndTagsArray.length; i++) {
                String[] tagArray = idAndTagsArray[i].split("-");
                this.tags.add(new TagType().withId(tagArray[0]).withMessage(tagArray.length > 1 ? tagArray[1] : null));
            }
        }
    }

    @Override // org.jooq.Commits
    public final Commits load(File directory) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Reading directory", directory);
        }
        File[] sql = directory.listFiles(f -> {
            return f.getName().endsWith(".sql");
        });
        File[] xml = directory.listFiles(f2 -> {
            return f2.getName().endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX);
        });
        if (!Tools.isEmpty(sql) && !Tools.isEmpty(xml)) {
            throw new DataMigrationVerificationException("A migration directory can only use either SQL files or XML files, not both.");
        }
        if (!Tools.isEmpty(sql)) {
            return loadSQL(sql);
        }
        if (!Tools.isEmpty(xml)) {
            return loadXML(xml);
        }
        return this;
    }

    private final Commits loadSQL(File[] sql) throws IOException {
        TreeMap<String, List<String>> versionToId = new TreeMap<>();
        Map<String, CommitType> idToCommit = new HashMap<>();
        List<FileData> list = (List) Stream.of((Object[]) sql).map(FileData::new).collect(Collectors.toList());
        if (log.isDebugEnabled()) {
            list.forEach(f -> {
                log.debug("Reading file", f.basename);
            });
        }
        for (FileData f2 : list) {
            ((List) versionToId.computeIfAbsent(f2.version, k -> {
                return new ArrayList();
            })).add(f2.id);
        }
        for (FileData f3 : list) {
            idToCommit.put(f3.id, new CommitType().withId(f3.id));
        }
        for (FileData f4 : list) {
            CommitType commit = idToCommit.get(f4.id);
            if (f4.parentIds.isEmpty()) {
                Map.Entry<String, List<String>> e = versionToId.lowerEntry(f4.version);
                if (e == null) {
                    continue;
                } else {
                    if (e.getValue().size() > 1) {
                        throw new DataMigrationVerificationException("Multiple predecessors for " + e.getKey() + ". Implicit parent cannot be detected: " + String.valueOf(e.getValue()));
                    }
                    commit.setParents(Arrays.asList(new ParentType().withId(e.getValue().get(0))));
                }
            } else {
                for (String parent : f4.parentIds) {
                    if (idToCommit.containsKey(parent)) {
                        commit.getParents().add(new ParentType().withId(parent));
                    } else {
                        throw new DataMigrationVerificationException("Parent " + parent + " is not defined");
                    }
                }
            }
            commit.withMessage(f4.message).withTags(f4.tags).withFiles(Arrays.asList(new FileType().withPath(f4.basename).withContentType(ContentType.INCREMENT).withContent(new String(Files.readAllBytes(f4.file.toPath())))));
        }
        return load(new MigrationsType().withCommits(idToCommit.values()));
    }

    private final Commits loadXML(File[] xml) {
        MigrationsType m = new MigrationsType();
        for (File f : xml) {
            MigrationsType u = (MigrationsType) MiniJAXB.unmarshal(f, MigrationsType.class);
            m = (MigrationsType) MiniJAXB.append(m, u);
        }
        return load(m);
    }

    @Override // org.jooq.Commits
    public final Commits load(MigrationsType migrations) {
        Map<String, CommitType> map = new HashMap<>();
        for (CommitType commit : migrations.getCommits()) {
            map.put(commit.getId(), commit);
        }
        Iterator<CommitType> it = migrations.getCommits().iterator();
        while (it.hasNext()) {
            load(map, it.next());
        }
        return this;
    }

    private final org.jooq.Commit load(Map<String, CommitType> map, CommitType commit) {
        org.jooq.Commit merge;
        org.jooq.Commit result = this.commitsById.get(commit.getId());
        if (result != null) {
            return result;
        }
        org.jooq.Commit p1 = this.root;
        org.jooq.Commit p2 = null;
        List<ParentType> parents = commit.getParents();
        int size = parents.size();
        if (size > 0) {
            CommitType c1 = map.get(parents.get(0).getId());
            if (c1 == null) {
                throw new DataMigrationVerificationException("Parent not found: " + parents.get(0).getId());
            }
            p1 = load(map, c1);
            if (size == 2) {
                CommitType c2 = map.get(parents.get(1).getId());
                if (c2 == null) {
                    throw new DataMigrationVerificationException("Parent not found: " + parents.get(0).getId());
                }
                p2 = load(map, c2);
            } else if (size > 2) {
                throw new DataMigrationVerificationException("Merging more than two parents not yet supported");
            }
        }
        if (p2 == null) {
            merge = p1.commit(commit.getId(), commit.getMessage(), files(commit));
        } else {
            merge = p1.merge(commit.getId(), commit.getMessage(), p2, files(commit));
        }
        org.jooq.Commit result2 = merge;
        for (TagType tag : commit.getTags()) {
            result2 = result2.tag(tag.getId(), tag.getMessage());
        }
        add(result2);
        return result2;
    }

    private final List<org.jooq.File> files(CommitType commit) {
        return Tools.map(commit.getFiles(), f -> {
            return this.migrations.file(f.getPath(), f.getChange() == ChangeType.DELETE ? null : f.getContent(), f.getContentType());
        });
    }

    @Override // org.jooq.Commits
    public final MigrationsType export() {
        return new MigrationsType().withCommits(Tools.map(this, commit -> {
            return new CommitType().withId(commit.id()).withMessage(commit.message()).withParents(Tools.map(commit.parents(), parent -> {
                return new ParentType().withId(parent.id());
            })).withTags(Tools.map(commit.tags(), tag -> {
                return new TagType().withId(tag.id()).withMessage(tag.message());
            })).withFiles(Tools.map(commit.files(), file -> {
                return new FileType().withPath(file.path()).withContent(file.content()).withContentType(file.type()).withChange(file.content() == null ? ChangeType.DELETE : ChangeType.MODIFY);
            }));
        }));
    }

    public String toString() {
        return String.valueOf(this.commitsById.values());
    }
}
