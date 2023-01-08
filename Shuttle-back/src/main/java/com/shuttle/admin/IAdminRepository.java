package com.shuttle.admin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IAdminRepository extends JpaRepository<Admin, Long> {
}