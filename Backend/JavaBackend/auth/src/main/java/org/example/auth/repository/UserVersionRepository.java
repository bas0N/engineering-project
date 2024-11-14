package org.example.auth.repository;

import org.example.auth.entity.UserVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserVersionRepository extends JpaRepository<UserVersion, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM user_version where uuid=:userId")
    Optional<UserVersion> findByUuid(String userId);
}
