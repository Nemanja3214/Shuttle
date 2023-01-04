package com.shuttle.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shuttle.security.Role;

public interface UserRepository extends JpaRepository<GenericUser,Long> {
    GenericUser findByEmail(String email);
    List<GenericUser> findByRoles(Role role);
}
