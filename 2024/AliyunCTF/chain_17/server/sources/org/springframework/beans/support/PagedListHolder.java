package org.springframework.beans.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/support/PagedListHolder.class */
public class PagedListHolder<E> implements Serializable {
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_MAX_LINKED_PAGES = 10;
    private List<E> source;

    @Nullable
    private Date refreshDate;

    @Nullable
    private SortDefinition sort;

    @Nullable
    private SortDefinition sortUsed;
    private int pageSize;
    private int page;
    private boolean newPageSet;
    private int maxLinkedPages;

    public PagedListHolder() {
        this(new ArrayList(0));
    }

    public PagedListHolder(List<E> source) {
        this(source, new MutableSortDefinition(true));
    }

    public PagedListHolder(List<E> source, SortDefinition sort) {
        this.source = Collections.emptyList();
        this.pageSize = 10;
        this.page = 0;
        this.maxLinkedPages = 10;
        setSource(source);
        setSort(sort);
    }

    public void setSource(List<E> source) {
        Assert.notNull(source, "Source List must not be null");
        this.source = source;
        this.refreshDate = new Date();
        this.sortUsed = null;
    }

    public List<E> getSource() {
        return this.source;
    }

    @Nullable
    public Date getRefreshDate() {
        return this.refreshDate;
    }

    public void setSort(@Nullable SortDefinition sort) {
        this.sort = sort;
    }

    @Nullable
    public SortDefinition getSort() {
        return this.sort;
    }

    public void setPageSize(int pageSize) {
        if (pageSize != this.pageSize) {
            this.pageSize = pageSize;
            if (!this.newPageSet) {
                this.page = 0;
            }
        }
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPage(int page) {
        this.page = page;
        this.newPageSet = true;
    }

    public int getPage() {
        this.newPageSet = false;
        if (this.page >= getPageCount()) {
            this.page = getPageCount() - 1;
        }
        return this.page;
    }

    public void setMaxLinkedPages(int maxLinkedPages) {
        this.maxLinkedPages = maxLinkedPages;
    }

    public int getMaxLinkedPages() {
        return this.maxLinkedPages;
    }

    public int getPageCount() {
        float nrOfPages = getNrOfElements() / getPageSize();
        return (int) ((nrOfPages > ((float) ((int) nrOfPages)) || ((double) nrOfPages) == 0.0d) ? nrOfPages + 1.0f : nrOfPages);
    }

    public boolean isFirstPage() {
        return getPage() == 0;
    }

    public boolean isLastPage() {
        return getPage() == getPageCount() - 1;
    }

    public void previousPage() {
        if (!isFirstPage()) {
            this.page--;
        }
    }

    public void nextPage() {
        if (!isLastPage()) {
            this.page++;
        }
    }

    public int getNrOfElements() {
        return getSource().size();
    }

    public int getFirstElementOnPage() {
        return getPageSize() * getPage();
    }

    public int getLastElementOnPage() {
        int endIndex = getPageSize() * (getPage() + 1);
        int size = getNrOfElements();
        return (endIndex > size ? size : endIndex) - 1;
    }

    public List<E> getPageList() {
        return getSource().subList(getFirstElementOnPage(), getLastElementOnPage() + 1);
    }

    public int getFirstLinkedPage() {
        return Math.max(0, getPage() - (getMaxLinkedPages() / 2));
    }

    public int getLastLinkedPage() {
        return Math.min((getFirstLinkedPage() + getMaxLinkedPages()) - 1, getPageCount() - 1);
    }

    public void resort() {
        SortDefinition sort = getSort();
        if (sort != null && !sort.equals(this.sortUsed)) {
            this.sortUsed = copySortDefinition(sort);
            doSort(getSource(), sort);
            setPage(0);
        }
    }

    protected SortDefinition copySortDefinition(SortDefinition sort) {
        return new MutableSortDefinition(sort);
    }

    protected void doSort(List<E> source, SortDefinition sort) {
        PropertyComparator.sort((List<?>) source, sort);
    }
}
