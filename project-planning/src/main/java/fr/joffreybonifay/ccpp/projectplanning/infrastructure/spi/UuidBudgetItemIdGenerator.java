package fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi;

import fr.joffreybonifay.ccpp.projectplanning.domain.spi.BudgetItemIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;

import java.util.UUID;

public class UuidBudgetItemIdGenerator implements BudgetItemIdGenerator {

    @Override
    public BudgetItemId generate() {
        return new BudgetItemId(UUID.randomUUID());
    }

}
