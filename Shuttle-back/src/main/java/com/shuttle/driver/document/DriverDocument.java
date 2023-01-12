package com.shuttle.driver.document;

import java.io.IOException;

import com.shuttle.common.FileUploadUtil;
import com.shuttle.driver.Driver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class DriverDocument {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	private String name;
	@ManyToOne
	private Driver driver;
	@Transient
	private String documentImage;
	
	public String getDocumentImage() {
		try {
			return FileUploadUtil.getImageBase64(FileUploadUtil.documentPictureUploadDir + "/", name);
		} catch (IOException e) {
			return null;
		}
		
	}
	

}
