package com.shuttle.driver;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface IDriverRepository extends CrudRepository<Driver, Long> {
	@Query("select d from Driver d where d.loggedIn = true")
	public List<Driver> findAllLoggedIn();
	
	@Query("select d from Driver d where d.available = true and d.loggedIn = true")
	public List<Driver> findAllLoggedInAvailable();
	
	@Query("select d from Driver d where d.available = false and d.loggedIn = true")
	public List<Driver> findAllLoggedInNotAvailable();
}
