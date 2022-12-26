package com.shuttle.workhours;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

import com.shuttle.driver.Driver;

@Data
@Entity
@Table(name="work_hours")
public class WorkHours {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	private LocalDateTime start;
	private LocalDateTime finish;
	
	@ManyToOne
	private Driver driver;
}
