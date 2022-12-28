package com.shuttle.workhours;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;
import com.shuttle.driver.IDriverService;

@Service
public class WorkHoursService implements IWorkHoursService {
	@Autowired
	private IWorkHoursRepository workHoursRepository;
    @Autowired
    @Lazy
    private IDriverService driverService;
	
	@Override
	public void addNew(Driver driver) {
		WorkHours wh = new WorkHours();
		wh.setStart(LocalDateTime.now());
		wh.setDriver(driver);
		wh.setFinish(null);
		wh = workHoursRepository.save(wh);
	}

	@Override
	public void finishLast(Driver driver) {
		List<WorkHours> allWh = workHoursRepository.findByDriver(driver);
		
		if (allWh.size() > 0) {
			WorkHours last = allWh.get(allWh.size() - 1);
			last.setFinish(LocalDateTime.now());
			workHoursRepository.save(last);
		}
	}
	
	@Override
	public List<WorkHours> findAllByDriver(Driver driver, Pageable pageable, LocalDateTime from, LocalDateTime to) {
        System.out.println("WorkHoursService::findAllByDriver() " + driverService.getDurationOfWorkToday(driver).toString());
		return workHoursRepository.findByDriverId(driver.getId(), pageable, from, to).getContent();
	}

    @Override
    public List<WorkHours> findAllByDriver(Driver driver, LocalDateTime from, LocalDateTime to) {    
        return workHoursRepository.findByDriverId(driver.getId(), from, to);
    }
}
