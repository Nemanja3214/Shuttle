package com.shuttle.workhours;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkHours {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	private LocalDateTime start;
	private LocalDateTime end;

}
