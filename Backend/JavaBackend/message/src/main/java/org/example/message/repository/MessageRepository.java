package org.example.message.repository;

import org.example.message.dto.response.ChatResponse;
import org.example.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :currentUserId AND m.receiverId = :contactId) OR " +
            "(m.senderId = :contactId AND m.receiverId = :currentUserId) " +
            "ORDER BY m.dateAdded ASC")
    List<Message> findMessagesBetweenUsers(@Param("currentUserId") String currentUserId, @Param("contactId") String contactId);

    @Query("SELECT m FROM Message m WHERE m.uuid = :messageId")
    Optional<Message> findByUuid(String messageId);

    @Query("SELECT m FROM Message m WHERE m.uuid IN :messageIds AND m.receiverId = :currentUserId")
    List<Message> findAllByUuidInAndReceiverId(List<String> messageIds, String currentUserId);

    @Query("""
        SELECT DISTINCT 
          CASE WHEN m.senderId = :userId THEN m.receiverId ELSE m.senderId END
        FROM Message m
        WHERE m.senderId = :userId OR m.receiverId = :userId
    """)
    List<String> findChatPartners(@Param("userId") String userId);

    @Query("""
        SELECT m
        FROM Message m
        WHERE (m.senderId = :userId AND m.receiverId = :partnerId)
           OR (m.senderId = :partnerId AND m.receiverId = :userId)
        ORDER BY m.dateAdded DESC
    """)
    List<Message> findLastMessage(@Param("userId") String userId,
                                  @Param("partnerId") String partnerId,
                                  Pageable pageable);

    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE (m.senderId = :userId OR m.receiverId = :userId)
          AND (m.senderId = :partnerId OR m.receiverId = :partnerId)
          AND m.receiverId = :userId
          AND m.isRead = false
    """)
    long countUnreadMessages(@Param("userId") String userId, @Param("partnerId") String partnerId);


    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :name AND m.isRead = false")
    int countAllByReceiverIdAndReadFalse(String name);
}
