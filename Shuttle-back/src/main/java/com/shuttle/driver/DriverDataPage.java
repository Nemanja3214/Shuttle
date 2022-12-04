package com.shuttle.driver;

import java.awt.dnd.DragGestureEvent;
import java.util.ArrayList;
import java.util.List;

public class DriverDataPage {
    public int totalCount;
    public ArrayList<Driver> results;

    public DriverDataPage(int totalCount) {
        this.totalCount = totalCount;
        results = new ArrayList<>();
    }

    public DriverDataPage(int totalCount, ArrayList<Driver> results) {
        this.totalCount = totalCount;
        this.results = results;
    }

    public void addResult(Driver driver) {
        this.results.add(driver);
    }


}
