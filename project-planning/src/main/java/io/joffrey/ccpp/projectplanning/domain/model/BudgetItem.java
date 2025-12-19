package io.joffrey.ccpp.projectplanning.domain.model;

import com.ccpp.shared.entity.Entity;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

import java.math.BigDecimal;
import java.util.Currency;

public class BudgetItem extends Entity {

    private final BudgetItemId budgetItemId;
    private final String description;
    private final Money amount;

    public BudgetItem(BudgetItemId budgetItemId, String description, Money amount) {
        this.budgetItemId = budgetItemId;
        this.description = description;
        this.amount = amount;
    }

    public Currency getCurrency() {
        return amount.currency();
    }

    public BigDecimal getCost() {
        return amount.value();
    }
}
