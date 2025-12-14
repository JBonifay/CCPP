package io.joffrey.ccpp.projectplanning.domain.spi;

import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public interface BudgetItemIdGenerator {
    BudgetItemId generate();
}
