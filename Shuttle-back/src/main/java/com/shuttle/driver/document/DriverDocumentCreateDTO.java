package com.shuttle.driver.document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DriverDocumentCreateDTO {
	private String name;
	private String documentImage;
}
