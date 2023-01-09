package com.shuttle.driver.document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DriverDocumentDTO {
	private Long id;
	private String name;
	private String documentImage;
	private Long driverId;
	
	public DriverDocumentDTO(DriverDocument dd) {
		this.id = dd.getId();
		this.name = dd.getName();
		this.documentImage = dd.getImage();
		this.driverId = dd.getDriver().getId();
	}
}