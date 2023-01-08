package com.shuttle.user.passwordReset;

import java.util.List;

import com.shuttle.user.GenericUser;

public interface IPasswordResetService {
	PasswordResetCode create(GenericUser user);
	PasswordResetCode save(PasswordResetCode pr);
	PasswordResetCode findById(Long id);
	PasswordResetCode findByCode(String code);
	PasswordResetCode findByUser(GenericUser user);
	List<PasswordResetCode> findByUserMaybeExpired(GenericUser user);
}
