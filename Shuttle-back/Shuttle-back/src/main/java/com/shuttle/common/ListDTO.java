package com.shuttle.common;

import java.util.ArrayList;
import java.util.List;

public class ListDTO<T> {
	private long totalCount;
	private List<T> results;
	
	public ListDTO() {
		super();
		this.results = new ArrayList<>();
	}

    public ListDTO(List<T> list) {
        this.results = list;
        this.totalCount = list.size();
    }

	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public List<T> getResults() {
		return results;
	}
	public void setResults(List<T> results) {
		this.results = results;
	}
	
	
}
