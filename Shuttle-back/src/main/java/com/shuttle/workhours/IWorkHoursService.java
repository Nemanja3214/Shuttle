package com.shuttle.workhours;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.shuttle.driver.Driver;

public interface IWorkHoursService {
	public WorkHours addNew(Driver driver);
	public WorkHours addNew(Driver driver, LocalDateTime start);
	public WorkHours finishLast(Driver driver);
	public WorkHours findLastByDriver(Driver driver);
	public List<WorkHours> findAllByDriver(Driver driver, Pageable pageable, LocalDateTime from, LocalDateTime to);
    public List<WorkHours> findAllByDriver(Driver driver, LocalDateTime from, LocalDateTime to);
}
