package com.shuttle.common;

import java.util.Collection;

public class CollectionDTO<T> {
	private long totalCount;
	private Collection<T> results;
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public Collection<T> getResults() {
		return results;
	}
	public void setResults(Collection<T> results) {
		this.results = results;
	}
	
	
}
