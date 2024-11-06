package org.example.message.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Table(name = "messages")
@Entity
@Setter
@Getter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messages_id_seq")
    @SequenceGenerator(name = "messages_id_seq", sequenceName = "messages_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "uuid", updatable = false, nullable = false, unique = true)
    private String uuid;

    // Zmieniamy z @Column na @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User senderId;

    // Zmieniamy z @Column na @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiverId;

    @Column(name = "date_added", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;

    @Column(name = "content", nullable = false)
    private String content;

    public Message() {
        generateUuid();
    }

    private void generateUuid() {
        if (uuid == null || uuid.isEmpty()) {
            setUuid(UUID.randomUUID().toString());
        }
    }

}
