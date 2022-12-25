package com.shuttle.driver;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

public interface IDriverRepository extends JpaRepository<Driver, Long> {
	@Query("select d from Driver d where d.loggedIn = true")
	public List<Driver> findAllLoggedIn();

	@Query("select d from Driver d where d.available = true and d.loggedIn = true")
	public List<Driver> findAllLoggedInAvailable();

	@Query("select d from Driver d where d.available = false and d.loggedIn = true")
	public List<Driver> findAllLoggedInNotAvailable();
}
