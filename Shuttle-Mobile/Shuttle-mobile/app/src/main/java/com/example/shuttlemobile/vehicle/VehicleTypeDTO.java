package com.example.shuttlemobile.vehicle;

import java.io.Serializable;

public class VehicleTypeDTO implements Serializable {
    private Long id;
    private String name;
    private Double pricePerKM;

    public VehicleTypeDTO() {
    }

    public VehicleTypeDTO(Long id, String name, Double pricePerKM) {
        this.id = id;
        this.name = name;
        this.pricePerKM = pricePerKM;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPricePerKM() {
        return pricePerKM;
    }

    public void setPricePerKM(Double pricePerKM) {
        this.pricePerKM = pricePerKM;
    }

    @Override
    public String toString() {
        return "VehicleTypeDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pricePerKM=" + pricePerKM +
                '}';
    }
}
