package com.shuttle.driver.document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DriverDocumentDTO {
	private Long id;
	private String name;
	private Long driverId;
	private String documentImage;
	
	public DriverDocumentDTO(DriverDocument dd) {
		this.id = dd.getId();
		this.name = dd.getName();
		this.driverId = dd.getDriver().getId();
		this.documentImage = dd.getDocumentImage();
	}
}