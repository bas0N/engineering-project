package org.example.auth.repository;

import org.example.auth.entity.ResetOperations;
import org.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResetOperationsRepository extends JpaRepository<ResetOperations, Long> {
    @Modifying
    void deleteAllByUser(User user);

    Optional<ResetOperations> findByUid(String uid);

    @Query(nativeQuery = true, value = "SELECT * FROM resetoperations WHERE TO_TIMESTAMP(createdate, 'YYYY-MM-DD HH24:MI:SS') <= current_timestamp - INTERVAL '15 minutes'")
    List<ResetOperations> findExpiredOperations();


}
