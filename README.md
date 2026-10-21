# SauceDemo Web Automation

Selenium + TestNG + Java tests for [Sauce Demo](https://www.saucedemo.com).

One Chrome window runs this flow: login, sort by price, add 2 products, checkout, logout, then a locked user.

## What you need

- JDK 11 or newer
- Maven 3.8 or newer
- Google Chrome (or Firefox / Edge)
- The project folder on your computer

Check in a terminal:

```bash
java -version
mvn -version
```

Both commands must print a version. If `mvn` is not recognized, install Maven and add its `bin` folder to PATH, then close and reopen the editor.

## Test data

File: `src/test/resources/testdata/saucedemoUserdata.xls`

| Sheet | What it is | Columns |
|-------|------------|---------|
| Sheet 1 | Valid user | Username, password |
| Sheet 2 | Locked user | Username, password |
| Sheet 3 | Checkout customer | First name, Last name, Zip |

Example valid user: `standard_user` / `secret_sauce`

Browser and site URL are in `src/test/resources/config.properties`. Default browser is Chrome.

## Run in Eclipse

1. Install Eclipse IDE for Java Developers.
2. Install the **TestNG** plugin: **Help → Eclipse Marketplace**, search for TestNG, install it, then restart Eclipse.
3. Import the project: **File → Import → Maven → Existing Maven Projects**.
4. Select this project folder and click **Finish**. Wait until Maven finishes downloading libraries.
5. Right-click `src/test/resources/testng.xml` → **Run As → TestNG Suite**.

To change the browser, edit `browser` in `src/test/resources/config.properties` (`chrome`, `firefox`, or `edge`) and run the suite again.

You can also right-click `TestCases.java` → **Run As → TestNG Test**.


## Run in VS Code

1. Install the **Extension Pack for Java** extension.
2. Open this project folder in VS Code: **File → Open Folder**.
3. Open a terminal: **Terminal → New Terminal**.
4. Run:

```bash
mvn clean test
```

To pick a browser:

```bash
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

You can also open `src/test/java/com/saucedemo/tests/TestCases.java` and click the **Run** button above the class.


## After the run

| Result | Where to look |
|--------|----------------|
| Pass or fail summary | Terminal or the TestNG result window |
| HTML report | `reports/ExtentReport_*.html` (open the newest file in a browser) |
| Screenshot when a test fails | `screenshots/` |

The first run can take a few minutes. Maven downloads the libraries, and the browser driver is saved in the `webdriver-cache` folder inside this project.
