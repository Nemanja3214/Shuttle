package com.shuttle.driver;

import lombok.Data;

@Data
public class DriverDocumentDTO  {

    Long id;

    String name;

    String documentImage;

    Long driverId;
}
