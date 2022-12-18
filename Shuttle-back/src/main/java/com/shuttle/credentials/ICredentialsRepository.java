package com.shuttle.credentials;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICredentialsRepository extends JpaRepository<Credentials,Long> {
}
