package com.shuttle.panic;

import java.time.LocalDateTime;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.user.dto.UserDTO;
import com.shuttle.user.dto.UserDTONoPassword;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PanicDTO {
    private Long id;
    private UserDTONoPassword user;
    private RideDTO ride;
    private String time;
    private String reason;
}
