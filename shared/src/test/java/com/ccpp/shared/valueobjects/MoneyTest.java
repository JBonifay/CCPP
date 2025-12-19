package com.ccpp.shared.valueobjects;

import com.ccpp.shared.exception.CurrencyException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class MoneyTest {

    @Test
    void should_create_money_with_amount_and_currency() {
        Money money = new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR"));

        assertThat(money.value()).isEqualTo(new BigDecimal(100));
        assertThat(money.currency()).isEqualTo(Currency.getInstance("EUR"));
    }

    @Test
    void should_add_two_money_amounts() {
        Money initial = new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR"));
        Money result = initial.add(new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR")));

        assertThat(result).isEqualTo(new Money(BigDecimal.valueOf(200), Currency.getInstance("EUR")));
    }

    @Test
    void should_subtract_two_money_amounts() {
        Money initial = new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR"));
        Money result = initial.subtract(new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR")));

        assertThat(result).isEqualTo(new Money(BigDecimal.valueOf(0), Currency.getInstance("EUR")));
    }

    @Test
    void should_multiply_two_money_amounts() {
        Money initial = new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR"));
        Money result = initial.multiply(10);

        assertThat(result).isEqualTo(new Money(BigDecimal.valueOf(1000), Currency.getInstance("EUR")));
    }

    @Test
    void should_throw_exception_when_adding_different_currencies() {
        assertProcessingDifferentCurrenciesThrows(() -> new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR")).add(new Money(BigDecimal.valueOf(100), Currency.getInstance("USD"))));
        assertProcessingDifferentCurrenciesThrows(() -> new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR")).subtract(new Money(BigDecimal.valueOf(100), Currency.getInstance("USD"))));
    }

    void assertProcessingDifferentCurrenciesThrows(ThrowableAssert.ThrowingCallable function) {
        assertThatThrownBy(function).isInstanceOf(CurrencyException.class).hasMessage("Cannot process money with different currencies.");
    }
}
