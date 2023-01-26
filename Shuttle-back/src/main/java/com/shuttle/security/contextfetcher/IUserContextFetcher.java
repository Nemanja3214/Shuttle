package com.shuttle.security.contextfetcher;

import com.shuttle.user.GenericUser;

public interface IUserContextFetcher {
	GenericUser getUserFromContext();
}
