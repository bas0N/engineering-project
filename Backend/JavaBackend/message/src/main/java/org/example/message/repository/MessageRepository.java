package org.example.message.repository;

import org.example.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.senderId = :senderId OR m.senderId= :receiverId")
    List<Message> findMessagesBySender_IdAndReceiver_Id(String senderId, String receiverId);
}
