package com.shuttle.workhours;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shuttle.driver.Driver;

public interface IWorkHoursRepository extends JpaRepository<WorkHours, Long> {
	public List<WorkHours> findByDriver(Driver driver);
}
