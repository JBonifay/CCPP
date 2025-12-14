package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

import java.math.BigDecimal;

public record AddBudgetItemRequest(
        String description,
        BigDecimal amount,
        String currency
) {
}
