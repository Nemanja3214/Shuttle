package com.shuttle.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<GenericUser,Long> {
    GenericUser findByEmail(String email);
}
