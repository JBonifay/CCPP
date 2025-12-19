package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto;

import fr.joffreybonifay.ccpp.shared.valueobjects.Money;

public record UpdateBudgetItemRequest(
        String description,
        Money amount
) {
}
