package com.shuttle.driver;

import com.shuttle.common.Entity;
import lombok.Getter;
import lombok.Setter;

public class DriverDocumentDTO extends Entity {

    @Getter
    @Setter
    Long id;

    @Getter
    @Setter
    String name;

    @Getter
    @Setter
    String documentImage;

    @Getter
    @Setter
    Long driverId;
}
