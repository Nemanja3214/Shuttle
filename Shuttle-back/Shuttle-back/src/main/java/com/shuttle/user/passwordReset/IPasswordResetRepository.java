package com.shuttle.user.passwordReset;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shuttle.user.GenericUser;

public interface IPasswordResetRepository extends JpaRepository<PasswordResetCode, Long> {
	@Query("select pr from PasswordResetCode pr where pr.code = :code")
	Optional<PasswordResetCode> findByCode(String code);
	@Query("select p from PasswordResetCode p where (p.user.id = :userId) and (p.active = true) and (p.expires > current_time)")
	Optional<PasswordResetCode> findByUser(Long userId);
	@Query("select p from PasswordResetCode p where (p.user.id = :userId) and (p.active = true)")
	List<PasswordResetCode> findByUserActiveMaybeExpired(Long userId);
}
