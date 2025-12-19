package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto;

import fr.joffreybonifay.ccpp.projectplanning.application.query.model.BudgetItemDTO;

import java.math.BigDecimal;
import java.util.Currency;

public record BudgetItemResponse(
        String id,
        String description,
        BigDecimal value,
        Currency currency
) {

    public static BudgetItemResponse from(BudgetItemDTO budgetItemDTO) {
        return new BudgetItemResponse(
                budgetItemDTO.budgetItemId().value().toString(),
                budgetItemDTO.description(),
                budgetItemDTO.amount().value(),
                budgetItemDTO.amount().currency()
        );

    }

}
