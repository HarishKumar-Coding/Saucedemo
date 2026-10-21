package com.saucedemo.pages;

import com.saucedemo.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for SauceDemo Inventory (Products) page — used to verify successful login.
 */
public class InventoryPage extends BasePage {

    private final By productsTitle = By.cssSelector(".title");
    private final By inventoryContainer = By.id("inventory_container");
    private final By shoppingCartLink = By.className("shopping_cart_link");
    private final By sortDropdown = By.cssSelector("[data-test='product-sort-container'], [data-test='product_sort_container']");
    private final By productPrices = By.cssSelector(".inventory_item_price");
    private final By productItems = By.cssSelector(".inventory_item");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isInventoryPageDisplayed() {
        return isDisplayed(inventoryContainer) && getProductsTitle().equalsIgnoreCase("Products");
    }

    public String getProductsTitle() {
        return getText(productsTitle);
    }

    public boolean isShoppingCartDisplayed() {
        return isDisplayed(shoppingCartLink);
    }

    public boolean isOnInventoryUrl() {
        return getCurrentUrl().contains("inventory.html");
    }

    public void sortByPriceLowToHigh() {
        selectByValue(sortDropdown, "lohi");
        wait.until(driver -> isSortedLowToHigh(getDisplayedPrices()));
    }

    public List<Double> getDisplayedPrices() {
        List<Double> prices = new ArrayList<>();
        for (WebElement price : findAll(productPrices)) {
            prices.add(Double.parseDouble(price.getText().replace("$", "").trim()));
        }
        return prices;
    }

    public boolean isSortedLowToHigh(List<Double> prices) {
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) {
                return false;
            }
        }
        return prices.size() > 1;
    }

    public List<String> addFirstProducts(int count) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<WebElement> items = findAll(productItems);
            WebElement item = items.get(i);
            names.add(item.findElement(By.cssSelector(".inventory_item_name")).getText().trim());
            item.findElement(By.cssSelector("button.btn_inventory")).click();
        }
        return names;
    }

    public int getCartCount() {
        return Integer.parseInt(getText(cartBadge));
    }

    public CartPage openCart() {
        click(shoppingCartLink);
        waitForUrlContains("cart.html");
        return new CartPage(driver);
    }
}
