package com.shuttle.user.passwordReset;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.user.GenericUser;

@Service
public class PasswordResetService implements IPasswordResetService {
	@Autowired
	private IPasswordResetRepository repo;
	
	@Override
	public PasswordResetCode create(GenericUser user) {
		PasswordResetCode pr = new PasswordResetCode();
		pr.setActive(true);
		pr.setUser(user);
		pr.setExpires(LocalDateTime.now().plusMinutes(5));
		pr = save(pr);
		pr.setCode(user.getId() + "" + pr.getId()); // TODO: This is bad.
		return save(pr);
	}
	
	@Override
	public PasswordResetCode save(PasswordResetCode pr) {
		return repo.save(pr);
	}

	@Override
	public PasswordResetCode findById(Long id) {
		return repo.findById(id).orElse(null);
	}

	@Override
	public PasswordResetCode findByCode(String code) {
		return repo.findByCode(code).orElse(null);
	}

	@Override
	public PasswordResetCode findByUser(GenericUser user) {
		PasswordResetCode r = repo.findByUser(user.getId()).orElse(null);
		if (r == null) {
			return null;
		}
		return r;
	}
	
	@Override
	public List<PasswordResetCode> findByUserMaybeExpired(GenericUser user) {
		return repo.findByUserActiveMaybeExpired(user.getId());
	}

	@Override
	public PasswordResetCode invalidate(PasswordResetCode pr) {
		pr.setActive(false);
		return repo.save(pr);
	}
}
