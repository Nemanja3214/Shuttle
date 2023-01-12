package com.shuttle.driver.document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.common.FileUploadUtil;
import com.shuttle.common.exception.InvalidBase64Exception;
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
	public void delete(DriverDocument doc) {
		String uploadPath = FileUploadUtil.documentPictureUploadDir + doc.getDriver().getFolderName();
		FileUploadUtil.deleteFile(uploadPath + "/", doc.getName());
		repo.delete(doc);
	}

	@Override
	public DriverDocument create(Driver driver, DriverDocumentCreateDTO driverDocumentDTO) throws IOException, InvalidBase64Exception {
		DriverDocument doc = new DriverDocument();
		
		String uploadPath = FileUploadUtil.documentPictureUploadDir + driver.getFolderName() + "/";
		File f = new File(uploadPath);
		FileUploadUtil.saveFile(uploadPath, driverDocumentDTO.getName(), driverDocumentDTO.getDocumentImage());
		
		doc.setName(driverDocumentDTO.getName());
		doc.setDriver(driver);
		doc.setDocumentImage(driverDocumentDTO.getDocumentImage());
		doc = repo.save(doc);
		return doc;
	}

}
