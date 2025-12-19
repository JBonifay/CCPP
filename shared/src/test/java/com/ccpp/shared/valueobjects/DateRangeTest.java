package com.ccpp.shared.valueobjects;

import com.ccpp.shared.exception.DateRangeException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateRangeTest {

    @Test
    void should_create_valid_date_range() {
        DateRange dateRange = new DateRange(
                LocalDate.of(2012, 1, 2),
                LocalDate.of(2013, 1, 3)
        );

        assertThat(dateRange).isEqualTo(
                new DateRange(
                        LocalDate.of(2012, 1, 2),
                        LocalDate.of(2013, 1, 3)
                )
        );
    }

    @Test
    void should_throw_exception_when_start_after_end() {
        assertThatThrownBy(() -> new DateRange(
                LocalDate.of(2015, 1, 2),
                LocalDate.of(2013, 1, 3)))
                .isInstanceOf(DateRangeException.class)
                .hasMessage("Start date must be before end date.");

    }
}
