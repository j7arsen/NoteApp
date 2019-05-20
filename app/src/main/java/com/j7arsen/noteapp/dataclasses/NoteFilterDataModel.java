package com.j7arsen.noteapp.dataclasses;

import com.j7arsen.noteapp.app.Constants;

import java.io.Serializable;

public class NoteFilterDataModel implements Serializable {

    public static final String SORT_ASC = "ASC";
    public static final String SORT_DESC = "DESC";

    private String sortType;
    private String searchQuery;
    private int page;
    private int limit;

    public NoteFilterDataModel() {
        sortType = SORT_DESC;
        page = 0;
        limit = Constants.NOTE_LIMIT;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType, OnChangeListener onChangeListener) {
        if (!sortType.equals(this.sortType)) {
            this.sortType = sortType;
            if (onChangeListener != null) {
                onChangeListener.onChange();
            }
        }
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset(){
        return page * limit;
    }

    public void resetPaging() {
        page = 0;
    }

    public void resetFilters() {
        resetPaging();
        sortType = SORT_ASC;
    }

    public interface OnChangeListener {
        void onChange();
    }

}
