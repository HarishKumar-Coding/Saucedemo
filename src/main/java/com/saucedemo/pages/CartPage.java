package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the SauceDemo cart.
 */
public class CartPage extends BasePage {

    private final By itemNames = By.cssSelector(".inventory_item_name");
    private final By cartItems = By.cssSelector(".cart_item");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getProductNames() {
        List<String> names = new ArrayList<>();
        for (WebElement name : findAll(itemNames)) {
            names.add(name.getText().trim());
        }
        return names;
    }

    public int getItemCount() {
        return findAll(cartItems).size();
    }

    public CheckoutPage clickCheckout() {
        click(checkoutButton);
        waitForUrlContains("checkout-step-one.html");
        return new CheckoutPage(driver);
    }
}
