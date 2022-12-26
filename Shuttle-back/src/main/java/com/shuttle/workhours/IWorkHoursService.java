package com.shuttle.workhours;

import com.shuttle.driver.Driver;

public interface IWorkHoursService {
	public void addNew(Driver driver);
	public void finishLast(Driver driver);
}
