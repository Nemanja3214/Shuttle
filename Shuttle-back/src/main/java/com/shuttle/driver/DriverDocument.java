package com.shuttle.driver;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DriverDocument {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	private String name;
	private String image;
	@ManyToOne
	private Driver driver;
}
