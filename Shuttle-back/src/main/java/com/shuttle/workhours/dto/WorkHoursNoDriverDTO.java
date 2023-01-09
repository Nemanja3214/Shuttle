package com.shuttle.workhours.dto;

import com.shuttle.workhours.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkHoursNoDriverDTO {
	private Long id;
	private String start;
	private String end;
	
	public WorkHoursNoDriverDTO(WorkHours workHours) {
		this.id = workHours.getId();
		this.start = workHours.getStart() == null ? null : workHours.getStart().toString();
		this.end = workHours.getFinish() == null ? null : workHours.getFinish().toString();
	}
}
