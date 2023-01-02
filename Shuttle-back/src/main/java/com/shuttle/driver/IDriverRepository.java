package com.shuttle.driver;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.shuttle.driver.dto.DriverDTO;

public interface IDriverRepository extends JpaRepository<Driver, Long> {
	@Query("select d from Driver d where d.active = true")
	public List<Driver> findAllActive();

	@Query("select d from Driver d where d.available = true and d.active = true")
	public List<Driver> findAllActiveAvailable();

	@Query("select d from Driver d where d.available = false and d.loggedIn = true")
	public List<Driver> findAllLoggedInNotAvailable();

	public List<Driver> findByAvailableTrue();
	@Query("select d from Driver d where d.available = false and d.active = true")
	public List<Driver> findAllActiveNotAvailable();
}
