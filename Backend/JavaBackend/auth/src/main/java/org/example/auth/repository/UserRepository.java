package org.example.auth.repository;

import jakarta.transaction.Transactional;
import org.example.auth.entity.Role;
import org.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.email = :login AND u.lock = false AND u.enabled = true")
    Optional<User> findUserByEmailAndLockAndEnabled(String login);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUuid(String uuid);

    @Query(nativeQuery = true, value = "SELECT * FROM users where uuid=:userId")
    Optional<User> findByUuid(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.imageUrl = :imageUrl WHERE u.uuid = :userUuid")
    void updateImageUrlByUuid(@Param("userUuid") String userUuid, @Param("imageUrl") String imageUrl);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findById(long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.role = :role WHERE u.id = :userId")
    int changeRole(Long userId, Role role);
}
