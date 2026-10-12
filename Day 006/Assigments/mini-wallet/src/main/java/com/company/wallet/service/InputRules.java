package com.company.wallet.service;

import java.math.BigDecimal;

import com.company.wallet.exception.ValidationException;

/*
All input validation rules in one place.
Each method returns the cleaned value or throws ValidationException.
*/
public final class InputRules {

    /*
    Largest single deposit / withdrawal / transfer: Rs. 10,00,000.
    */
    public static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("1000000.00");

    private static final String NAME_PATTERN = "[A-Za-z .'-]+";
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$";
    private static final String PHONE_PATTERN = "[6-9]\\d{9}";
    private static final String WALLET_NUMBER_PATTERN = "WAL\\d{7}";

    private InputRules() {
    }

    public static String name(String value) throws ValidationException {

        String name = value == null ? "" : value.trim();

        if (name.isEmpty()) {
            throw new ValidationException("Name cannot be blank.");
        }

        if (name.length() > 120 || !name.matches(NAME_PATTERN)) {
            throw new ValidationException(
                    "Name must be up to 120 letters, spaces, dots, hyphens or apostrophes.");
        }

        return name;
    }

    public static String email(String value) throws ValidationException {

        String email = value == null ? "" : value.trim().toLowerCase();

        if (email.length() > 120 || !email.matches(EMAIL_PATTERN)) {
            throw new ValidationException("Invalid email address: '" + value + "'.");
        }

        return email;
    }

    public static String phone(String value) throws ValidationException {

        String phone = value == null ? "" : value.trim();

        if (!phone.matches(PHONE_PATTERN)) {
            throw new ValidationException(
                    "Phone must be a 10-digit mobile number starting with 6-9.");
        }

        return phone;
    }

    public static String walletNumber(String value) throws ValidationException {

        String number = value == null ? "" : value.trim().toUpperCase();

        if (!number.matches(WALLET_NUMBER_PATTERN)) {
            throw new ValidationException(
                    "Wallet number must look like WAL0000001 (found '" + value + "').");
        }

        return number;
    }

    /*
    Amount of a deposit / withdrawal / transfer / credit.
    */
    public static BigDecimal amount(BigDecimal amount) throws ValidationException {

        if (amount == null || amount.signum() <= 0) {
            throw new ValidationException("Amount must be greater than 0.");
        }

        if (amount.scale() > 2) {
            throw new ValidationException("Amount can have at most 2 decimal places.");
        }

        if (amount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            throw new ValidationException(
                    "Amount cannot exceed Rs. " + MAX_TRANSACTION_AMOUNT + " per transaction.");
        }

        return amount;
    }

    /*
    Optional text (description): blank becomes null.
    */
    public static String optionalText(String value, int maxLength) throws ValidationException {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String text = value.trim();

        if (text.length() > maxLength) {
            throw new ValidationException("Text cannot be longer than " + maxLength + " characters.");
        }

        return text;
    }
}
