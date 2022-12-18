package com.shuttle.credentials;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Set;

@Service
public class CredentialsService implements ICredentialService {

    @Autowired
    ICredentialsRepository allCredentials;
    @Autowired
    PasswordEncoder encoder;

    @Override
    public Collection<Credentials> getAll() {
        return null;
    }

    @Override
    public Credentials findCredentials(Long credentialsId) {
        return null;
    }

    @Override
    public Credentials insert(Credentials student) {
        return null;
    }

    @Override
    public Credentials insert(String email, String password) {
        try {
            Credentials credentials = new Credentials(email,encoder.encode(password));
            allCredentials.save(credentials);
            allCredentials.flush();
            return credentials;
        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> errors = ex.getConstraintViolations();
            StringBuilder sb = new StringBuilder(1000);
            for (ConstraintViolation<?> error : errors) {
                sb.append(error.getMessage() + "\n");
            }
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, sb.toString());
        }
    }

    @Override
    public Credentials update(Credentials student) {
        return null;
    }

    @Override
    public Credentials delete(Long studentId) {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
