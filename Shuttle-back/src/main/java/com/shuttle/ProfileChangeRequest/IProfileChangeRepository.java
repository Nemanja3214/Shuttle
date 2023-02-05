package com.shuttle.ProfileChangeRequest;

import com.shuttle.driver.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProfileChangeRepository extends JpaRepository<ProfileChangeRequest, Long>{
}
