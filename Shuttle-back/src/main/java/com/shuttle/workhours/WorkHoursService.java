package com.shuttle.workhours;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shuttle.driver.Driver;

@Service
public class WorkHoursService implements IWorkHoursService {
	@Autowired
	private IWorkHoursRepository workHoursRepository;
	
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
		return workHoursRepository.findByDriverId(driver.getId(), pageable, from, to).getContent();
	}
}
