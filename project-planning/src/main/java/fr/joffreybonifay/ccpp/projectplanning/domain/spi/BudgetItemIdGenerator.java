package fr.joffreybonifay.ccpp.projectplanning.domain.spi;

import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public interface BudgetItemIdGenerator {
    BudgetItemId generate();
}
