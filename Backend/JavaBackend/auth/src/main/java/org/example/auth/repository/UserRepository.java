package org.example.auth.repository;

import org.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM users where email=?1 and islock=false and isenabled=true")
    Optional<User> findUserByEmailAndLockAndEnabled(String login);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUuid(String uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM users where uuid=:userId")
    Optional<User> findByUuid(String userId);
}
