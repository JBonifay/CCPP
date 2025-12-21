package fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    private String email;
    private String displayName;

    @Enumerated(EnumType.STRING)
    private UserStatusEntity status;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<RoleEntity> roles;

}
