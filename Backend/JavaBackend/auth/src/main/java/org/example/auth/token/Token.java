package org.example.auth.token;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.auth.entity.User;
import org.flywaydb.core.internal.parser.TokenType;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String tokenId;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;

    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
