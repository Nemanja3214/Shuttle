package com.shuttle.driver;

public class DriverDocumentDTO {
    Long id;
    String name;
    String docImage;
    Long driverId;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDocImage(String docImage) {
        this.docImage = docImage;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDocImage() {
        return docImage;
    }

    public Long getDriverId() {
        return driverId;
    }
}
