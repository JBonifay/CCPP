package fr.joffreybonifay.ccpp.projectplanning.application.query.model;

import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record BudgetItemDTO(
        BudgetItemId budgetItemId,
        String description,
        Money amount
) {
}
