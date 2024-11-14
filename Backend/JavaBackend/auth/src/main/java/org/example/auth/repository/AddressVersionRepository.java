package org.example.auth.repository;

import org.example.auth.entity.AddressVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressVersionRepository extends JpaRepository<AddressVersion, Long> {

}
