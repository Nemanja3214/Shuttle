package com.shuttle.panic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPanicRepository extends JpaRepository<Panic, Long> {
    
}
