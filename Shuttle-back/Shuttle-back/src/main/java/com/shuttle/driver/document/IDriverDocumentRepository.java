package com.shuttle.driver.document;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IDriverDocumentRepository extends JpaRepository<DriverDocument, Long> {
	@Query("select d from DriverDocument d where d.driver.id = :id")
	List<DriverDocument> findByDriver(Long id);
}
