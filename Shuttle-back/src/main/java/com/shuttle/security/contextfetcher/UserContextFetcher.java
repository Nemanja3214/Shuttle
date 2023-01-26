package com.shuttle.security.contextfetcher;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shuttle.user.GenericUser;

@Service
public class UserContextFetcher implements IUserContextFetcher {
	public GenericUser getUserFromContext() {
		return (GenericUser)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	}
}
