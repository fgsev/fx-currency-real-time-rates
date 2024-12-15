package com.fxcurrency.Model;

import com.fxcurrency.MoneyFormatException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyFormatter {
    private static final Pattern VALID_AMOUNT_PATTERN = Pattern.compile("^(\\d+(\\.\\d{1,2})?)([A-Za-z]{3})$");

    public Money parse(String input) {
        if (input == null || input.isEmpty()) {
            throw new MoneyFormatException("Invalid amount");
        }

        Matcher matcher = VALID_AMOUNT_PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new MoneyFormatException("Invalid amount");
        }

        String amountString = matcher.group(1);
        String currencyCode = matcher.group(3);

        BigDecimal amount = new BigDecimal(amountString.replace(",", "."));
        if (amount.signum() <= 0) {
            throw new MoneyFormatException("Invalid amount");
        }

        Currency currency = Currency.getInstance(currencyCode);

        return new Money(amount, currency);
    }
}
