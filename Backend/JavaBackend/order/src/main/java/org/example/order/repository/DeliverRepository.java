package org.example.order.repository;

import org.example.order.entity.Deliver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliverRepository extends JpaRepository<Deliver, Long> {
    @Query("SELECT d FROM Deliver d WHERE d.uuid = :uuid")
    Optional<Deliver> findByUuid(String uuid);
}
