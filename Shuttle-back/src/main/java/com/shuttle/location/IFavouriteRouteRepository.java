package com.shuttle.location;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IFavouriteRouteRepository extends JpaRepository<FavoriteRoute, Long>{

	@Query(" SELECT COUNT(f) > 0 FROM "
		+ " FavoriteRoute f JOIN f.passengers p "
		+ " WHERE p.id IN (:ids)"
		+ " GROUP BY p"
		+ " HAVING COUNT(f) >= (:limitation) ")
	public Boolean anyPassengerExceededLimit(List<Long> ids, Long limitation);

	@Query("SELECT f FROM FavoriteRoute f JOIN f.passengers p WHERE (:passengerId) = p.id")
	public List<FavoriteRoute> findByPassengerId(long passengerId);

}