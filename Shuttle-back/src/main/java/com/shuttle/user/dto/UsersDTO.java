package com.shuttle.user.dto;

import java.util.Collection;

public class UsersDTO {
	private long totalCount;
	Collection<UserDTO> users;
	
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public Collection<UserDTO> getUsers() {
		return users;
	}
	public void setUsers(Collection<UserDTO> users) {
		this.users = users;
	}

	
}
