package com.shuttle.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PutMapping;

import com.shuttle.driver.IDriverService;
import com.shuttle.location.ILocationService;
import com.shuttle.panic.IPanicService;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.passenger.IPassengerService;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.Ride.Status;
import com.shuttle.ride.RideController;
import com.shuttle.ride.cancellation.ICancellationService;
import com.shuttle.user.UserService;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;

@WebMvcTest(controllers = RideController.class)
public class RideControllerTest {
    @MockBean
    private IRideService rideService;
    @MockBean
    private IDriverService driverService;
    @MockBean
    private IVehicleTypeRepository vehicleTypeRepository;
    @MockBean
    private IPassengerRepository passengerRepository;
    @MockBean
    private ILocationService locationService;
    @MockBean
    private ICancellationService cancellationService;
    @MockBean
    private SimpMessagingTemplate template;
    @MockBean
    private IPassengerService passengerService;
    @MockBean
    private IPanicService panicService;
    @MockBean
    private UserService userService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("Accept ride that isn't pending")
    public void acceptRide_isNotPending() throws Exception {
    	// TODO: How to inject JWT?
    	final long RIDE_ID = 1;
    	Ride ride = new Ride();
    	ride.setStatus(Status.ACCEPTED);
    	
    	Mockito.when(rideService.findById(RIDE_ID)).thenReturn(ride);
    	mockMvc.perform(put("/{id}/accept", RIDE_ID)).andDo(print());
    }
}
