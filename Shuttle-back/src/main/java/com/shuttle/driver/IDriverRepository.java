package com.shuttle.driver;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface IDriverRepository extends CrudRepository<Driver, Long> {
	@Query("select d from Driver d where d.loggedIn = true")
	public List<Driver> findAllLoggedIn();
	
	@Query("select d from Driver d where d.available = true")
	public List<Driver> findAllAvailable(); // loggedIn = true
	
	@Query("select d from Driver d where d.available = false")
	public List<Driver> findAllNotAvailable(); // loggedIn = true
}
