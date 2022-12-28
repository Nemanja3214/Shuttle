package com.shuttle.workhours;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.shuttle.driver.Driver;

public interface IWorkHoursService {
	public void addNew(Driver driver);
	public void finishLast(Driver driver);
	public List<WorkHours> findAllByDriver(Driver driver, Pageable pageable, LocalDateTime from, LocalDateTime to);
}
