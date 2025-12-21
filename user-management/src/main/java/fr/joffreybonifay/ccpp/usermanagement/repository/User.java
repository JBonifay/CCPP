package fr.joffreybonifay.ccpp.usermanagement.repository;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    public User() {

    }

    public User(String email, String password, String fullName) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public User(UUID id, String email, String password, String fullName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

}
