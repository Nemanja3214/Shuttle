package com.shuttle.driver.document;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;

@Service
public class DriverDocumentService implements IDriverDocumentService {
	@Autowired
	private IDriverDocumentRepository repo;
	
	@Override
	public DriverDocument findById(Long id) {
		return repo.findById(id).orElse(null);
	}

	@Override
	public List<DriverDocument> findByDriver(Driver driver) {
		return repo.findByDriver(driver.getId());
	}

	@Override
	public DriverDocument save(DriverDocument doc) {
		return repo.save(doc);
	}

	@Override
	public void delete(DriverDocument doc) {
		repo.delete(doc);
	}

	@Override
	public DriverDocument create(Driver driver, DriverDocumentCreateDTO driverDocumentDTO) {
		DriverDocument doc = new DriverDocument();
		doc.setDriver(driver);
		doc.setName(driverDocumentDTO.getName());
		doc.setImage(driverDocumentDTO.getDocumentImage());
		doc = repo.save(doc);
		return doc;
	}

}
