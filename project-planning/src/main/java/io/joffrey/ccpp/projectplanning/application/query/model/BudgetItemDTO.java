package io.joffrey.ccpp.projectplanning.application.query.model;

import com.ccpp.shared.domain.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record BudgetItemDTO(
        BudgetItemId budgetItemId,
        String description,
        Money amount
) {
}
