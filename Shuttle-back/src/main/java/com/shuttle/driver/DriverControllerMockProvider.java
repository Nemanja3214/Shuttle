package com.shuttle.driver;

public class DriverControllerMockProvider {
    public DriverDataPage getDriverDataPage() {
        DriverDataPage driverDataPage = new DriverDataPage(243);
        driverDataPage.addResult(getDriverData());
        return driverDataPage;
    }

    public Driver getDriverData() {
        return new Driver(Long.parseLong("123"), "Pera", "Peric", "U3dhZ2dlciByb2Nrcw==",
                "+381123123", "pera.peric@email.com", "Bulevar Oslobodjenja 74");
    }
}
