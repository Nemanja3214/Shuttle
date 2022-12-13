package com.shuttle.driver;

import com.shuttle.common.Entity;
import lombok.Data;

@Data
public class DriverDocumentDTO extends Entity {

    Long id;

    String name;

    String documentImage;

    Long driverId;
}
