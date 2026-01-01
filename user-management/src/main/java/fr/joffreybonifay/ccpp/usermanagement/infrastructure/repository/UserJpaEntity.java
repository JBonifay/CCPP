package fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    public UserJpaEntity() {

    }

    public UserJpaEntity(String email, String password, String fullName) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public UserJpaEntity(UUID id, String email, String password, String fullName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

}
