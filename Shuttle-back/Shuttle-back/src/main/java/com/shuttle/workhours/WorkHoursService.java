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
	public WorkHours addNew(Driver driver) {
		return addNew(driver, LocalDateTime.now());
	}

	@Override
	public WorkHours finishLast(Driver driver) {
		List<WorkHours> allWh = workHoursRepository.findByDriver(driver);
		
		if (allWh.size() > 0) {
			WorkHours last = allWh.get(allWh.size() - 1);
			last.setFinish(LocalDateTime.now());
			workHoursRepository.save(last);
			return last;
		} else {
			return null;
		}
	}
	
	@Override
	public List<WorkHours> findAllByDriver(Driver driver, Pageable pageable, LocalDateTime from, LocalDateTime to) {
        //System.out.println("WorkHoursService::findAllByDriver() " + driverService.getDurationOfWorkInTheLast24Hours(driver).toString());
		return workHoursRepository.findByDriverId(driver.getId(), pageable, from, to).getContent();
	}

    @Override
    public List<WorkHours> findAllByDriver(Driver driver, LocalDateTime from, LocalDateTime to) {    
        return workHoursRepository.findByDriverId(driver.getId(), from, to);
    }

	@Override
	public WorkHours addNew(Driver driver, LocalDateTime start) {
		WorkHours wh = new WorkHours();
		wh.setStart(start);
		wh.setDriver(driver);
		wh.setFinish(null);
		wh = workHoursRepository.save(wh);
		return wh;
	}

	@Override
	public WorkHours findLastByDriver(Driver driver) {
		return workHoursRepository.findLastByDriver(driver.getId()).orElse(null);
	}

	@Override
	public WorkHours findById(Long id) {
		return workHoursRepository.findById(id).orElse(null);
	}

	@Override
	public WorkHours setEnd(WorkHours wh, LocalDateTime t) {
		wh.setFinish(t);
		wh = workHoursRepository.save(wh);
		return wh;
	}
}
