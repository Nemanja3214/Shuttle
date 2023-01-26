package com.shuttle.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverService;
import com.shuttle.location.ILocationService;
import com.shuttle.panic.IPanicService;
import com.shuttle.passenger.IPassengerRepository;
import com.shuttle.passenger.IPassengerService;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.RideController;
import com.shuttle.ride.cancellation.ICancellationService;
import com.shuttle.security.contextfetcher.IUserContextFetcher;
import com.shuttle.security.jwt.JwtTokenUtil;
import com.shuttle.user.UserService;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;

@WebMvcTest(RideController.class)
@AutoConfigureMockMvc(addFilters = false)
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
    @MockBean
    private IUserContextFetcher userContextFetcher;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private MockMvc mockMvc;

    
    @Test
    @DisplayName("Accept ride that is pending")
    @WithMockUser(roles = "admin")
    public void acceptRide_isPending() throws Exception {
    	final long DRIVER_ID = 1;
    	final Driver driver = MockProvider.driver(DRIVER_ID, "bob@gmail.com", UserService.ROLE_DRIVER);
    
    	final long RIDE_ID = 1;
    	final Ride.Status RIDE_STATUS = Ride.Status.PENDING;
    	Ride ride = MockProvider.rideBase(RIDE_ID, RIDE_STATUS);
    	ride.setDriver(driver);
    	
    	Mockito.when(userService.isDriver(driver)).thenReturn(true);
    	Mockito.when(userContextFetcher.getUserFromContext()).thenReturn(driver);
    	Mockito.when(rideService.findById(RIDE_ID)).thenReturn(ride);
    	
    	mockMvc.perform(put("/api/ride/{id}/accept", RIDE_ID)).andDo(print());
    }
}
