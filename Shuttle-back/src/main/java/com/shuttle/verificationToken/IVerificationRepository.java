package com.shuttle.verificationToken;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IVerificationRepository extends JpaRepository<VerificationToken, Long>{

}
