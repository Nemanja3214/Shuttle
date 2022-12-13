package com.shuttle.driver;

import com.shuttle.user.User;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Driver extends User {
    boolean available;
    Long timeWorkedToday;
    boolean blocked;



}
