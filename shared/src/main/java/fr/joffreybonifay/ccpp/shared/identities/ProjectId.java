package fr.joffreybonifay.ccpp.shared.identities;

import java.util.UUID;

public record ProjectId(
        UUID value
) {

    public ProjectId(String value) {
        this(UUID.fromString(value));
    }

}
