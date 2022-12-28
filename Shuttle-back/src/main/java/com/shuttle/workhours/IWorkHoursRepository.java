package com.shuttle.workhours;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shuttle.driver.Driver;

public interface IWorkHoursRepository extends JpaRepository<WorkHours, Long> {
	public List<WorkHours> findByDriver(Driver driver);

    @Query(value = "select wh from WorkHours wh where wh.driver.id = :driverId and (wh.start >= :timeFrom and (wh.finish = null or wh.finish <= :timeTo))")
    public List<WorkHours> findByDriverId(Long driverId, LocalDateTime timeFrom, LocalDateTime timeTo);

	@Query(value = "select wh from WorkHours wh where wh.driver.id = :driverId and (wh.start >= :timeFrom and (wh.finish = null or wh.finish <= :timeTo))")
	public Page<WorkHours> findByDriverId(Long driverId, Pageable page, LocalDateTime timeFrom, LocalDateTime timeTo);
}
