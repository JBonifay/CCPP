package com.ccpp.shared.valueobjects;

import com.ccpp.shared.exception.CurrencyException;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(
        BigDecimal amount,
        Currency currency
) {

    public Money add(Money money) {
        verifySameCurrency(money);
        return new Money(
                amount.add(money.amount()),
                currency
        );
    }

    public Money subtract(Money money) {
        verifySameCurrency(money);
        return new Money(
                amount.subtract(money.amount),
                currency
        );
    }

    public Money multiply(int count) {
        return new Money(
                amount.multiply(BigDecimal.valueOf(count)),
                currency);
    }

    private void verifySameCurrency(Money money) {
        if (!currency.equals(money.currency()))
            throw new CurrencyException("Cannot process money with different currencies.");
    }
}
