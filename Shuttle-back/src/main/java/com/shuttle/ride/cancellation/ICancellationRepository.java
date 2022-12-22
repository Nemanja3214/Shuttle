package com.shuttle.ride.cancellation;

import org.springframework.data.repository.CrudRepository;

public interface ICancellationRepository extends CrudRepository<Cancellation, Long> {

}
