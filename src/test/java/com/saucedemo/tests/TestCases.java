package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.dataprovider.LoginDataProvider;
import com.saucedemo.listeners.ExtentTestListener;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.LogoutPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * One browser session: login, sort, cart, checkout, logout, then locked user.
 */
public class TestCases extends BaseTest {

    private List<String> addedProducts;

    @Test(priority = 1, description = "Login with the valid user from Excel sheet 1",
            groups = {"smoke", "e2e"})
    public void loginWithValidUser() {
        Map<String, String> data = LoginDataProvider.validUser();
        String username = data.get("username");
        String password = data.get("password");

        ExtentTestListener.getTest().info("Username: " + username);

        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be visible");

        InventoryPage inventoryPage = loginPage.loginAs(username, password);

        Assert.assertTrue(inventoryPage.isOnInventoryUrl(),
                "URL should contain inventory.html after successful login");
        Assert.assertTrue(inventoryPage.isInventoryPageDisplayed(),
                "Products page should be displayed after login");
        Assert.assertEquals(inventoryPage.getProductsTitle(), "Products",
                "Page title should be Products");
        Assert.assertTrue(inventoryPage.isShoppingCartDisplayed(),
                "Shopping cart icon should be visible after login");

        ExtentTestListener.getTest().pass("Valid login successful for user: " + username);
    }

    @Test(priority = 2, dependsOnMethods = "loginWithValidUser",
            description = "Sort products by price low to high and verify the order",
            groups = {"e2e"})
    public void sortProductsByPriceLowToHigh() {
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.sortByPriceLowToHigh();

        List<Double> prices = inventoryPage.getDisplayedPrices();
        ExtentTestListener.getTest().info("Prices after sort: " + prices);
        Assert.assertTrue(inventoryPage.isSortedLowToHigh(prices),
                "Product prices should be sorted from low to high. Actual: " + prices);

        ExtentTestListener.getTest().pass("Products are sorted by price low to high");
    }

    @Test(priority = 3, dependsOnMethods = "sortProductsByPriceLowToHigh",
            description = "Add two products and verify the cart count and names",
            groups = {"e2e"})
    public void addTwoProductsAndVerifyCart() {
        InventoryPage inventoryPage = new InventoryPage(driver);
        addedProducts = inventoryPage.addFirstProducts(2);

        Assert.assertEquals(inventoryPage.getCartCount(), 2, "Cart badge should show 2");

        CartPage cartPage = inventoryPage.openCart();
        Assert.assertEquals(cartPage.getItemCount(), 2, "Cart should contain 2 products");
        Assert.assertEquals(cartPage.getProductNames(), addedProducts,
                "Cart product names should match the products that were added");

        ExtentTestListener.getTest().pass("Cart count and product names verified: " + addedProducts);
    }

    @Test(priority = 4, dependsOnMethods = "addTwoProductsAndVerifyCart",
            description = "Complete checkout and verify total equals item total plus tax",
            groups = {"e2e"})
    public void checkoutAndVerifyTotal() {
        Map<String, String> customer = LoginDataProvider.checkoutCustomer();
        String firstName = LoginDataProvider.value(customer, "firstname", "first name");
        String lastName = LoginDataProvider.value(customer, "lastname", "last name");
        String zip = LoginDataProvider.value(customer, "zip", "zipcode", "postalcode", "postal code");

        ExtentTestListener.getTest().info("Checkout customer: " + firstName + " " + lastName + ", " + zip);

        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = cartPage.clickCheckout()
                .enterCustomerDetails(firstName, lastName, zip)
                .continueToOverview();

        BigDecimal itemTotal = checkoutPage.getItemTotal();
        BigDecimal tax = checkoutPage.getTax();
        BigDecimal total = checkoutPage.getTotal();
        BigDecimal expectedTotal = itemTotal.add(tax);

        ExtentTestListener.getTest().info("Item total: " + itemTotal + ", Tax: " + tax + ", Total: " + total);
        Assert.assertEquals(total.compareTo(expectedTotal), 0,
                "Final total should equal item total plus tax. Item " + itemTotal + " + tax " + tax + " = " + expectedTotal + ", page total " + total);

        checkoutPage.finish();
        Assert.assertTrue(checkoutPage.isOrderComplete(), "Order completion message should be displayed");

        ExtentTestListener.getTest().pass("Checkout total verified: " + total);
    }

    @Test(priority = 5, dependsOnMethods = "checkoutAndVerifyTotal",
            description = "Logout and return to the login page",
            groups = {"e2e"})
    public void logout() {
        LoginPage loginPage = new LogoutPage(driver).logout();
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Login page should be displayed after logout");
        ExtentTestListener.getTest().pass("Logout completed in the same browser");
    }

    @Test(priority = 6, dependsOnMethods = "logout",
            description = "Locked user from Excel sheet 2 cannot login",
            groups = {"e2e"})
    public void lockedUserCannotLogin() {
        Map<String, String> data = LoginDataProvider.lockedUser();
        String username = data.get("username");
        String password = data.get("password");
        String expectedError = data.getOrDefault("expectederror", "locked out");

        ExtentTestListener.getTest().info("Locked username: " + username);

        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed for the locked user");
        Assert.assertTrue(loginPage.getErrorMessage().toLowerCase().contains(expectedError.toLowerCase()),
                "Error should contain '" + expectedError + "'. Actual: " + loginPage.getErrorMessage());
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Locked user should stay on the login page");

        ExtentTestListener.getTest().pass("Locked user was rejected: " + loginPage.getErrorMessage());
    }
} 
