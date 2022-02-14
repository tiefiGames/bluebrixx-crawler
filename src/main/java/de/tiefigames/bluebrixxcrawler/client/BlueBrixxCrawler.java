package de.tiefigames.bluebrixxcrawler.client;

import de.tiefigames.bluebrixxcrawler.entity.Product;
import de.tiefigames.bluebrixxcrawler.entity.ProductStatus;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BlueBrixxCrawler {

    private static Logger logger = LoggerFactory.getLogger(BlueBrixxCrawler.class);

    private WebDriver webDriver;

    @Value("${chrome.driver.url}")
    private String driverUrl;

    @Value("${proxy.url}")
    private String proxyUrl;

    public synchronized Product getProduct(String productUrl) {
        try {
            initializeWebDriver();

            webDriver.get(proxyUrl);
            webDriver.findElement(By.xpath("(//input[@placeholder='Enter Url'])[1]")).sendKeys(productUrl);
            webDriver.findElement(By.xpath("(//button)[1]")).click();

            Product product = new Product();
            product.setUrl(productUrl);
            product.setName(webDriver.findElement(By.xpath("//*[@id='item_desc']/h1")).getText());
            product.setProductStatus(getProductStatus());
            product.setCreated(LocalDateTime.now());
            // extract setnumber from url
            Pattern pattern = Pattern.compile("\\d{6}");
            Matcher matcher = pattern.matcher(productUrl);
            if (matcher.find()) {
                product.setSetNumber(matcher.group(0));
            }

            this.webDriver.quit();

            return product;
        } catch (TimeoutException e){
            throw e;
        } finally {
            this.webDriver.quit();
        }
    }

    public synchronized ProductStatus getProductStatus(String productUrl) {
        try {
            initializeWebDriver();

            webDriver.get(proxyUrl);
            webDriver.findElement(By.xpath("(//input[@placeholder='Enter Url'])[1]")).sendKeys(productUrl);
            webDriver.findElement(By.xpath("(//button)[1]")).click();

            ProductStatus productStatus = getProductStatus();
            this.webDriver.quit();

            return productStatus;
        } catch (TimeoutException e){
            throw e;
        } finally {
            this.webDriver.quit();
        }
    }

    private ProductStatus getProductStatus() {
        try {
            webDriver.findElement(By.className("informAvailable_button_container"));
        } catch (NoSuchElementException ex) {
            return ProductStatus.AVAILABLE;
        }

        WebElement webElement = webDriver.findElement(By.xpath("//div[@class='mainPicContainer']/*[last()]"));
        logger.info("text: {}", webElement.getText());
        switch (webElement.getText()) {
            case "ZURZEIT VERGRIFFEN":
                return ProductStatus.NOT_AVAILABLE;
            case "BALD ERHÄLTLICH!":
                return ProductStatus.SOON_AVAILABLE;
            case "ANKÜNDIGUNG":
                return ProductStatus.ANNOUNCEMENT;
            default:
                return ProductStatus.UNKNOWN;
        }
    }

    private void initializeWebDriver() {
        /*
        Chrome local without Docker Container
        Check Version and update driver if not works!


        System.setProperty("webdriver.chrome.driver", "/home/dtiefenbach//Downloads/chromedriver_linux64/chromedriver");
        this.webDriver = new ChromeDriver();
        this.webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        */

        ChromeOptions chromeOptions = new ChromeOptions();
        try {
            this.webDriver = new RemoteWebDriver(new URL(driverUrl), chromeOptions);
            this.webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
