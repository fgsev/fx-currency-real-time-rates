package com.fxcurrency.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.fxcurrency.Service.ServiceNBP;
import com.fxcurrency.Model.Money;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    @FXML
    private ComboBox<String> ComboBoxCurrencyFirst;

    @FXML
    private ComboBox<String> ComboBoxCurrencySecond;

    @FXML
    private TextArea currentCurrencies;

    @FXML
    private TextField amountField;

    @FXML
    private TextField calculatedField;

    private ServiceNBP serviceNBP = new ServiceNBP();

    private static final Pattern MONEY_PATTERN = Pattern.compile("^(\\d{1,3}(?:[,.]\\d{1,2})?)$");

    @FXML
    private void handleConversion() {
        try {
            BigDecimal inputAmount = new BigDecimal(amountField.getText().replace(",", "."));
            String fromCurrency = ComboBoxCurrencyFirst.getValue();
            String toCurrency = ComboBoxCurrencySecond.getValue();

            if (fromCurrency == null || toCurrency == null) {
                calculatedField.setText("Wybierz waluty");
                return;
            }

            Map<String, Money> rates = serviceNBP.getExchangeRate(fromCurrency, toCurrency);
            Money fromRate = rates.get(fromCurrency);
            Money toRate = rates.get(toCurrency);

            if (fromRate != null && toRate != null && toRate.amount().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal result = serviceNBP.convertCurrency(fromCurrency, toCurrency, inputAmount);
                calculatedField.setText(result.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            } else {
                calculatedField.setText("Nie udało się pobrać kursu walut.");
            }
        } catch (NumberFormatException e) {
            calculatedField.setText("Niepoprawny format liczby.");
        }
    }

    @FXML
    public void displayCurrencyFromAPI() {
        String[] currencies = {"USD", "EUR", "GBP"};
        var rates = serviceNBP.getExchangeRate(currencies);
        currentCurrencies.clear();
        rates.forEach((currency, money) -> {
            String formattedText = String.format("Kurs %s: %.2f %s\n",
                    currency,
                    money.amount().setScale(2, BigDecimal.ROUND_HALF_UP),
                    money.currency().getCurrencyCode());
            currentCurrencies.appendText(formattedText);
        });
    }

    @FXML
    public void amountInput() {
        String inputText = amountField.getText().replace(",", ".");
        Matcher matcher = MONEY_PATTERN.matcher(inputText);

        if (matcher.matches()) {
            try {
                BigDecimal inputAmount = new BigDecimal(inputText);
                calculatedField.setText(inputAmount.toString());
            } catch (NumberFormatException e) {
                calculatedField.setText("Błąd w wartości kwoty.");
            }
        } else {
            calculatedField.setText("Niepoprawny format kwoty.");
        }
    }

    public void initialize() {
        ComboBoxCurrencyFirst.getItems().addAll("PLN", "USD", "EUR", "GBP");
        ComboBoxCurrencySecond.getItems().addAll("PLN", "USD", "EUR", "GBP");
    }

    public void toggleCurrencyIfEqual() {
        String firstSelectedValue = ComboBoxCurrencyFirst.getValue();
        String secondSelectedValue = ComboBoxCurrencySecond.getValue();

        if (firstSelectedValue.equals(secondSelectedValue)) {
            if (ComboBoxCurrencyFirst.isFocused()) {
                ComboBoxCurrencySecond.setValue(firstSelectedValue.equals("PLN") ? "USD" : "PLN");
            } else {
                ComboBoxCurrencyFirst.setValue(firstSelectedValue.equals("PLN") ? "USD" : "PLN");
            }
        }
    }
}
