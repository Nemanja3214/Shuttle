package com.shuttle.notification.dto;

import java.util.Collection;

public class PanicNotificationsDTO {
	private long size;
	Collection<PanicNotificationDTO> results;
	
	
	public PanicNotificationsDTO() {
		super();
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public Collection<PanicNotificationDTO> getResults() {
		return results;
	}
	public void setResults(Collection<PanicNotificationDTO> results) {
		this.results = results;
	}
	
	 
}
