package com.shuttle.driver;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import com.shuttle.driver.dto.DriverStatDTO;
import com.shuttle.panic.IPanicRepository;
import com.shuttle.panic.Panic;
import com.shuttle.ride.IRideRepository;
import com.shuttle.ride.IRideService;
import com.shuttle.ride.Ride;
import com.shuttle.vehicle.VehicleAdminHomeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shuttle.ProfileChangeRequest.IProfileChangeRepository;
import com.shuttle.ProfileChangeRequest.ProfileChangeRequest;
import com.shuttle.common.FileUploadUtil;
import com.shuttle.common.exception.InvalidBase64Exception;
import com.shuttle.driver.dto.DriverDTO;
import com.shuttle.driver.dto.DriverUpdateDTO;
import com.shuttle.location.dto.LocationDTO;
import com.shuttle.security.Role;
import com.shuttle.security.RoleService;
import com.shuttle.user.UserService;
import com.shuttle.vehicle.IVehicleRepository;
import com.shuttle.vehicle.Vehicle;
import com.shuttle.workhours.IWorkHoursService;
import com.shuttle.workhours.WorkHours;

import jakarta.transaction.Transactional;

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
    @Autowired
    private IProfileChangeRepository profileChangeRepository;

    @Autowired
    private IRideRepository rideRepository;

    @Autowired
    IPanicRepository panicRepository;

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
    public List<VehicleAdminHomeDTO> getActiveDriversVehicleLocations() {
        List<Driver> activeDrivers = driverRepository.findAllActive();
        List<Vehicle> driversVehicles = vehicleRepository.findByDriverIn(activeDrivers);
        return driversVehicles.stream().map(vehicle -> {
            VehicleAdminHomeDTO vehicleAdminHomeDTO = new VehicleAdminHomeDTO(vehicle);
            List<Panic> panic = panicRepository.findPanicsByRide(rideRepository.findFirstByDriverOrderByStartTimeDesc(vehicle.getDriver()));
            vehicleAdminHomeDTO.setPanic(!panic.isEmpty());
            return vehicleAdminHomeDTO;
        }).toList();
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
    public Driver create(DriverDTO driverDTO) throws IOException {
        Driver d = createDriver(driverDTO);
        try {
            FileUploadUtil.saveFile(FileUploadUtil.profilePictureUploadDir, d.getProfilePictureName(), driverDTO.getProfilePicture());
        } catch (InvalidBase64Exception e) {
//			Nothing just user has default picture
        }
        d.setEnabled(true);
        d = driverRepository.save(d);
        return d;
    }

    private Driver createDriver(DriverDTO driverDTO) {
        Driver d = driverDTO.to();
        d.setActive(false);
        d.setBlocked(false);
        d.setEnabled(false);
        d.setAvailable(false);
        List<Role> driverRole = roleService.findByName("driver");
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
    public Driver update(Driver driver, DriverUpdateDTO dto) throws IOException {
        if (dto.getProfilePicture() != null) {
            FileUploadUtil.deleteFile(FileUploadUtil.profilePictureUploadDir, driver.getProfilePictureName());
        }

        changeDriver(driver, dto);
        driver = driverRepository.save(driver);

        if (dto.getProfilePicture() != null) {
            try {
                FileUploadUtil.saveFile(FileUploadUtil.profilePictureUploadDir, driver.getProfilePictureName(), dto.getProfilePicture());
            } catch (InvalidBase64Exception e) {
//				Nothing just user has default picture
            }
        }
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

    @Override
    public DriverStatDTO getDriverStatistics(Driver driver, String scope) {
        List<Ride> finishedRides = rideRepository.findByDriverAndStatus(driver, Ride.Status.FINISHED);
        double cancelledRidesCount = rideRepository.findByDriverAndStatus(driver, Ride.Status.CANCELED).size();

        finishedRides = finishedRides.stream().sorted(Comparator.comparing(Ride::getStartTime)).toList();

        try {

            Ride firstRide = finishedRides.get(0);
            Ride lastRide = finishedRides.get(finishedRides.size() - 1);

            final Duration[] hoursDriven = {Duration.ZERO};
            AtomicReference<Double> moneyEarned = new AtomicReference<>((double) 0);

            finishedRides.forEach(ride -> {
                moneyEarned.updateAndGet(v -> v + ride.getTotalCost());
                hoursDriven[0] = hoursDriven[0].plus(Duration.between(ride.getEndTime(), ride.getStartTime()));
            });
            long modifier = 1;
            if (scope.equals("daily")) {
                modifier = ChronoUnit.DAYS.between(lastRide.getEndTime(), firstRide.getStartTime());
            }
            if (scope.equals("weekly")) {
                modifier = ChronoUnit.WEEKS.between(lastRide.getEndTime(), firstRide.getStartTime());
            }
            if (scope.equals("monthly")) {
                modifier = ChronoUnit.MONTHS.between(lastRide.getEndTime(), firstRide.getStartTime());
            }
            if (modifier == 0) modifier = 1;
            return new DriverStatDTO(cancelledRidesCount / modifier, finishedRides.size() / modifier, hoursDriven[0].toHours() / modifier, moneyEarned.get() / modifier);

        } catch (Exception ignored) {

        }
        return new DriverStatDTO(0, 0, 0, 0);
    }

	@Override
	@Transactional
	public ProfileChangeRequest requestUpdate(Driver driver, DriverUpdateDTO dto) {
		ProfileChangeRequest request = new ProfileChangeRequest();
		request.setAddress(dto.getAddress());
		request.setName(dto.getName());
        request.setSurname(dto.getSurname());
        request.setTelephoneNumber(dto.getTelephoneNumber());
        if (dto.getProfilePicture() != null) request.setProfilePicture(dto.getProfilePicture());
		request.setUser(driver);
        request = profileChangeRepository.save(request);
        return request;
	}

	@Override
	public ProfileChangeRequest getProfileChange(Long id) {
		return this.profileChangeRepository.getById(id);
	}

	@Override
	@Transactional
	public Driver applyRequest(ProfileChangeRequest request) throws IOException {
		request = this.profileChangeRepository.save(request);
		 if (request.getProfilePicture() != null) {
            FileUploadUtil.deleteFile(FileUploadUtil.profilePictureUploadDir, request.getUser().getProfilePictureName());
        }
		Driver driver = this.driverRepository.getById(request.getUser().getId());
		driver.setName(request.getName());
		driver.setAddress(request.getAddress());
		driver.setSurname(request.getSurname());
		driver.setTelephoneNumber(request.getTelephoneNumber());

        driver = driverRepository.save(driver);

        if (request.getProfilePicture() != null) {
            try {
                FileUploadUtil.saveFile(FileUploadUtil.profilePictureUploadDir, driver.getProfilePictureName(), request.getProfilePicture());
            } catch (InvalidBase64Exception e) {
//					Nothing just user has default picture
            }
        }
		return driver;
	}
}
