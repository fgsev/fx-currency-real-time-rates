package com.fxcurrency.Service;

import com.fxcurrency.Model.Money;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;



public class ServiceNBP {
    private String API_URL = "https://api.nbp.pl/api/exchangerates/rates/a/";
    private Map<String, Money> exchangeRates;

    public ServiceNBP() {
        this.exchangeRates = new HashMap<>();
    }

    public Map<String, Money> getExchangeRate(String... currencyCodeS) {
        for (String currencyCode : currencyCodeS) {
            String urlString = API_URL + currencyCode.toLowerCase() + "/?format=json";

            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    exchangeRates.put(currencyCode, new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode)));
                    continue;
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String jsonResponse = reader.lines().reduce("", String::concat);
                    double rate = new JSONObject(jsonResponse).getJSONArray("rates").getJSONObject(0).getDouble("mid");
                    BigDecimal rateDecimal = BigDecimal.valueOf(rate);
                    Currency currency = Currency.getInstance(currencyCode);
                    exchangeRates.put(currencyCode, new Money(rateDecimal, currency));
                }

            } catch (Exception e) {
                exchangeRates.put(currencyCode, new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode)));
            }
        }

        return exchangeRates;
    }

    public BigDecimal convertCurrency(String fromCurrencyCode, String toCurrencyCode, BigDecimal amount) {
        Money fromRate = exchangeRates.get(fromCurrencyCode);
        Money toRate = exchangeRates.get(toCurrencyCode);

        if (fromRate == null || toRate == null || fromRate.amount().equals(BigDecimal.ZERO) || toRate.amount().equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("Invalid currency code or rates not available.");
        }

        return amount.multiply(fromRate.amount()).divide(toRate.amount(), 4, RoundingMode.HALF_UP);
    }

    public Map<String, Money> getExchangeRates() {
        return exchangeRates;
    }
}
