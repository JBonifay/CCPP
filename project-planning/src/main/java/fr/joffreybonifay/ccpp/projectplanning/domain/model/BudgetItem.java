package fr.joffreybonifay.ccpp.projectplanning.domain.model;

import fr.joffreybonifay.ccpp.shared.domain.Entity;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;

@Getter
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
