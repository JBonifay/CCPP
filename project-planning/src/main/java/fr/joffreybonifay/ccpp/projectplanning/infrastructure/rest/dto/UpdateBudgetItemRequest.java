package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto;

import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;

public record UpdateBudgetItemRequest(
        String description,
        Money amount
) {
}
