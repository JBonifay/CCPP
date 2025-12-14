package io.joffrey.ccpp.projectplanning.infrastructure.spi;

import io.joffrey.ccpp.projectplanning.domain.spi.BudgetItemIdGenerator;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import lombok.Setter;

@Setter
public class MockBudgetItemIdGenerator implements BudgetItemIdGenerator {

    private BudgetItemId mock;

    @Override
    public BudgetItemId generate() {
        return mock;
    }

    public String getValue() {
        return mock.value().toString();
    }

}
