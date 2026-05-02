import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class Bot {
    private final String URL;
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actionProvider;

    private static final int TIMEOUT = 5; // 5 seconds

    private String Tabtitle;
    private String Error;
    private int cartQuantity;

    Bot(String URL) {
        this.URL = URL;
        initialize();
    }

    void initialize() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        this.driver = new ChromeDriver(options);
        this.actionProvider = new Actions(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
    }

    void connect() {
        driver.get(URL);
    }

    void quit() {
        actionProvider.pause(Duration.ofSeconds(TIMEOUT)).build().perform();
        driver.quit();
    }

    public String getTabtitle() {
        return Tabtitle;
    }

    public String getError() {
        return Error;
    }

    private void acceptCookie() {
        List<WebElement> cookiebanner = driver.findElements(By.id("cos-banner"));
        if(cookiebanner.isEmpty()) return;
        wait.until(driver -> driver.findElement(By.id("cos-banner")).isDisplayed()); //id="cos-banner"
        WebElement acceptbttn = driver.findElement(By.id("sp-cc-accept"));
        acceptbttn.click();
    }

    public void clearCart() {
        WebElement clearbtn = driver.findElement(By.cssSelector(".sc-action-delete-active .a-color-link"));
        clearbtn.click();
    }

    void makesearch(String query){
        //write query
        wait.until(driver -> driver.findElement(By.cssSelector("#twotabsearchtextbox")).isDisplayed());
        WebElement searchBox = driver.findElement(By.cssSelector("#twotabsearchtextbox"));
        searchBox.click();
        searchBox.sendKeys(query);

        acceptCookie();

        // send query and check result
        searchBox.submit();
        wait.until(driver -> Objects.equals(((JavascriptExecutor) driver)
                .executeScript("return document.readyState"), "complete"));
        this.Tabtitle = driver.getTitle();


    }

    void addItemToCart(String strquantity, String product_id){

        if (strquantity.isEmpty()){
            Error = "not a num";
            return;
        }
        try {
            Integer.parseInt(strquantity);

        } catch (NumberFormatException nfe) {
            Error = "not a num";
            return;
        }


        int quantity = Integer.parseInt(String.valueOf(strquantity));
        //go to product
        driver.navigate().to(URL+"dp/"+product_id); //"URL+"dp/"+product_id)

        //cookie accept
        acceptCookie();

        //select quantity
        WebElement showlist = driver.findElement(By.id("a-autoid-0-announce"));
        showlist.click();
        WebElement list = driver.findElement(By.cssSelector("ul[role='listbox']"));
        List<WebElement> quantities = list.findElements(By.cssSelector("li[role='presentation']"));

        try {
            quantities.get(quantity-1).click();
        } catch (IndexOutOfBoundsException e) {
            Error = "Out of Stock";
            return;
        }

        //add and go to cart
        WebElement addtocartbtn = driver.findElement(By.id("add-to-cart-button"));
        addtocartbtn.click();
        driver.navigate().to(URL+"cart/");

        //get stock
        //fetchQuantity();
    }

     int fetchQuantity() {
        //get stock
         if(isCartEmpty()) return 0;

         List<WebElement> barStockText = driver.findElements(By.cssSelector(".a-declarative > span:nth-child(2) > span"));
         List<WebElement> textStockText = driver.findElements(By.name("quantityBox"));
         if(!barStockText.isEmpty()){
            cartQuantity = Integer.parseInt(barStockText.get(0).getText());
            return cartQuantity;
         }else if(!textStockText.isEmpty()){
             cartQuantity = Integer.parseInt(textStockText.get(0).getAttribute("value"));
             return cartQuantity;
         }

         return cartQuantity;

    }

    boolean isCartEmpty(){
        wait.until(driver -> driver.findElement(By.id("sc-subtotal-label-activecart")).isDisplayed());
        WebElement totalQ = driver.findElement(By.id("sc-subtotal-label-activecart"));
        System.out.println("Total Q: " + totalQ.getText());
        return totalQ.getText().contains("0 ürün");
    }

    void setCartQuantity(int newQuantity) {
        //get stock
        fetchQuantity();
        int Diff = newQuantity - cartQuantity; //52

        if(cartQuantity < 10 && Math.abs(newQuantity) < 10){ //bar to bar
            if(Diff < 0){ //B2B down
                for(int i = 0; i < Math.abs(Diff); i++){
                    if(cartQuantity == 1){
                        WebElement trashBtn = driver.findElement(By.cssSelector(".a-icon-small-trash"));
                        trashBtn.click();
                        fetchQuantity();
                        break;
                    }
                    WebElement minusQntyBtn = driver.findElement(By.cssSelector(".a-icon-small-remove"));
                    minusQntyBtn.click();
                    wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.cssSelector("div.sc-list-item-overwrap"))));
                    fetchQuantity();
                }
            }else if (Diff > 0){ //B2B up
                for(int i = 0; i < Diff; i++){
                    WebElement addQntyBtn = driver.findElement(By.cssSelector(".a-icon-small-add"));
                    addQntyBtn.click();
                    wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.cssSelector("div.sc-list-item-overwrap"))));
                }
            } else {
                return;
            }
        }
        if (cartQuantity < 10 && Math.abs(newQuantity) > 10) { // bar to text ONLY UP possible
            for(int i = 0; i < 10-cartQuantity; i++){
                WebElement addQntyBtn = driver.findElement(By.cssSelector(".a-icon-small-add"));
                addQntyBtn.click();
                wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.cssSelector("div.sc-list-item-overwrap"))));
            }
            textQntySet(newQuantity);
        }
        if (cartQuantity > 10) { // text to text and text to bar
            textQntySet(newQuantity);
        }
        fetchQuantity();
        if(cartQuantity != newQuantity){ Error = "Out of Stock";}

    }

    private void textQntySet(int amount){
        wait.until(driver -> driver.findElement(By.name("quantityBox")).isDisplayed());
        WebElement qntybox = driver.findElement(By.name("quantityBox"));
        qntybox.click();
//        qntybox.clear();
        qntybox.sendKeys(Keys.ARROW_RIGHT);
        qntybox.sendKeys(Keys.ARROW_RIGHT);
        qntybox.sendKeys(Keys.BACK_SPACE);
        qntybox.sendKeys(Keys.BACK_SPACE);
        qntybox.sendKeys(Integer.toString(amount));
        driver.findElement(By.cssSelector(".sc-quantity-update-button .a-button-text")).click();
    }

    public void resetError() {
        this.Error = "";
    }

    public void pagerefresh() {
        driver.navigate().refresh();
    }
}