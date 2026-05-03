import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/*
Prime account session "C:\Program Files\Google\Chrome\Application\chrome.exe"  --remote-debugging-port=9222 --user-data-dir="C:\Chromes\\utku-prime"
Non-Prime account session "C:\Program Files\Google\Chrome\Application\chrome.exe"  --remote-debugging-port=9222 --user-data-dir="C:\Chromes\alp-no-prime"
*/
public class Bot {
    private final String URL;
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actionProvider;

    private boolean useCookies;
    private static final int TIMEOUT = 5; // 5 seconds

    private String Tabtitle;
    private String Error;
    private int cartQuantity;
    private boolean correctFilter = false;

    Bot(String URL, boolean useCookies) {
        this.URL = URL;

        this.useCookies = useCookies;
        initialize();
    }

    void initialize() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        if (useCookies) {
            options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        }
        this.driver = new ChromeDriver(options);
        this.actionProvider = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
    }

    void connect() {
        driver.get(URL);
    }

    void quit() {
        actionProvider.pause(Duration.ofSeconds(TIMEOUT)).build().perform();
        driver.quit();
    }

    public void pagerefresh() {
        driver.navigate().refresh();
    }

    public void GoBack() {
        driver.navigate().back();
    }

    public String getTabtitle() {
        return Tabtitle;
    }

    public String getError() {
        return Error;
    }

    List<String> getAdvantages(){
        List<String> result = new ArrayList<>();
        List<WebElement> discountSpace = driver.findElements(By.cssSelector(".a-row.a-color-success"));
        for(int i = 0; i < discountSpace.size(); i++){
            result.add(discountSpace.get(i).findElement(By.cssSelector(".a-column.a-span8")).getAttribute("innerText"));
            //System.out.println(discountSpace.get(i).findElement(By.cssSelector(".a-column.a-span8")).getAttribute("innerText"));
        }
        return result;
    }

    public boolean isCorrectFilter() {
        return correctFilter;
    }

    boolean isCartEmpty(){
        wait.until(driver -> driver.findElement(By.id("sc-subtotal-label-activecart")).isDisplayed());
        WebElement totalQ = driver.findElement(By.id("sc-subtotal-label-activecart"));
        //System.out.println("Total Q: " + totalQ.getText());
        return totalQ.getText().contains("0 ürün");
//        List<WebElement> shareBtns = driver.findElements(By.cssSelector("[data-feature-id='save-for-later-action']"));
//        if(shareBtns.isEmpty()) return true;
//        return false;
    }

    boolean isPrime(){
        List<WebElement> primelogo = driver.findElements(By.cssSelector("i.a-icon.a-icon-prime"));
        if(primelogo.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isPrimeCargoFree() {
        try {
            WebElement amount = driver.findElement(By.xpath("//*[@id=\"subtotals-marketplace-table\"]/li[2]/span/div/div[2]/span/span"));
            String text = amount.getAttribute("innerText").replace("\u00a0", " ").trim();
            return text.contains("0,00");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isCargoFree() {
        List<String> Advantages = getAdvantages();
        if(Advantages.isEmpty()) return false;
        if(Advantages.contains("Kargo BEDAVA.")) return true;
        return false;
    }

    public boolean isDiscounted() {
        List<String> Advantages = getAdvantages();
        if(Advantages.isEmpty()) return false;
        if(Advantages.size() == 1 && Advantages.contains("Kargo BEDAVA.")) return false; //free shipping doesnt count as a advantage
        return true;
    }

    private void acceptCookie() {
        List<WebElement> cookiebanner = driver.findElements(By.id("cos-banner"));
        if(cookiebanner.isEmpty()) {
            return;
        }
        wait.until(driver -> driver.findElement(By.id("cos-banner")).isDisplayed()); //id="cos-banner"
        WebElement acceptbttn = driver.findElement(By.id("sp-cc-accept"));
        acceptbttn.click();
    }

    void makeSearch(String query){
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

    void openProduct(String product_id){
        driver.navigate().to(URL+"dp/"+product_id); //"URL+"dp/"+product_id)

        //cookie accept
        acceptCookie();
    }

    void addItemToCart(String strquantity, String product_id,int annonceid) throws InterruptedException {

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
        //System.out.println("annonceid "+ annonceid);
        WebElement showlist = driver.findElement(By.id("a-autoid-"+annonceid+"-announce"));
        showlist.click();
        Thread.sleep(2000);
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

    int fetchQuantity(int ItemID) {
        WebElement countainer = driver.findElement(By.cssSelector("[data-item-index='" + ItemID + "']"));
        //get stock
         if(isCartEmpty()) return 0;

         List<WebElement> barStockText = countainer.findElements(By.cssSelector(".a-declarative > span:nth-child(2) > span"));
         List<WebElement> textStockText = countainer.findElements(By.name("quantityBox"));
         if(!barStockText.isEmpty()){
            cartQuantity = Integer.parseInt(barStockText.get(0).getText());
            return cartQuantity;
         }else if(!textStockText.isEmpty()){
             cartQuantity = Integer.parseInt(textStockText.get(0).getAttribute("value"));
             return cartQuantity;
         }

         return cartQuantity;

    }

    void setCartQuantity(int newQuantity,int itemID) throws InterruptedException {
        //get stock
        fetchQuantity(itemID);
        int Diff = newQuantity - cartQuantity; //52
        if(cartQuantity < 10 && Math.abs(newQuantity) < 10){ //bar to bar
            if(Diff < 0){ //B2B down
                for(int i = 0; i < Math.abs(Diff); i++){
                    if(cartQuantity == 1){
                        List<WebElement> trashBtnList = driver.findElements(By.cssSelector(".a-icon-small-trash"));
                        WebElement trashBtn = trashBtnList.size() == 1 ? trashBtnList.get(0) : trashBtnList.get(itemID-1);;
                        trashBtn.click();
                        fetchQuantity(itemID);
                        break;
                    }
                    WebElement minusQntyBtn = driver.findElements(By.cssSelector(".a-icon-small-remove")).get(itemID-1);
                    minusQntyBtn.click();
                    wait.until(ExpectedConditions.stalenessOf(driver.findElements(By.cssSelector("div.sc-list-item-overwrap")).get(0)));
                    fetchQuantity(itemID);
                }
            }else if (Diff > 0){ //B2B up
                for(int i = 0; i < Diff; i++){
                    WebElement addQntyBtn = driver.findElements(By.cssSelector(".a-icon-small-add")).get(itemID-1);
                    addQntyBtn.click();
                    if(!useCookies){
                        wait.until(ExpectedConditions.stalenessOf(driver.findElements(By.cssSelector("div.sc-list-item-overwrap")).get(0)));
                    }else{
                        Thread.sleep(2500);
                    }

                }
            } else {
                return;
            }
        }
        if (cartQuantity < 10 && Math.abs(newQuantity) > 10) { // bar to text ONLY UP possible
            for(int i = 0; i < 10-cartQuantity; i++){
                WebElement addQntyBtn = driver.findElements(By.cssSelector(".a-icon-small-add")).get(itemID-1);
                addQntyBtn.click();
                wait.until(ExpectedConditions.stalenessOf(driver.findElements(By.cssSelector("div.sc-list-item-overwrap")).get(0)));
            }
            textQntySet(newQuantity);
        }
        if (cartQuantity > 10) { // text to text and text to bar
            textQntySet(newQuantity);
        }
        fetchQuantity(itemID);
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

    public void checkout() {
        WebElement checkoutBtn = driver.findElement(By.name("proceedToRetailCheckout"));
        checkoutBtn.click();
        wait.until(driver -> Objects.equals(((JavascriptExecutor) driver).executeScript("return document.readyState"), "complete"));
    }

    public void goSmartPhone() {
        wait.until(driver -> driver.findElement(By.id("nav-hamburger-menu")).isDisplayed());
        acceptCookie();

        //go to phones
        WebElement hamburgerMn = driver.findElement(By.id("nav-hamburger-menu"));
        hamburgerMn.click();
        WebElement electronicMn = driver.findElement(By.linkText("Elektronik"));
        electronicMn.click();
        WebElement phoneBtn = driver.findElement(By.linkText("Cep Telefonları ve Aksesuarlar"));
        phoneBtn.click();
        WebElement cellPhoneBtn = driver.findElement(By.xpath("//span[@class='a-list-item']//span[@dir='auto'][text()='Cep Telefonları']"));
        cellPhoneBtn.click();

        // filter apple and ascending order
        WebElement appleBtn = driver.findElement(By.xpath("//span[@class='a-size-base a-color-base'][@dir='auto'][text()='Apple']"));
        appleBtn.click();
        WebElement filterMn = driver.findElement(By.id("a-autoid-0-announce"));
        filterMn.click();
        WebElement ascBtn = driver.findElement(By.id("s-result-sort-select_1"));
        ascBtn.click();

    }

    void ascFilterProducts() {
        List<WebElement> allProducts = driver.findElements(
                By.cssSelector("[data-csa-c-item-id]")
        );

        List<Integer> PriceList = new ArrayList<>();

        for (WebElement Product : allProducts) {
            List<WebElement> price = Product.findElements(By.className("a-price-whole"));

            if (!price.isEmpty()) {
                String asinIdPure = Product.getAttribute("data-csa-c-item-id");
                String asinId = asinIdPure.split("\\.")[2];
                if (!Objects.equals(asinId, "1") && !asinId.contains(":")) {
                    String priceStr = price.get(0).getText();
                    int priceInt = Integer.valueOf(priceStr.replace(".", ""));
                    PriceList.add(priceInt);
                    //System.out.println(priceInt);
                }

            }
        }
        if(PriceList.get(0) < PriceList.get(1)){
            correctFilter = true;
        }
    }

    public void confirmAddress() throws InterruptedException {
        WebElement adressBtn = driver.findElement(By.id("change-delivery-link"));
        adressBtn.click();
        Thread.sleep(1000);
        WebElement confirmButton = driver.findElement(By.cssSelector(".a-button-inner")); //aria-labelledby="checkout-primary-continue-button-id-announce"
        confirmButton.click();
    }

}