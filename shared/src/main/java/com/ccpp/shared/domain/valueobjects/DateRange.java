package com.ccpp.shared.domain.valueobjects;

import com.ccpp.shared.domain.exception.DateRangeException;

import java.time.LocalDate;

public record DateRange(
        LocalDate startDate,
        LocalDate endDate
) {

    public DateRange {
        if (startDate.isAfter(endDate)) {
            throw new DateRangeException("Start date must be before end date.");
        }
    }
}
