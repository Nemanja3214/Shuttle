package com.shuttle.workhours.dto;

import java.time.LocalDateTime;

import com.shuttle.workhours.WorkHours;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkHoursNoDriverDTO {
	private LocalDateTime start;
	private LocalDateTime finish;
	
	public WorkHoursNoDriverDTO(WorkHours workHours) {
		this.start = workHours.getStart();
		this.finish = workHours.getFinish();
	}
}
