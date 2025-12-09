package com.ccpp.shared.valueobjects;

import com.ccpp.shared.exception.DateRangeException;

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
