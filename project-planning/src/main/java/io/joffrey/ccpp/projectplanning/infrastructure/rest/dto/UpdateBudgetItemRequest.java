package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

import com.ccpp.shared.valueobjects.Money;

public record UpdateBudgetItemRequest(
        String description,
        Money amount
) {
}
