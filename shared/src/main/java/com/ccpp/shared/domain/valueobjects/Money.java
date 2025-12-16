package com.ccpp.shared.domain.valueobjects;

import com.ccpp.shared.domain.exception.CurrencyException;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(
        BigDecimal value,
        Currency currency
) {

    public static Money of(int amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), Currency.getInstance(currency));
    }

    public Money add(Money money) {
        verifySameCurrency(money);
        return new Money(
                value.add(money.value()),
                currency
        );
    }

    public Money subtract(Money money) {
        verifySameCurrency(money);
        return new Money(
                value.subtract(money.value),
                currency
        );
    }

    public Money multiply(int count) {
        return new Money(
                value.multiply(BigDecimal.valueOf(count)),
                currency);
    }

    private void verifySameCurrency(Money money) {
        if (!currency.equals(money.currency()))
            throw new CurrencyException("Cannot process money with different currencies.");
    }
}
