package com.shuttle.driver;

import com.shuttle.location.Location;
import com.shuttle.note.Note;
import com.shuttle.security.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.List;

import com.shuttle.user.GenericUser;
import com.shuttle.workhours.WorkHours;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.sql.Timestamp;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="driver")
public class Driver extends GenericUser {
    boolean available; // This is determined by the current ride.
    Long timeWorkedToday;
    boolean blocked;


}
