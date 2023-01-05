package com.shuttle.panic;

import java.time.LocalDateTime;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.user.dto.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PanicDTO {
    private long id;
    private UserDTO user;
    private RideDTO ride;
    private String time;
    private String reason;
}
