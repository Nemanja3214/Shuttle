package com.shuttle.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IFavouriteRouteRepository extends JpaRepository<FavoriteRoute, Long>{

	@Query(" SELECT COUNT(f) > 0 FROM "
		+ " FavoriteRoute f JOIN f.passengers p "
		+ " WHERE (:id) = p.id "
		+ " GROUP BY p"
		+ " HAVING COUNT(f) >= (:limitation) ")
	public Boolean findCountPassengerFavorites(Long id, Long limitation);

}