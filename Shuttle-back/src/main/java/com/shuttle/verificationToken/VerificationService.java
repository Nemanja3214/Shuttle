package com.shuttle.verificationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService implements IVerificationService{
	@Autowired
	private IVerificationRepository verificationRepository;

}
