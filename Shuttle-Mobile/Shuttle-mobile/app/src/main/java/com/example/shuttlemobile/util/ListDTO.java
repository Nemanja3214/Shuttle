package com.example.shuttlemobile.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListDTO<T> implements Serializable {
    private Long totalCount;
    private List<T> results;

    public ListDTO(Long totalCount, List<T> results) {
        this.totalCount = totalCount;
        this.results = results;
    }

    public ListDTO() {
        this.totalCount = 0L;
        this.results = new ArrayList<>();
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ListDTO{" +
                "totalCount=" + totalCount +
                ", results=" + results +
                '}';
    }
}
