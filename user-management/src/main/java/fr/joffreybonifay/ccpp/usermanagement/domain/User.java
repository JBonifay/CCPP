package fr.joffreybonifay.ccpp.usermanagement.domain;

import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;
import fr.joffreybonifay.ccpp.usermanagement.domain.model.Role;
import fr.joffreybonifay.ccpp.usermanagement.domain.model.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class User {

    private final UserId id;
    private Email email;
    private String displayName;
    private UserStatus status;
    private Set<Role> roles;

    public User(
            UserId id,
            Email email,
            String displayName,
            UserStatus status,
            Set<Role> roles
    ) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.status = status;
        this.roles = roles;
    }

    public static User create(UserId id, Email email, String displayName) {
        return new User(
                id,
                email,
                displayName,
                UserStatus.ACTIVE,
                Set.of(Role.USER)
        );
    }

    public void updateProfile(String displayName) {
        this.displayName = displayName;
    }
}
