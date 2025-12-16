package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

import com.ccpp.shared.domain.valueobjects.Money;

public record UpdateBudgetItemRequest(
        String description,
        Money amount
) {
}
