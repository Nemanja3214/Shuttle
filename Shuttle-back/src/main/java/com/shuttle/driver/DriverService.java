package com.shuttle.driver;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shuttle.common.FileUploadUtil;
import com.shuttle.common.exception.InvalidBase64Exception;
import com.shuttle.common.exception.NonExistantUserException;
import com.shuttle.driver.dto.DriverDTO;
import com.shuttle.driver.dto.DriverUpdateDTO;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.passenger.Passenger;
import com.shuttle.passenger.PassengerDTO;
import com.shuttle.passenger.PassengerUpdateDTO;
import com.shuttle.security.Role;
import com.shuttle.security.RoleService;
import com.shuttle.user.GenericUser;
import com.shuttle.user.UserService;
import com.shuttle.user.dto.UserDTO;
import com.shuttle.vehicle.IVehicleRepository;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.workhours.IWorkHoursService;
import com.shuttle.workhours.WorkHours;

@Service
public class DriverService implements IDriverService {
    @Autowired
	private IDriverRepository driverRepository;
    @Autowired
	private IVehicleRepository vehicleRepository;
    @Autowired
    private IWorkHoursService workHoursService;
    @Autowired
    private UserService userService;
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleService roleService;

	@Override
	public Driver add(Driver driver) {
		return driverRepository.save(driver);
	}

	@Override
	public Driver get(Long id) {
		return driverRepository.findById(id).orElse(null);
	}

	@Override
	public Driver setAvailable(Driver driver, boolean available) {
		driver.setAvailable(available);
		driver = driverRepository.save(driver);
		
		return driver;
	}

	@Override
	public List<LocationDTO> getActiveDriversLocations() {
		List<Driver> activeDrivers = driverRepository.findAllActive();
		List<Vehicle> driversVehicles = vehicleRepository.findByDriverIn(activeDrivers);
		return driversVehicles.stream().map(vehicle -> LocationDTO.from(vehicle.getCurrentLocation())).toList();
	}

	@Override
	public List<Driver> findAllActive() {
		return this.driverRepository.findAllActive();
	}
    @Override
    public Duration getDurationOfWorkInTheLast24Hours(Driver driver) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime startOfToday = now.minusHours(24);
        Duration totalWorked = Duration.ZERO;
        for (WorkHours wh : workHoursService.findAllByDriver(driver, startOfToday, now)) {
            if (wh.getFinish() == null) {
                // This is the last one in the list.
                totalWorked = totalWorked.plus(Duration.between(wh.getStart(), now));
            } else {
                totalWorked = totalWorked.plus(Duration.between(wh.getStart(), wh.getFinish()));
            }
            
        }

        return totalWorked;
    }

    @Override
    public boolean workedMoreThan8Hours(Driver d) {
        Duration dur = getDurationOfWorkInTheLast24Hours(d);
        return (dur.compareTo(Duration.ofHours(8)) > 0);
    }

	@Override
	public Driver create(DriverDTO driverDTO) {
		Driver d = createDriver(driverDTO);
		d = driverRepository.save(d);
		return d;
	}
	
	private Driver createDriver(DriverDTO driverDTO) {
		Driver d = driverDTO.to();
		d.setActive(false);
		d.setBlocked(false);
		d.setEnabled(false);
		d.setAvailable(false);
		List<Role> driverRole = roleService.findByName("passenger");
		d.setRoles(driverRole);
		
		String encodedPassword = passwordEncoder.encode(driverDTO.getPassword());
		d.setPassword(encodedPassword);
		return d;
	}

	@Override
	public List<Driver> findAll(Pageable pageable) {
		return this.driverRepository.findAll(pageable).toList();
	}

	@Override
	public Driver update(Driver driver, DriverUpdateDTO dto) throws IOException, InvalidBase64Exception {
		
		if (dto.getProfilePicture() != null) {
			FileUploadUtil.deleteFile(FileUploadUtil.profilePictureUploadDir, driver.getProfilePictureName());
			FileUploadUtil.saveFile(FileUploadUtil.profilePictureUploadDir, driver.getProfilePictureName(), dto.getProfilePicture());
		}
		
		changeDriver(driver, dto);
		driver = driverRepository.save(driver);
		return driver;
	}
	
	private void changeDriver(Driver driver, DriverUpdateDTO dto) {
		driver.setAddress(dto.getAddress());
		driver.setEmail(dto.getEmail());
		driver.setName(dto.getName());
		if (dto.getProfilePicture() != null) driver.setProfilePicture(dto.getProfilePicture());
		driver.setSurname(dto.getSurname());
		if (dto.getTelephoneNumber() != null) driver.setTelephoneNumber(dto.getTelephoneNumber());
	}
}
