package io.joffrey.ccpp.projectplanning.domain.valueobject;

import java.util.UUID;

public record BudgetItemId(
        UUID value
) {

    public BudgetItemId(String value) {
        this(UUID.fromString(value));
    }
}
