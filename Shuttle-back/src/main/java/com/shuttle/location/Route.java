package com.shuttle.location;

import com.shuttle.common.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@jakarta.persistence.Entity
@Data
public class Route extends Entity {
	@ManyToMany
	List<Location> locations;
}
