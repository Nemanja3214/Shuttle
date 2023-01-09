package com.shuttle.driver.document;

import java.util.List;

import com.shuttle.driver.Driver;

public interface IDriverDocumentService {
	DriverDocument findById(Long id);
	List<DriverDocument> findByDriver(Driver driver);
	DriverDocument save(DriverDocument doc);
	void delete(DriverDocument doc);
	DriverDocument create(Driver driver, DriverDocumentCreateDTO driverDocumentDTO);
}
