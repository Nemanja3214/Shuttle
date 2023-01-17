package com.shuttle.ride.cancellation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICancellationRepository extends JpaRepository<Cancellation, Long> {

}
