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

    @Query(value = """
            SELECT
                CASE
                    WHEN m.senderId = :userId THEN u_receiver.firstName || ' ' || u_receiver.lastName
                    ELSE u_sender.firstName || ' ' || u_sender.lastName
                END AS username,
                m.content AS lastMessage,
                MAX(m.dateAdded) AS lastMessageTime,
                MAX(m.isRead) AS isRead,
                COUNT(CASE WHEN m.receiverId = :userId AND m.isRead = false THEN 1 END) AS unreadCount
            FROM Message m
            JOIN User u_sender ON m.senderId = u_sender.uuid
            JOIN User u_receiver ON m.receiverId = u_receiver.uuid
            WHERE m.senderId = :userId OR m.receiverId = :userId
            GROUP BY
                CASE 
                    WHEN m.senderId = :userId THEN m.receiverId
                    ELSE m.senderId
                END
            ORDER BY lastMessageTime DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT CASE 
                            WHEN m.senderId = :userId THEN m.receiverId
                            ELSE m.senderId
                        END)
                    FROM Message m
                    WHERE m.senderId = :userId OR m.receiverId = :userId
                    """,
            nativeQuery = true)
    Page<ChatResponse> findChats(@Param("userId") String userId, Pageable pageable);
}
