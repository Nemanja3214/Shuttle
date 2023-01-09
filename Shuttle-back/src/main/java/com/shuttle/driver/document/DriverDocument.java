package com.shuttle.driver.document;

import com.shuttle.driver.Driver;

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
