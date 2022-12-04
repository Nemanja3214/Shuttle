package com.shuttle.driver;

import com.shuttle.passenger.Passenger;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DriverController {

    @PostMapping("/api/driver")
    public ResponseEntity<DriverDTO> createDriver(@RequestBody Driver driver) {
        driver.setId(Long.valueOf(123));
        return new ResponseEntity<>(driver.parse2DTO(), HttpStatus.OK);
    }
    @GetMapping("/api/driver")
    public ResponseEntity<DriverDataPage> getPaginatedDrivers(@PathParam("page") int page, @PathParam("size") int size) {
        DriverControllerMockProvider driverControllerMockProvider= new DriverControllerMockProvider();
        return new ResponseEntity<>(driverControllerMockProvider.getDriverDataPage(), HttpStatus.OK);
    }

    @GetMapping("/api/driver/{id}")
    public ResponseEntity<DriverDTO> getDriverDetails(@PathVariable(value="id") Long id) {
        DriverControllerMockProvider driverControllerMockProvider= new DriverControllerMockProvider();
        return new ResponseEntity<>(driverControllerMockProvider.getDriverData(), HttpStatus.OK);
    }

    @PutMapping("/api/driver/{id}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable(value="id") Long id,@RequestBody DriverDTO driver) {
        return new ResponseEntity<>(driver, HttpStatus.OK);
    }
}

