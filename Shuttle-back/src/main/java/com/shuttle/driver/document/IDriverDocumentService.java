package com.shuttle.driver.document;

import java.io.IOException;
import java.util.List;

import com.shuttle.common.exception.InvalidBase64Exception;
import com.shuttle.driver.Driver;

public interface IDriverDocumentService {
	DriverDocument findById(Long id);
	List<DriverDocument> findByDriver(Driver driver);
	void delete(DriverDocument doc);
	DriverDocument create(Driver driver, DriverDocumentCreateDTO driverDocumentDTO) throws IOException, InvalidBase64Exception;
}
