package com.shuttle.panic;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.shuttle.ride.dto.RideDTO;
import com.shuttle.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanicDTO {


    private long id;
    private UserDTO user;
    private RideDTO ride;
    private LocalDateTime time;
    private String reason;


//    public static PanicDTO getMock() {
//        return new PanicDTO(10, UserDTO.getMock(), RideDTO.getMock(), LocalDateTime.now(), "Driver is drinking while driving");
//    }


}
