package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;

/**
 * Page Object for SauceDemo checkout information and overview.
 */
public class CheckoutPage extends BasePage {

    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By finishButton = By.id("finish");
    private final By itemTotal = By.cssSelector("[data-test='subtotal-label'], .summary_subtotal_label");
    private final By tax = By.cssSelector("[data-test='tax-label'], .summary_tax_label");
    private final By total = By.cssSelector("[data-test='total-label'], .summary_total_label");
    private final By completeHeader = By.cssSelector("[data-test='complete-header'], .complete-header");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage enterCustomerDetails(String first, String last, String zip) {
        type(firstName, first);
        type(lastName, last);
        type(postalCode, zip);
        return this;
    }

    public CheckoutPage continueToOverview() {
        click(continueButton);
        waitForUrlContains("checkout-step-two.html");
        return this;
    }

    public BigDecimal getItemTotal() {
        return parseAmount(getText(itemTotal));
    }

    public BigDecimal getTax() {
        return parseAmount(getText(tax));
    }

    public BigDecimal getTotal() {
        return parseAmount(getText(total));
    }

    public CheckoutPage finish() {
        click(finishButton);
        waitForUrlContains("checkout-complete.html");
        return this;
    }

    public boolean isOrderComplete() {
        return getText(completeHeader).equalsIgnoreCase("Thank you for your order!");
    }

    private BigDecimal parseAmount(String label) {
        String amount = label.replaceAll("[^0-9.]", "");
        return new BigDecimal(amount);
    }
}
