package com.shuttle.message.dto;

import java.util.Collection;

public class MessagesDTO {
	private long totalCount;
	private Collection<MessageDTO> results;
	
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public Collection<MessageDTO> getResults() {
		return results;
	}
	public void setResults(Collection<MessageDTO> results) {
		this.results = results;
	}
	
	

}
