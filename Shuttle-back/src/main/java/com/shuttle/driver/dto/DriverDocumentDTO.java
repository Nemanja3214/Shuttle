package com.shuttle.driver.dto;

import lombok.Data;

@Data
public class DriverDocumentDTO  {

    Long id;

    String name;

    String documentImage;

    Long driverId;
}
