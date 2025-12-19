package fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi;

import fr.joffreybonifay.ccpp.projectplanning.domain.spi.BudgetItemIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
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
