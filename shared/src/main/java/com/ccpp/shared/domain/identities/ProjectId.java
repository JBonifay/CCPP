package com.ccpp.shared.domain.identities;

import java.util.UUID;

public record ProjectId(
        UUID value
) {

    public ProjectId(String value) {
        this(UUID.fromString(value));
    }

}
