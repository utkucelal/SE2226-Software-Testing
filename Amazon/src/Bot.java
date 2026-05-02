import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

public class Bot {
    private String URL;
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actionProvider;

    private static final int TIMEOUT = 5; // 5 seconds

    private String Tabtitle;
    private String Error;



    Bot(String URL) {
        this.URL = URL;
        initialize();
    }

    private void initialize() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        this.driver = new ChromeDriver(options);
        this.actionProvider = new Actions(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
    }

    private void connect() {
        driver.get(URL);
    }


    private void quit() {
        actionProvider.pause(Duration.ofSeconds(TIMEOUT)).build().perform();
        driver.quit();
    }

    public String getTabtitle() {
        return Tabtitle;
    }

    public String getError() {
        return Error;
    }

    void makesearch(String query){
        connect();
        //write query
        wait.until(driver -> driver.findElement(By.cssSelector("#twotabsearchtextbox")).isDisplayed());
        WebElement searchBox = driver.findElement(By.cssSelector("#twotabsearchtextbox"));
        searchBox.click();
        searchBox.sendKeys(query);

        //cookie accept
        wait.until(driver -> driver.findElement(By.id("cos-banner")).isDisplayed()); //id="cos-banner"
        WebElement acceptbttn = driver.findElement(By.id("sp-cc-accept"));
        acceptbttn.click();

        // send query and check result
        searchBox.submit();
        wait.until(driver -> Objects.equals(((JavascriptExecutor) driver)
                .executeScript("return document.readyState"), "complete"));
        String tabtitle = driver.getTitle();
        this.Tabtitle = tabtitle;

        quit();
    }

    // TODO: adet listesini açıyor ama adet seçmiyor
    // TODO: ürünüde parameter olarak alabilir maybe
    void addItemToCart(int quantity){
        connect();
        //go to product
        driver.navigate().to("https://www.amazon.com.tr/Reflex-Plus-Siamese-Yeti%C5%9Fkin-Mamas%C4%B1/dp/B0FFBV27QL/");

        //cookie accept
        wait.until(driver -> driver.findElement(By.id("cos-banner")).isDisplayed()); //id="cos-banner"
        WebElement acceptance = driver.findElement(By.id("sp-cc-accept"));
        acceptance.click();

        //select quantity
        WebElement listbutton = driver.findElement(By.id("a-autoid-0-announce"));
        listbutton.click();
        if(driver.findElement(By.cssSelector("css=#quantity_"+quantity)).isDisplayed()){
            WebElement stockbutton = driver.findElement(By.cssSelector("css=#quantity_"+quantity));
            stockbutton.click();
        }else{
            Error = "Out of Stock";
        }

    }

    public void run(){
        connect();
        makesearch("laptop");
        quit();
    }
}
