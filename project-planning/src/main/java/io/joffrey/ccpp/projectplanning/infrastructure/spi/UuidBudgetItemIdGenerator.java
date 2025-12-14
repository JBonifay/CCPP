package io.joffrey.ccpp.projectplanning.infrastructure.spi;

import io.joffrey.ccpp.projectplanning.domain.spi.BudgetItemIdGenerator;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

import java.util.UUID;

public class UuidBudgetItemIdGenerator implements BudgetItemIdGenerator {

    @Override
    public BudgetItemId generate() {
        return new BudgetItemId(UUID.randomUUID());
    }

}
