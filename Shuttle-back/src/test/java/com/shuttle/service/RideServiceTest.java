package com.shuttle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.shuttle.ride.IRideRepository;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.ride.RideService;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {
    @Autowired
    @InjectMocks
    private RideService rideService;
    
	@Mock
	private IRideRepository rideRepository;
	
    @Test
    @DisplayName("findById [positive]")
    public void shouldFindById() {
    	final Long id = 123L;
    	Ride ride = new Ride();
    	ride.setId(id);
   	
    	Mockito.when(rideRepository.findById(id)).thenReturn(Optional.of(ride));	
    	Ride rideGot = rideService.findById(id);
    		
    	assertEquals(rideGot.getId(), ride.getId());
    }
   
    @ParameterizedTest
    @DisplayName("findById [negative] (returns null)")
    @ValueSource(longs = {-1, 0, -32832, 328372})
    public void shouldFindById(Long id) {
    	Mockito.when(rideRepository.findById(id)).thenReturn(Optional.empty());
    	Ride rideGot = rideService.findById(id);

    	assertEquals(rideGot, null);
    }
}
