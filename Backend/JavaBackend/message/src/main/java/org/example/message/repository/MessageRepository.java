package org.example.message.repository;

import org.example.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.uuid = :userId AND m.receiver.uuid = :contactId) OR " +
            "(m.sender.uuid = :contactId AND m.receiver.uuid = :userId) " +
            "ORDER BY m.dateAdded ASC")
    List<Message> findMessagesBetweenUsers(String currentUserId, String contactId);

    @Query("SELECT m FROM Message m WHERE m.uuid = :messageId")
    Optional<Message> findByUuid(String messageId);
}
