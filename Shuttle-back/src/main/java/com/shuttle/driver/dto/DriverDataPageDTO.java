package com.shuttle.driver.dto;

import java.util.ArrayList;

public class DriverDataPageDTO {
    public int totalCount;
    public ArrayList<DriverDTO> results;

    public DriverDataPageDTO(int totalCount) {
        this.totalCount = totalCount;
        results = new ArrayList<>();
    }

    public DriverDataPageDTO(int totalCount, ArrayList<DriverDTO> results) {
        this.totalCount = totalCount;
        this.results = results;
    }

    public void addResult(DriverDTO driver) {
        this.results.add(driver);
    }


}
