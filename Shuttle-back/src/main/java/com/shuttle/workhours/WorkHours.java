package com.shuttle.workhours;

import java.time.LocalDateTime;

import com.shuttle.common.Entity;

public class WorkHours extends Entity {
	private LocalDateTime start;
	private LocalDateTime end;

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}
}
