package com.shuttle.driver;

import java.util.List;

import com.shuttle.user.GenericUser;
import com.shuttle.workhours.WorkHours;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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
