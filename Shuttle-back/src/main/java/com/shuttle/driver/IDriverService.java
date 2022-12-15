package com.shuttle.driver;

import java.util.Optional;

public interface IDriverService {
	public Driver add(Driver driver);
	public Optional<Driver> get(Long id);
}
