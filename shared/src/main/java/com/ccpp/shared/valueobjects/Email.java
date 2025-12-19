package com.ccpp.shared.valueobjects;

import com.ccpp.shared.exception.EmailFormatException;

import java.util.regex.Pattern;

public record Email(
        String value
) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email {
        if (value == null || value.isBlank()) {
            throw new EmailFormatException("Invalid email: email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new EmailFormatException("Invalid email format: " + value);
        }
    }
}
