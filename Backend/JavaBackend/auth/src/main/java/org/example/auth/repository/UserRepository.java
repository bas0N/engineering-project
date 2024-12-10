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

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findUserByEmailAndIsActive(String email);

    @Query("SELECT u FROM User u WHERE u.uuid = :uuid AND u.isActive = true")
    Optional<User> findUserByUuidAndIsActive(String uuid);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUuid(String uuid);

    @Query("SELECT u FROM User u WHERE u.uuid = :userId")
    Optional<User> findByUuid(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.imageUrl = :imageUrl WHERE u.id = :userId")
    void updateImageUrlById(@Param("userId") Long userId, @Param("imageUrl") String imageUrl);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    User findById(long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.role = :role WHERE u.id = :userId")
    int changeRole(Long userId, Role role);

    @Modifying
    @Query("""
            UPDATE User u
            SET u.isActive = false,
                u.email = null,
                u.imageUrl = null,
                u.phoneNumber = null,
                u.firstName = null,
                u.lastName = null,
                u.password = null,
                u.lock = true,
                u.enabled = false
            WHERE u.id = :userId
            """)
    void deactivateAndClearUser(@Param("userId") Long userId);
}
