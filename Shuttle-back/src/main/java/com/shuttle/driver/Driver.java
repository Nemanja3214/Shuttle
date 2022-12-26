package com.shuttle.driver;

import com.shuttle.user.GenericUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="driver")
public class Driver extends GenericUser {
    boolean available; // This is determined by the current ride.
	//boolean blocked;
    Long timeWorkedToday;
}
